package com.metaverse.common.Utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class VerificationCodeUtil {

    // 缓存验证码
    private static final LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES) // 设置过期时间为1分钟
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String email) throws Exception {
                    return generateVerificationCode();
                }
            });

    // 邮件发送配置
    private static final String SMTP_HOST = "smtp.qq.com";
    private static final int SMTP_PORT = 587; // 也可以使用465
    private static final String EMAIL_USER = "1273393857@qq.com";
    private static final String EMAIL_PASSWORD = "azmhyeycwdzcfjgb"; // 应该替换成你的授权码

    /**
     * 生成验证码。
     *
     * @return 生成的验证码
     */
    private static String generateVerificationCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int number = (int) (Math.random() * 10);
            sb.append(number);
        }
        return sb.toString();
    }

    /**
     * 发送验证码到指定邮箱。
     *
     * @param email 邮箱地址
     */
    public static void sendVerificationCode(String email) {
        String verificationCode;
        try {
            verificationCode = cache.get(email);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_USER, EMAIL_PASSWORD);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("验证码");
            message.setText("您的验证码是：" + verificationCode);

            Transport.send(message);

            log.info("验证码已发送至邮箱：{}", email);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 校验验证码。
     *
     * @param email 邮箱地址
     * @param code  输入的验证码
     * @return 如果验证码正确且未过期，则返回true；否则返回false
     */
    public static boolean verifyCode(String email, String code) {
        try {
            String cachedCode = cache.getIfPresent(email);
            if (Objects.isNull(cachedCode)) {
                throw new IllegalArgumentException("验证码已过期");
            }
            if (!StringUtils.equals(code, cachedCode)) {
                throw new IllegalArgumentException("验证码错误");
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        String email = "1661886969@qq.com";
        sendVerificationCode(email);

        // 假设用户输入的验证码
        String inputCode = "123456"; // 这里应该是实际发送的验证码

        boolean isValid = verifyCode(email, inputCode);
        System.out.println("验证码是否有效: " + isValid);
    }
}



