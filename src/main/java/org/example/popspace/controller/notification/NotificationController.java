package org.example.popspace.controller.notification;

import lombok.RequiredArgsConstructor;
import org.example.popspace.service.notification.NotificationService;
import org.example.popspace.dto.notification.NotificationRequestDto;
import org.example.popspace.dto.notification.NotificationResponseDto;
import org.example.popspace.util.notification.ImageUploader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final ImageUploader imageUploader;

    @PostMapping
    public ResponseEntity<String> create(@ModelAttribute NotificationRequestDto dto) throws IOException {
        String imageUrl = imageUploader.saveImage(dto.getImage());  // 여기서 이미지 저장
        notificationService.createNotificationAndNotify(dto, imageUrl);
        return ResponseEntity.ok("공지 등록 및 알림 전송 완료");
    }

    @GetMapping("/nickname/{nickname}")
    public List<NotificationResponseDto> getAllNotificationsForMember(@PathVariable String nickname){
        return notificationService.getAllNotifications(nickname);
    }

}