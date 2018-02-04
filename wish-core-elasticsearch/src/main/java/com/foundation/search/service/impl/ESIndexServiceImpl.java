package com.foundation.search.service.impl;

import com.foundation.search.service.ESIndexService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TemplateQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:索引服务
 * <p/>
 * Created by fqh on 2016/10/07.
 */
@Service(value = "eSIndexService")
public class ESIndexServiceImpl implements ESIndexService {
    private static final Logger logger = LoggerFactory.getLogger(ESIndexServiceImpl.class);
    private static final String INDEX_NAME = "elasticsearch";
    @Autowired
    @Qualifier("eSClientFactory")
    private Client client;


    /**
     * 关闭elasticsearch client对象
     */
    @PreDestroy
    protected void destroy() {
        try {
            client.close();
        } catch (Exception e) {
            logger.warn("failed to close es client", e);
        }
    }

    /**
     * 索引(对指定的json数据添加索引)
     *
     * @param json 索引json对象
     * @param type 索引类型
     * @return
     */
    @Override
    public String index(String type, String json, String id) {
        logger.debug("indexing {}:{}", type, json);
        if (StringUtils.isBlank(id)) {
            return null;
        }
        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(INDEX_NAME, type);
        indexRequestBuilder.setId(id);
        indexRequestBuilder.setSource(json);
        IndexResponse response = indexRequestBuilder.execute().actionGet();
        logger.debug("indexed {}:{},index:{}", type, json, response.getIndex());
        return response.getIndex();
    }

    /**
     * 删除指定的索引
     *
     * @param type 索引类型
     * @param id   索引id
     * @return
     */
    @Override
    public String remove(String type, String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        logger.debug("to delete indexing {}:{}", type, id);
        DeleteResponse response = client.prepareDelete(INDEX_NAME, type, id).execute().actionGet();
        return response.getIndex();
    }

    /**
     * @param params   索引参数
     * @param template 索引json数据模板
     * @param types    索引的类名
     * @return
     */
    @Override
    public List<Map<String, Object>> query(String template, Map<String, Object> params, String... types) {
        QueryBuilder queryBuilder = new TemplateQueryBuilder(template, params);
        SearchRequestBuilder searchRequest = client.prepareSearch(INDEX_NAME);
        searchRequest.setTypes(types);
        searchRequest.setSearchType(SearchType.QUERY_AND_FETCH);
        searchRequest.setQuery(queryBuilder);
        //执行查询
        SearchResponse searchResponse = searchRequest.execute().actionGet();
        logger.debug("search request:{}", searchRequest);
        List<Map<String, Object>> list = new ArrayList<>();
        //获取搜索命中的结果
        if (searchResponse != null) {
            for (SearchHit searchHit : searchResponse.getHits()) {
                list.add(searchHit.sourceAsMap());
            }
        }
        return list;
    }

    /**
     * @param template 索引json数据模板
     * @param types    索引的类名
     * @return
     */
    @Override
    public List<Map<String, Object>> query(String template, String[] types) {
        SearchRequestBuilder searchRequest = client.prepareSearch(INDEX_NAME);
        searchRequest.setTypes(types);
        searchRequest.setSearchType(SearchType.QUERY_AND_FETCH);
        searchRequest.setQuery(template);
        logger.debug("search request:{}", searchRequest);
        SearchResponse searchResponse = searchRequest.execute().actionGet();
        List<Map<String, Object>> list = new ArrayList<>();
        if (searchResponse != null) {
            for (SearchHit searchHit : searchResponse.getHits()) {
                list.add(searchHit.sourceAsMap());
            }
        }
        return list;
    }
}
