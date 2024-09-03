package com.metaverse.common.Utils;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtil {

    /**
     * 对密码进行安全哈希。
     *
     * @param plainTextPassword 明文密码
     * @return 安全哈希后的密码
     */
    public static String hashPassword(String plainTextPassword) {
        // BCrypt的默认工作因子为10，可以根据需要调整
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * 验证提供的明文密码是否与存储的哈希密码匹配。
     *
     * @param plainTextPassword    明文密码
     * @param storedHashedPassword 存储的哈希密码
     * @return 如果匹配则返回true，否则返回false
     */
    public static boolean checkPassword(String plainTextPassword, String storedHashedPassword) {
        return BCrypt.checkpw(plainTextPassword, storedHashedPassword);
    }

    public static void main(String[] args) {
        String password = "mySecurePassword";
        String hashedPassword = hashPassword(password);
        System.out.println("Hashed Password: " + hashedPassword);

        // 验证密码
        boolean matches = checkPassword(password, hashedPassword);
        System.out.println("Does the password match? " + matches);
    }
}