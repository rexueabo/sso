package com.huimin.util;


import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author zhuliang
 *
 * @date 2018年1月7日
 */
public class EncryptUtil {

    private static final Logger logger = LoggerFactory.getLogger(EncryptUtil.class);

    private static final String md5 = "md5";
    private static final String rsa2 = "SHA256WithRSA";
    private static final String hmacSha1 = "HmacSHA1";

    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String md5(String orig) {
        return md5(orig, null, 1);
    }

    public static String md5(String orig, String salt, int hashIterations) {
        MessageDigest digest = getDigest(md5);
        if (StringUtils.isNotBlank(salt)) {
            digest.reset();
            digest.update(salt.getBytes());
        }
        byte[] hashed = digest.digest(orig.getBytes());
        int iterations = hashIterations - 1;
        for (int i = 0; i < iterations; i++) {
            digest.reset();
            hashed = digest.digest(hashed);
        }
        return toHex(hashed);
    }

    public static String md516(String orig) {
    	return md5(orig).substring(8, 24);
    }
    public static String rsa2(String content, String privateKey) {
        try {
            byte[] encodedKey = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(encodedKey);
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature.getInstance(rsa2);

            signature.initSign(priKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));

            byte[] signed = signature.sign();

            byte[] encoded = Base64.getEncoder().encode(signed);
            return new String(encoded);
        } catch (Exception e) {
            logger.error("签名失败：content={},privateKey={}", content, privateKey);
            logger.error("失败原因：", e);
            throw new RuntimeException("签名失败");
        }
    }

    public static String hmacSha1(String content, String privateKey) {
        try {
            byte[] keyBytes = privateKey.getBytes(StandardCharsets.UTF_8);
            SecretKey secretKey = new SecretKeySpec(keyBytes, hmacSha1);
            Mac mac = Mac.getInstance(hmacSha1);
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return toBase64(rawHmac);
        } catch (Exception e) {
            logger.error("签名失败：content={},privateKey={}", content, privateKey);
            logger.error("失败原因：", e);
            throw new RuntimeException("签名失败");
        }
    }

    public static boolean verify(String content, String sign, String publicKey) {
        return verify(content, sign, publicKey, rsa2, StandardCharsets.UTF_8.name());
    }

    private static boolean verify(String content, String sign, String publicKey, String signAlgorithms, String characterEncoding) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.getDecoder().decode(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature.getInstance(signAlgorithms);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(characterEncoding));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (Exception e) {
            logger.error("验签失败：content={},sign={},publicKey={},signAlgorithms={},characterEncoding={}", content, sign, publicKey, signAlgorithms, characterEncoding);
            logger.error("失败原因：", e);
            return false;
        }
    }

    private static String toHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }

        return new String(out);
    }

    private static String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static MessageDigest getDigest(String algorithmName) {
        try {
            return MessageDigest.getInstance(algorithmName);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("签名失败");
        }
    }
}
