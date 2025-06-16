package org.example.popspace.controller.notification;

import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.service.notification.NotificationService;
import org.example.popspace.dto.notification.NotificationRequestDto;
import org.example.popspace.dto.notification.NotificationResponseDto;
import org.example.popspace.service.s3.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<String> create(@AuthenticationPrincipal CustomUserDetail user, @RequestPart("file") MultipartFile file, @ModelAttribute NotificationRequestDto dto) {
        String url =s3Service.uploadImage(file, "notification");  // 여기서 이미지 저장
        notificationService.createNotificationAndNotify(user.getId(), dto, url);
        return ResponseEntity.ok("공지 등록 및 알림 전송 완료");
    }

    @GetMapping
    public List<NotificationResponseDto> getAllNotificationsForMember(@AuthenticationPrincipal CustomUserDetail user){
        return notificationService.getAllNotifications(user.getId());
    }

}