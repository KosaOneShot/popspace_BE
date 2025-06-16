package org.example.popspace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.popspace.dto.home.MostLikedPopupDto;
import org.example.popspace.dto.home.UpcomingReservationDto;

@Mapper
public interface HomeMapper {

	// 내 예약 중 예약일이 가장 임박한 팝업 (오늘 이후인데, 예약일이 가장 작은 팝업)
	@Select("""
		SELECT
	         RES.RESERVE_ID,
			 RES.RESERVE_DATE,
			 RES.RESERVE_TIME,
			 RES.RESERVATION_STATE,
			 RES.RESERVATION_TYPE,
		 	 P.POPUP_ID,
			 P.POPUP_NAME,
			 P.IMAGE_URL,
			 P.LOCATION
		FROM RESERVATION RES
		JOIN POPUP P ON RES.POPUP_ID = P.POPUP_ID
		WHERE RESERVE_DATE > SYSDATE AND RES.MEMBER_ID=#{memberId} AND RESERVATION_STATE = 'RESERVED'
		ORDER BY RESERVE_DATE, RESERVE_TIME, RESERVE_ID
		FETCH FIRST 1 ROW ONLY
	""")
	UpcomingReservationDto findUpcomingReservation(Long memberId);

	// 10일동안 가장 찜 수 많은 예약 (끝난 팝업 제외)
	@Select("""
		SELECT
			P.POPUP_ID,
			P.POPUP_NAME,
			P.IMAGE_URL,
			PL.CNT AS LIKE_COUNT
		FROM POPUP P
		JOIN (
			SELECT POPUP_ID, COUNT(*) AS CNT
			FROM POPUP_LIKE
			WHERE CREATED_AT > SYSDATE - 10 AND LIKE_STATE = 'ACTIVE'
			GROUP BY POPUP_ID
			ORDER BY CNT DESC, POPUP_ID
		) PL ON P.POPUP_ID = PL.POPUP_ID AND P.END_DATE >= SYSDATE
		ORDER BY PL.CNT DESC, POPUP_ID
		FETCH FIRST 1 ROWS ONLY
	""")
	MostLikedPopupDto findMostLikedPopupLast10Days();
}
