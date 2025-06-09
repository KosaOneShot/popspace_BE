package org.example.popspace.dto.mypage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {
    private Long reserveId;
    private int rating;
    private String content;
}