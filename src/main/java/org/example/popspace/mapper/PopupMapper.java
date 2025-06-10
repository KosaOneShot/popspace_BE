package org.example.popspace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.popspace.dto.statistics.PopupInfoWithLikeCount;
import org.example.popspace.dto.statistics.ReservationTypeStateCount;
import org.example.popspace.dto.statistics.ReservationMemberData;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PopupMapper {

    @Select("""
    SELECT member_id
    FROM popup
    WHERE popup_id = #{popupId}
    """)
    Optional<Long> findPopupOwnerIdByPopupId(Long popupId);

    @Select("""
            SELECT
                m.MEMBER_ID,
                m.BIRTH_DATE,
                m.SEX,
                r.RESERVE_ID,
                r.RESERVATION_TYPE,
                r.RESERVATION_STATE,
                r.RESERVE_DATE,
                r.RESERVE_TIME,
                rv.RATING,
                el.CREATED_AT,
                el.ENTRANCE_STATE
            FROM ENTRANCE_LOG el
            JOIN RESERVATION r ON el.RESERVE_ID = r.RESERVE_ID
            JOIN MEMBER m ON r.MEMBER_ID = m.MEMBER_ID
            LEFT JOIN REVIEW rv ON rv.RESERVE_ID = r.RESERVE_ID
            WHERE el.POPUP_ID = #{popupId}
            """)
    List<ReservationMemberData> findStatisticsDataDtoByPopupId(Long popupId);

    @Select("""
            select p.POPUP_ID,p.POPUP_NAME,p.START_DATE,p.END_DATE,p.OPEN_TIME,p.CLOSE_TIME, count(pl.LIKE_STATE) as like_count
            from POPUP p
            join POPUP_LIKE pl on p.POPUP_ID=pl.POPUP_ID and pl.LIKE_STATE='ACTIVE'
            where p.POPUP_ID=#{popupId}
            group by p.POPUP_ID, p.START_DATE, p.END_DATE, p.POPUP_NAME,p.OPEN_TIME,p.CLOSE_TIME
            """)
    PopupInfoWithLikeCount findPopupWithLikeCount(Long popupId);

    @Select("""
            SELECT
                RESERVATION_TYPE,
                RESERVATION_STATE,
                COUNT(*) AS CNT
            FROM RESERVATION
            WHERE POPUP_ID = #{popupId}
            GROUP BY RESERVATION_TYPE, RESERVATION_STATE
            """)
    List<ReservationTypeStateCount> findReservationData(Long popupId);
}
