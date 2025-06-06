package org.example.popspace.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class NotificationResponseDto {
    private Long notifyId;
    private Long popupId;
    private String title;
    private String content;
    private String imageUrl;
    private Date createdAt;
    private String notificationState;
}