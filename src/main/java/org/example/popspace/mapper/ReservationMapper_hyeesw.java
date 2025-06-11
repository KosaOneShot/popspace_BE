package org.example.popspace.mapper;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
                R.RESERVE_TIME,
                P.LOCATION,
                P.IMAGE_URL,
                R.RESERVATION_TYPE,
                R.RESERVATION_STATE
              from RESERVATION R
              join POPUP P on R.POPUP_ID = P.POPUP_ID
              <where>
                R.MEMBER_ID = #{memberId}
                <if test="searchKeyword != null and searchKeyword.trim() != ''">
                  and P.POPUP_NAME like '%' || #{searchKeyword} || '%'
                </if>
                <if test="searchDate != null">
                  and trunc(R.RESERVE_TIME) = #{searchDate}
                </if>
                <if test="reservationType != null and reservationType.trim() != '' and reservationType != 'ALL'">
                  and R.RESERVATION_TYPE = #{reservationType}
                </if>
              </where>
            order by R.RESERVE_TIME desc
        </script>
    """)
    public List<ReservationListResponseDto> findReservationListByMemberId(String searchKeyword, LocalDate searchDate,
                                                                          String reservationType, Long memberId);
}
