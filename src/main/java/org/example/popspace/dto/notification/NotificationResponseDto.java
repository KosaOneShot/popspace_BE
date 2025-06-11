package org.example.popspace.dto.notification;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto {
    private Long notifyId;
    private Long popupId;
    private String title;
    private String content;
    private String imageUrl;
    private Date createdAt;
    private String notificationState;
}