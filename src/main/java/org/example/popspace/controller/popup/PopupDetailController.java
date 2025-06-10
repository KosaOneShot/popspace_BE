package org.example.popspace.controller.popup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.popup.LikeUpdateRequestDto;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.dto.popup.ReviewDto;
import org.example.popspace.service.popup.PopupDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/popup/detail")
@RequiredArgsConstructor
public class PopupDetailController {
    private final PopupDetailService popupDetailService;

    /* 팝업 상세 페이지 조회 (상세, 리뷰) */
    @GetMapping("/info-review/{popupId}")
    public ResponseEntity<Map<String, Object>> infoAndReview(@PathVariable Long popupId) {
        PopupInfoDto popupInfo = popupDetailService.findPopupInfoByPopupId(popupId);
        List<ReviewDto> reviewDtoList = popupDetailService.findReviewByPopupId(popupId);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("popupInfo", popupInfo);
        responseMap.put("reviewList", reviewDtoList);

        return ResponseEntity.ok(responseMap);
    }

    /* 팝업 상세 페이지 조회 (찜) */
    @GetMapping("/reserve-like/{popupId}")
    public ResponseEntity<Map<String, Object>> reserveAndLike(@PathVariable Long popupId,
                                                              @AuthenticationPrincipal CustomUserDetail userDetail) {
        String isPopupLike = popupDetailService.findPopupLikeByPopupIdMemberId(popupId, userDetail.getId());
        boolean isLiked = "ACTIVE".equals(isPopupLike); // 찜 상태 확인

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("isPopupLike", isLiked);

        return ResponseEntity.ok(responseMap);
    }

    /* 찜
     입력 : popupId, memberId, toBeState
     merge 쿼리 -> 존재하면 update, 없으면 생성
     */
    @PostMapping("/like-update")
    public ResponseEntity<Map<String, Object>> updatePopupLike(@RequestBody LikeUpdateRequestDto dto,
                                                               @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("popup/detail/like-update: popupId={}, memberId={}, toBeState={}", dto.getPopupId(), userDetail.getId(), dto.isToBeState());

        popupDetailService.updatePopupLike(dto.getPopupId(), userDetail.getId(), dto.isToBeState());
        String likeState = popupDetailService.findPopupLikeByPopupIdMemberId(dto.getPopupId(), userDetail.getId()); // 찜 상태 확인
        log.info("업데이트된 찜 상태: {}", likeState);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("isLiked", likeState);
        return ResponseEntity.ok(responseMap);
    }

}
