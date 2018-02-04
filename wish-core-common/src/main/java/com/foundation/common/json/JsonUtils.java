package com.foundation.common.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;
import java.util.Map;

/**
 * <p>Created by: fqh
 * <p>Date: 15-1-9 下午3:33
 * <p>Version: 1.0
 */
public class JsonUtils{


    /**
     * map转换成json String
     * @param map
     * @return
     */
    public static String formateMap(Map map) {
        String result=JSON.toJSONString(map,SerializerFeature.WriteMapNullValue);
        return result;
    }


    /**
     * 把json数据转换成类 T
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * 把json数据转换成 list<T> 类型
     * @param json
     * @param clazz
     * @return
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }


    /**
     * po类转换成json String
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        //String result = JSON.toJSONString(obj);
        String result=JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
        return result;
    }
}
