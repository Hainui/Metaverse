package com.metaverse.common.Utils;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisServer {

    private final StringRedisTemplate stringRedisTemplate;


    public static final Duration EXPIRATION_TIME = Duration.ofHours(24); // 过期时间为24小时
    public static final String EXPIRES_AT_FIELD = "expires_at"; // 存储过期时间的字段名

    public final static String HASH_KEY = "user_token";
    private static final DefaultRedisScript<Boolean> LUA_SCRIPT = new DefaultRedisScript<>();

    static {
        LUA_SCRIPT.setScriptText(
                "local userIdStr = KEYS[1]; " +
                        "local token = ARGV[1]; " +
                        "local expiresAt = ARGV[2]; " +
                        "redis.call('hset', 'user_token', userIdStr, token); " +
                        "redis.call('hset', 'user_token', userIdStr .. '_expires_at', expiresAt); " +
                        "return true;"
        );
        LUA_SCRIPT.setResultType(Boolean.class);
    }


    public HashOperations<String, String, String> hashOps() {
        return stringRedisTemplate.opsForHash();
    }

    /**
     * 存储用户ID和Token到Redis的Hash中，并设置过期时间为24小时。
     *
     * @param userId 用户ID
     * @param token  用户Token
     */
    public void storeToken(Long userId, String token) {
        String userIdStr = String.valueOf(userId);
        Instant expiresAt = Instant.now().plus(EXPIRATION_TIME);
        Boolean result = stringRedisTemplate.execute(LUA_SCRIPT,
                Collections.singletonList(userIdStr),
                token, String.valueOf(expiresAt.toEpochMilli()));

        // 检查脚本执行的结果
        if (result == null || !result) {
            throw new RuntimeException("Lua script execution failed.");
        }

        // 为整个Hash键设置过期时间
        stringRedisTemplate.expire(HASH_KEY, (int) EXPIRATION_TIME.getSeconds(), java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * 从Redis的Hash中获取用户Token。
     *
     * @param userId 用户ID
     * @return 用户Token
     */
    public String getToken(Long userId) {
        String userIdStr = String.valueOf(userId);
        String token = hashOps().get(HASH_KEY, userIdStr);
        String expiresAtStr = hashOps().get(HASH_KEY, userIdStr + "_" + EXPIRES_AT_FIELD);
        if (token != null && expiresAtStr != null) {
            long expiresAt = Long.parseLong(expiresAtStr);
            if (Instant.ofEpochMilli(expiresAt).isAfter(Instant.now())) {
                return token;
            } else {
                removeToken(userId); // 如果已过期，则删除该Token
                return null;
            }
        }
        return null;
    }

    /**
     * 从Redis的Hash中删除用户Token。
     *
     * @param userId 用户ID
     */
    public void removeToken(Long userId) {
        String userIdStr = String.valueOf(userId);
        Set<String> fields = hashOps().keys(HASH_KEY);
        for (String field : fields) {
            if (field.startsWith(userIdStr)) {
                hashOps().delete(HASH_KEY, field);
            }
        }
    }

    /**
     * 获取Redis中所有用户Token。
     *
     * @return 包含所有用户Token的Map
     */
    public Map<String, String> getAllTokens() {
        Map<String, String> allTokens = new HashMap<>();
        Map<String, String> entries = (Map<String, String>) hashOps().entries(HASH_KEY);
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            if (!entry.getKey().endsWith("_" + EXPIRES_AT_FIELD)) { // 排除过期时间字段
                allTokens.put(entry.getKey(), entry.getValue());
            }
        }
        return allTokens;
    }

    /**
     * 判断给定的userId和token是否与Redis中存储的匹配。
     *
     * @param userId 用户ID
     * @param token  用户Token
     * @return 如果userId存在于Redis中且对应的token与传入的相同，则返回true，否则返回false。
     */
    public boolean validateToken(Long userId, String token) {
        String storedToken = getToken(userId);
        return storedToken != null && storedToken.equals(token);
    }
}
