package com.dummyjson.tests.regression;

import com.dummyjson.base.BaseTest;
import com.dummyjson.models.request.AddToCartRequest;
import com.dummyjson.models.request.CartProductRequest;
import com.dummyjson.services.CartService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Regression")
@Feature("Data Driven")
public class DataDrivenTests extends BaseTest {

    private final CartService cartService = new CartService();

    @ParameterizedTest
    @ValueSource(ints = { -1, 0, 1, 99, 100, 9999 })
    @Tag("regression")
    @Description("Verify boundary values for product quantity when adding to cart")
    public void testQuantityBoundaries(int quantity) {
        AddToCartRequest addReq = AddToCartRequest.builder()
                .userId(1L)
                .products(Collections.singletonList(
                        CartProductRequest.builder().id(1L).quantity(quantity).build()))
                .build();

        Response res = cartService.addToCart(addReq);
        System.out.println("Tested quantity " + quantity + " - Response Code: " + res.getStatusCode());
        assertTrue(res.getStatusCode() == 200 || res.getStatusCode() == 201 || res.getStatusCode() == 400);
    }
}
