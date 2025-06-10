package org.example.popspace.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    BAD_GATEWAY_TEST(HttpStatus.BAD_GATEWAY,"이거 바꿔야함"),
    INVALID_ENCRYPTION(HttpStatus.FORBIDDEN,"암호화가 유효하지 않습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다. 서버 팀에 연락주세요!"),
    INVALID_DECRYPTION(HttpStatus.FORBIDDEN,"복호화가 유효하지 않습니다."),
    UNACCEPT_TOKEN(HttpStatus.UNAUTHORIZED, "Token is null or too short"),
    BADTYPE_BEARER(HttpStatus.UNAUTHORIZED, "Token type Bearer"),
    MALFORM_TOKEN(HttpStatus.FORBIDDEN, "Malformed Token"),
    BADSIGN_TOKEN(HttpStatus.FORBIDDEN, "BadSignatured Token"),
    EXPIRED_TOKEN(HttpStatus.FORBIDDEN, "Expired Token"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    DUPLICATE_USER(HttpStatus.CONFLICT,"Duplicate User" ),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND,"Member not found" ),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token" ),
    NOT_FOUND_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access Token not found" ),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token not found" ),
    NO_PROUDCT(HttpStatus.NOT_FOUND,"No Proudct" ),
    S3_INVALID(HttpStatus.BAD_REQUEST,"S3 Invalid" ),
    INVALID_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "이메일 또는 비밀번호가 누락되었습니다."),

    INVALID_INDEX(HttpStatus.BAD_REQUEST, "잘못된 인덱스입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 서버 오류입니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "member not found"),

    // QR
    QR_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "QR 코드 생성에 실패했습니다."),
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "서명 위조 또는 변조된 QR 코드입니다."),
    HMAC_INIT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "HMAC 초기화에 실패했습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),
    NOT_EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "존재하지 않는 이메일 주소입니다. 메일 정보를 변경해 주세요"),
    INVALID_RESET_CODE(HttpStatus.BAD_REQUEST, "reset code is invalid or expired"),
    INVALID_SIGNATURE_INPUT(HttpStatus.BAD_REQUEST, "서명 생성에 필요한 입력값이 유효하지 않습니다."),

    // Reservation
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND,"해당하는 예약 정보가 없습니다."),
    INVALID_RESERVATION_STATE(HttpStatus.BAD_REQUEST, "예약 상태가 유효하지 않습니다."),

    // 403 권한 부족 에러
    NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // Popup
    POPUP_NOT_FOUND(HttpStatus.NOT_FOUND, "팝업이 존재하지 않습니다."),
    TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "해당 토큰은 블랙리스트에 등록되어 사용할 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호가 다릅니다" );

    private final HttpStatus status;
    private final String message;
}
