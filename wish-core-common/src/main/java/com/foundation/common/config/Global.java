package com.foundation.common.config;

import com.foundation.common.utils.PropertiesLoader;
import com.foundation.common.utils.StringUtils;
import com.google.common.collect.Maps;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 全局配置类
 * @author fqh
 * @version 2016-08-15
 */
public class Global {

	/**
	 * 当前对象实例
	 */
	private static Global global=new Global();

	/**
	 * 加载配置文件
	 */
	private static PropertiesLoader loader=new PropertiesLoader("config.properties");
	/**
	 * 保存全局属性值
	 */
	private static HashMap<String, String> map =new HashMap<String,String>(){{put("jdbc.type","mysql");}};
	

	/**
	 * 显示/隐藏
	 */
	public static final String SHOW = "1";
	public static final String HIDE = "0";

	/**
	 * 是/否
	 */
	public static final String YES = "1";
	public static final String NO = "0";
	
	/**
	 * 对/错
	 */
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	/**
	 * 获取当前对象实例
	 */
	public static Global getInstance() {
		return global;
	}
	
	/**
	 * 获取配置
	 * @see ${fns:getConfig('adminPath')}
	 */
	public static String getConfig(String key) {
		String value = map.get(key);
		if (value == null){
			try{
				value = loader.getProperty(key);
				map.put(key, value != null ? value : StringUtils.EMPTY);
			}catch (Exception e){
				Logger.getAnonymousLogger().info("config key不存在 key：" + key);
				//e.printStackTrace();
			}
		}
		return value;
	}

	/**
	 * 获取管理端根路径
	 */
	public static String getAdminPath() {
		return getConfig("adminPath");
	}
	
	/**
	 * 页面获取常量
	 * @see ${fns:getConst('YES')}
	 */
	public static Object getConst(String field) {
		try {
			return Global.class.getField(field).get(null);
		} catch (Exception e) {
			// 异常代表无配置，这里什么也不做
		}
		return null;
	}

    /**
     * 获取工程路径
     * @return
     */
    public static String getProjectPath(){
    	// 如果配置了工程路径，则直接返回，否则自动获取。
		String projectPath = Global.getConfig("projectPath");
		if (StringUtils.isNotBlank(projectPath)){
			return projectPath;
		}
		try {
			File file = new DefaultResourceLoader().getResource("").getFile();
			if (file != null){
				while(true){
					File f = new File(file.getPath() + File.separator + "src" + File.separator + "main");
					if (f == null || f.exists()){
						break;
					}
					if (file.getParentFile() != null){
						file = file.getParentFile();
					}else{
						break;
					}
				}
				projectPath = file.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return projectPath;
    }

	public static void main(String[] aurgs){
		System.out.println(getProjectPath());
	}

}
