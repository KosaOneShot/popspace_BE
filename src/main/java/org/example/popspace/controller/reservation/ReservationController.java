package org.example.popspace.controller.reservation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.reservation.ReservationListRequestDto;
import org.example.popspace.dto.reservation.ReservationListResponseDto;
import org.example.popspace.service.reservation.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    /* 예약 목록 조회
    * 입력 : 날짜, 제목, 예약타입, 정렬(x)
    *  */
    @GetMapping("/list")
    public ResponseEntity<List<ReservationListResponseDto>> findReservationListByMemberId(
            @ModelAttribute ReservationListRequestDto dto, @AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("/api/reservation/list - {}", dto.toString());
        List<ReservationListResponseDto> responseDto = reservationService.findReservationListByMemberId(
                dto.getSearchKeyword(), dto.getSearchDate(), dto.getReservationType(), userDetail.getId());
        log.info("조회된 예약 목록 개수: {}", responseDto.size());
        return ResponseEntity.ok(responseDto);
    }
    /* 예약 상세 조회 */

}
