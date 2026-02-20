package com.dummyjson.services;

import com.dummyjson.clients.RestClient;
import com.dummyjson.utils.TokenManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserService {

    private static final String USERS_ENDPOINT = "/users";

    public Response getUserById(Long id) {
        return RestAssured.given()
                .spec(RestClient.getBaseSpec())
                .pathParam("id", id)
                .get(USERS_ENDPOINT + "/{id}");
    }

    public Response getUserByIdWithAuth(Long id) {
        String token = TokenManager.getToken();
        return RestAssured.given()
                .spec(RestClient.getAuthenticatedSpec(token))
                .pathParam("id", id)
                .get(USERS_ENDPOINT + "/{id}");
    }
}
