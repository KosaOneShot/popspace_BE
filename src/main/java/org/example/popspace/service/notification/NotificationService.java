package org.example.popspace.service.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.notification.NotificationRequestDto;
import org.example.popspace.dto.notification.NotificationResponseDto;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.NotificationMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final StringRedisTemplate authRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void createNotificationAndNotify(Long memberId, NotificationRequestDto dto, String imageUrl) {
        NotificationResponseDto notification = NotificationResponseDto.builder()
                .popupId(dto.getPopupId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .imageUrl(imageUrl)
                .notificationState("ACTIVE")
                .build();

        notificationMapper.insertNotification(notification);
        sendNotificationToReservedMembers(dto.getPopupId(), notification);
    }

//    private void sendNotificationToReservedMembers(Long popupId, NotificationResponseDto notification) {
//        List<Long> ids = notificationMapper.selectReservedMemberIds(popupId);
//        for (Long reservedId : ids) {
//                if (sseEmitterManager.hasEmitter(reservedId)) {
//                    sseEmitterManager.send(reservedId, notification);
//                    log.info(">> SSE 전송 완료: {}",reservedId);
//                }
//        }
//    }

    //publisher
    private void sendNotificationToReservedMembers(Long popupId, NotificationResponseDto notification) {
        List<Long> ids = notificationMapper.selectReservedMemberIds(popupId);

        if (ids.isEmpty())
            return;

        String message = buildBroadcastMessage(ids, notification);
        authRedisTemplate.convertAndSend("sse-channel", message);
        log.info(">> Redis broadcast 전송 완료. 대상 인원: {}", ids.size());
    }

    public List<NotificationResponseDto> getAllNotifications(Long memberId) {
        return notificationMapper.selectNotificationsByMemberId(memberId);
    }

    private String buildBroadcastMessage(List<Long> memberIds, NotificationResponseDto notification) {
        Map<String, Object> data = new HashMap<>();
        data.put("memberIds", memberIds);
        data.put("notifyId", notification.getNotifyId());
        data.put("popupId", notification.getPopupId());
        data.put("title", notification.getTitle());
        data.put("content", notification.getContent());
        data.put("imageUrl", notification.getImageUrl());
        data.put("createdAt", notification.getCreatedAt());
        data.put("notificationState", notification.getNotificationState());

        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.JSON_CONVERT_FAILED);
        }
    }
}