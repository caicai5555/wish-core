package com.foundation.mongo.constant;

/**
 * <P>
 * 用于引用系统中数据库字段的名称，使其保持一致
 * </P>
 * <P>
 * File name : DBConstants.java
 * </P>
 * <P>
 * Author : chengchen
 * </P>
 * <P>
 * Date : 2016年8月9日
 * </P>
 */
public interface DBConstants {
	/** 公共模块 */
	interface Common {

		/** 主键ID */
		String ID = "id";
		/**删除标记，0表示正常，1表示删除*/
		String DEL_FLAG = "delFlag";
		/**删除时间*/
		String DEL_DATE = "delDate";

	}
}
