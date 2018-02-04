package com.foundation.mongo.dao.impl;

import com.foundation.common.persistence.Page;
import com.foundation.common.utils.StringUtils;
import com.foundation.mongo.constant.DBConstants;
import com.foundation.mongo.dao.IMongoBaseDao;
import com.foundation.mongo.template.AbstractBaseMongoTemplete;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
@Component("mongoBaseDao")
public class MongoBaseDaoImpl extends AbstractBaseMongoTemplete implements IMongoBaseDao {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T queryById(Object id, Class<T> entityClass) {
		return mongoTemplate.findById(id, entityClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T queryOne(Map<String, Object> params, Class<T> entityClass) {
		Assert.notNull(params);
		Query query = map2Query(params);
		return mongoTemplate.findOne(query, entityClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> long queryCount(Map<String, Object> params, Class<T> entityClass) {
		Assert.notNull(params);
		Query query = map2Query(params);
		return mongoTemplate.count(query, entityClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> List<T> queryAll(Class<T> entityClass) {
		return mongoTemplate.findAll(entityClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> List<T> queryList(Map<String, Object> params, Class<T> entityClass) {
		Assert.notNull(params);
		Query query = map2Query(params);
		return mongoTemplate.find(query, entityClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Page<T> queryPage(int currentPage, int pageSize, Map<String, Object> params,
			Class<T> entityClass) {
		Assert.notNull(params);
		Query query = map2Query(params);
		long totalCount = this.queryCount(params, entityClass);
		Page<T> page = new Page<T>(currentPage, pageSize, totalCount);
		query.skip(page.getFirstResult());
		query.limit(pageSize);
		List<T> list = mongoTemplate.find(query, entityClass);
		page.setList(list);
		return page;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void save(T entity) {
		mongoTemplate.save(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void update(Map<String, Object> whereParams, Map<String, Object> updateParams, Class<T> entityClass) {
		Assert.notNull(whereParams);
		Assert.notNull(updateParams);
		Query query = map2Query(whereParams);
		Update update = map2Update(updateParams);
		mongoTemplate.updateFirst(query, update, entityClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void update(Object id, Map<String, Object> updateParams, Class<T> entityClass) {
		Assert.notNull(updateParams);
		Query query = id2Query(id);
		Update update = map2Update(updateParams);
		mongoTemplate.updateFirst(query, update, entityClass);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void batchUpdate(Map<String, Object> whereParams, Map<String, Object> updateParams,
			Class<T> entityClass) {
		Assert.notNull(whereParams);
		Assert.notNull(updateParams);
		Query query = map2Query(whereParams);
		Update update = map2Update(updateParams);
		mongoTemplate.updateMulti(query, update, entityClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void deleteReally(Object id, Class<T> entityClass) {
		Query query = id2Query(id);
		mongoTemplate.remove(query, entityClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void deleteReally(Map<String, Object> params, Class<T> entityClass) {
		Query query = map2Query(params);
		mongoTemplate.remove(query, entityClass);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void deleteLogical(Object id, Class<T> entityClass) {
		Query query = id2Query(id);
		Update update = map2Update(updateDeleteMap());
		mongoTemplate.updateMulti(query, update, entityClass);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void deleteLogical(Map<String, Object> params, Class<T> entityClass) {
		Query query = map2Query(params);
		Update update = map2Update(updateDeleteMap());
		mongoTemplate.updateMulti(query, update, entityClass);

	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> void updateInsert(Map<String, Object> whereParams, Map<String, Object> updateParams, Class<T> entityClass) {
		Query query = map2Query(whereParams);
		Update update = map2Update(updateParams);
		mongoTemplate.upsert(query,update,entityClass);
	}

	@Override
	public <T> List<T> queryFieldsList(Map<String, Object> params, List<String> fields, Class<T> entityClass, String colName) {
		Assert.notNull(params);
		Assert.notNull(fields);
		BasicDBObject queryObject = new BasicDBObject(params);
		HashMap<Object, Object> fieldsMap = Maps.newHashMap();
		for (String field : fields) {
			fieldsMap.put(field, 1);
		}
		BasicDBObject fieldsObject = new BasicDBObject(fieldsMap);
		Query query = new BasicQuery(queryObject, fieldsObject);
		if (StringUtils.isEmpty(colName)) {
			return mongoTemplate.find(query, entityClass);
		}
		return mongoTemplate.find(query, entityClass, colName);
	}

	@Override
	public <T> T queryFieldsOne(Map<String, Object> params, List<String> fields, Class<T> entityClass, String colName) {
		Assert.notNull(params);
		Assert.notNull(fields);
		BasicDBObject queryObject = new BasicDBObject(params);
		HashMap<Object, Object> fieldsMap = Maps.newHashMap();
		for (String field : fields) {
			fieldsMap.put(field, 1);
		}
		BasicDBObject fieldsObject = new BasicDBObject(fieldsMap);
		Query query = new BasicQuery(queryObject, fieldsObject);
		if (StringUtils.isEmpty(colName)) {
			return mongoTemplate.findOne(query, entityClass);
		}
		return mongoTemplate.findOne(query, entityClass, colName);
	}

	/**
	 * map转换为Query<BR>
	 * MongoBaseDaoImpl.map2Query()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param params
	 * @return
	 */
	private Query map2Query(Map<String, Object> params) {
		Query query = new Query();
		for (Entry<String, Object> entry : params.entrySet()) {
			Criteria criteria = new Criteria(entry.getKey());
			criteria.is(entry.getValue());
			query.addCriteria(criteria);
		}
		return query;
	}

	/**
	 * id转换为Query<BR>
	 * MongoBaseDaoImpl.id2Query()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param id
	 * @return
	 */
	private Query id2Query(Object id) {
		Query query = new Query();
		Criteria criteria = new Criteria(DBConstants.Common.ID);
		criteria.is(id);
		query.addCriteria(criteria);
		return query;
	}

	/**
	 * map转换为Update<BR>
	 * MongoBaseDaoImpl.map2Update()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @param params
	 * @return
	 */
	private Update map2Update(Map<String, Object> params) {
		Update update = new Update();
		for (Entry<String, Object> entry : params.entrySet()) {
			update.set(entry.getKey(), entry.getValue());
		}
		return update;
	}

	/**
	 * 创建逻辑删除时使用的更新map<BR>
	 * MongoBaseDaoImpl.updateDeleteMap()<BR>
	 * <P>
	 * Author : chengchen
	 * </P>
	 * <P>
	 * Date : 2016年8月23日
	 * </P>
	 * 
	 * @return
	 */
	private Map<String, Object> updateDeleteMap() {
		Map<String, Object> map = new HashMap<>();
		map.put(DBConstants.Common.DEL_FLAG, 1);
		map.put(DBConstants.Common.DEL_DATE, new Date());
		return map;
	}

}
