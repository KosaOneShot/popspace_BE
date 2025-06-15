package org.example.popspace.mapper;

import org.apache.ibatis.annotations.*;
import org.example.popspace.dto.qr.QrReservationDTO;
import org.example.popspace.dto.qr.ReservationStatusDTO;
import org.example.popspace.dto.reservation.*;

import java.time.LocalDate;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Mapper
public interface ReservationMapper {

    // 예약 아이디로 예약 찾기
    @Select("""
            SELECT r.reserve_id, r.reservation_type, r.reservation_state, r.reserve_date, r.reserve_time, 
                   p.popup_name, m.member_name
            FROM reservation r
            JOIN member m ON r.member_id = m.member_id
            JOIN popup p ON r.popup_id = p.popup_id
            WHERE r.reserve_id = #{reserveId}
            """)
    Optional<QrReservationDTO> findByReserveId(Long reserveId);

    @Select("""
    SELECT reserve_id, member_id, popup_id, 
           reserve_date, reserve_time, 
           reservation_type, reservation_state
    FROM reservation
    WHERE reserve_id = #{reserveId}
    """)
    Optional<ReservationDTO> findReservationById(Long reserveId);

    // 예약 아이디로 예약한 팝업의 사장 찾기
    @Select("""
    SELECT p.member_id
    FROM reservation r
    JOIN popup p ON r.popup_id = p.popup_id
    WHERE r.reserve_id = #{reserveId}
    """)
    Optional<Long> findPopupOwnerIdByReserveId(Long reserveId);

    // 예약 아이디로 예약 상태 찾기
    @Select("SELECT reserve_id, reservation_state, reserve_date, reserve_time FROM reservation WHERE reserve_id = #{reserveId}")
    ReservationStatusDTO findReservationStatus(@Param("reserveId") Long reserveId);

    // 예약 상태 변경 (입퇴장)
    @Update("UPDATE reservation SET reservation_state = #{state} WHERE reserve_id = #{reserveId}")
    int updateReservationState(@Param("reserveId") Long reserveId, @Param("state") String state);

    // 시간대별 사전 예약 수 조회 (redis fallback)
    @Select("""
        SELECT COUNT(*)
        FROM RESERVATION
        WHERE POPUP_ID = #{popupId}
          AND RESERVATION_STATE = 'RESERVED'
          AND RESERVE_DATE = #{reserveDate}
          AND RESERVE_TIME = #{reserveTime, jdbcType=VARCHAR}
    """)
    int countConfirmedAdvance(@Param("popupId") Long popupId,
                              @Param("reserveDate") LocalDate reserveDate,
                              @Param("reserveTime") String reserveTime);

    // 웨이팅 수 조회
    @Select("""
        SELECT COUNT(*)
        FROM RESERVATION
        WHERE POPUP_ID = #{popupId}
          AND RESERVE_DATE = #{reserveDate}
          AND RESERVATION_TYPE = 'WALK_IN'
          AND RESERVATION_STATE = 'RESERVED'
    """)
    int countConfirmedWalkIn(@Param("popupId") Long popupId,
                             @Param("reserveDate") LocalDate reserveDate);


    // 예약 페이지 생성에 필요한 팝업 정보 (날짜, 시간)
    @Select("""
    SELECT popup_id, popup_name, start_date, end_date,
           open_time,
           close_time,
           max_reservations
    FROM popup
    WHERE popup_id = #{popupId}
    """)
    Optional<ReservationPopupInfoDTO> findPopupTimeById(Long popupId);

    // 팝업 예약 max 값
    @Select("""
    SELECT max_reservations
    FROM popup
    WHERE popup_id = #{popupId}
    """)
    int findPopupMaxById(Long popupId);

    // 예약 생성
    @Insert("""
    INSERT INTO reservation (
        reserve_id,
        popup_id,
        member_id,
        reserve_date,
        reserve_time,
        reservation_type,
        reservation_state,
        created_at
    )
    VALUES (
        #{reserveId},
        #{popupId},
        #{memberId},
        #{reserveDate},
        #{reserveTime, jdbcType=VARCHAR},
        #{reservationType},
        #{reservationState},
        sysdate
    )
    """)
    @SelectKey(statement = "SELECT SEQ_RESERVE_ID.NEXTVAL FROM dual", keyProperty = "reserveId", before = true, resultType = Long.class)
    void insertReservation(ReservationCreateDTO reservation);


