package com.trackersurvey.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zh931 on 2018/5/9.
 */

public class HMAC_SHA1_Util {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    /**
     * 使用 HMAC-SHA1 签名方法对data进行签名
     *
     * @param   data 被签名的字符串
     * @param   key 密钥
     * @return  加密后的字符串
     */
    public static String genHMAC(String data, String key) {
        byte[] rawHmac = null;
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            //用给定密钥初始化 Mac 对象
            mac.init(signinKey);
            //完成 Mac 操作
            rawHmac = mac.doFinal(data.getBytes());
            //byte[] result = Base64.encodeBase64(rawHmac);
            return byte2hex(rawHmac);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException ignore) {

        }
        return "";
    }
    public static String byte2hex(byte[] b)
    {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }
    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {
        String genHMAC = genHMAC("111", "2222");
        System.out.println(genHMAC.length()); //28
        System.out.println(genHMAC);  // O5fviq3DGCB5NrHcl/JP6+xxF6s=
    }
}
