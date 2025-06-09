package org.example.popspace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.popspace.dto.qr.QrReservationDTO;
import org.example.popspace.dto.qr.ReservationStatusDTO;

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

}
