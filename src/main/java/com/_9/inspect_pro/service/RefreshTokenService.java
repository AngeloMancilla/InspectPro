package com._9.inspect_pro.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    public void storeRefreshToken(String email, String token) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, token, REFRESH_TOKEN_TTL);
    }

    public boolean validateRefreshToken(String email, String token) {
        String key = REFRESH_TOKEN_PREFIX + email;
        String storedToken = redisTemplate.opsForValue().get(key);
        return storedToken != null && storedToken.equals(token);
    }

    public void revokeRefreshToken(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.delete(key);
    }
}
