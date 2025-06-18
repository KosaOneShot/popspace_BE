package org.example.popspace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.popspace.dto.EntryEmail.Reservation;
import org.example.popspace.dto.qr.QrReservationDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface EntryEmailMapper {

    @Select("""
        SELECT R.reserve_id, R.popup_id, R.member_id, 
               R.reserve_date, R.reserve_time,
               R.reservation_type, R.reservation_state,
               M.email
        FROM RESERVATION R
        JOIN MEMBER M ON R.member_id = M.member_id
        WHERE R.popup_id = #{popupId}
          AND R.reserve_date = #{reserveDate}
          AND R.reserve_time = #{reserveTime}
          AND R.reservation_type = 'ADVANCE'
          AND R.reservation_state = 'RESERVED'
    """)
    List<Reservation> selectAdvanceReservations(
            @Param("popupId") Long popupId,
            @Param("reserveDate") LocalDate reserveDate,
            @Param("reserveTime") String reserveTime
    );

    @Update("""
    MERGE INTO RESERVATION T
    USING (
        SELECT reserve_id
        FROM (
            SELECT reserve_id
            FROM RESERVATION
            WHERE popup_id = #{popupId}
              AND reserve_date = #{reserveDate}
              AND reservation_type = 'WALK-IN'
              AND reservation_state = 'RESERVED'
            ORDER BY reserve_id
            FETCH FIRST #{limit} ROWS ONLY
        )
    ) S
    ON (T.reserve_id = S.reserve_id)
    WHEN MATCHED THEN
    UPDATE SET reservation_state = 'EMAIL_PENDING'
    """)
    int updateWaitingReservationsToPending(
            @Param("popupId") Long popupId,
            @Param("reserveDate") LocalDate reserveDate,
            @Param("limit") int limit
    );

    @Select("""
        SELECT R.reserve_id, R.popup_id, R.member_id, R.reserve_date, R.reserve_time, R.reservation_type, R.reservation_state, M.email
        FROM RESERVATION R
        JOIN MEMBER M ON R.member_id = M.member_id
        WHERE popup_id = #{popupId}
        AND reserve_date = #{reserveDate}
        AND reservation_type = 'WALK-IN'
        AND reservation_state = 'EMAIL_PENDING'
        ORDER BY reserve_id
    """)
    List<Reservation> selectPendingReservations(
            @Param("popupId") Long popupId,
            @Param("reserveDate") LocalDate reserveDate
    );

    @Update("""
    UPDATE RESERVATION
    SET 
        reservation_state = #{reservationState},
        reserve_time = CASE 
            WHEN reserve_time IS NULL THEN #{reserveTime}
            ELSE reserve_time
        END
    WHERE reserve_id = #{reserveId}
""")
    void updateReservationState(
            @Param("reserveId") Long reserveId,
            @Param("reservationState") String reservationState,
            @Param("reserveTime") String reserveTime
    );

    @Select("""
        SELECT R.reserve_id
        FROM RESERVATION R
        WHERE R.popup_id = #{popupId}
          AND R.reserve_date = #{reserveDate}
          AND (
            (R.reservation_type = 'ADVANCE' AND R.reserve_time = #{reserveTime})
            OR
            (R.reservation_type = 'WALK-IN')
          )
          AND R.reservation_state = 'EMAIL_SEND'
          AND NOT EXISTS (
              SELECT 1 FROM ENTRANCE_LOG E
              WHERE E.reserve_id = R.reserve_id
          )
    """)
    List<Long> selectNoShowCandidates(
            @Param("popupId") Long popupId,
            @Param("reserveDate") LocalDate reserveDate,
            @Param("reserveTime")
            String reserveTime
    );

    //checked_in했는데 checked_out은 안한 인원
    @Select("""
    SELECT COUNT(*)
    FROM ENTRANCE_LOG EL
    JOIN RESERVATION R ON EL.reserve_id = R.reserve_id
    WHERE EL.popup_id = #{popupId}
      AND R.reserve_date = #{reserveDate}
      AND EL.entrance_state = 'CHECKED_IN'
      AND EL.created_at BETWEEN TO_DATE(TO_CHAR(R.reserve_date, 'YYYY-MM-DD') || ' ' || #{reserveTime}, 'YYYY-MM-DD HH24:MI')
                           AND TO_DATE(TO_CHAR(R.reserve_date, 'YYYY-MM-DD') || ' ' || #{reserveTime}, 'YYYY-MM-DD HH24:MI') + INTERVAL '10' MINUTE
      AND NOT EXISTS (
          SELECT 1
          FROM ENTRANCE_LOG sub
          WHERE sub.reserve_id = EL.reserve_id
            AND sub.entrance_state = 'CHECKED_OUT'
      )
    """)
    int countConfirmedReservations(
            @Param("popupId") Long popupId,
            @Param("reserveDate") LocalDate reserveDate,
            @Param("reserveTime") String reserveTime
    );

    @Update("""
    UPDATE RESERVATION
    SET reservation_state = #{toState}
    WHERE reserve_date = #{date}
    AND reservation_state = #{fromState}
    """)
    int updateReservationStateBatch(@Param("date") LocalDate date,
                                    @Param("fromState") String fromState,
                                    @Param("toState") String toState);


}
