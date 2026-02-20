package com.dummyjson.clients;

import com.dummyjson.config.ConfigurationManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

public class RestClient {

    public static RequestSpecification getBaseSpec() {
        ConfigurationManager config = ConfigurationManager.getInstance();
        return new RequestSpecBuilder()
                .setBaseUri(config.getString("base.url"))
                .setContentType("application/json")
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    public static RequestSpecification getAuthenticatedSpec(String token) {
        return getBaseSpec().auth().oauth2(token);
    }
}
