package com.foundation.search;

import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by fanqinghui on 2016/8/9.
 * 建议采用TransportClient
 */
public class TestEs {

    @Test
    public void test1() throws Exception{
        //获取一个transportClient客户端
        TransportClient client=TransportClient.builder().build();
        InetAddress address=InetAddress.getByName("127.0.0.1");
        client.addTransportAddress(new InetSocketTransportAddress(address,9300));
        List<DiscoveryNode> nodeList=client.listedNodes();
        for(DiscoveryNode node:nodeList){
            System.out.println(node);
        }

    }

    @Test
    public void test2() throws UnknownHostException {
        Settings settings= Settings.settingsBuilder().put("cluster.transport.sniff",true).put("cluster.name","elasticsearch")
                .build();
        //获取一个transportClient客户端
        TransportClient client=TransportClient.builder().settings(settings).build();
        InetAddress address=InetAddress.getByName("127.0.0.1");
        client.addTransportAddress(new InetSocketTransportAddress(address,9300));
        InetAddress address99=InetAddress.getByName("192.168.88.129");
        client.addTransportAddress(new InetSocketTransportAddress(address99,9300));
        List<DiscoveryNode> nodeList=client.connectedNodes();
        for(DiscoveryNode node:nodeList){
            System.out.println(node);
        }

        /*IndexResponse response = client.prepareIndex("db", "table")
                .setSource("hello ela")
                .execute().actionGet();*/

        IndexRequestBuilder indexRequestBuilder = client.prepareIndex("dbtest", "typetest");
        indexRequestBuilder.setId("1");
        indexRequestBuilder.setSource("hello ela".getBytes());
        IndexResponse response = indexRequestBuilder.execute().actionGet();
        System.out.println(response);

        //transportClient.prepareGet()
       // GetResponse response=client.prepareGet("wish", "appversion", "100").execute().actionGet();
       // System.out.println(response);
    }

    @Test
    public void test3(){
        String cluseterName="elasticsearch";
        Node node= NodeBuilder.nodeBuilder().clusterName(cluseterName).client(true).build();
        Client client=node.client();
        GetRequestBuilder builder=client.prepareGet().setIndex("wish").setType("appversion").setId("100");
        System.out.println(builder.execute().actionGet());
    }
}
