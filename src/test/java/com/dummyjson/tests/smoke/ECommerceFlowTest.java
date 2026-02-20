package com.dummyjson.tests.smoke;

import com.dummyjson.base.BaseTest;
import com.dummyjson.models.request.AddToCartRequest;
import com.dummyjson.models.request.CartProductRequest;
import com.dummyjson.models.request.LoginRequest;
import com.dummyjson.models.response.CartResponse;
import com.dummyjson.models.response.LoginResponse;
import com.dummyjson.models.response.ProductListResponse;
import com.dummyjson.services.AuthService;
import com.dummyjson.services.CartService;
import com.dummyjson.services.ProductService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

@Epic("E-Commerce Flow")
@Feature("Smoke Tests")
public class ECommerceFlowTest extends BaseTest {

    private final AuthService authService = new AuthService();
    private final ProductService productService = new ProductService();
    private final CartService cartService = new CartService();

    @Test
    @Tag("smoke")
    @Story("End-to-end shopping cart flow")
    @Description("Verify that a user can login, fetch products, add a product to cart, and verify the consistency behavior")
    public void testECommerceEndToEnd() {
        // Step 1: Login
        LoginRequest loginRequest = LoginRequest.builder()
                .username("emilys")
                .password("emilyspass")
                .expiresInMins(30)
                .build();

        Response loginRes = authService.login(loginRequest);
        assertEquals(200, loginRes.getStatusCode(), "Login should be successful");
        LoginResponse loginData = loginRes.as(LoginResponse.class);
        assertNotNull(loginData.getAccessToken(), "Token should not be null");

        // Step 2: Get products
        Response productsRes = productService.getProducts();
        assertEquals(200, productsRes.getStatusCode(), "Products should be retrieved successfully");
        ProductListResponse productsData = productsRes.as(ProductListResponse.class);
        assertFalse(productsData.getProducts().isEmpty(), "Product list should not be empty");

        Long firstProductId = productsData.getProducts().get(0).getId();

        // Step 3: Add to cart
        AddToCartRequest addReq = AddToCartRequest.builder()
                .userId(loginData.getId())
                .products(Collections.singletonList(
                        CartProductRequest.builder().id(firstProductId).quantity(2).build()))
                .build();

        Response addCartRes = cartService.addToCart(addReq);
        assertTrue(addCartRes.getStatusCode() == 200 || addCartRes.getStatusCode() == 201,
                "Add cart should be successful");
        CartResponse cartData = addCartRes.as(CartResponse.class);
        assertNotNull(cartData.getId(), "Cart ID should not be null");
        assertEquals(loginData.getId(), cartData.getUserId(), "User ID should match");

        // Step 4: Verify cart (Consistency check)
        // Note: DummyJSON simulates POST requests. The newly added cart with
        // cartData.getId() might not be fetchable!
        try {
            Response getCartRes = cartService.getCartById(cartData.getId());
            if (getCartRes.getStatusCode() == 200) {
                CartResponse fetchedCart = getCartRes.as(CartResponse.class);
                assertEquals(cartData.getId(), fetchedCart.getId(), "Fetched cart ID should match the added cart ID");
            } else {
                // Documenting known dummy API limitation
                System.out.println("Dummy API does not persist new objects. GET returned: " + getCartRes.getStatusCode());
                assertTrue(getCartRes.getStatusCode() == 404, "Since object isn't saved, expect 404");
            }
        } catch (Exception e) {
            // RestAssured throws AssertionError on non-2xx responses
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            System.out.println("Dummy API does not persist new objects. Caught exception: " + e.getClass().getName() + ", message: " + errorMsg);
            assertTrue(errorMsg.contains("404"), "Expected 404 in error message. Got: " + errorMsg);
        }
    }
}
