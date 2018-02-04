package com.foundation.cache.redis;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.foundation.cache.redis.pool.JedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

/**
 * JedisTemplate 提供了一个template方法，通过构造方法传递JedisPool来负责对Jedis连接的获取与归还，以及数据curd操作。
 * JedisAction<T> 和 JedisActionNoResult两种回调接口，适用于有无返回值两种情况。
 * PipelineAction 与 PipelineActionResult两种接口，适合于pipeline中批量传输命令的情况。
 *
 * 同时提供一些JedisOperation中定义的 最常用函数的封装, 如get/set/zadd等。几乎按照redis命令都进行了方法封装
 * 所有redis命令参考：http://doc.redisfans.com/,所有方法声明均参考此文档
 * 套用了建造者模式，根据是否返回结果等对jedis进行了内部封装
 * 另外Redis2版本并不支持服务器端分片，不像memcached那样，不用关心具体怎么实现服务端数据分片。。。
 * 如果要进行分配，需要安装redis3以上版本，然后用JedisShardedTemplate
 * Created by fqh on 2015/12/13
 */
public class JedisTemplate {

	private static Logger logger = LoggerFactory.getLogger(JedisTemplate.class);

	private JedisPool jedisPool;

	public JedisTemplate(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}


	//================通用Jedis操作接口定义，以及实现========================

	/**
	 * 有返回值的模版Action接口
	 */
	public interface JedisAction<T> {
		T action(Jedis jedis);
	}

	/**
	 * 没有返回值的模版Action接口
	 */
	public interface JedisActionNoResult {
		void action(Jedis jedis);
	}

	/**
	 * pipeline适用于批处理
	 * 有返回值的管道Action模版接口
	 */
	public interface PipelineAction {
		List<Object> action(Pipeline pipeline);
	}

	/**
	 * 管道适用于批量数据操作的情况
	 * 没有返回值的管道Action模版接口
	 */
	public interface PipelineActionNoResult {
		void action(Pipeline Pipeline);
	}

