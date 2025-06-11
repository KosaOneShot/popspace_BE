package org.example.popspace.controller.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.statistics.PopupStatisticsResponse;
import org.example.popspace.service.statics.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    //이후 활성화
    @PreAuthorize("hasAnyRole('POPUP_ADMIN')")
    @GetMapping("/popup-admin/statistics/{popupId}")
    public ResponseEntity<PopupStatisticsResponse> getStatisticsData(@PathVariable Long popupId) {

        log.info("getStatisticsData");
        PopupStatisticsResponse popupStatisticsResponse = statisticsService.findPopupStatistics(popupId);

        return ResponseEntity.ok(popupStatisticsResponse);
    }
}
