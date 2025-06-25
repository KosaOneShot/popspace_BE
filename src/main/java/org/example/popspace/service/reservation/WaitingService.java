package org.example.popspace.service.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.reservation.CountEntranceDTO;
import org.example.popspace.dto.reservation.ReservationSequenceResponse;
import org.example.popspace.mapper.ReservationMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingService {

    private final ReservationMapper reservationMapper;

    public ReservationSequenceResponse getMyWaitingSequenceInfo(Long reservationId, Long popupId) {

        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime now = LocalDateTime.of(2025, 6, 12, 16, 0);

        int myTurn = reservationMapper.countReservedBeforeMe(now.toLocalDate(), reservationId, popupId);  // RESERVED만 포함
        log.info("[대기자 수] 앞에 입장 안 한 예약자 수 (RESERVED 상태): {}", myTurn);

        CountEntranceDTO countEntrance = calculateEntrance(myTurn, now, popupId);

        return calculateReservation(now, myTurn, popupId, countEntrance);
    }

    public ReservationSequenceResponse getTotalWaitingInfo(Long popupId) {

        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime now = LocalDateTime.of(2025, 6, 15, 15, 22);

        int myTurn = reservationMapper.countReservedAll(now.toLocalDate(), popupId);

        CountEntranceDTO countEntrance = calculateEntrance(myTurn, now, popupId);

        return calculateReservation(now, myTurn, popupId, countEntrance);
    }

    private CountEntranceDTO calculateEntrance(int myTurn, LocalDateTime now, Long popupId) {

        String hour = String.valueOf(now.getHour());

        CountEntranceDTO countEntrance = reservationMapper.countEntrance(now.toLocalDate(), hour, popupId);

        countEntrance.checkIsAllowed(myTurn);

        log.info("countEntrance {}", countEntrance);
        return countEntrance;
    }

    private ReservationSequenceResponse calculateReservation(LocalDateTime now, int myTurn, Long popupId, CountEntranceDTO countEntrance) {

        int averageWaitTime = reservationMapper.averageWaitingTime(now.toLocalDate(), popupId)
                .orElse(-1);
        log.info("[평균 대기 시간] 평균 입장 간격 (분): {}", averageWaitTime);

        LocalTime entranceTime = now.toLocalTime().plusMinutes(myTurn * averageWaitTime);

        return ReservationSequenceResponse.of(myTurn, averageWaitTime, entranceTime, countEntrance);
    }

    //todo
    // 해당 로직의 단점이 몇시에 해도 대기시간*앞에 남은 사람임
    // 이게 싫으면 웨이팅시 앞에 존재하는 웨이팅을 값으로 저장해야함 -> 그러며 여기 로직을 해당 값 + cancel
}
