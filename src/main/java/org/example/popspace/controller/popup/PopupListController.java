package org.example.popspace.controller.popup;

import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.popup.PopupListDto;
import org.example.popspace.service.popup.PopupListService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/popup")
@RequiredArgsConstructor
public class PopupListController {
    private final PopupListService popupListService;

    // 전체 목록 조회
    @GetMapping("/list")
    public PopupListDto getPopupList(@AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestParam (required = false) String searchKeyword,
            @RequestParam (required = false) String searchDate,
            @RequestParam (required = false) String sortKey) throws ParseException {

        log.info("/popup/list : searchKeyword={}, searchDate={}, sortKey={}", searchKeyword, searchDate, sortKey);
        PopupListDto popupListDto = PopupListDto.builder()
                .popupList(popupListService.getPopupList(userDetail.getId(), searchKeyword, searchDate, sortKey))
                .build();

        log.info("조회된 팝업 개수: {}", popupListDto.getPopupList().size());
        return popupListDto;
    }
}
