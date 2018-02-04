package com.foundation.search;

import com.foundation.common.io.PropertiesUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by fanqinghui on 2016/8/9.
 */
public class ESPoolBuilder {

    private ESPoolBuilder builder=new ESPoolBuilder();
    static Logger logger=Logger.getAnonymousLogger();
    private static TransportClient client;

    static {
        try {
            String esUrl= PropertiesUtils.getValue("es.url");
            String esPool= PropertiesUtils.getValue("es.port");
            logger.info("esURL:"+esUrl);
            logger.info("esPort:"+esPool);
            if(StringUtils.isNotBlank(esUrl)){
                if(StringUtils.isBlank(esPool)){
                    esPool="9300";
                }
                //集群连接超时设置
                //Settings settings=Settings.settingsBuilder().put("client.transport.ping_timeout", "10s").build();
                try {

                    Settings settings= Settings.settingsBuilder()
                            .put("cluster.name", "my-application")//集群名称
                            .put("cluster.transport.sniff", true)//自动添加集群
                            .build();

                    client =TransportClient.builder().settings(settings).build();
                    for(String ipAddress:esUrl.split(",")) {
                        InetAddress address=InetAddress.getByName(ipAddress);
                        client.addTransportAddress(new InetSocketTransportAddress(address, Integer.valueOf(esPool)));
                    }

                    List<DiscoveryNode> nodeList=client.listedNodes();
                    for(DiscoveryNode node:nodeList){
                        logger.info("es节点列表:"+node);
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

            }else {
                throw new Exception("es 初始化失败,请检查配置文件es.url");
            }
        }catch (Exception e){
            throw new RuntimeException("请检查es配置文件");
        }
    }


    public static TransportClient getEsClient(){
        return client;
    }


}
