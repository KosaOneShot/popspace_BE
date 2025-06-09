package org.example.popspace.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PendingReviewDto {
    private Long reserveId;
    private String title;
    private String visitedDate;
    private String imageUrl;
}