package org.example.popspace.notification.util;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterManager {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String nickname) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 60초 타임아웃
        emitters.put(nickname, emitter);

        emitter.onCompletion(() -> emitters.remove(nickname));
        emitter.onTimeout(() -> emitters.remove(nickname));

        return emitter;
    }

    public void send(String nickname, Object data) {
        SseEmitter emitter = emitters.get(nickname);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("new-notification").data(data));
            } catch (IOException e) {
                emitters.remove(nickname);
            }
        }
    }

    public boolean hasEmitter(String nickname) {
        return emitters.containsKey(nickname);
    }
}