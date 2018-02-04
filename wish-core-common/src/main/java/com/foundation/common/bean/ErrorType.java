package com.foundation.common.bean;

/**
 * Created by fqh on 2016/8/27.
 */
public enum ErrorType {

    Success(1,"成功"),Fail(0,"失败");

    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

   private ErrorType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }



}
