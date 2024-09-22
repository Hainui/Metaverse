package com.metaverse.common.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class UrlEncryptorDecryptor {

    private static final String ALGORITHM = "AES";

    /**
     * Generates a secure key from the given ID.
     *
     * @param id The ID used as the base for generating the key.
     * @return A secure key as a byte array.
     */
    private static byte[] generateSecureKeyFromId(Long id) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(id.toString().getBytes());
            // Truncate the hash to 16 bytes for AES
            return Arrays.copyOfRange(hash, 0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate secure key", e);
        }
    }

    /**
     * Encrypts the given URL using the provided ID as the encryption key.
     *
     * @param url The URL to be encrypted.
     * @param id  The ID used as the encryption key.
     * @return The encrypted URL as a Base64 encoded string.
     */
    public static String encryptUrl(String url, Long id) {
        try {
            byte[] keyBytes = generateSecureKeyFromId(id);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] encryptedBytes = cipher.doFinal(url.getBytes());
            return Base64.getUrlEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt URL", e);
        }
    }

    /**
     * Decrypts the given URL using the provided ID as the decryption key.
     *
     * @param encryptedUrl The URL to be decrypted.
     * @param id           The ID used as the decryption key.
     * @return The decrypted URL as a string.
     */
    public static String decryptUrl(String encryptedUrl, Long id) {
        try {
            byte[] keyBytes = generateSecureKeyFromId(id);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] encryptedBytes = Base64.getUrlDecoder().decode(encryptedUrl);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt URL", e);
        }
    }

    public static void main(String[] args) {
        Long id = 1111L; // Replace with your custom ID
        String originalUrl = "http://example.com/path?query=value";
        String encryptedUrl = encryptUrl(originalUrl, id);
        String decryptedUrl = decryptUrl(encryptedUrl, id);

        System.out.println("Original URL: " + originalUrl);
        System.out.println("Encrypted URL: " + encryptedUrl);
        System.out.println("Decrypted URL: " + decryptedUrl);
    }
}