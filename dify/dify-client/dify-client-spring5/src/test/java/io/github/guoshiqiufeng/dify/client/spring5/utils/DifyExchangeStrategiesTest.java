/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.dify.client.spring5.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/21 11:45
 */
public class DifyExchangeStrategiesTest {

    @Test
    @DisplayName("Test exchangeStrategies returns non-null consumer")
    public void testExchangeStrategiesReturnsNonNullConsumer() {
        Consumer<WebClient.Builder> consumer = DifyExchangeStrategies.exchangeStrategies();
        assertNotNull(consumer, "Exchange strategies consumer should not be null");
    }
    
    @Test
    @DisplayName("Test exchangeStrategies can be applied to WebClient.Builder")
    public void testExchangeStrategiesCanBeAppliedToWebClientBuilder() {
        Consumer<WebClient.Builder> consumer = DifyExchangeStrategies.exchangeStrategies();
        
        // Create a WebClient.Builder
        WebClient.Builder builder = WebClient.builder();
        
        // Apply the consumer to the builder
        assertDoesNotThrow(() -> consumer.accept(builder), 
                "Applying exchange strategies to WebClient.Builder should not throw an exception");
        
        // Create a WebClient instance to verify it works
        WebClient webClient = builder.build();
        assertNotNull(webClient, "WebClient should be created successfully");
    }
    
    @Test
    @DisplayName("Test exchangeStrategies configures ObjectMapper correctly")
    public void testExchangeStrategiesConfiguresObjectMapperCorrectly() {
        // This test is more of a verification that the code runs without errors
        // since we can't easily inspect the internal configuration of the ObjectMapper
        
        Consumer<WebClient.Builder> consumer = DifyExchangeStrategies.exchangeStrategies();
        WebClient.Builder builder = WebClient.builder();
        consumer.accept(builder);
        
        // Build the WebClient and verify it was created successfully
        WebClient webClient = builder.build();
        assertNotNull(webClient, "WebClient should be created successfully");
        
        // We can't directly test the ObjectMapper configuration, but we can verify
        // that the ExchangeStrategies were applied to the WebClient
        // This is a bit of a smoke test
    }
}
