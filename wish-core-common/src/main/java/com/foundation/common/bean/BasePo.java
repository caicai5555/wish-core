package com.foundation.common.bean;

import java.io.Serializable;

/**
 * 基本Vo定义
 * Created by fqh on 2015/12/9.
 */
public class BasePo implements Serializable {

  //  protected Long id;
    protected Long createDate;
    protected Long updateDate;

  /*  public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }*/

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }
}
