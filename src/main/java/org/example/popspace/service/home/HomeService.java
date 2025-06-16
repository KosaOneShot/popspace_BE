package org.example.popspace.service.home;

import org.example.popspace.dto.home.MostLikedPopupDto;
import org.example.popspace.dto.home.UpcomingReservationDto;
import org.example.popspace.mapper.HomeMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class HomeService {
	private final HomeMapper homeMapper;

	// 내 예약 중 예약일이 가장 임박한 팝업 조회
	public UpcomingReservationDto findUpcomingReservation(Long memberId) {
		log.info("findUpcomingReservation - memberId: {}", memberId);
		return homeMapper.findUpcomingReservation(memberId);
	}

	// 10일 동안 가장 찜 수 많은 팝업 조회
	public MostLikedPopupDto findMostLikedPopupLast10Days() {
		return homeMapper.findMostLikedPopupLast10Days();
	}
}
