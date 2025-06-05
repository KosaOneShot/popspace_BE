package org.example.popspace.notification.domain;


import lombok.Data;
import java.util.Date;

@Data
public class Notification {
    private Integer notifyId;
    private Integer popupId;
    private String title;
    private String content;
    private String imageUrl;
    private String notificationState;
    private Date createdAt;
}