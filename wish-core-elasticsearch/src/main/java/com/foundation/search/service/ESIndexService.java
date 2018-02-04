package com.foundation.search.service;

import java.util.List;
import java.util.Map;

/**
 * Description:索引服务接口
 * Created by fqh on 2016/10/17.
 */
public interface ESIndexService {

    /**
     * 索引(对指定的json数据添加索引)
     *
     * @param json 索引json对象
     * @param type 索引类型
     * @param id   索引Id
     * @return
     */
    String index(String type, String json, String id);

    /**
     * 删除指定的索引
     *
     * @param type 索引类型
     * @param id   索引id
     * @return
     */
    String remove(String type, String id);

    /**
     * 根据指定的数据模板搜索查询
     *
     * @param params   索引参数
     * @param template 索引json数据模板
     * @param types    索引的类名
     * @return
     */
    List<Map<String, Object>> query(String template, Map<String, Object> params, String... types);

    /**
     * 根据指定的模板搜索查询
     *
     * @param template 索引json数据模板
     * @param types    索引的类名
     * @return
     */
    List<Map<String, Object>> query(String template, String[] types);
}
