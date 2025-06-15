package org.example.popspace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.popspace.dto.qr.QrReservationDTO;
import org.example.popspace.dto.qr.ReservationStatusDTO;

import java.time.LocalDate;
import java.util.Optional;

@Mapper
public interface ReservationMapper {

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
                SELECT p.member_id
                FROM reservation r
                JOIN popup p ON r.popup_id = p.popup_id
                WHERE r.reserve_id = #{reservationId}
            """)
    Optional<Long> findPopupOwnerIdByReservationId(Long reservationId);

    @Select("SELECT reserve_id, reservation_state FROM reservation WHERE reserve_id = #{reservationId}")
    ReservationStatusDTO findReservationStatus(@Param("reservationId") Long reservationId);

    @Update("UPDATE reservation SET reservation_state = #{state} WHERE reserve_id = #{reservationId}")
    int updateReservationState(@Param("reservationId") Long reservationId, @Param("state") String state);

    @Select("""
            select count(r.RESERVE_ID)+1
            from RESERVATION r
            JOIN (
                SELECT RESERVE_ID, RESERVE_DATE
                FROM RESERVATION
                WHERE RESERVE_ID = #{reservationId}
            ) target ON r.RESERVE_DATE = target.RESERVE_DATE
            where r.POPUP_ID= #{popupId}
              and r.RESERVATION_TYPE = 'WALK_IN'
              and r.RESERVATION_STATE = 'RESERVED'
              and r.RESERVE_ID < #{reservationId}
              and r.RESERVE_DATE= #{now}
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