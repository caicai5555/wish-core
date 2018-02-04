package com.foundation.common.security;

import com.foundation.common.io.StreamUtils;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by fqh on 2015/12/11.
 */
public class Security {

    /**
     * byte数组转换层base64
     * @param bytes
     * @return
     */
    public static String byte2base64(byte[] bytes){
        BASE64Encoder base64Encoder=new BASE64Encoder();
        return base64Encoder.encode(bytes);
    }
    /**
     * base64转换成byte
     * @param base64
     * @return
     */
    public static byte[] base642byte(String base64) throws IOException{
        BASE64Decoder base64Decoder=new BASE64Decoder();
        return base64Decoder.decodeBuffer(base64);
    }

    /**
     * 使用guava的MD5实现现
     * @param dueStr
     * @return
     */
    public static String md5(String dueStr){
        byte[] md5= Hashing.md5().hashString(dueStr, Charsets.UTF_8).asBytes();
        return StreamUtils.byte2hex(md5);
    }


}
