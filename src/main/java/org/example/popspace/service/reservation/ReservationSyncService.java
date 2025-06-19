package org.example.popspace.service.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.reservation.ReservationDateKeyInfo;
import org.example.popspace.dto.reservation.ReservationKeyInfo;
import org.example.popspace.mapper.ReservationMapper;
import org.example.popspace.mapper.redis.ReservationRedisRepository;
import org.example.popspace.util.reservation.RedisKeyUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationSyncService {

    private final ReservationMapper reservationMapper;
    private final RedisKeyUtil redisKeyUtil;
    private final ReservationRedisRepository redisRepo;
    private final ReservationRedisRepository reservationRedisRepository;


    // 시간대별 사전 예약 수 - advance count
    public void syncAdvanceCount(Long popupId, LocalDate reserveDate, String reserveTime) {
        int count = reservationMapper.countConfirmedAdvance(popupId, reserveDate, reserveTime);
        String key = redisKeyUtil.advanceCountKey(popupId, reserveDate, reserveTime);
        redisRepo.setCount(key, count);
    }

    // 해당 날짜 예약한 멤버들 - member set
    public void syncReservedMembers(Long popupId, LocalDate reserveDate) {
        List<Long> memberIds = reservationMapper.findReservedMemberIds(popupId, reserveDate);

        String key = redisKeyUtil.memberSetKey(popupId, reserveDate);
        redisRepo.clearSet(key);
        redisRepo.addMembersToSet(key, memberIds);
    }


    // 현재 입장 예정 고객 수 - entrance count
    public void syncEntranceCount(Long popupId, LocalDate reserveDate, String reserveTime) {
        String timestampStr = reserveDate.toString() + " " + reserveTime;
        int count = reservationMapper.countConfirmedEntrance(popupId, reserveDate, reserveTime, timestampStr);
        String key = redisKeyUtil.entranceCountKey(popupId, reserveDate, reserveTime);
        redisRepo.setCount(key, count);
    }


    public List<ReservationKeyInfo> getAdvanceSyncTargets() {
        return reservationMapper.findAdvanceCountSyncTargets();
    }

    public List<ReservationKeyInfo> getEntranceSyncTargets() {
        return reservationMapper.findEntranceCountSyncTargets();
    }

    public List<ReservationDateKeyInfo> getReservedMemberSyncTargets() {
        return reservationMapper.findReservedMemberSyncTargets();
    }

}
