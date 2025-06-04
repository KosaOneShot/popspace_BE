package org.example.popspace.util.auth;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.global.error.ErrorCode;

import java.io.IOException;
import java.util.Map;


@Slf4j
public class ErrorResponseUtil {

    private static final Gson gson = new Gson();

    public static void sendJsonError(HttpServletResponse response, ErrorCode errorCode) {
        try {
            response.setStatus(errorCode.getStatus().value());
            response.setContentType("application/json; charset=UTF-8");

            Map<String, String> errorBody = Map.of("error", errorCode.getMessage());
            String json = gson.toJson(errorBody);

            response.getWriter().write(json);
        } catch (IOException e) {
            log.error("Failed to send error response: {}", e.getMessage());
        }
    }
}
