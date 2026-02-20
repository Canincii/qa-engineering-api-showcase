package com.dummyjson.utils;

import com.dummyjson.config.ConfigurationManager;
import lombok.extern.slf4j.Slf4j;
import java.util.function.Supplier;

@Slf4j
public class RetryHelper {

    public static <T> T retry(Supplier<T> function, java.util.function.Predicate<T> successCondition) {
        ConfigurationManager config = ConfigurationManager.getInstance();
        int maxAttempts = config.getInt("retry.maxAttempts");
        int delayMs = config.getInt("retry.backoffDelayMs");

        if (maxAttempts <= 0)
            maxAttempts = 5;
        if (delayMs <= 0)
            delayMs = 1000;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                T result = function.get();
                if (successCondition.test(result)) {
                    log.info("Action succeeded on attempt {}", attempt);
                    return result;
                }
                log.warn("Action did not meet success condition on attempt {}. Retrying...", attempt);
            } catch (Exception e) {
                log.warn("Exception on attempt {}: {}", attempt, e.getMessage());
            }

            if (attempt < maxAttempts) {
                try {
                    int currentDelay = delayMs * (int) Math.pow(2, attempt - 1);
                    log.info("Waiting {} ms before next attempt...", currentDelay);
                    Thread.sleep(currentDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
        throw new RuntimeException("Action failed after " + maxAttempts + " attempts.");
    }
}
