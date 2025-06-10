package org.example.popspace.dto.popup;

import java.time.LocalDate;
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
    private LocalDate startDate;
    private LocalDate endDate;
    private String imageUrl;
    private String likeState; // 로그인 유저가 해당 팝업을 좋아요 눌렀는지 여부
}
