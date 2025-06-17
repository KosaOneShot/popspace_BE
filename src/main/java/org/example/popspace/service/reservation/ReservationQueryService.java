package org.example.popspace.service.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.reservation.AvailableDateResponseDTO;
import org.example.popspace.dto.reservation.AvailableTimeResponseDTO;
import org.example.popspace.dto.reservation.ReservationPopupInfoDTO;
import org.example.popspace.dto.reservation.TimeSlotDTO;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReservationMapper;
import org.example.popspace.mapper.redis.ReservationRedisRepository;
import org.example.popspace.util.reservation.RedisKeyUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


/**
조회 화면 (캘린더, 시간대 등) 전용
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationQueryService {

    private final ReservationRedisRepository redisRepo;
    private final ReservationMapper reservationMapper;

    private static final List<String> TIME_SLOTS = List.of(
            "10:00", "11:00", "12:00", "13:00", "14:00",
            "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00"
    );
    private final RedisKeyUtil redisKeyUtil;

    // 달력에서 사용할 예약 가능 날짜 및 마감된 날짜
    public AvailableDateResponseDTO getAvailableDates(long popupId) {
        log.info("Get available date for popup " + popupId);

        ReservationPopupInfoDTO popup = reservationMapper.findPopupTimeById(popupId)
                .orElseThrow(() -> new CustomException(ErrorCode.POPUP_NOT_FOUND));

        LocalDate start = popup.getStartDate();
        LocalDate end = popup.getEndDate();
        // 시간대별 최대 예약 수
        int max = popup.getMaxReservations();

        List<LocalDate> availableDates = new ArrayList<>();
        List<LocalDate> fullyBookedDates = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            boolean allFull = true;
            for (String time : TIME_SLOTS) {
                String key = redisKeyUtil.advanceCountKey(popupId, date, time);
                String countStr = redisRepo.getCount(key);
                int count = (countStr != null) ? Integer.parseInt(countStr) : 0;
                if (count < max) {
                    allFull = false;
                    break;
                }

            }
            if (allFull) {
                fullyBookedDates.add(date);
            }else{
                availableDates.add(date);
            }
        }

        AvailableDateResponseDTO response = new AvailableDateResponseDTO();
        response.setAvailableDates(availableDates);
        response.setFullyBookedDates(fullyBookedDates);
        return response;
    }

    // 선택한 날짜의 예약 가능 시간대
    public AvailableTimeResponseDTO getAvailableTimes(long popupId, LocalDate date) {
        log.info("Get available time slots for popupId={} on date={}", popupId, date);

        ReservationPopupInfoDTO popup = reservationMapper.findPopupTimeById(popupId)
                .orElseThrow(() -> new CustomException(ErrorCode.POPUP_NOT_FOUND));

        int max = popup.getMaxReservations();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        LocalTime open = LocalTime.parse(popup.getOpenTime());
        LocalTime close = LocalTime.parse(popup.getCloseTime());

        List<TimeSlotDTO> times = new ArrayList<>();

        for (String time : TIME_SLOTS) {
            LocalTime slotTime = LocalTime.parse(time);

            // 운영 시간 범위에 포함되지 않으면 건너뜀
            if (slotTime.isBefore(open) || !slotTime.isBefore(close)) {
                continue;
            }

            boolean isPast = date.isBefore(today) || (date.isEqual(today) && slotTime.isBefore(now));
            boolean isFull = false;

            if (!isPast) {
                String key = redisKeyUtil.advanceCountKey(popupId, date, time);
                String countStr = redisRepo.getCount(key);
                int count = (countStr != null) ? Integer.parseInt(countStr) : 0;
                isFull = count >= max;
            }

            TimeSlotDTO slot = new TimeSlotDTO();
            slot.setTime(time);
            slot.setAvailable(!isPast && !isFull);

            times.add(slot);
        }

        AvailableTimeResponseDTO response = new AvailableTimeResponseDTO();
        response.setDate(date);
        response.setTimes(times);
        return response;
    }
}