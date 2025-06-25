package org.example.popspace.dto.notification;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDto {
    private Long popupId;
    private String title;
    private String content;
}
