package org.example.popspace.controller.notification;


import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.util.notification.SseEmitterManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterManager sseEmitterManager;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetail user) {
        return sseEmitterManager.addEmitter(user.getId());
    }
}
