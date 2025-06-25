package org.example.popspace.controller.home;

import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.home.MostLikedPopupDto;
import org.example.popspace.dto.home.UpcomingReservationDto;
import org.example.popspace.service.home.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
	private final HomeService homeService;

	@GetMapping("/upcoming-reservation")
	public ResponseEntity<UpcomingReservationDto> getUpcomingReservation(@AuthenticationPrincipal CustomUserDetail userDetail) {
		log.info("getUpcomingReservation - userId: {}", userDetail.getId());
		UpcomingReservationDto upcomingReservation = homeService.findUpcomingReservation(userDetail.getId());
		log.info("조회된 예약 정보: {}", upcomingReservation);
		return ResponseEntity.ok(upcomingReservation);
	}

	@GetMapping("/most-liked")
	public ResponseEntity<MostLikedPopupDto> getMostLikedPopupLast10Days() {
		MostLikedPopupDto mostLikedPopup = homeService.findMostLikedPopupLast10Days();
		log.info("가장 찜 수 많은 팝업: {}", mostLikedPopup);
		return ResponseEntity.ok(mostLikedPopup);
	}
}
