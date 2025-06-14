package org.example.popspace.mapper;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.popspace.dto.reservation.ReservationDetailResponseDto;
import org.example.popspace.dto.reservation.ReservationListResponseDto;

@Mapper
public interface ReservationMapper_hyeesw {

    /* 예약 목록
    * RESERVE_TIME 의 날짜 일치 별, POPUP_NAME 에 키워드 포함 별, RESERVATION_TYPE 별 검색
     */
    @Select("""
        <script>
            select
                R.RESERVE_ID,
                P.POPUP_NAME,
                RESERVE_DATE,
                RESERVE_TIME,
                P.LOCATION,
                P.IMAGE_URL,
                R.RESERVATION_TYPE,
                R.RESERVATION_STATE
              from RESERVATION R
              join POPUP P on R.POPUP_ID = P.POPUP_ID
              <where>
               R.MEMBER_ID = #{memberId}
               <if test="lastReserveDate != null">
                   <!-- 페이지네이션 (날짜, 시간, 분, 아이디 순으로 중복 필터) -->
                   <![CDATA[
                    and (TRUNC(R.RESERVE_DATE) < #{lastReserveDate}
                        or (TRUNC(R.RESERVE_DATE) = #{lastReserveDate}
                            and (TO_NUMBER(SUBSTR(R.RESERVE_TIME, 1, 2)) < #{lastReserveHour}
                                or (TO_NUMBER(SUBSTR(R.RESERVE_TIME, 1, 2)) = #{lastReserveHour}
                                    and ( TO_NUMBER(SUBSTR(R.RESERVE_TIME, 4, 2)) < #{lastReserveMinute}
                                        or (TO_NUMBER(SUBSTR(R.RESERVE_TIME, 4, 2)) = #{lastReserveMinute}
                                            and R.RESERVE_ID < #{lastReserveId}))))))
                   ]]>
                </if>
                <if test="searchKeyword != null and searchKeyword.trim() != ''">
                  and P.POPUP_NAME like '%' || #{searchKeyword} || '%'
                </if>
                <if test="searchDate != null">
                  and trunc(R.RESERVE_DATE) = #{searchDate}
                </if>
                <if test="reservationType != null and reservationType.trim() != '' and reservationType != 'ALL'">
                  and R.RESERVATION_TYPE = #{reservationType}
                </if>
              </where>
                ORDER BY TRUNC(R.RESERVE_DATE) DESC, -- 날짜
                     TO_NUMBER(SUBSTR(R.RESERVE_TIME, 1, 2)) desc, -- 시
                     TO_NUMBER(SUBSTR(R.RESERVE_TIME, 4, 2)) desc -- 분
                FETCH FIRST 5 ROWS ONLY
        </script>
    """)
    public List<ReservationListResponseDto> findReservationListByMemberId(String searchKeyword, LocalDate searchDate,
                                                                          String reservationType, Long memberId,
    LocalDate lastReserveDate, int lastReserveHour, int lastReserveMinute, Long lastReserveId);
    @Select("""
        select
            RES.RESERVE_ID,
            RES.RESERVE_DATE,
            RESERVE_TIME,
            RES.CREATED_AT,
            RES.CANCELED_AT,
            RES.RESERVATION_STATE,
            RES.RESERVATION_TYPE,
            RES.POPUP_ID,
            M.MEMBER_NAME,
            p.POPUP_NAME,
            p.LOCATION,
            p.START_DATE,
            p.END_DATE,
            p.OPEN_TIME,
            p.close_time,
            p.DESCRIPTION,
            p.CATEGORY,
            p.MAX_RESERVATIONS,
            p.IMAGE_URL
        from RESERVATION RES
        join member M on RES.MEMBER_ID = M.MEMBER_ID
        join POPUP P on RES.POPUP_ID = P.POPUP_ID
        where res.RESERVE_ID = #{reserveId}
        order by res.CREATED_AT desc
    """)
    public ReservationDetailResponseDto findReservationDetailByReserveId(Long reserveId);
}
