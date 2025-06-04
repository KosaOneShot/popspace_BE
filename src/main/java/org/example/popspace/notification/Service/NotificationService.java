package org.example.popspace.notification.Service;


import lombok.RequiredArgsConstructor;
import org.example.popspace.notification.domain.Notification;
import org.example.popspace.notification.dto.NotificationRequestDto;
import org.example.popspace.notification.dto.NotificationResponseDto;
import org.example.popspace.notification.mapper.NotificationMapper;
import org.example.popspace.notification.util.SseEmitterManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
//    private final ReservationMapper reservationMapper;
    private final SseEmitterManager sseEmitterManager;

    public void createNotificationAndNotify(NotificationRequestDto dto, String imageUrl) {
            // 공지 생성 및 저장
            Notification notification = new Notification();
            notification.setPopupId(dto.getPopupId());
            notification.setTitle(dto.getTitle());
            notification.setContent(dto.getContent());
            notification.setImageUrl(imageUrl);
            notification.setNotificationState("VISIBLE");
            notification.setCreatedAt(new Date());

            System.out.println(">> insert 시작");
            System.out.println(">> Insert 파라미터 확인");
            System.out.println("popupId: " + notification.getPopupId());
            System.out.println("title: " + notification.getTitle());
            System.out.println("content: " + notification.getContent());
            System.out.println("imageUrl: " + imageUrl);
            System.out.println("notificationState: " + notification.getNotificationState());
            System.out.println("createdAt: " + notification.getCreatedAt());
            notificationMapper.insertNotification(notification);
            System.out.println(">> insert 결과: ");

            // 예약자 조회
            List<Integer> memberIds = notificationMapper.selectReservedMemberIds(dto.getPopupId());
            System.out.println(">> 예약된 멤버 수: " + memberIds.size());

            NotificationResponseDto responseDto = new NotificationResponseDto(
                    dto.getPopupId(), dto.getTitle(), dto.getContent(), imageUrl
            );

            for (int memberId : memberIds) {
                try {
                    if (sseEmitterManager.hasEmitter(memberId)) {
                        sseEmitterManager.send(memberId, responseDto);
                        System.out.println(">> SSE 전송 완료: " + memberId);
                    }
                } catch (Exception e) {
                    System.err.println(">> SSE 전송 실패: " + memberId);
                    e.printStackTrace();
                }
            }
    }


    public List<NotificationResponseDto> getAllNotifications(String nickname) {
        return notificationMapper.selectNotificationsByNickname(nickname);
    }
}