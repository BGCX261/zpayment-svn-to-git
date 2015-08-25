package com.zing.zbookstore.directpay;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class HMACHelper {

    private static byte[] HMacEncode(final String algorithm, final String key, final String data) {
        Mac macGenerator = null;
        try {
            macGenerator = Mac.getInstance(algorithm);
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(UTF8CHARSET), algorithm);
            macGenerator.init(signingKey);
        } catch (NoSuchAlgorithmException ex) {
        } catch (InvalidKeyException ex) {
        }
        if (macGenerator == null) {
            return null;
        }
        return macGenerator.doFinal(data.getBytes(UTF8CHARSET));
    }

    public static String HMacBase64Encode(final String algorithm, final String key, final String data) {
        byte[] hmacEncodeBytes = HMacEncode(algorithm, key, data);
        if (hmacEncodeBytes == null) {
            return null;
        }
        return Base64.encodeToString(hmacEncodeBytes, Base64.DEFAULT);
    }
    public static String HMacHexStringEncode(final String algorithm, final String key, final String data) {
        byte[] hmacEncodeBytes = HMacEncode(algorithm, key, data);
        if (hmacEncodeBytes == null) {
            return null;
        }
        return HexStringUtil.byteArrayToHexString(hmacEncodeBytes);
    }
    public final static String HMACMD5 = "HmacMD5";
    public final static String HMACSHA1 = "HmacSHA1";
    public final static String HMACSHA256 = "HmacSHA256";
    public final static String HMACSHA512 = "HmacSHA512";
    public final static Charset UTF8CHARSET = Charset.forName("UTF-8");
    public final static LinkedList<String> HMACS = new LinkedList<String>(Arrays.asList("UnSupport", "HmacSHA256", "HmacMD5", "HmacSHA384", "HMacSHA1", "HmacSHA512"));
}