package com.dummyjson.services;

import com.dummyjson.clients.RestClient;
import com.dummyjson.models.request.AddToCartRequest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CartService {

    private static final String CARTS_ENDPOINT = "/carts";

    public Response addToCart(AddToCartRequest addToCartRequest) {
        return RestAssured.given()
                .spec(RestClient.getBaseSpec())
                .body(addToCartRequest)
                .post(CARTS_ENDPOINT + "/add");
    }

    public Response getCartById(Long id) {
        return RestAssured.given()
                .spec(RestClient.getBaseSpec())
                .pathParam("id", id)
                .get(CARTS_ENDPOINT + "/{id}");
    }
}
