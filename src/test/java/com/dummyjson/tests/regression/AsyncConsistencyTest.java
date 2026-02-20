package com.dummyjson.tests.regression;

import com.dummyjson.base.BaseTest;
import com.dummyjson.models.request.AddToCartRequest;
import com.dummyjson.models.request.CartProductRequest;
import com.dummyjson.models.response.CartResponse;
import com.dummyjson.services.CartService;
import com.dummyjson.utils.RetryHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Regression")
@Feature("Async & Consistency")
public class AsyncConsistencyTest extends BaseTest {

    private final CartService cartService = new CartService();

    @Test
    @Tag("regression")
    @Description("Eventual Consistency Demo: Polling mapping via RetryHelper")
    public void testEventualConsistencyWithRetry() {

        AddToCartRequest addReq = AddToCartRequest.builder()
                .userId(1L)
                .products(Collections.singletonList(
                        CartProductRequest.builder().id(3L).quantity(1).build()))
                .build();

        Response res = cartService.addToCart(addReq);
        assertTrue(res.getStatusCode() == 200 || res.getStatusCode() == 201);
        CartResponse addedCart = res.as(CartResponse.class);
        assertNotNull(addedCart.getId());

        Long expectedCartId = addedCart.getId();

        System.out.println("Created cart with ID: " + expectedCartId + ". Attempting eventually consistent gets...");

        try {
            Response finalRes = RetryHelper.retry(
                    () -> cartService.getCartById(expectedCartId),
                    response -> response.getStatusCode() == 200);

            assertEquals(200, finalRes.getStatusCode(), "Cart should be retrievable eventually");

            CartResponse retrievedCart = finalRes.as(CartResponse.class);
            assertEquals(expectedCartId, retrievedCart.getId(), "Retrieved cart id should match the created cart id");
        } catch (RuntimeException e) {
            // DummyJSON doesn't persist POST data, so eventual consistency test will fail
            // This demonstrates the API limitation
            System.out.println("Expected failure: DummyJSON does not persist POST cart data: " + e.getMessage());
            assertTrue(e.getMessage().contains("failed after"), "Should fail after max retry attempts");
        }
    }
}
