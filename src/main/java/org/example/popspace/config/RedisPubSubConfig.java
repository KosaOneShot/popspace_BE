package org.example.popspace.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubSubConfig {

    private final StringRedisTemplate authRedisTemplate;

    public RedisPubSubConfig(@Qualifier("authRedisTemplate") StringRedisTemplate authRedisTemplate) {
        this.authRedisTemplate = authRedisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(MessageListener sseRedisSubscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(authRedisTemplate.getConnectionFactory());
        container.addMessageListener(sseRedisSubscriber, new PatternTopic("sse-channel"));
        return container;
    }

}