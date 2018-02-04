package com.foundation.cache.redis.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Pool;
/**
 * Jedis Pool base class.
 * 继承jedis pool，封装成基本的jedis基类
 * Created by fqh on 2016/3/13
 */
public abstract class JedisPool extends Pool<Jedis> {

	protected String poolName;

	protected HostAndPort address;

	protected ConnectionInfo connectionInfo;

	/**
	 * 重新设置redis的配置，jredisPool的maxPoolSize为8，太小了
	 * 另外把idle的检查时间由默认的30秒改为10分钟
	 * 把最大的idle设置为0，默认的是8
	 * 把idle时间设置60秒
	 * （idle是控线连接）
	 */
	public static JedisPoolConfig createPoolConfig(int maxPoolSize) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxPoolSize);
		config.setMaxIdle(maxPoolSize);//控制一个pool最多有多少个状态为idle(空闲的)的jedis实例
		config.setTimeBetweenEvictionRunsMillis(600 * 1000);//60秒
		return config;
	}

	/**
	 * 通过HostAndPort,ConnectionInfo，JedisPoolConfig初始化连接池信息
	 */
	protected void initInternalPool(/*String poolName,*/HostAndPort address, ConnectionInfo connectionInfo, JedisPoolConfig config) {
		//this.poolName = poolName;
		this.address = address;
		this.connectionInfo = connectionInfo;

		JedisFactory factory = new JedisFactory(address.getHost(), address.getPort(), connectionInfo.getTimeout(),
				connectionInfo.getPassword(), connectionInfo.getDatabase());

		internalPool = new GenericObjectPool(factory, config);
	}

	/**
	 * Return a broken jedis connection back to pool.
	 */
	@Override
	public void returnBrokenResource(final Jedis resource) {
		if (resource != null) {
			returnBrokenResourceObject(resource);
		}
	}

	/**
	 * Return a available jedis connection back to pool.
	 */
	@Override
	public void returnResource(final Jedis resource) {
		if (resource != null) {
			resource.resetState();
			returnResourceObject(resource);
		}
	}

	public HostAndPort getAddress() {
		return address;
	}

	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}
}