package com.foundation.mongo.dao;

import com.foundation.common.persistence.Page;

import java.util.List;
import java.util.Map;

/**
 * <P>
 * mongo操作基础dao
 * </P>
 * <P>
 * File name : MongoBaseDao.java
 * </P>
 * <P>
 * Author : chengchen
 * </P>
 * <P>
 * Date : 2016年8月23日
 * </P>
 */
public interface IMongoBaseDao {
	/**
	 * 根据id获取实体数据<BR>
	 * MongoBaseDao.queryById()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param id
	 *            实体id
	 * @param entityClass
	 *            实体class
	 * @return
	 */
	public <T> T queryById(Object id, Class<T> entityClass);

	/**
	 * 根据参数获取一个实体数据<BR>
	 * MongoBaseDao.queryOne()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param params
	 *            查询参数map，key要和db中对应
	 * @param entityClass
	 *            实体class
	 * @return
	 */
	public <T> T queryOne(Map<String, Object> params, Class<T> entityClass);

	/**
	 * 根据参数获取记录数量<BR>
	 * MongoBaseDao.queryCount()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param params
	 *            查询参数map，key要和db中对应
	 * @param entityClass
	 *            实体class
	 * @return
	 */
	public <T> long queryCount(Map<String, Object> params, Class<T> entityClass);

	/**
	 * 获取所有实体数据<BR>
	 * MongoBaseDao.queryAll()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param entityClass
	 *            实体class
	 * @return
	 */
	public <T> List<T> queryAll(Class<T> entityClass);

	/**
	 * 根据参数获取实体数据列表<BR>
	 * MongoBaseDao.queryList()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param params
	 *            查询参数map，key要和db中对应
	 * @param entityClass
	 *            实体class
	 * @return
	 */
	public <T> List<T> queryList(Map<String, Object> params, Class<T> entityClass);

	/**
	 * 根据参数获取实体分页数据列表<BR>
	 * MongoBaseDao.queryPage()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param currentPage
	 *            当前页
	 * @param pageSize
	 *            每页多少条
	 * @param params
	 *            查询参数map，key要和db中对应
	 * @param entityClass
	 *            实体class
	 * @return
	 */
	public <T> Page<T> queryPage(int currentPage, int pageSize, Map<String, Object> params, Class<T> entityClass);

	/**
	 * 保存实体数据<BR>
	 * MongoBaseDao.save()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param entity
	 *            实体class
	 */
	public <T> void save(T entity);

	/**
	 * 根据参数更新一条实体数据<BR>
	 * MongoBaseDao.update()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param whereParams
	 *            查询参数map，key要和db中对应
	 * @param updateParams
	 *            更新参数map，key要和db中对应
	 * @param entityClass
	 *            实体class
	 */
	public <T> void update(Map<String, Object> whereParams, Map<String, Object> updateParams, Class<T> entityClass);

	/**
	 * 根据id更新一条实体数据<BR>
	 * MongoBaseDao.update()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param id
	 *            实体id
	 * @param updateParams
	 *            更新参数map，key要和db中对应
	 * @param entityClass
	 *            实体class
	 */
	public <T> void update(Object id, Map<String, Object> updateParams, Class<T> entityClass);

	/**
	 * 根据参数更新所有符合条件的实体数据<BR>
	 * MongoBaseDao.batchUpdate()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param whereParams
	 *            查询参数map，key要和db中对应
	 * @param updateParams
	 *            更新参数map，key要和db中对应
	 * @param entityClass
	 *            实体class
	 */
	public <T> void batchUpdate(Map<String, Object> whereParams, Map<String, Object> updateParams,
								Class<T> entityClass);

	/**
	 * 根据id删除实体数据（物理删除），慎用！<BR>
	 * MongoBaseDao.deleteReally()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param id
	 *            实体id
	 * @param entityClass
	 *            实体class
	 */
	public <T> void deleteReally(Object id, Class<T> entityClass);

	/**
	 * 根据参数删除实体数据（物理删除），慎用！<BR>
	 * MongoBaseDao.deleteReally()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param params
	 *            查询参数map，key要和db中对应
	 * @param entityClass
	 *            实体class
	 */
	public <T> void deleteReally(Map<String, Object> params, Class<T> entityClass);

	/**
	 * 根据id删除实体数据（逻辑删除）<BR>
	 * MongoBaseDao.deleteLogical()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param id
	 *            实体id
	 * @param entityClass
	 *            实体class
	 */
	public <T> void deleteLogical(Object id, Class<T> entityClass);

	/**
	 * 根据参数删除实体数据（逻辑删除）<BR>
	 * MongoBaseDao.deleteLogical()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param params
	 *            查询参数map，key要和db中对应
	 * @param entityClass
	 *            实体class
	 */
	public <T> void deleteLogical(Map<String, Object> params, Class<T> entityClass);


	/**
	 * @Description: 更新插入实体数据
	 * @param whereParams 更新条件
	 * @param updateParams
	 * @param entityClass
	 * @param <T>
	 */
	public <T> void updateInsert(Map<String, Object> whereParams, Map<String, Object> updateParams, Class<T> entityClass);


	/**
	 * 根据参数获取实体数据列表<BR>
	 * MongoBaseDao.queryFieldsList()<BR>
	 *     此方法可以对深层次字段，进行查询 <br>
	 *     比如 查询mongodb 中 某集合中 属性item下 的属性indicator 的属性subId <br>
	 *     将 "item.indicator.subId" 放入fields中即可 <br>
	 *         返回值 可以是 需要的实体，也可以是 嵌套的Map
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 *
	 * @param params
	 *            查询参数map，key要和db中对应
	 * @param fields 要获取的字段
	 * @param entityClass
	 * @param colName
	 *            实体class
	 * @return
	 */
	public <T> List<T> queryFieldsList(Map<String, Object> params, List<String> fields, Class<T> entityClass, String colName);

	/**
	 * 根据参数获取实体数据列表<BR>
	 * MongoBaseDao.queryFieldsOne()<BR>
	 *     此方法可以对深层次字段，进行查询 <br>
	 *     比如 查询mongodb 中 某集合中 属性item下 的属性indicator 的属性subId <br>
	 *     将 "item.indicator.subId" 放入fields中即可 <br>
	 *         返回值 可以是 需要的实体，也可以是 嵌套的Map
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 *
	 * @param params
	 *            查询参数map，key要和db中对应
	 * @param fields 要获取的字段
	 * @param entityClass
	 * @param colName
	 *            实体class
	 * @return
	 */
	public <T> T queryFieldsOne(Map<String, Object> params, List<String> fields, Class<T> entityClass, String colName);
}
