package com.dummyjson.services;

import com.dummyjson.clients.RestClient;
import com.dummyjson.models.request.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthService {

    private static final String LOGIN_ENDPOINT = "/auth/login";

    public Response login(LoginRequest loginRequest) {
        return RestAssured.given()
                .spec(RestClient.getBaseSpec())
                .body(loginRequest)
                .post(LOGIN_ENDPOINT);
    }
}
