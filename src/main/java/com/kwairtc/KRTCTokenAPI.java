package com.kwairtc;

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.Deflater;

public class KRTCTokenAPI {
    final private String sdkappid;
    final private String key;

    public KRTCTokenAPI(String sdkappid, String key) {
        this.sdkappid = sdkappid;
        this.key = key;
    }

    /**
     * 【功能说明】用于签发 KwaiRTC 服务中必须要使用的 Token 鉴权签名
     * 【参数说明】
     * @param userId - 用户id
     * @param expire - Token 签名的过期时间，单位是秒，比如 86400 代表生成的 Token 签名在一天后就无法再使用了。
     * @return token -生成的签名
     */
    public String genToken(String userId, long expire) {
        return genToken(userId, expire, null);
    }

    private String hmacsha256(String identifier, long currTime, long expire, String base64Userbuf) {
        String contentToBeSigned = "TLS.identifier:" + identifier + "\n"
                + "TLS.sdkappid:" + sdkappid + "\n"
                + "TLS.time:" + currTime + "\n"
                + "TLS.expire:" + expire + "\n";
        if (null != base64Userbuf) {
            contentToBeSigned += "TLS.userbuf:" + base64Userbuf + "\n";
        }
        try {
            byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
            hmac.init(keySpec);
            byte[] byteToken = hmac.doFinal(contentToBeSigned.getBytes(StandardCharsets.UTF_8));
            return (Base64.getEncoder().encodeToString(byteToken)).replaceAll("\\s*", "");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

    private String genToken(String userId, long expire, byte[] userbuf) {

        long currTime = System.currentTimeMillis() / 1000;

        JSONObject tokenDoc = new JSONObject();
        tokenDoc.put("TLS.ver", "1.0");
        tokenDoc.put("TLS.identifier", userId);
        tokenDoc.put("TLS.sdkappid", sdkappid);
        tokenDoc.put("TLS.expire", expire);
        tokenDoc.put("TLS.time", currTime);

        String base64UserBuf = null;
        if (null != userbuf) {
            base64UserBuf = Base64.getEncoder().encodeToString(userbuf).replaceAll("\\s*", "");
            tokenDoc.put("TLS.userbuf", base64UserBuf);
        }
        String token = hmacsha256(userId, currTime, expire, base64UserBuf);
        if (token.length() == 0) {
            return "";
        }
        tokenDoc.put("TLS.token", token);
        Deflater compressor = new Deflater();
        compressor.setInput(tokenDoc.toString().getBytes(StandardCharsets.UTF_8));
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedBytesLength = compressor.deflate(compressedBytes);
        compressor.end();
        return (new String(Base64URL.base64EncodeUrl(Arrays.copyOfRange(compressedBytes,
                0, compressedBytesLength)))).replaceAll("\\s*", "");
    }
    public static void main(String []args){
        String sdkappid = "5857402072";//您的appid 可在控制台项目详情中查询
        String key = "c71ec608327e04044ef9e118ed2316ea08150d03";//您的appsign 可在控制台项目详情中查询
        KRTCTokenAPI api = new KRTCTokenAPI(sdkappid,key);
        String userId = "121212";//用户id
        long expire = 86400L;//代表有效期一天
        String token = api.genToken(userId,expire);
        System.out.println("生成的token为："+token);
    }
}