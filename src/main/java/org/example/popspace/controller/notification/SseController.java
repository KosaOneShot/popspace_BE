package org.example.popspace.controller.notification;


import lombok.RequiredArgsConstructor;
import org.example.popspace.util.notification.SseEmitterManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterManager sseEmitterManager;

    @GetMapping("/subscribe/{nickname}")
    public SseEmitter subscribe(@PathVariable String nickname) {
        return sseEmitterManager.addEmitter(nickname);
    }
}