    // 사전 예약 취소, 웨이팅은 취소 불가능
    @Update("""
    UPDATE reservation
    SET reservation_state = 'CANCELED',
        canceled_at = sysdate
    WHERE reserve_id = #{reserveId}
    """)
    void cancelReservation(Long reserveId);

    // 팝업 아이디로 존재 확인
    @Select("SELECT COUNT(*) FROM popup WHERE popup_id = #{popupId}")
    int countPopup(@Param("popupId") Long popupId);

    // 중복 예약 막기 위한 (redis fallback)
    // reserve_date 기준 CHECKED_IN, CHECKED_OUT, RESERVED 인 member_id
    @Select("""
    SELECT COUNT(*)
    FROM reservation
    WHERE popup_id = #{popupId}
      AND member_id = #{memberId}
      AND reserve_date = #{reserveDate}
      AND reservation_state IN ('CHECKED_IN', 'CHECKED_OUT', 'RESERVED')
    """)
    boolean isReserved(@Param("memberId") Long memberId,
                       @Param("popupId") Long popupId,
                       @Param("reserveDate") LocalDate reserveDate);


    @Select("""
    SELECT member_id
    FROM reservation
    WHERE popup_id = #{popupId}
      AND reserve_date = #{reserveDate}
      AND reservation_type = 'ADVANCE'
      AND reservation_state IN ('CHECKED_IN', 'CHECKED_OUT', 'RESERVED')
    """)
    List<Long> findReservedMemberIds(@Param("popupId") Long popupId,
                                     @Param("reserveDate") LocalDate reserveDate);

    @Select("""
    SELECT DISTINCT popup_id, reserve_date, reserve_time
    FROM reservation
    WHERE reservation_type = 'ADVANCE'
      AND reservation_state IN ('RESERVED')
    """)
    List<ReservationKeyInfo> findAdvanceCountSyncTargets();


    @Select("""
    SELECT DISTINCT popup_id, reserve_date
    FROM reservation
    WHERE reservation_state IN ('RESERVED', 'CHECKED_IN', 'CHECKED_OUT')
    """)
    List<ReservationDateKeyInfo> findReservedMemberSyncTargets();



    @Select("""
            select count(r.RESERVE_ID)+1
            from RESERVATION r
            where r.POPUP_ID= #{popupId}
              and r.RESERVATION_TYPE = 'WALK_IN'
              and r.RESERVATION_STATE = 'RESERVED'
              and r.RESERVE_DATE= #{now}
              and r.RESERVE_ID < #{reservationId}
            """)
    int countReservedBeforeMe(LocalDate now,Long reservationId, Long popupId);

    @Select("""
            select count(r.RESERVE_ID)+1
            from RESERVATION r
            where r.POPUP_ID= #{popupId}
              and r.RESERVATION_TYPE = 'WALK_IN'
              and r.RESERVATION_STATE = 'RESERVED'
              AND r.RESERVE_DATE =#{now}
            """)
    int countReservedAll(LocalDate now,Long popupId);

    @Select("""
            SELECT
                ROUND(AVG((el.CREATED_AT - r.CREATED_AT) * 24 * 60))
            FROM RESERVATION r
            JOIN ENTRANCE_LOG el ON el.RESERVE_ID = r.RESERVE_ID AND el.ENTRANCE_STATE = 'CHECKED_IN'
            WHERE r.POPUP_ID = #{popupId}
              AND r.RESERVATION_TYPE = 'WALK_IN'
              AND r.RESERVATION_STATE IN ('CHECKED_IN', 'CHECKED_OUT')
              AND r.RESERVE_DATE =#{now}
            """)
    Optional<Integer> averageWaitingTime(LocalDate now,Long popupId);

}