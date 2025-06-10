package org.example.popspace.service.popup;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.dto.popup.ReservationDto;
import org.example.popspace.dto.popup.ReviewDto;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.PopupMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PopupDetailService {
    private final PopupMapper popupMapper;

    /* 팝업 상세 */
    public PopupInfoDto findPopupInfoByPopupId(Long popupId){
        Optional<PopupInfoDto> popupInfoDto = popupMapper.findPopupInfoByPopupId(popupId);
        System.out.println();
        return popupInfoDto.orElseThrow(() -> new CustomException(ErrorCode.POPUP_NOT_FOUND));
    }
    /* 리뷰들 */
    public List<ReviewDto> findReviewByPopupId(Long popupId){
        return popupMapper.findReviewByPopupId(popupId);
    }

    /* 찜 여부 */
    public boolean findPopupLikeByPopupIdMemberId(Long popupId, Long memberId){
        String popupLike = popupMapper.findPopupLikeByPopupIdMemberId(popupId, memberId);
        return "Y".equals(popupLike);
    }

    /* 예약 여부 */
    public ReservationDto findReservationByPopupIdMemberId(Long popupId, Long memberId){
        Optional<ReservationDto> reservation = popupMapper.findReservationByPopupIdMemberId(popupId, memberId);
        return reservation.orElse(null); // 예약이 없을 수 있음
    }

    /* 찜 업데이트 */
    public void updatePopupLike(Long memberId, Long popupId, boolean isLiked) {
        String toBeState = isLiked ? "Y" :  "N";
        int updatedRows = popupMapper.upsertPopupLike(memberId, popupId, toBeState);
        if (updatedRows == 0) throw new CustomException(ErrorCode.UPDATE_ERROR);
    }
}

