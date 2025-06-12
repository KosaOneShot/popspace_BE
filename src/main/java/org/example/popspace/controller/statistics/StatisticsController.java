package org.example.popspace.controller.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.popup.PopupDetailForAdminResponse;
import org.example.popspace.dto.popup.PopupDetailResponse;
import org.example.popspace.dto.statistics.PopupStatisticsResponse;
import org.example.popspace.service.popup.PopupService;
import org.example.popspace.service.statics.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final PopupService popupService;

    @PreAuthorize("hasAnyRole('POPUP_ADMIN','ADMIN')")
    @GetMapping("/popup-admin/popup/list")
    public ResponseEntity<List<PopupDetailResponse>> getPopupList(@AuthenticationPrincipal CustomUserDetail user) {

        List<PopupDetailResponse> popupList =popupService.getPopupList(user.getId());
        log.info(popupList.toString());
        return ResponseEntity.ok(popupList);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/popup/list")
    public ResponseEntity<List<PopupDetailForAdminResponse>> getAllPopupListForAdmin() {

        List<PopupDetailForAdminResponse> popupList =popupService.getAllPopupListForAdmin();

        return ResponseEntity.ok(popupList);
    }

    //이후 활성화
    @PreAuthorize("hasAnyRole('POPUP_ADMIN','ADMIN')")
    @GetMapping("/popup-admin/popup/statistics/{popupId}")
    public ResponseEntity<PopupStatisticsResponse> getStatisticsData(@PathVariable Long popupId) {

        log.info("getStatisticsData");
        PopupStatisticsResponse popupStatisticsResponse = statisticsService.findPopupStatistics(popupId);

        return ResponseEntity.ok(popupStatisticsResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/popup/statistics/{popupId}")
    public ResponseEntity<PopupStatisticsResponse> getStatisticsDataForAdmin(@PathVariable Long popupId) {

        log.info("getStatisticsData");
        PopupStatisticsResponse popupStatisticsResponse = statisticsService.findPopupStatistics(popupId);

        return ResponseEntity.ok(popupStatisticsResponse);
    }
}
