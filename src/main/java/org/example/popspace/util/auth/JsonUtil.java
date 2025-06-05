package org.example.popspace.util.auth;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Slf4j
public class JsonUtil {
    private static final Gson gson = new Gson();

    public static Map<String, String> parseJsonToMap(HttpServletRequest request) {
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            TypeToken<Map<String, String>> typeToken = new TypeToken<>() {};
            return gson.fromJson(reader, typeToken.getType());
        } catch (IOException e) {
            log.error("Failed to parse request JSON: {}", e.getMessage());
            return null;
        }
    }
}

