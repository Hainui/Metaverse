package com.metaverse.common.Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class SignatureGenerator {

    private static final Long SECRET_KEY = 1111L; // 私钥
    private static final String ALGORITHM = "HmacSHA256"; // 算法
    private static final int EXPIRE_TIME_SECONDS = 3600; // 1小时

    public static String generateSignedUrl(String originalUrl, Long regionId) {
        Instant now = Instant.now();
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .withZone(ZoneOffset.UTC)
                .format(now.plusSeconds(EXPIRE_TIME_SECONDS));

        String toSign = originalUrl + timestamp;
        String signature = signWithSecretKey(toSign, regionId, ALGORITHM);

        return originalUrl + "?timestamp=" + timestamp + "&signature=" + signature;
    }

    private static String signWithSecretKey(String toSign, Long secretKeyRegionId, String algorithm) {
        try {
            Mac sha256Hmac = Mac.getInstance(algorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyRegionId.toString().getBytes(), algorithm);
            sha256Hmac.init(secretKeySpec);
            byte[] hash = sha256Hmac.doFinal(toSign.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}