package org.example.popspace.notification.controller;


import lombok.RequiredArgsConstructor;
import org.example.popspace.notification.util.SseEmitterManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterManager sseEmitterManager;

    @GetMapping("/subscribe/{memberId}")
    public SseEmitter subscribe(@PathVariable int memberId) {
        return sseEmitterManager.addEmitter(memberId);
    }
}
