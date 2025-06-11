package org.example.popspace.service.popup;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.popup.PopupCardDto;
import org.example.popspace.dto.popup.PopupDetailResponseDto;
import org.example.popspace.dto.popup.PopupInfoDto;
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
        Optional<PopupInfoDto> popupInfoDto = popupMapper.findPopupInfoAndReviewsByPopupId(popupId);
        return popupInfoDto.orElseThrow(() -> new CustomException(ErrorCode.POPUP_NOT_FOUND));
    }

    /* 찜 여부 */
    public String findPopupLikeByPopupIdMemberId(Long popupId, Long memberId){
        log.info("findPopupLikeByPopupIdMemberId() popupId: {}, memberId: {}", popupId, memberId);
        return popupMapper.findPopupLikeByPopupIdMemberId(popupId, memberId);
    }

    /* 예약 여부 */
    public ReservationDto findReservationByPopupIdMemberId(Long popupId, Long memberId){
        Optional<ReservationDto> reservation = popupMapper.findReservationByPopupIdMemberId(popupId, memberId);
        return reservation.orElse(null); // 예약이 없을 수 있음
    }

    /* 찜 업데이트 */
    @Transactional
    public void updatePopupLike(Long popupId, Long memberId, boolean isLiked) {
        String toBeState = isLiked ? "ACTIVE" :  "DELETED";
//        String toBeState = "DELETE";
        String before = popupMapper.findPopupLikeByPopupIdMemberId(popupId, memberId);
        if(before == null){
            int row = popupMapper.insertLikeState(popupId, memberId, toBeState);
            if(row == 0) throw new CustomException(ErrorCode.INSERT_ERROR);
        } else {
            if(!before.equals(toBeState)){
                int row = popupMapper.updateLikeState(popupId, memberId, toBeState);
                if(row == 0) throw new CustomException(ErrorCode.INSERT_ERROR);
            }
        }
        String after = popupMapper.findPopupLikeByPopupIdMemberId(popupId, memberId);
        log.info(" !!!!!!!!!! 찜 상태 업데이트 완료: {}, {}, {} == {}", memberId, popupId, toBeState, after);
    }

    /* 팝업 목록 */
    public List<PopupCardDto> getPopupList(Long memberId, String searchKeyword, String searchDateStr, String sortKey) throws ParseException {
        LocalDate searchDate = !"".equals(searchDateStr) ? LocalDate.parse(searchDateStr, DateTimeFormatter.ISO_DATE) : null;
        return popupMapper.findPopupListBySearchKeywordAndDate(memberId, searchKeyword, searchDate, sortKey);
    }
}

