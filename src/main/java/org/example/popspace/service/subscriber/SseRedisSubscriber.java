package org.example.popspace.service.subscriber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.util.notification.SseEmitterManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseRedisSubscriber implements MessageListener {

    private final SseEmitterManager sseEmitterManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            JsonNode node = new ObjectMapper().readTree(msg);
            int sentCount = 0;

            JsonNode memberIdsNode = node.get("memberIds");
            if (memberIdsNode != null && memberIdsNode.isArray()) {
                for (JsonNode idNode : memberIdsNode) {
                    Long memberId = idNode.asLong();
                    if (sseEmitterManager.hasEmitter(memberId)) {
                        sseEmitterManager.send(memberId, node);  // 전체 메시지 그대로 전송
                    }
                }
            }

            log.info("✅ Redis 메시지 수신 완료. 전송 대상 수: {}", sentCount);
        } catch (Exception e) {
            log.error("Redis 메시지 처리 실패", e);
        }
    }
}
