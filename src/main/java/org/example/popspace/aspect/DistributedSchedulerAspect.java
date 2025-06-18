package org.example.popspace.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.popspace.annotation.DistributedScheduled;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
public class DistributedSchedulerAspect {

    private final StringRedisTemplate authRedisTemplate;

    public DistributedSchedulerAspect(@Qualifier("authRedisTemplate") StringRedisTemplate authRedisTemplate) {
        this.authRedisTemplate = authRedisTemplate;
    }

    @Around("@annotation(dist)")
    public Object lockAndRun(ProceedingJoinPoint joinPoint, DistributedScheduled dist) throws Throwable {
        Boolean lock = authRedisTemplate.opsForValue()
                .setIfAbsent(dist.lockKey(), "locked", Duration.ofSeconds(dist.expireSeconds()));

        if (Boolean.TRUE.equals(lock)) {
            try {
                return joinPoint.proceed();
            } finally {
                authRedisTemplate.delete(dist.lockKey());
            }
        } else {
            return null; // 다른 인스턴스가 처리 중
        }
    }
}
