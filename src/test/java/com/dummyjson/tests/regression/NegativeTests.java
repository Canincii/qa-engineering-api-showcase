package com.dummyjson.tests.regression;

import com.dummyjson.base.BaseTest;
import com.dummyjson.models.request.LoginRequest;
import com.dummyjson.services.AuthService;
import com.dummyjson.services.ProductService;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Regression")
@Feature("Negative Tests")
public class NegativeTests extends BaseTest {

    private final AuthService authService = new AuthService();
    private final ProductService productService = new ProductService();

    @Test
    @Tag("regression")
    @Description("Verify that login fails with invalid credentials")
    public void testInvalidLogin() {
        LoginRequest req = LoginRequest.builder()
                .username("invalidUser")
                .password("wrongPass")
                .build();
        Response res = authService.login(req);
        // DummyJSON typically returns 400 for bad creds
        assertTrue(res.getStatusCode() == 400 || res.getStatusCode() == 401, "Expected 400 or 401 for invalid login");
    }

    @Test
    @Tag("regression")
    @Description("Verify 404 response for non-existent product ID")
    public void testNonExistentProduct() {
        try {
            Response res = productService.getProductById(999999L);
            assertEquals(404, res.getStatusCode(), "Expected 404 Not Found for invalid product ID");
        } catch (Exception e) {
            // RestAssured throws AssertionError for non-2xx responses
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            System.out.println("Caught exception type: " + e.getClass().getName() + ", message: " + errorMsg);
            assertTrue(errorMsg.contains("404"), "Expected 404 in error message. Got: " + errorMsg);
        }
    }

    @Test
    @Tag("regression")
    @Description("Security Demo: Verify behavior with invalid/missing token on a protected endpoint")
    public void testAuthTokenSecurityDemo() {
        try {
            Response res = io.restassured.RestAssured.given()
                    .spec(com.dummyjson.clients.RestClient.getBaseSpec().auth().oauth2("invalid_token_123"))
                    .get("/auth/me");

            System.out.println("Security Demo: Trying GET /auth/me with invalid token. Response: " + res.getStatusCode());
            assertEquals(401, res.getStatusCode(),
                    "Expected 401 Unauthorized when accessing a protected endpoint with an invalid token");
        } catch (Exception e) {
            // RestAssured throws AssertionError for 401 responses
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.toString();
            System.out.println("Caught exception type: " + e.getClass().getName() + ", message: " + errorMsg);
            assertTrue(errorMsg.contains("401"), "Expected 401 in error message. Got: " + errorMsg);
        }
    }
}
