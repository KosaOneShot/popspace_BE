package org.example.popspace.service.popup;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.popup.LikeResponseDto;
import org.example.popspace.dto.popup.PopupCardDto;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.dto.popup.PopupReviewDto;
import org.example.popspace.dto.popup.ReservationDto;
import org.example.popspace.dto.popup.ReviewDto;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.PopupMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /* 팝업 목록 */
    public List<PopupCardDto> getPopupList(Long memberId, String searchKeyword, LocalDate searchDate, String sortKey) {
        return popupMapper.findPopupListBySearchKeywordAndDate(memberId, searchKeyword, searchDate, sortKey);
    }
}

