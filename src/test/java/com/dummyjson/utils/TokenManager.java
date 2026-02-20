package com.dummyjson.utils;

import com.dummyjson.config.ConfigurationManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenManager {

    private static final ThreadLocal<String> tokenMap = new ThreadLocal<>();

    public static String getToken() {
        if (tokenMap.get() == null || tokenMap.get().isEmpty()) {
            log.info("Token is missing for current thread, fetching a new one...");
            tokenMap.set(fetchToken());
        }
        return tokenMap.get();
    }

    private static String fetchToken() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        String baseUrl = config.getString("base.url");

        Map<String, String> credentials = new HashMap<>();
        // Using sample credentials from dummyjson documentation
        credentials.put("username", "emilys");
        credentials.put("password", "emilyspass");

        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(credentials)
                .post("/auth/login");

        if (response.statusCode() == 200) {
            String fetchedToken = response.jsonPath().getString("accessToken");
            if (fetchedToken == null) {
                fetchedToken = response.jsonPath().getString("token");
            }
            log.info("Successfully fetched token");
            return fetchedToken;
        } else {
            log.error("Failed to fetch token: " + response.statusLine());
            throw new RuntimeException("Could not fetch auth token");
        }
    }

    public static void clearToken() {
        tokenMap.remove();
    }
}
