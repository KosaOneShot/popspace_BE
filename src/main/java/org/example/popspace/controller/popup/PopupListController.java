package org.example.popspace.controller.popup;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.popup.PopupListDto;
import org.example.popspace.service.popup.PopupListService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/popup")
@RequiredArgsConstructor
public class PopupListController {
    private final PopupListService popupListService;

    // 전체 목록 조회
    @GetMapping("/list")
    public Map<String, Object> getPopupList() throws ParseException {
        // TODO : spring security
        Long memberId = 3L; // 예시 ID
        String searchKeyword = ""; // 검색어
        String searchDateStr = "2025-06-06"; // 검색 날짜

        List<PopupListDto> list = popupListService.getPopupList(memberId, searchKeyword, searchDateStr);
        log.info("조회된 팝업 개수: {}", list.size());
        return Map.of("popupList", list);
    }
}
