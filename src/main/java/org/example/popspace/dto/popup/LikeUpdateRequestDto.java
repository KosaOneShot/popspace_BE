package org.example.popspace.dto.popup;

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
public class LikeUpdateRequestDto {
    private Long popupId;
    private boolean toBeState; // true: 찜, false: 찜 해제
}
