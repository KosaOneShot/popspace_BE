package org.example.popspace.dto.notification;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class NotificationRequestDto {
    private int popupId;
    private String title;
    private String content;
    private MultipartFile image;  // 업로드된 이미지 파일
}
