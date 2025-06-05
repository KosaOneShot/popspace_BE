package org.example.popspace.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.example.popspace.dto.notification.Notification;
import org.example.popspace.dto.notification.NotificationResponseDto;

import java.util.List;

@Mapper
public interface NotificationMapper {
    void insertNotification(Notification notification);
    List<NotificationResponseDto> selectNotificationsByNickname(String nickname);
    List<String> selectReservedNicknames(int popupId);
}
