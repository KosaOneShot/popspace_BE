package org.example.popspace.dto.popup;

import java.util.List;

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
public class ReviewPaginationResponseDto {
	private List<ReviewDto> reviewList;
	private int totalCount;
}