	/**
	 * 执行有返回值的JedisAction操作方法。
	 */
	public <T> T execute(JedisAction<T> jedisAction) throws JedisException {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = jedisPool.getResource();
			return jedisAction.action(jedis);
		} catch (JedisException e) {
			broken = handleJedisException(e);
			throw e;
		} finally {
			closeResource(jedis, broken);
		}
	}

	/**
	 * 执行又返回值的JedisAction操作方法。
	 */
	public void execute(JedisActionNoResult jedisAction) throws JedisException {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = jedisPool.getResource();
			jedisAction.action(jedis);
		} catch (JedisException e) {
			broken = handleJedisException(e);
			throw e;
		} finally {
			closeResource(jedis, broken);
		}
	}

	/**
	 * 执行有返回值的，PipelineAction 方法
	 */
	public List<Object> execute(PipelineAction pipelineAction) throws JedisException {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = jedisPool.getResource();
			Pipeline pipeline = jedis.pipelined();
			List<Object> result=pipelineAction.action(pipeline);
			return result;
		} catch (JedisException e) {
			broken = handleJedisException(e);
			throw e;
		} finally {
			closeResource(jedis, broken);
		}
	}

	/**
	 * 执行没有返回值的，PipelineActionNoResult方法
	 */
	public void execute(PipelineActionNoResult pipelineAction) throws JedisException {
		Jedis jedis = null;
		boolean broken = false;
		try {
			jedis = jedisPool.getResource();
			Pipeline pipeline = jedis.pipelined();
			pipelineAction.action(pipeline);
			pipeline.sync();
		} catch (JedisException e) {
			broken = handleJedisException(e);
			throw e;
		} finally {
			closeResource(jedis, broken);
		}
	}


	//=============连接池常用操作===================

	/**
	 * 返回jedis连接池信息
	 */
	public JedisPool getJedisPool() {
		return jedisPool;
	}

	/**
	 * jredis异常处理方法
	 */
	protected boolean handleJedisException(JedisException jedisException) {
		if (jedisException instanceof JedisConnectionException) {
			logger.error("Redis connection " + jedisPool.getAddress() + " lost.", jedisException);
		} else if (jedisException instanceof JedisDataException) {
			if ((jedisException.getMessage() != null) && (jedisException.getMessage().indexOf("READONLY") != -1)) {
				logger.error("Redis connection " + jedisPool.getAddress() + " are read-only slave.", jedisException);
			} else {
				// dataException, isBroken=false
				return false;
			}
		} else {
			logger.error("Jedis exception happen.", jedisException);
		}
		return true;
	}

	/**
	 * 把jedis连接返回给jedis连接池
	 */
	protected void closeResource(Jedis jedis, boolean conectionBroken) {
		try {
			if (conectionBroken) {
				jedisPool.returnBrokenResource(jedis);
			} else {
				jedisPool.returnResource(jedis);
			}
		} catch (Exception e) {
			logger.error("return back jedis failed, will fore close the jedis.", e);
			JedisUtils.destroyJedis(jedis);
		}

	}

	//========= 常用操作=================//

	/**
	 * 删除key,key可以传入多个
	 * 只要有一个key不存在，就返回false
	 */
	public Boolean del(final String... keys) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.del(keys) == keys.length ? true : false;
			}
		});
	}
	/**
	 * 删除key,key可以传入多个
	 * 只要有一个key不存在，就返回false
	 */
	public Boolean del(final byte[] keys) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.del(keys) == keys.length ? true : false;
			}
		});
	}



	/**
	 * 批量添加字符串
	 * @param map
	 * @return
	 */
	public List<Object> batchAddStr(final Map<String, String> map) {
		List<Object> result= execute(new PipelineAction() {
			@Override
			public List<Object> action(Pipeline pipeline) {
				for (Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator(); itr.hasNext(); ) {
					Map.Entry<String, String> entry = itr.next();
					pipeline.setnx(entry.getKey(), entry.getValue());
				}
				List<Object> result= pipeline.syncAndReturnAll();
				return result;
			}
		});
		return result;
	}


	/**
	 * 将 key 改名为 newkey 。
	 * 当 key 和 newkey 相同，或者 key 不存在时，返回一个错误。
	 * 当 newkey 已经存在时， RENAME 命令将覆盖旧值。
	 */
	public String reName(final String oldkey, final String newkey) {
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				return jedis.rename(oldkey, newkey);
			}
		});
	}


	/**
	 * EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间。
	 * 不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)
	 *
	 * @return 如果生存时间设置成功，返回 1 。
	 * 当 key 不存在或没办法设置生存时间，返回 0 。
	 */
	public Long expireAt(final String key, final long unixTime) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.expireAt(key, unixTime);
			}
		});
	}

	/**
	 * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
	 *
	 * @return 设置成功返回 1 。
	 * 当 key 不存在或者不能为 key 设置生存时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的生存时间)，返回 0 。
	 */
	public Long expire(final String key, final int seconds) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.expire(key, seconds);
			}
		});
	}

	/**
	 * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
	 *
	 * @return 设置成功返回 1 。
	 * 当 key 不存在或者不能为 key 设置生存时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的生存时间)，返回 0 。
	 */
	public Long expire(final byte[] key, final int seconds) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.expire(key, seconds);
			}
		});
	}

	/**
	 * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
	 * 当 key 不存在或者存在但没有设置剩余生存时间时，返回 -1 。
	 * 否则，以秒为单位，返回 key 的剩余生存时间。
	 */
	public Long ttl(final String key) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.ttl(key);
			}
		});
	}

	/**
	 * 清空redis（删除所有keys）
	 */
    /*public void flushDB() {
        execute(new JedisActionNoResult() {
            @Override
            public void action(Jedis jedis) {
                jedis.flushDB();
            }
        });
    }*/

	/**
	 * 判断key是否存在
	 */
	public boolean exists(final String key) {
		return execute(new JedisAction<Boolean>() {
						   @Override
						   public Boolean action(Jedis jedis) {
							   return jedis.exists(key);
						   }
					   }
		);
	}

	/**
	 * 判断key是否存在
	 */
	public boolean exists(final byte[] key) {
		return execute(new JedisAction<Boolean>() {
						   @Override
						   public Boolean action(Jedis jedis) {
							   return jedis.exists(key);
						   }
					   }
		);
	}


	// ================String（字符串） 操作============ //

	/**
	 * 根据key获得string类型的value
	 */
	public String get(final String key) {
		return execute(new JedisAction<String>() {
			@Override
			public String action(Jedis jedis) {
				return jedis.get(key);
			}
		});
	}

	/**
	 * 根据key获得string类型的value
	 */
	public byte[] get(final byte[] key) {
		return execute(new JedisAction<byte[]>() {
			@Override
			public byte[] action(Jedis jedis) {
				return jedis.get(key);
			}
		});
	}

	/**
	 * 根据key获得string类型的value
	 */
	public byte[] getAsByte(final String key) {
		return execute(new JedisAction<byte[]>() {
			@Override
			public byte[] action(Jedis jedis) {
				return jedis.get(key.getBytes());
			}
		});
	}

	/**
	 * 如果确定存进去的key可以转换成Long，用此方法get
	 */
	public Long getAsLong(final String key) {
		String result = get(key);
		return result != null ? Long.valueOf(result) : null;
	}

	/**
	 * 如果确定存进去的key可以转换成Integer，用此方法
	 */
	public Integer getAsInt(final String key) {
		String result = get(key);
		return result != null ? Integer.valueOf(result) : null;
	}


	/**
	 * 如果 key 已经存在并且是一个字符串， APPEND 命令将 value 追加到 key 原来的值的末尾。
	 * 如果 key 不存在， APPEND 就简单地将给定 key 设为 value ，就像执行 SET key value 一样。
	 * @param key
	 * @param value
	 * @return
	 */
	public Long append(final String key, final String value) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.append(key, value);
			}
		});
	}

	/**
	 * 返回所有(一个或多个)给定 key 的值
	 * 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil 。因此，该命令永不失败
	 */
	public List<String> mget(final String... keys) {
		return execute(new JedisAction<List<String>>() {
			@Override
			public List<String> action(Jedis jedis) {
				return jedis.mget(keys);
			}
		});
	}

	/**
	 * 将字符串值 value 关联到 key 。
	 * 如果 key 已经持有其他值， SET 就覆写旧值，无视类型
	 * ps:value虽然可以放入最多不超过1GB的字符串。但还是存入的越短越好
	 */
	public void set(final String key, final String value) {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.set(key, value);
			}
		});
	}
	/**
	 * 将二进制值 value 关联到二进制 key 。
	 * 如果 key 已经持有其他值， SET 就覆写旧值，无视类型
	 * ps:value虽然可以放入最多不超过1GB的字符串。但还是存入的越短越好
	 */
	public void set(final byte[] key, final byte[] value) {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.set(key, value);
			}
		});
	}



	/**
	 * 将值 value 关联到 key ，并将 key 的生存时间设为 seconds (以秒为单位)
	 */
	public String setex(final String key, final String value, final int seconds) {
		return execute(new JedisAction<String>() {
			@Override
			public String action(Jedis jedis) {
				return jedis.setex(key, seconds, value);
			}
		});
	}


	/**
	 * 将值 value 关联到 key ，并将 key 的生存时间设为 seconds (以秒为单位)
	 */
	public String setex(final byte[] key, final byte[] value, final int seconds) {
		return execute(new JedisAction<String>() {
			@Override
			public String action(Jedis jedis) {
				return jedis.setex(key, seconds, value);
			}
		});
	}

	/**
	 * 将 key 的值设为 value ，当且仅当 key 不存在。 若给定的 key 已经存在，则 SETNX 不做任何动作。
	 * 只有设置成功（key不存在的情况），才会返回true
	 */
	public Boolean setnx(final String key, final String value) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.setnx(key, value) == 1 ? true : false;
			}
		});
	}

	/**
	 * 将key的值设置为value，当且仅当 key 不存在。 若给定的 key 已经存在，则 SETNX 不做任何动作。若存在 将 key 的生存时间设为 seconds (以秒为单位)
	 * 该方法相当于sexex与setnx的组合命令方法
	 * {#setex(String, String, int) SETEX} + { #sexnx(String, String) SETNX}.
	 */
	public Boolean setnxex(final String key, final String value, final int seconds) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				String result = jedis.set(key, value, "NX", "EX", seconds);
				return JedisUtils.isStatusOk(result);
			}
		});
	}

	/**
	 * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
	 */
	public String getSet(final String key, final String value) {
		return execute(new JedisAction<String>() {
			@Override
			public String action(Jedis jedis) {
				return jedis.getSet(key, value);
			}
		});
	}

	/**
	 * 查找所有符合给定模式 pattern 的 key 。
	 *
	 * @param key
	 * @return
	 */
	public Set<String> keys(final String key) {
		return execute(new JedisAction<Set<String>>() {
			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.keys(key);
			}
		});
	}


	/**
	 * 将 key 中储存的数字值增一。
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
	 *
	 * @return的是返回自增操作以后的值。
	 */
	public Long incr(final String key) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.incr(key);
			}
		});
	}

	/**
	 * 将 key 所储存的值加上增量 increment 。（Long长整形类型增量）
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令
	 */
	public Long incrBy(final String key, final long increment) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.incrBy(key, increment);
			}
		});
	}


	/**
	 * 将 key 所储存的值加上 浮点型 增量 increment 。
	 * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令
	 */
	public Double incrByFloat(final String key, final double increment) {
		return execute(new JedisAction<Double>() {
			@Override
			public Double action(Jedis jedis) {
				return jedis.incrByFloat(key, increment);
			}
		});
	}

	/**
	 * 将 key 中储存的数字值减一。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。
	 *
	 * @return 返回的是递减1之后的结果
	 */
	public Long decr(final String key) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.decr(key);
			}
		});
	}


	/**
	 * 将 key 所储存的值减去减量 decrement 。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作
	 *
	 * @return 返回的是递减1之后的结果
	 */
	public Long decrBy(final String key, final long decrement) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.decrBy(key, decrement);
			}
		});
	}


	// ================== Hash（哈希表） 操作===============//

	/**
	 * 哈希get
	 */
	public String hget(final String key, final String fieldName) {
		return execute(new JedisAction<String>() {
			@Override
			public String action(Jedis jedis) {
				return jedis.hget(key, fieldName);
			}
		});
	}

	public byte[] hget(final byte[] key, final byte[] fieldName) {
		return execute(new JedisAction<byte[]>() {
			@Override
			public byte[] action(Jedis jedis) {
				return jedis.hget(key, fieldName);
			}
		});
	}


	/**
	 * 返回哈希表 key 中，一个或多个给定域的值。如果给定的域不存在于哈希表，那么返回一个 nil 值。
	 *
	 * @param key
	 * @param fieldsNames
	 * @return
	 */
	public List<String> hmget(final String key, final String... fieldsNames) {
		return execute(new JedisAction<List<String>>() {
			@Override
			public List<String> action(Jedis jedis) {
				return jedis.hmget(key, fieldsNames);
			}
		});
	}

	/**
	 * 返回哈希表 key 中，所有的域和值。
	 *
	 * @param key
	 * @return
	 */
	public Map<String, String> hgetAll(final String key) {
		return execute(new JedisAction<Map<String, String>>() {
			@Override
			public Map<String, String> action(Jedis jedis) {
				return jedis.hgetAll(key);
			}
		});
	}

	public void hset(final String key, final String fieldName, final String value) {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.hset(key, fieldName, value);
			}
		});
	}
	public void hset(final byte[] key, final byte[] fieldName, final byte[] value) {
		execute(new JedisActionNoResult() {

			@Override
			public void action(Jedis jedis) {
				jedis.hset(key, fieldName, value);
			}
		});
	}

	/**
	 * 同时将多个 field-value (域-值)对设置到哈希表 key 中。
	 *
	 * @param key
	 * @param map
	 * @return
	 */
	public String hmset(final String key, final Map<String, String> map) {
		return execute(new JedisAction<String>() {
			@Override
			public String action(Jedis jedis) {
				return jedis.hmset(key, map);
			}
		});
	}

	public Boolean hsetnx(final String key, final String fieldName, final String value) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.hsetnx(key, fieldName, value) == 1 ? true : false;
			}
		});
	}

	public Long hincrBy(final String key, final String fieldName, final long increment) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.hincrBy(key, fieldName, increment);
			}
		});
	}

	public Double hincrByFloat(final String key, final String fieldName, final double increment) {
		return execute(new JedisAction<Double>() {
			@Override
			public Double action(Jedis jedis) {
				return jedis.hincrByFloat(key, fieldName, increment);
			}
		});
	}

	/**
	 * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
	 *
	 * @param key
	 * @param fieldsNames
	 * @return
	 */
	public Long hdel(final String key, final String... fieldsNames) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.hdel(key, fieldsNames);
			}
		});
	}
	/**
	 * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
	 *
	 * @param key
	 * @param fieldsNames
	 * @return
	 */
	public Long hdel(final byte[] key, final byte[] fieldsNames) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.hdel(key, fieldsNames);
			}
		});
	}

	public Boolean hexists(final String key, final String fieldName) {
		return execute(new JedisAction<Boolean>() {
			@Override
			public Boolean action(Jedis jedis) {
				return jedis.hexists(key, fieldName);
			}
		});
	}

	public Set<String> hkeys(final String key) {
		return execute(new JedisAction<Set<String>>() {
			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.hkeys(key);
			}
		});
	}
	public Set<byte[]> hkeys(final byte[] key) {
		return execute(new JedisAction<Set<byte[]>>() {
			@Override
			public Set<byte[]> action(Jedis jedis) {
				return jedis.hkeys(key);
			}
		});
	}

	public List<byte[]> hvals(final byte[] key) {
		return execute(new JedisAction<List<byte[]>>() {
			@Override
			public List<byte[]> action(Jedis jedis) {
				return jedis.hvals(key);
			}
		});
	}

	public List<String> hvals(final String key) {
		return execute(new JedisAction<List<String>>() {
			@Override
			public List<String> action(Jedis jedis) {
				return jedis.hvals(key);
			}
		});
	}

	/**
	 * 返回哈希表 key 中域的数量。
	 *
	 * @param key
	 * @return
	 */
	public Long hlen(final String key) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.hlen(key);
			}
		});
	}
	public Long hlen(final byte[] key) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.hlen(key);
			}
		});
	}


	//====================List（列表）操作=========================//

	/**
	 * 将一个或多个值 value 插入到列表 key 的表头(最左边)
	 *
	 * @param key
	 * @param values
	 * @return
	 */
	public Long lpush(final String key, final String... values) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.lpush(key, values);
			}
		});
	}

	/**
	 * 将一个或多个值 value 插入到列表 key 的表头(最左边)
	 *
	 * @param key
	 * @param values
	 * @return
	 */
	public Long lpush(final byte[] key, final byte[] values) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.lpush(key, values);
			}
		});
	}


	/**
	 * 将一个或多个值 value 插入到列表 key 的表尾(最右边)
	 *
	 * @param key
	 * @param values
	 * @return
	 */
	public Long rpush(final String key, final String... values) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.rpush(key, values);
			}
		});
	}

	/**
	 * 将一个或多个值 value 插入到列表 key 的表尾(最右边)
	 *
	 * @param key
	 * @param values
	 * @return
	 */
	public Long rpush(final byte[] key, final byte[] values) {
		return execute(new JedisAction<Long>() {
			@Override
			public Long action(Jedis jedis) {
				return jedis.rpush(key, values);
			}
		});
	}

	/**
	 * 移除并返回列表 key 的尾元素
	 *
	 * @param key
	 * @return
	 */
	public String rpop(final String key) {
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				return jedis.rpop(key);
			}
		});
	}

	/**
	 * 移除并返回列表 key 的尾元素
	 *
	 * @param key
	 * @return
	 */
	public byte[] rpop(final byte[] key) {
		return execute(new JedisAction<byte[]>() {

			@Override
			public byte[] action(Jedis jedis) {
				return jedis.rpop(key);
			}
		});
	}


	/**
	 * 移除并返回列表 key 的头元素
	 *
	 * @param key
	 * @return
	 */
	public String lpop(final String key) {
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				return jedis.lpop(key);
			}
		});
	}

	/**
	 * 移除并返回列表 key 的头元素
	 *
	 * @param key
	 * @return
	 */
	public byte[] lpop(final byte[] key) {
		return execute(new JedisAction<byte[]>() {

			@Override
			public byte[] action(Jedis jedis) {
				return jedis.lpop(key);
			}
		});
	}


	/**
	 * 它是 RPOP 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被 BRPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
	 *
	 * @param key
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public String brpop(final String key) {
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				List<String> nameValuePair = jedis.brpop(key);
				if (nameValuePair != null) {
					return nameValuePair.get(1);
				} else {
					return null;
				}
			}
		});
	}

	/**
	 * 它是 RPOP 命令的阻塞版本，当给定列表内没有任何元素可供弹出的时候，连接将被 BRPOP 命令阻塞，直到等待超时或发现可弹出元素为止。
	 * 假如在指定时间内没有任何元素被弹出,则返回一个 nil
	 *
	 * @param key
	 * @return
	 */
	public String brpop(final int timeout, final String key) {
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				List<String> nameValuePair = jedis.brpop(timeout, key);
				if (nameValuePair != null) {
					return nameValuePair.get(1);
				} else {
					return null;
				}
			}
		});
	}

	/**
	 * Not support for sharding.
	 */
	public String rpoplpush(final String sourceKey, final String destinationKey) {
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				return jedis.rpoplpush(sourceKey, destinationKey);
			}
		});
	}

	/**
	 * Not support for sharding.
	 */
	public String brpoplpush(final String source, final String destination, final int timeout) {
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				return jedis.brpoplpush(source, destination, timeout);
			}
		});
	}

	/**
	 * 返回列表 key 的长度。
	 * 如果 key 不存在，则 key 被解释为一个空列表，返回 0 .
	 * 如果 key 不是列表类型，返回一个错误。
	 *
	 * @param key
	 * @return
	 */
	public Long llen(final String key) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.llen(key);
			}
		});
	}

	public String lindex(final String key, final long index) {
		return execute(new JedisAction<String>() {

			@Override
			public String action(Jedis jedis) {
				return jedis.lindex(key, index);
			}
		});
	}

	public List<String> lrange(final String key, final int start, final int end) {
		return execute(new JedisAction<List<String>>() {

			@Override
			public List<String> action(Jedis jedis) {
				return jedis.lrange(key, start, end);
			}
		});
	}

	/**
	 * redis消息队列 pop方法
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public List<byte[]> lrange(final byte[] key, final int start, final int end) {
		return execute(new JedisAction<List<byte[]>>() {

			@Override
			public List<byte[]> action(Jedis jedis) {
				return jedis.lrange(key, start, end);
			}
		});
	}

	/**
	 * redis消息队列 pop方法
	 *
	 * @param key
	 * @return
	 */
	public List<byte[]> lrange(final byte[] key) {
		return execute(new JedisAction<List<byte[]>>() {

			@Override
			public List<byte[]> action(Jedis jedis) {
				return jedis.lrange(key, 0, -1);
			}
		});
	}


	public void ltrim(final String key, final int start, final int end) {
		execute(new JedisActionNoResult() {
			@Override
			public void action(Jedis jedis) {
				jedis.ltrim(key, start, end);
			}
		});
	}

	public void ltrimFromLeft(final String key, final int size) {
		execute(new JedisActionNoResult() {
			@Override
			public void action(Jedis jedis) {
				jedis.ltrim(key, 0, size - 1);
			}
		});
	}

	public Boolean lremFirst(final String key, final String value) {
		return execute(new JedisAction<Boolean>() {
			@Override
			public Boolean action(Jedis jedis) {
				Long count = jedis.lrem(key, 1, value);
				return (count == 1);
			}
		});
	}

	public Boolean lremAll(final String key, final String value) {
		return execute(new JedisAction<Boolean>() {
			@Override
			public Boolean action(Jedis jedis) {
				Long count = jedis.lrem(key, 0, value);
				return (count > 0);
			}
		});
	}


	// =============================Set（集合）操作========================== //

	/**
	 * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public Boolean sadd(final String key, final String member) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.sadd(key, member) == 1 ? true : false;
			}
		});
	}

	/**
	 * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public Boolean sadd(final String key, final String[] member) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.sadd(key, member) == 1 ? true : false;
			}
		});
	}

	/**
	 * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
	 *
	 * @param key
	 * @param member
	 * @return
	 */
	public Boolean srem(final String key, final String[] member) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.srem(key, member) == 1 ? true : false;
			}
		});
	}

	/**
	 * 返回一个集合的全部成员，该集合是所有给定集合的并集。不存在的 key 被视为空集
	 *
	 * @param key
	 * @param member
	 * @return Set<String>
	 */
	public Set<String> sunion(final String[] keys) {
		return execute(new JedisAction<Set<String>>() {

			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.sunion(keys);
			}
		});
	}

	/**
	 * 返回一个集合的全部成员，该集合是所有给定集合的交集,不存在的 key 被视为空集
	 *
	 * @param key
	 * @param member
	 * @return Set<String>
	 */
	public Set<String> sinter(final String[] keys) {
		return execute(new JedisAction<Set<String>>() {
			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.sinter(keys);
			}
		});
	}

	/**
	 * 返回集合 key 中的所有成员。
	 * 不存在的 key 被视为空集合。
	 *
	 * @param key
	 * @return
	 */
	public Set<String> smembers(final String key) {
		return execute(new JedisAction<Set<String>>() {

			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.smembers(key);
			}
		});
	}

	/**
	 * 返回集合 key 的基数(集合中元素的数量)
	 *
	 * @param key
	 * @return
	 */
	public Long scard(final String key) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.scard(key);
			}
		});
	}


	//======================SortedSet（有序集合）操作==================================//

	/**
	 * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中
	 * (只有新元素添加返回true,update元素的时候反悔false)
	 *
	 * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
	 */
	public Boolean zadd(final String key, final double score, final String member) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.zadd(key, score, member) == 1 ? true : false;
			}
		});
	}

	public Double zscore(final String key, final String member) {
		return execute(new JedisAction<Double>() {

			@Override
			public Double action(Jedis jedis) {
				return jedis.zscore(key, member);
			}
		});
	}

	public Long zrank(final String key, final String member) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.zrank(key, member);
			}
		});
	}

	public Long zrevrank(final String key, final String member) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.zrevrank(key, member);
			}
		});
	}

	public Long zcount(final String key, final double min, final double max) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.zcount(key, min, max);
			}
		});
	}

	public Set<String> zrange(final String key, final int start, final int end) {
		return execute(new JedisAction<Set<String>>() {

			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.zrange(key, start, end);
			}
		});
	}

	public Set<Tuple> zrangeWithScores(final String key, final int start, final int end) {
		return execute(new JedisAction<Set<Tuple>>() {

			@Override
			public Set<Tuple> action(Jedis jedis) {
				return jedis.zrangeWithScores(key, start, end);
			}
		});
	}

	public Set<String> zrevrange(final String key, final int start, final int end) {
		return execute(new JedisAction<Set<String>>() {

			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.zrevrange(key, start, end);
			}
		});
	}

	public Set<Tuple> zrevrangeWithScores(final String key, final int start, final int end) {
		return execute(new JedisAction<Set<Tuple>>() {

			@Override
			public Set<Tuple> action(Jedis jedis) {
				return jedis.zrevrangeWithScores(key, start, end);
			}
		});
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。
	 *
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<String> zrangeByScore(final String key, final double min, final double max) {
		return execute(new JedisAction<Set<String>>() {

			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.zrangeByScore(key, min, max);
			}
		});
	}

	public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max) {
		return execute(new JedisAction<Set<Tuple>>() {

			@Override
			public Set<Tuple> action(Jedis jedis) {
				return jedis.zrangeByScoreWithScores(key, min, max);
			}
		});
	}

	public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
		return execute(new JedisAction<Set<String>>() {

			@Override
			public Set<String> action(Jedis jedis) {
				return jedis.zrevrangeByScore(key, max, min);
			}
		});
	}

	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min) {
		return execute(new JedisAction<Set<Tuple>>() {

			@Override
			public Set<Tuple> action(Jedis jedis) {
				return jedis.zrevrangeByScoreWithScores(key, max, min);
			}
		});
	}

	public Boolean zrem(final String key, final String member) {
		return execute(new JedisAction<Boolean>() {

			@Override
			public Boolean action(Jedis jedis) {
				return jedis.zrem(key, member) == 1 ? true : false;
			}
		});
	}

	public Long zremByScore(final String key, final double start, final double end) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.zremrangeByScore(key, start, end);
			}
		});
	}

	public Long zremByRank(final String key, final long start, final long end) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.zremrangeByRank(key, start, end);
			}
		});
	}

	public Long zcard(final String key) {
		return execute(new JedisAction<Long>() {

			@Override
			public Long action(Jedis jedis) {
				return jedis.zcard(key);
			}
		});
	}
}
