package com.foundation.mongo.template;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * mongo模板类，辅助dao操作
 * <P>File name : AbstractBaseMongoTemplete.java </P>
 * <P>Author : chengchen </P> 
 * <P>Date : 2016年8月22日 </P>
 */
public abstract class AbstractBaseMongoTemplete implements ApplicationContextAware {

	protected MongoTemplate mongoTemplate;

	/**
	 * @Description 根据配置文件设置mongoTemplate
	 * @param mongoTemplate
	 */
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		MongoTemplate mongoTemplate = applicationContext.getBean("mongoTemplate", MongoTemplate.class);
		setMongoTemplate(mongoTemplate);
	}
}