package org.example.popspace.controller.popup;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.dto.popup.ReviewDto;
import org.example.popspace.service.popup.PopupDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/popup/detail")
@RequiredArgsConstructor
public class PopupDetailController {
    private final PopupDetailService popupDetailService;

    /* 팝업 상세 페이지 조회 (상세, 리뷰) */
    @GetMapping("/info-review")
    public ResponseEntity<Map<String, Object>> test(HttpServletRequest request) {
        log.info("도착했다!!!");
        Long popupId = 4L; // 예시 ID

        PopupInfoDto popupInfo = popupDetailService.findPopupInfoByPopupId(popupId);
        List<ReviewDto> reviewDtoList = popupDetailService.findReviewByPopupId(popupId);

        Map<String, Object> responseMap = Map.of(
            "popupInfo", popupInfo,
            "reviewList", reviewDtoList!=null ? reviewDtoList : List.of()
        );
        return ResponseEntity.ok(responseMap);
    }

    /* 팝업 상세 페이지 조회 (예약, 찜) */
}
