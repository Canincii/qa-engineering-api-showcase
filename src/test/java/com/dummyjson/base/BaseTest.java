package com.dummyjson.base;

import com.dummyjson.config.ConfigurationManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;

@Slf4j
public abstract class BaseTest {

    @BeforeAll
    public static void globalSetup() {
        log.info("Performing global setup before tests...");
        ConfigurationManager config = ConfigurationManager.getInstance();

        // Add Allure reporting filter globally
        RestAssured.filters(new AllureRestAssured());

        log.info("Base URL configured to: {}", config.getString("base.url"));
    }
}
