package com.foundation.common.security;

import java.security.MessageDigest;
/**
 * 
* @ClassName: MD5Util 
* @Description: 可解密的MD5
* @author chengchen 
* @date 2016年10月20日 上午9:20:51 
*
 */
public class MD5Util {  
	  
    /**
     * 
    * @Title: string2MD5 
    * @Description: MD5加码 生成32位md5码 
    * @author chengchen
    * @date 2016年10月20日 上午9:21:48 
    * @param @param inStr
    * @param @return    设定参数 
    * @return String    返回类型 
    * @throws
     */
    public static String MD5(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    }  
  
    /**
     *  
    * @Title: convertMD5 
    * @Description: 加密解密算法 执行一次加密，两次解密 
    * @author chengchen
    * @date 2016年10月20日 上午9:21:58 
    * @param @param inStr
    * @param @return    设定参数 
    * @return String    返回类型 
    * @throws
     */
    public static String convertMD5(String inStr){  
        char[] a = inStr.toCharArray();  
        for (int i = 0; i < a.length; i++){  
            a[i] = (char) (a[i] ^ 't');  
        }  
        String s = new String(a);  
        return s;  
    }  
  
    /**
     * 
    * @Title: main 
    * @Description: 测试主函数  
    * @author chengchen
    * @date 2016年10月20日 上午9:22:17 
    * @param @param args    设定参数 
    * @return void    返回类型 
    * @throws
     */
    /*public static void main(String args[]) {  
        String s = new String("9b4ceab4-0b3b-4ca3-be0c-31e9b97c381d+dffe2f50-b985-42c0-acfd-dc8a49a20444+f4d81225-f73c-4d03-8f26-3db612561d44+b47071d3-f263-4a5c-b8cc-637900ba4940+7addb092-c495-4d59-af48-2f532cfcf789+78f0b1b5-211c-4537-a524-4bf958ba9706+946e90f3-5932-416c-9877-32bb6822ea3a+d1e713e5-3542-44da-aee4-d376964cece2");  
        System.out.println("原始：" + s);  
        System.out.println("MD5后：" + string2MD5(s));  
        System.out.println("加密的：" + convertMD5(s));  
        System.out.println("解密的：" + convertMD5(convertMD5(s)));  
    }*/
}
