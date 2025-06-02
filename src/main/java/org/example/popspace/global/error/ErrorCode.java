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
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"User not found" ),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token" ),
    NOT_FOUND_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access Token not found" ),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token not found" ),
    NO_PROUDCT(HttpStatus.NOT_FOUND,"No Proudct" ),
    S3_INVALID(HttpStatus.BAD_REQUEST,"S3 Invalid" ),
    INVALID_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "이메일 또는 비밀번호가 누락되었습니다."),

    INVALID_INDEX(HttpStatus.BAD_REQUEST, "잘못된 인덱스입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 서버 오류입니다.")
    ;
    ;

    private final HttpStatus status;
    private final String message;
}
