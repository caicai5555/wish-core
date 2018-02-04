package com.foundation;

import com.foundation.common.security.DesUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fanqinghui on 2016/8/31.
 */
public class SecurityTest {

    @Test
    public void testDes() throws Exception {
        String key="fanqinghui1";
        DesUtil util=new DesUtil(key+"1dsfs");
        String desP= util.encrypt(key);
        System.out.println(desP);
        String pass= util.decrypt(desP);
        System.out.println(pass);
        Assert.assertEquals(pass, key);
    }
}
