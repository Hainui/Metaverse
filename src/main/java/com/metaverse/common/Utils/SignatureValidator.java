package com.metaverse.common.Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class SignatureValidator {

    private static final Long SECRET_KEY = 1111L; // 私钥
    private static final String ALGORITHM = "HmacSHA256"; // 算法

    public static String validateSignedUrl(String signedUrl, Long regionId) {
        String[] parts = signedUrl.split("\\?");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid URL");
        }

        String originalUrl = parts[0];
        String queryPart = parts[1];

        String timestamp = null;
        String signature = null;

        for (String param : queryPart.split("&")) {
            if (param.startsWith("timestamp=")) {
                timestamp = param.substring("timestamp=".length());
            } else if (param.startsWith("signature=")) {
                signature = param.substring("signature=".length());
            }
        }

        if (timestamp == null || signature == null) {
            throw new IllegalArgumentException("Invalid URL");
        }

        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC);
        Instant expires = Instant.from(formatter.parse(timestamp));

        if (now.isAfter(expires)) {
            throw new IllegalArgumentException("URL资源已过期");
        }

        String toSign = originalUrl + timestamp;
        String computedSignature = signWithSecretKey(toSign, regionId, ALGORITHM);
        if (!signature.equals(computedSignature)) {
            throw new IllegalArgumentException("该用户无权访问此资源");
        }

        return originalUrl;
    }

    private static String signWithSecretKey(String toSign, Long secretKey, String algorithm) {
        try {
            Mac sha256Hmac = Mac.getInstance(algorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.toString().getBytes(), algorithm);
            sha256Hmac.init(secretKeySpec);
            byte[] hash = sha256Hmac.doFinal(toSign.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate signature", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        String signedUrl = SignatureGenerator.generateSignedUrl("1111", 1111L);
        System.out.println("SignedUrl: " + signedUrl);
        String originalUrl = SignatureValidator.validateSignedUrl(signedUrl, 1111L);
        System.out.println("originalUrl: " + originalUrl);
    }
}