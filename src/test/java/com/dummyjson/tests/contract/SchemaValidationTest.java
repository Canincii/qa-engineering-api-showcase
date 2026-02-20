package com.dummyjson.tests.contract;

import com.dummyjson.base.BaseTest;
import com.dummyjson.models.request.AddToCartRequest;
import com.dummyjson.models.request.CartProductRequest;
import com.dummyjson.services.CartService;
import com.dummyjson.services.ProductService;
import com.dummyjson.utils.SchemaValidatorHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.Collections;

@Epic("Contract Validations")
@Feature("JSON Schema")
public class SchemaValidationTest extends BaseTest {

    private final ProductService productService = new ProductService();
    private final CartService cartService = new CartService();

    @Test
    @Tag("contract")
    @Description("Validate JSON Schema for GET /products")
    public void validateProductsSchema() {
        Response res = productService.getProducts();

        res.then()
                .statusCode(200);

        SchemaValidatorHelper.validateSchema(res.then(), "schemas/products-schema.json");
    }

    @Test
    @Tag("contract")
    @Description("Validate JSON Schema for POST /carts/add")
    public void validateAddToCartSchema() {
        AddToCartRequest addReq = AddToCartRequest.builder()
                .userId(1L)
                .products(Collections.singletonList(
                        CartProductRequest.builder().id(1L).quantity(2).build()))
                .build();

        Response res = cartService.addToCart(addReq);

        res.then()
                .statusCode(org.hamcrest.Matchers.anyOf(org.hamcrest.Matchers.is(200), org.hamcrest.Matchers.is(201)));

        SchemaValidatorHelper.validateSchema(res.then(), "schemas/add-cart-schema.json");
    }
}
