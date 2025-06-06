package org.example.popspace.service.notification;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.notification.NotificationRequestDto;
import org.example.popspace.dto.notification.NotificationResponseDto;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.NotificationMapper;
import org.example.popspace.util.notification.SseEmitterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private SseEmitterManager sseEmitterManager;


    @Transactional
    public void createNotificationAndNotify(Long memberId, NotificationRequestDto dto, String imageUrl) {
        Long popupId = notificationMapper.selectPopupIdByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.POPUP_NOT_FOUND));
        NotificationResponseDto notification = NotificationResponseDto.builder()
                .popupId(popupId)
                .title(dto.getTitle())
                .content(dto.getContent())
                .imageUrl(imageUrl)
                .notificationState("1")
                .build();

        int result = notificationMapper.insertNotification(notification);
        if(result == 0 || notification.getNotifyId() == null){
            throw new CustomException(ErrorCode.NOTIFICATION_INSERT_FAILED);
        }
        sendNotificationToReservedMembers(popupId, notification);
    }

    private void sendNotificationToReservedMembers(Long popupId, NotificationResponseDto notification) {
        List<Long> ids = notificationMapper.selectReservedMemberIds(popupId);
        for (Long reservedId : ids) {
            try {
                if (sseEmitterManager.hasEmitter(reservedId)) {
                    sseEmitterManager.send(reservedId, notification);
                    log.info(">> SSE 전송 완료: {}",reservedId);
                }
            } catch (Exception e) {
                throw new CustomException(ErrorCode.SSE_SEND_FAILED);
            }
        }
    }

    public List<NotificationResponseDto> getAllNotifications(Long memberId) {
        return notificationMapper.selectNotificationsByMemberId(memberId);
    }
}