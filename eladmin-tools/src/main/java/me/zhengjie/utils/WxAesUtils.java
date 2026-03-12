package me.zhengjie.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class WxAesUtils {

    private static final String AES = "AES";
    private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";

    /**
     * AES加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @param iv   偏移量
     * @return 加密后的Base64字符串
     */
    public String encrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES);
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(encrypted);
    }

    /**
     * AES解密
     *
     * @param data 待解密数据(Base64格式)
     * @param key  密钥
     * @param iv   偏移量
     * @return 解密后的字符串
     */
    public String decrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES);
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(Base64.decodeBase64(data));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * 使用微信小程序sessionKey解密用户数据
     *
     * @param encryptedData 加密数据
     * @param sessionKey    会话密钥
     * @param iv            偏移量
     * @return 解密后的JSON字符串
     */
    public String decryptUserData(String encryptedData, String sessionKey, String iv) throws Exception {
        return decrypt(encryptedData, sessionKey, iv);
    }

    /**
     * 验证数据签名
     *
     * @param rawData    原始数据
     * @param sessionKey 会话密钥
     * @param signature  签名
     * @return 是否验证通过
     */
    public boolean validateSignature(String rawData, String sessionKey, String signature) {
        try {
            String data = rawData + sessionKey;
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
