package org.example.popspace.dto.popup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PopupLikeDto {
    private Long memberId;
    private Long popupId;
    private Long likeId;
    private String likeState;
}
