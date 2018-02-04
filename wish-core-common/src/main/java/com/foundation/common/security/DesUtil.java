package com.foundation.common.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES加密和解密-需要new
 * @fqh
 */
public class DesUtil {

    /**
     * 安全密钥
     */
    private String keyData = "?sdfa;lksdAGcjao0-1;2a@ksfaGEQ|ldkfjo!1";

    public DesUtil() {}

    public DesUtil(String keyData) {
        this.keyData = keyData;
    }

    /**
     * 功能：加密 (UTF-8)
     * @param source
     * @return
     * @throws UnsupportedEncodingException
     */
    public String encrypt(String source) throws UnsupportedEncodingException {
        return encrypt(source, "UTF-8");
    }

    /**
     * 功能：解密 (UTF-8)
     * @param encryptedData
     * @return
     * @throws UnsupportedEncodingException
     */
    public String decrypt(String encryptedData) throws UnsupportedEncodingException {
        return decrypt(encryptedData, "UTF-8");
    }

    /**
     * 功能：加密
     * @param source
     * @param charSet
     * @return
     * @throws UnsupportedEncodingException
     */
    public String encrypt(String source, String charSet) throws UnsupportedEncodingException {
        String encrypt = null;
        byte[] ret = encrypt(source.getBytes(charSet));
        encrypt = new String(Base64.encode(ret));
        return encrypt;
    }

    /**
     * 功能：解密
     * @param encryptedData
     * @param charSet
     * @return
     * @throws UnsupportedEncodingException
     */
    public String decrypt(String encryptedData, String charSet) throws UnsupportedEncodingException {
        String descryptedData = null;
        byte[] ret = descrypt(Base64.decode(encryptedData));
        descryptedData = new String(ret, charSet);
        return descryptedData;
    }

    /**
     * 加密数据
     * @param primaryData
     * @return
     */
    private byte[] encrypt(byte[] primaryData) {
        /** 取得安全密钥 */
        byte rawKeyData[] = getKey();
        /** DES算法要求有一个可信任的随机数源 */
        SecureRandom sr = new SecureRandom();
        /** 使用原始密钥数据创建DESKeySpec对象 */
        DESKeySpec dks = null;
        try {
            System.out.println("encrypt keyData:"+keyData);
            dks = new DESKeySpec(keyData.getBytes());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        /** 创建一个密钥工厂 */
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        /** 用密钥工厂把DESKeySpec转换成一个SecretKey对象 */
        SecretKey key = null;
        try {
            key = keyFactory.generateSecret(dks);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        /** Cipher对象实际完成加密操作 */
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        /** 用密钥初始化Cipher对象 */try {
        cipher.init(Cipher.ENCRYPT_MODE, key, sr);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        /** 正式执行加密操作 */
        byte encryptedData[] = null;
        try {
            encryptedData = cipher.doFinal(primaryData);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        /** 返回加密数据 */
        return encryptedData;
    }

    /**
     * 用密钥解密数据
     * @param encryptedData
     * @return
     */
    private byte[] descrypt(byte[] encryptedData) {
        /** DES算法要求有一个可信任的随机数源 */
        SecureRandom sr = new SecureRandom();
        /** 取得安全密钥 */
        byte rawKeyData[] = getKey();
        /** 使用原始密钥数据创建DESKeySpec对象 */
        DESKeySpec dks = null;
        try {
            System.out.println("descrypt keyData:"+keyData);
            dks = new DESKeySpec(keyData.getBytes());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        /** 创建一个密钥工厂 */
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        /** 用密钥工厂把DESKeySpec转换成一个SecretKey对象 */
        SecretKey key = null;
        try {
            key = keyFactory.generateSecret(dks);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        /** Cipher对象实际完成加密操作 */
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        /** 用密钥初始化Cipher对象 */
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, sr);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        /** 正式执行解密操作 */
        byte decryptedData[] = null;
        try {
            decryptedData = cipher.doFinal(encryptedData);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return decryptedData;
    }

    /**
     * 取得安全密钥 此方法作废,因为每次key生成都不一样导致解密加密用的密钥都不一样，
     * 从而导致Given final block not properly padded错误
     */
    private byte[] getKey() {
        /** DES算法要求有一个可信任的随机数源 */
        SecureRandom sr = new SecureRandom();
        /** 为我们选择的DES算法生成一个密钥生成器对象 */
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance("DES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kg.init(sr);
        /** 生成密钥工具类 */
        SecretKey key = kg.generateKey();
        /** 生成密钥byte数组 */
        byte rawKeyData[] = key.getEncoded();
        return rawKeyData;
    }
}