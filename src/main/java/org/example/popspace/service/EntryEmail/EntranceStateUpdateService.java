package org.example.popspace.service.EntryEmail;

import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.EntryEmail.Reservation;
import org.example.popspace.mapper.EntryEmailMapper;
import org.example.popspace.mapper.redis.ReservationRedisRepository;
import org.example.popspace.util.reservation.RedisKeyUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EntranceStateUpdateService {

    private final EntryEmailMapper entryEmailMapper;
    private final ReservationRedisRepository redisRepo;
    private final RedisKeyUtil redisKeyUtil;

//    @Transactional
//    public void updateReservations(List<Reservation> reservations) {
//
//        for (Reservation reservation : reservations) {
//            // email-send 처리
//            entryEmailMapper.updateReservationState(reservation.getReserveId(), "EMAIL_SEND");
//
//            String countKey = redisKeyUtil.entranceCountKey(
//                    reservation.getPopupId(), reservation.getReserveDate(), String.valueOf(reservation.getReserveTime())
//            );
//            String lockKey = redisKeyUtil.entranceLockKey(reservation.getPopupId());
//
//            // 락 설정
//            redisRepo.setLock(lockKey, Duration.ofSeconds(60));
//
//            // entrance count 단순 증가
//            redisRepo.incrementCount(countKey);
//        }
//
//        // 락 해제
//        //redisRepo.releaseLock(popupId);
//    }

    @Transactional
    public void updateReservations(List<Reservation> reservations) {
        // 1. popupId + reserveDate + reserveTime 기준으로 그룹핑
        Map<String, List<Reservation>> groupedMap = new HashMap<>();

        for (Reservation res : reservations) {
            String key = res.getPopupId() + "|" + res.getReserveDate() + "|" + res.getReserveTime();

            groupedMap.computeIfAbsent(key, k -> new ArrayList<>()).add(res);
        }

        // 2. 각 그룹별로 처리
        for (Map.Entry<String, List<Reservation>> entry : groupedMap.entrySet()) {
            List<Reservation> group = entry.getValue();
            // redis key 만들기 위함
            Reservation sample = group.get(0);

            Long popupId = sample.getPopupId();
            LocalDate reserveDate = sample.getReserveDate();
            String reserveTime = String.valueOf(sample.getReserveTime());

            String countKey = redisKeyUtil.entranceCountKey(popupId, reserveDate, reserveTime);
            String lockKey = redisKeyUtil.entranceLockKey(popupId);

            // 락 설정
            redisRepo.setLock(lockKey, Duration.ofSeconds(60));

            // 상태 업데이트
            for (Reservation reservation : group) {
                entryEmailMapper.updateReservationState(reservation.getReserveId(), "EMAIL_SEND");
            }

            // 정확한 수로 count 덮어쓰기
            redisRepo.setCount(countKey, group.size());

            // 락 해제
            redisRepo.releaseLock(lockKey);
        }
    }


}