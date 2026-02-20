package com.dummyjson.tests.regression;

import com.dummyjson.base.BaseTest;
import com.dummyjson.models.request.AddToCartRequest;
import com.dummyjson.models.request.CartProductRequest;
import com.dummyjson.models.response.CartResponse;
import com.dummyjson.services.CartService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Epic("Regression")
@Feature("Idempotency")
public class IdempotencyTest extends BaseTest {

    private final CartService cartService = new CartService();

    @Test
    @Tag("regression")
    @Description("Idempotency Demo: Sending the identical 'add to cart' request twice")
    public void idempotencyDemo() {
        AddToCartRequest addReq = AddToCartRequest.builder()
                .userId(1L)
                .products(Collections.singletonList(
                        CartProductRequest.builder().id(2L).quantity(1).build()))
                .build();

        System.out.println("Sending first add to cart request...");
        Response res1 = cartService.addToCart(addReq);
        CartResponse cart1 = res1.as(CartResponse.class);
        assertNotNull(cart1.getId());

        System.out.println("Sending second identical request...");
        Response res2 = cartService.addToCart(addReq);
        CartResponse cart2 = res2.as(CartResponse.class);
        assertNotNull(cart2.getId());

        System.out
                .println("First response cart ID: " + cart1.getId() + ", Total Quantity: " + cart1.getTotalQuantity());
        System.out
                .println("Second response cart ID: " + cart2.getId() + ", Total Quantity: " + cart2.getTotalQuantity());

        assertEquals(201, res2.getStatusCode(), "Second request should succeed with 201 Created");

        // Assert explicitly what DummyJSON does: it returns the same mock ID (51) with
        // same quantities, acting idempotently.
        assertEquals(cart1.getId(), cart2.getId(),
                "Duplicate request should return the same cart ID (Idempotency Simulation)");
        assertEquals(cart1.getTotalQuantity(), cart2.getTotalQuantity(),
                "Total quantity should not double on a duplicated identical create request");
    }
}
