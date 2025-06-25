package org.example.popspace.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorDTO> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("CustomException occurred: {} - {}", errorCode, ex.getMessage());
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(IndexOutOfBoundsException.class)
    protected ResponseEntity<ErrorDTO> handleIndexOutOfBounds(IndexOutOfBoundsException ex) {
        log.warn("IndexOutOfBoundsException: {}", ex.getMessage());
        return handleExceptionInternal(ErrorCode.INVALID_INDEX);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorDTO> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return handleExceptionInternal(ErrorCode.INVALID_INPUT);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorDTO> handleUncaughtException(Exception ex) {
        log.error("Unhandled Exception", ex);
        return handleExceptionInternal(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDTO> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(makeErrorResponse(errorCode));
    }

    private ErrorDTO makeErrorResponse(ErrorCode errorCode) {
        return ErrorDTO.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity<ErrorDTO> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        log.warn("AuthorizationDeniedException caught: {}", ex.getMessage());
        return handleExceptionInternal(ErrorCode.ACCESS_DENIED);
    }
}
