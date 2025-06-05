package org.example.popspace.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponseDto {
    private int notifyId;
    private int popupId;
    private String title;
    private String content;
    private String imageUrl;
}