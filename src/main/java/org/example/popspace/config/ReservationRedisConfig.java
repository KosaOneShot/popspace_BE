package org.example.popspace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@Configuration
public class ReservationRedisConfig {

    @Value("${spring.datasource.data.redis.host}")
    private String redisHost;

    @Value("${spring.datasource.data.redis.port}")
    private int redisPort;

    @Bean(name = "reservationRedisTemplate")
    public StringRedisTemplate reservationRedisTemplate() {
        log.info("예약 Redis host: {}", redisHost);
        log.info("예약 Redis port: {}", redisPort);

        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl() // ✅ Valkey용 TLS 연결
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfig);
        factory.afterPropertiesSet();

        return new StringRedisTemplate(factory);
    }
}