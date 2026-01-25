package com._9.inspect_pro.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ProfileSessionService {

    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String ACTIVE_PROFILE_PREFIX = "active_profile:";
    private static final Duration SESSION_TTL = Duration.ofDays(7);

    public void setActiveProfile(Long userId, Long profileId) {
        String key = ACTIVE_PROFILE_PREFIX + userId;
        redisTemplate.opsForValue().set(key, profileId.toString(), SESSION_TTL);
    }

    public Long getActiveProfile(Long userId) {
        String key = ACTIVE_PROFILE_PREFIX + userId;
        String profileId = redisTemplate.opsForValue().get(key);
        return profileId != null ? Long.parseLong(profileId) : null;
    }

    public void clearActiveProfile(Long userId) {
        String key = ACTIVE_PROFILE_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
