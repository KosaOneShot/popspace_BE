package org.example.popspace.controller.popup;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.popup.*;
import org.example.popspace.service.popup.PopupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/popup")
@RequiredArgsConstructor
@Slf4j
public class PopupController {

    private final PopupService popupService;

    /* 팝업 상세 */
    @GetMapping("/detail/{popupId}")
    public ResponseEntity<PopupInfoDto> getInfo(@PathVariable Long popupId) {
        PopupInfoDto dto = popupService.findPopupInfoAndReviewsByPopupId(popupId);
        return ResponseEntity.ok(dto);
    }

    /* 팝업 리뷰 */
    // @GetMapping("/review/{popupId}")
    // public ResponseEntity<List<PopupReviewDto>> getReviews(@PathVariable Long popupId) {
    //     List<PopupReviewDto> dto = popupService.findReviewsByPopupId(popupId);
    //     return ResponseEntity.ok(dto);
    // }

    @GetMapping("/review")
    public ResponseEntity<ReviewPaginationResponseDto> getReviews(@ModelAttribute ReviewPaginationRequestDto dto) {
        List<ReviewDto> reviewList = popupService.getPopupReviewsByPagination(dto);
        int totalCount = popupService.getTotalReviewCountByPopupId(dto.getPopupId());
        ReviewPaginationResponseDto response = ReviewPaginationResponseDto.builder()
                .reviewList(reviewList)
                .totalCount(totalCount)
                .build();

        log.info(response.toString());
        return ResponseEntity.ok(response);
    }


    /* 찜 여부 */
    @GetMapping("/like/{popupId}")
    public ResponseEntity<LikeResponseDto> reserveAndLike(@PathVariable Long popupId,
                                               @AuthenticationPrincipal CustomUserDetail userDetail) {
        LikeResponseDto dto = popupService.findPopupLikeByPopupIdMemberId(popupId, userDetail.getId());
        return ResponseEntity.ok(dto);
    }

    /* 찜 상태 업데이트
     입력 : popupId, memberId, toBeState
     merge 쿼리 -> 존재하면 update, 없으면 생성
     */
    @PostMapping("/like-update")
    public ResponseEntity<Map<String, String>> updatePopupLike(@RequestBody LikeUpdateRequestDto dto,
                                               @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("popup/detail/like-update: popupId={}, memberId={}, toBeState={}", dto.getPopupId(), userDetail.getId(), dto.isToBeState());
        popupService.updatePopupLike(dto.getPopupId(), userDetail.getId(), dto.isToBeState());
        return ResponseEntity.ok(Map.of("message", "success"));
    }

    /* 팝업 목록 */
    @GetMapping("/list")
    public ResponseEntity<List<PopupCardDto>> getPopupList(@AuthenticationPrincipal CustomUserDetail userDetail,
                                                           @ModelAttribute PopupSearchDto dto) {
        List<PopupCardDto> list = popupService.getPopupList(userDetail, dto);
        log.info("조회된 팝업 개수: {}", list.size());
        return ResponseEntity.ok(list);
    }
}
