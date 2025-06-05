package org.example.popspace.notification.Service;


import lombok.RequiredArgsConstructor;
import org.example.popspace.notification.domain.Notification;
import org.example.popspace.notification.dto.NotificationRequestDto;
import org.example.popspace.notification.dto.NotificationResponseDto;
import org.example.popspace.notification.mapper.NotificationMapper;
import org.example.popspace.notification.util.SseEmitterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private SseEmitterManager sseEmitterManager;


    @Transactional
    public void createNotificationAndNotify(NotificationRequestDto dto, String imageUrl) {
            // 공지 생성 및 저장
            Notification notification = new Notification();
            notification.setPopupId(dto.getPopupId());
            notification.setTitle(dto.getTitle());
            notification.setContent(dto.getContent());
            notification.setImageUrl(imageUrl);
            notification.setNotificationState("1");
            notification.setCreatedAt(new Date());

            notificationMapper.insertNotification(notification);

            // 예약자 조회
            List<String> nicknames = notificationMapper.selectReservedNicknames(dto.getPopupId());

            NotificationResponseDto responseDto = new NotificationResponseDto(
                    notification.getNotifyId(), dto.getPopupId(), dto.getTitle(), dto.getContent(), imageUrl
            );

            for (String nickname : nicknames) {
                    if (sseEmitterManager.hasEmitter(nickname)) {
                        sseEmitterManager.send(nickname, responseDto);
                        System.out.println(">> SSE 전송 완료: " + nickname);
                    }
            }
    }


    public List<NotificationResponseDto> getAllNotifications(String nickname) {
        return notificationMapper.selectNotificationsByNickname(nickname);
    }
}