package org.example.popspace.notification.dto;

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