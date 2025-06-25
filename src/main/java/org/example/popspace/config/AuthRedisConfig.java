package org.example.popspace.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class AuthRedisConfig {

    // 기본 RedisTemplate은 Spring Boot가 자동 구성함

    @Value("${custom-redis.host}")
    private String secondHost;

    @Value("${custom-redis.port}")
    private int secondPort;

    @Bean(name = "authRedisTemplate")
    public StringRedisTemplate authRedisTemplate() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(secondHost);
        redisConfig.setPort(secondPort);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl()  // 🔥 TLS 적용 필수
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfig);
        factory.afterPropertiesSet();

        return new StringRedisTemplate(factory);
    }
}