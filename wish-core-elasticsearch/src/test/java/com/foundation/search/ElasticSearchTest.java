package com.foundation.search;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * Description:
 * Author:fqh
 * Date:2016/9/20
 */
public class ElasticSearchTest {

    private Client client;
    private String host;

    /**
     * 默认
     */
    public ElasticSearchTest() {
        String cluseterName="my-application";
        Node node= NodeBuilder.nodeBuilder().clusterName(cluseterName).client(true).build();
        Client client=node.client();
    }



    /**
     * 创建索引
     *
     * @param indexName
     * @param indexType
     * @param jsonData
     */
    private void createIndex(String indexName, String indexType, String jsonData) {
        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, indexType);
        indexRequestBuilder.setRefresh(true);

        //TODO
    }

    public static void main(String[] args) {
        Node node = NodeBuilder.nodeBuilder().node();
        Client client = node.client();
        //TODO
        node.close();
    }

}
