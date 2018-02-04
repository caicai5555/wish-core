package com.foundation;

import com.foundation.common.security.PwdSha1Util;
import junit.framework.Assert;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * Created by fanqinghui on 2016/8/30.
 */
public class PwdSha1Test {

    @Test
    public void testPwd() {
        String password="123456";
        String pwdEntrypt=PwdSha1Util.entryptPassword(password);
        Assert.assertEquals(PwdSha1Util.validatePassword(password,pwdEntrypt),true);
    }
}
