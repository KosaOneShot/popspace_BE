package org.example.popspace.service.popup;

import java.util.List;

import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.popup.LikeResponseDto;
import org.example.popspace.dto.popup.PopupCardDto;
import org.example.popspace.dto.popup.PopupDetailForAdminResponse;
import org.example.popspace.dto.popup.PopupDetailResponse;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.dto.popup.PopupReviewDto;
import org.example.popspace.dto.popup.PopupSearchDto;
import org.example.popspace.dto.popup.ReviewDto;
import org.example.popspace.dto.popup.ReviewPaginationRequestDto;
import org.example.popspace.mapper.PopupMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PopupService {
    private final PopupMapper popupMapper;

    /* 팝업 상세 */
    public PopupInfoDto findPopupInfoAndReviewsByPopupId(Long popupId){
        log.info("findPopupInfoByPopupId() popupId: {}", popupId);
        return popupMapper.findPopupInfoAndReviewsByPopupId(popupId);
    }

    /* 팝업 리뷰 */
    public List<PopupReviewDto> findReviewsByPopupId(Long popupId) {
        log.info("findReviewsByPopupId() popupId: {}", popupId);
        return popupMapper.findReviewsByPopupId(popupId);
    }

    /* 찜 여부 */
    public LikeResponseDto findPopupLikeByPopupIdMemberId(Long popupId, Long memberId){
        log.info("findPopupLikeByPopupIdMemberId() popupId: {}, memberId: {}", popupId, memberId);
        String likeStr = popupMapper.findPopupLikeByPopupIdMemberId(popupId, memberId);
        return LikeResponseDto.builder()
                .isLiked(LikeConvertStringToBoolean(likeStr))
                .build();
    }

    /* 찜 업데이트 */
    @Transactional
    public void updatePopupLike(Long popupId, Long memberId, boolean toBeState) {
        String before = popupMapper.findPopupLikeByPopupIdMemberId(popupId, memberId);

        String toBeStateStr = LikeConvertBooleanToString(toBeState);
        if(before == null){
            popupMapper.insertLikeState(popupId, memberId, toBeStateStr);
            return;
        }
        if(!before.equals(toBeStateStr)){
            popupMapper.updateLikeState(popupId, memberId, toBeStateStr);
        }
    }

    // 찜 상태 boolean -> string
    String LikeConvertBooleanToString(boolean likeState) {
        return likeState ? "ACTIVE" : "DELETED";
    }
    // 찜 상태 string -> boolean
    boolean LikeConvertStringToBoolean(String likeState) {
        return "ACTIVE".equals(likeState);
    }

    public List<PopupDetailResponse> getPopupList(Long memberId) {
        return popupMapper.findAllPopupDetailByMemberId(memberId);
    }


	/* 팝업 목록 */
	public List<PopupCardDto> getPopupList(CustomUserDetail userDetail, PopupSearchDto popupSearchDto) {
		log.info("/popup/list : memberId : {}, dto : {}", userDetail, popupSearchDto.toString());
		return popupMapper.findPopupListBySearchKeywordAndDate(
			userDetail!= null ? userDetail.getId() : null,
			popupSearchDto.getSearchKeyword(),
			popupSearchDto.getSearchDate(),
			popupSearchDto.getSortKey(),
			popupSearchDto.getLastEndDate(),
			popupSearchDto.getLastLikeCnt(),
			popupSearchDto.getLastPopupId()
		);
	}

	public List<PopupDetailForAdminResponse> getAllPopupListForAdmin() {
		return popupMapper.findAllPopupListForAdmin();
	}

	// 리뷰 페이지네이션
	public List<ReviewDto> getPopupReviewsByPagination(ReviewPaginationRequestDto dto) {
		if (dto.getPageNum() < 0 || dto.getPageSize() <= 0) {
			throw new IllegalArgumentException("Invalid pagination parameters");
		}
		int pageOffset = (dto.getPageNum() - 1) * dto.getPageSize();
		log.info("getPopupReviews() popupId: {}, pageNum: {}, pageSize: {}, pageOffset: {}", dto.getPopupId(), dto.getPageNum(), dto.getPageSize(), pageOffset);
		return popupMapper.findReviewsByPopupIdWithPagination(dto.getPopupId(), pageOffset, dto.getPageSize());
	}
	public int getTotalReviewCountByPopupId(Long popupId) {
		log.info("getTotalReviewCountByPopupId() popupId: {}", popupId);
		return popupMapper.countReviewsByPopupId(popupId);
	}
}

