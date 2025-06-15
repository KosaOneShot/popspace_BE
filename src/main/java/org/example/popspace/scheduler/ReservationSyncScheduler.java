package org.example.popspace.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.reservation.ReservationDateKeyInfo;
import org.example.popspace.dto.reservation.ReservationKeyInfo;
import org.example.popspace.service.reservation.ReservationSyncService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationSyncScheduler {

    private final ReservationSyncService reservationSyncService;

    @Scheduled(fixedDelay = 5 * 60 * 1000) // 5분마다
    public void syncRedisWithDb() {
        log.info("sync redis with DB");

        // 시간대별 사전 예약 수
        List<ReservationKeyInfo> advanceTargets = reservationSyncService.getAdvanceSyncTargets();
        for (ReservationKeyInfo info : advanceTargets) {
            reservationSyncService.syncCount(info.popupId(), info.reserveDate(), info.reserveTime());
        }

        // 해당 날짜 예약한 멤버들
        List<ReservationDateKeyInfo> memberTargets = reservationSyncService.getReservedMemberSyncTargets();
        for (ReservationDateKeyInfo info : memberTargets) {
            reservationSyncService.syncReservedMembers(info.popupId(), info.reserveDate());
        }
    }
}