package com.foundation.cache.redis.pool;

import redis.clients.jedis.Protocol;

/**
 * redis 连接信息
 *  Created by fqh on 2015/12/13
 */
public class ConnectionInfo {

	public static final String DEFAULT_PASSWORD = null;

	private int database = Protocol.DEFAULT_DATABASE;//采用默认的jredis，数据库0
	private String password = DEFAULT_PASSWORD;
	private int timeout = Protocol.DEFAULT_TIMEOUT;//jredis默认超时时间为2000毫秒

	public ConnectionInfo() {
	}

	public ConnectionInfo(int database, String password, int timeout) {
		this.timeout = timeout;
		this.password = password;
		this.database = database;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString() {
		return "ConnectionInfo [database=" + database + ", password=" + password + ", timeout=" + timeout + "]";
	}
}
