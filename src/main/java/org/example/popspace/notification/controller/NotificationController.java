package org.example.popspace.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.popspace.notification.Service.NotificationService;
import org.example.popspace.notification.dto.NotificationRequestDto;
import org.example.popspace.notification.dto.NotificationResponseDto;
import org.example.popspace.notification.util.ImageUploader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        System.out.println(">> 파일 저장 완료, DB 저장 시도");
        notificationService.createNotificationAndNotify(dto, imageUrl);
        System.out.println(">> DB 저장 완료");
        return ResponseEntity.ok("공지 등록 및 알림 전송 완료");
    }

    @GetMapping("/nickname/{nickname}")
    public List<NotificationResponseDto> getAllNotificationsForMember(@PathVariable String nickname){
        return notificationService.getAllNotifications(nickname);
    }

}