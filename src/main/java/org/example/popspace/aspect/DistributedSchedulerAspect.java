package org.example.popspace.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.popspace.annotation.DistributedScheduled;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class DistributedSchedulerAspect {

    private final StringRedisTemplate authRedisTemplate;
    private static final String RELEASE_LOCK_SCRIPT = """
        if redis.call("get", KEYS[1]) == ARGV[1] then
            return redis.call("del", KEYS[1])
        else
            return 0
        end
        """;

    public DistributedSchedulerAspect(@Qualifier("authRedisTemplate") StringRedisTemplate authRedisTemplate) {
        this.authRedisTemplate = authRedisTemplate;
    }

    @Around("@annotation(dist)")
    public Object lockAndRun(ProceedingJoinPoint joinPoint, DistributedScheduled dist) throws Throwable {
        String lockKey = dist.lockKey();
        String lockId = UUID.randomUUID().toString(); // 🔑 고유 락 ID 생성
        log.info("lockKey: {}, lockId: {}", lockKey, lockId);
        Boolean lock = authRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockId, Duration.ofSeconds(dist.expireSeconds()));

        if (Boolean.TRUE.equals(lock)) {
            log.info("[LOCK ACQUIRED] {}", lockKey);
            try {
                return joinPoint.proceed();
            } finally {
                // 락 해제는 여기에서 Lua 스크립트로 수행해야 함
                authRedisTemplate.execute(
                        new DefaultRedisScript<>(RELEASE_LOCK_SCRIPT, Long.class),
                        Collections.singletonList(lockKey),
                        lockId
                );
            }
        } else {
            log.info("[LOCK SKIPPED] Already running: {}", lockKey);
            return null;
        }
    }
}
