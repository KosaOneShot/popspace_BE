package org.example.popspace.mapper.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Slf4j
public class AuthRedisRepository {

    private final StringRedisTemplate authRedisTemplate;

    public AuthRedisRepository(@Qualifier("authRedisTemplate") StringRedisTemplate authRedisTemplate) {
        this.authRedisTemplate = authRedisTemplate;
    }

    @Transactional
    public void setTokenBlacklist(String token) {
        authRedisTemplate.opsForValue().set(token, "blacklisted", Duration.ofDays(14));
    }

    @Transactional
    public void setEmailCodeValues(String email, String code) {
        authRedisTemplate.opsForValue().set(email, code, Duration.ofMinutes(5));
    }

    public String getEmailCodeValue(String email) {
        return authRedisTemplate.opsForValue().get(email);
    }

    public boolean checkBlackList(String token) {
        return authRedisTemplate.hasKey(token);
    }
}
