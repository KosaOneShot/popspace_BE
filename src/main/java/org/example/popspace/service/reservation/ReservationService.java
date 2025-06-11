package org.example.popspace.service.reservation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.reservation.ReservationListResponseDto;
import org.example.popspace.mapper.ReservationMapper_hyeesw;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationMapper_hyeesw reservationMapper;

    /* 예약 목록 조회 */
    public List<ReservationListResponseDto> findReservationListByMemberId(String searchKeyword, LocalDate searchDate,
                                                                          String reservationType, Long memberId) {
        return reservationMapper.findReservationListByMemberId(searchKeyword, searchDate, reservationType, memberId);
    }
}
