package org.example.popspace.controller.popup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.popup.LikeUpdateRequestDto;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.dto.popup.ReservationDto;
import org.example.popspace.dto.popup.ReviewDto;
import org.example.popspace.service.popup.PopupDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/popup/detail")
@RequiredArgsConstructor
public class PopupDetailController {
    private final PopupDetailService popupDetailService;

    /* 팝업 상세 페이지 조회 (상세, 리뷰) */
    @GetMapping("/info-review")
    public ResponseEntity<Map<String, Object>> infoAndReview() {
        Long popupId = 4L; // 예시 ID TODO : spring security
        PopupInfoDto popupInfo = popupDetailService.findPopupInfoByPopupId(popupId);
        List<ReviewDto> reviewDtoList = popupDetailService.findReviewByPopupId(popupId);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("popupInfo", popupInfo);
        responseMap.put("reviewList", reviewDtoList);

        return ResponseEntity.ok(responseMap);
    }

    /* 팝업 상세 페이지 조회 (예약, 찜) */
    @GetMapping("/reserve-like")
    public ResponseEntity<Map<String, Object>> reserveAndLike() {
        // TODO : spring security
        Long popupId = 4L;
        Long memberId = 3L;

        boolean isPopupLike = popupDetailService.findPopupLikeByPopupIdMemberId(popupId, memberId);
        ReservationDto reservation = popupDetailService.findReservationByPopupIdMemberId(popupId, memberId);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("isPopupLike", isPopupLike);
        responseMap.put("reservation", reservation);

        return ResponseEntity.ok(responseMap);
    }

    /* 찜
     입력 : popupId, memberId, toBeState
     merge 쿼리 -> 존재하면 update, 없으면 생성
     */
    @PostMapping("/like-update")
    public ResponseEntity<Map<String, Object>> updatePopupLike() {
        // TODO : spring security (RequestBody 로 변경)
        Long popupId = 4L;
        Long memberId = 1L;
        boolean isLiked = true;

        popupDetailService.updatePopupLike(memberId, popupId, isLiked);
        boolean likeState = popupDetailService.findPopupLikeByPopupIdMemberId(popupId, memberId); // 찜 상태 확인
        System.out.println("상태 : " + likeState);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("isLiked", likeState);
        return ResponseEntity.ok(responseMap);
    }

}
