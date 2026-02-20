package com.dummyjson.services;

import com.dummyjson.clients.RestClient;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ProductService {

    private static final String PRODUCTS_ENDPOINT = "/products";

    public Response getProducts() {
        return RestAssured.given()
                .spec(RestClient.getBaseSpec())
                .get(PRODUCTS_ENDPOINT);
    }

    public Response getProductById(Long id) {
        return RestAssured.given()
                .spec(RestClient.getBaseSpec())
                .pathParam("id", id)
                .get(PRODUCTS_ENDPOINT + "/{id}");
    }
}
