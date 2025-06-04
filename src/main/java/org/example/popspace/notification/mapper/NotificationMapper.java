package org.example.popspace.notification.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.example.popspace.notification.domain.Notification;
import org.example.popspace.notification.dto.NotificationResponseDto;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void insertNotification(Notification notification);
    List<NotificationResponseDto> selectNotificationsByNickname(String nickname);
    List<Integer> selectReservedMemberIds(int popupId);
}
