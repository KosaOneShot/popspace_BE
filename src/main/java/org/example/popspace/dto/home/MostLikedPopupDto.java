package org.example.popspace.dto.home;

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
public class MostLikedPopupDto {
	private Long popupId;
	private String popupName;
	private String imageUrl;
	private int likeCount;
}
