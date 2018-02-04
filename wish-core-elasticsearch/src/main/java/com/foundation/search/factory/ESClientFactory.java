package com.foundation.search.factory;

import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by fqh
 * create on 2016/10/31.
 */
public class ESClientFactory implements FactoryBean<Client> {
    private static final Logger logger = LoggerFactory.getLogger(ESClientFactory.class);
    /**
     * 集群名
     */
    private String clusterName;
    /**
     * 集群节点服务器ip
     */
    private TransportAddress[] address;

    public ESClientFactory() {
    }

    @Override
    public Client getObject() throws Exception {
        Settings settings= Settings.settingsBuilder()
                .put("cluster.name", clusterName)//集群名称
                .put("cluster.transport.sniff", true)//自动添加集群
                .build();
        TransportClient transportClient =TransportClient.builder().settings(settings).build();
        /*Settings settings = ImmutableSettings.builder().put("cluster.name", clusterName).build();
        TransportClient transportClient = new TransportClient(settings);*/
        Client client = transportClient.addTransportAddresses(address);
        return client;
    }

    @Override
    public Class<?> getObjectType() {
        return Client.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public void setAddress(List<String> address) throws UnknownHostException {
        if (CollectionUtils.isEmpty(address)) {
            logger.error("transport address is not null !");
            this.address = new TransportAddress[]{};
        } else {
            this.address = new TransportAddress[address.size()];
            int i = 0;
            for (String addr : address) {
                String[] addrParts = addr.split(":");
                InetAddress inetAddress=InetAddress.getByName(addrParts[0]);
                this.address[i++] = new InetSocketTransportAddress(inetAddress, NumberUtils.toInt(addrParts[1]));
            }
        }
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
