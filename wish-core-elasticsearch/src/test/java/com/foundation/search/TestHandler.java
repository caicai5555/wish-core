package com.foundation.search;

import Bean.AppVersion;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;

import java.util.List;

/**
 * Created by fanqinghui on 2016/8/10.
 */
public class TestHandler {

    @Test
    public void test(){
        ElasticSearchHandler<AppVersion> handler=new ElasticSearchHandler<AppVersion>();

        QueryBuilder queryBuilder= QueryBuilders.prefixQuery("type", "1");
        List<AppVersion> list=handler.searcher(queryBuilder, "wish", "appversion", AppVersion.class);
        for (AppVersion version:list){
            System.out.println(version);
        }
    }
}
