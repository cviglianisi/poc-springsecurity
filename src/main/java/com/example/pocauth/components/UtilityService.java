package com.example.pocauth.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



import java.util.*;


@Service
@Slf4j
public class UtilityService {

    public Optional<String> getAuthToken(String cookieHeader) {
        if (Objects.nonNull(cookieHeader) && !cookieHeader.isEmpty()) {
            var cookies = cookieHeader.split("; ");
            for (String cookie : cookies) {
                if (cookie.startsWith("auth_token=")) {
                    String authToken = cookie.substring("auth_token=".length());
                    return Optional.of(authToken);
                }
            }
        }
        return Optional.empty();
    }
}
