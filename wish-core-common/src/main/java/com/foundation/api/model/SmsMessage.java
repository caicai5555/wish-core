package com.foundation.api.model;

import java.io.Serializable;

/**
 * Created by fanqinghui on 2016/9/12.
 * 短信发送soa Model
 */
public class SmsMessage implements Serializable{

    /**
     * 短信服务商
     */
    private Integer providerId;
    /**
     * 短信发送人
     */
    private String[] mobiles;
    /**
     * 短信内容
     */
    private String content;

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String[] getMobiles() {
        return mobiles;
    }

    public void setMobiles(String[] mobiles) {
        this.mobiles = mobiles;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

