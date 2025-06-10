package org.example.popspace.controller.popup;

import java.text.ParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.popup.LikeResponseDto;
import org.example.popspace.dto.popup.LikeUpdateRequestDto;
import org.example.popspace.dto.popup.PopupDetailResponseDto;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.dto.popup.PopupListDto;
import org.example.popspace.dto.popup.ReviewDto;
import org.example.popspace.service.popup.PopupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/popup")
@RequiredArgsConstructor
public class PopupController {
    private final PopupService popupService;

    /* 팝업 상세 페이지 조회 (상세, 리뷰) */
    @GetMapping("/detail/{popupId}")
    public ResponseEntity<PopupDetailResponseDto> infoAndReview(@PathVariable Long popupId) {
        PopupInfoDto popupInfo = popupService.findPopupInfoByPopupId(popupId);
        List<ReviewDto> reviewDtoList = popupService.findReviewByPopupId(popupId);

        PopupDetailResponseDto responseDto = PopupDetailResponseDto.builder()
                .popupInfo(popupInfo)
                .reviewList(reviewDtoList)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    /* 팝업 상세 페이지 조회 (찜) */
    @GetMapping("/like/{popupId}")
    public ResponseEntity<LikeResponseDto> reserveAndLike(@PathVariable Long popupId,
                                                          @AuthenticationPrincipal CustomUserDetail userDetail) {
        String isPopupLike = popupService.findPopupLikeByPopupIdMemberId(popupId, userDetail.getId());
        boolean isLiked = "ACTIVE".equals(isPopupLike); // 찜 상태 확인

        LikeResponseDto likeResponseDto = LikeResponseDto.builder()
                .popupLike(isLiked)
                .build();

        return ResponseEntity.ok(likeResponseDto);
    }

    /* 찜 상태 업데이트
     입력 : popupId, memberId, toBeState
     merge 쿼리 -> 존재하면 update, 없으면 생성
     */
    @PostMapping("/like-update")
    public ResponseEntity<LikeResponseDto> updatePopupLike(@RequestBody LikeUpdateRequestDto dto,
                                                               @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("popup/detail/like-update: popupId={}, memberId={}, toBeState={}", dto.getPopupId(), userDetail.getId(), dto.isToBeState());

        popupService.updatePopupLike(dto.getPopupId(), userDetail.getId(), dto.isToBeState());
        String isPopupLike = popupService.findPopupLikeByPopupIdMemberId(dto.getPopupId(), userDetail.getId()); // 찜 상태 확인
        log.info("업데이트된 찜 상태: {}", isPopupLike);

        boolean isLiked = "ACTIVE".equals(isPopupLike); // 찜 상태 확인
        LikeResponseDto likeResponseDto = LikeResponseDto.builder()
                .popupLike(isLiked)
                .build();

        return ResponseEntity.ok(likeResponseDto);
    }

    /* 팝업 목록 */
    @GetMapping("/list")
    public PopupListDto getPopupList(@AuthenticationPrincipal CustomUserDetail userDetail,
                                     @RequestParam(required = false) String searchKeyword,
                                     @RequestParam (required = false) String searchDate,
                                     @RequestParam (required = false) String sortKey) throws ParseException {

        log.info("/popup/list : searchKeyword={}, searchDate={}, sortKey={}", searchKeyword, searchDate, sortKey);
        PopupListDto popupListDto = PopupListDto.builder()
                .popupList(popupService.getPopupList(userDetail.getId(), searchKeyword, searchDate, sortKey))
                .build();

        log.info("조회된 팝업 개수: {}", popupListDto.getPopupList().size());
        return popupListDto;
    }
}
