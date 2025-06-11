package org.example.popspace.controller.popup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.popup.PopupDetailResponse;
import org.example.popspace.service.popup.PopupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/popup")
@RequiredArgsConstructor
@Slf4j
public class PopupController {

    private final PopupService popupService;

    @GetMapping("/list")
    public ResponseEntity<List<PopupDetailResponse>> getPopupList(@AuthenticationPrincipal CustomUserDetail user) {

        List<PopupDetailResponse> popupList =popupService.getPopupList(user.getId());
        log.info(popupList.toString());
        return ResponseEntity.ok(popupList);
    }
}
