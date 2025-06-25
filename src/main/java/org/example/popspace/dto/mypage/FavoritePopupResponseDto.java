package org.example.popspace.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoritePopupResponseDto {
    private Long popupId;
    private String title;
    private String dateRange;
    private String location;
    private String imageUrl;
}