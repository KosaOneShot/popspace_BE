package org.example.popspace.dto.popup;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReviewDto {
    private Long popupId;
    private Long memberId;
    private Long reviewId;
    private int rating;
    private String content;
    private LocalDateTime createdAt;
}
