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
public class PopupListDto {
    private Long popupId;
    private String popupName;
    private String location;
    private String startDate;
    private String endDate;
    private String imageUrl;
    private Boolean likeState; // 로그인 유저가 해당 팝업을 좋아요 눌렀는지 여부
}
