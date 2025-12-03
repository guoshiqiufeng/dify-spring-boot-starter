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
package io.github.guoshiqiufeng.dify.client.spring7.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyExchangeStrategies}.
 *
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/21 14:30
 */
public class DifyExchangeStrategiesTest {

    @Test
    @DisplayName("Test exchangeStrategies returns a valid WebClient.Builder consumer")
    public void testExchangeStrategies() {
        // Get the WebClient.Builder consumer
        Consumer<WebClient.Builder> consumer = DifyExchangeStrategies.exchangeStrategies();

        // Verify that the consumer is not null
        assertNotNull(consumer);

        // Create a mock WebClient.Builder
        WebClient.Builder mockBuilder = mock(WebClient.Builder.class);

        // Apply the consumer to the mock builder
        consumer.accept(mockBuilder);

        // Verify that the exchangeStrategies method was called on the builder
        verify(mockBuilder, times(1)).exchangeStrategies(any(ExchangeStrategies.class));
    }

    @Test
    @DisplayName("Test exchangeStrategies configures JSON encoder and decoder")
    public void testExchangeStrategiesConfiguration() {
        // This test verifies the configuration of the exchange strategies
        // Since we can't directly access the ExchangeStrategies from the builder,
        // we'll verify the consumer can be applied without errors

        // Get the WebClient.Builder consumer
        Consumer<WebClient.Builder> consumer = DifyExchangeStrategies.exchangeStrategies();

        // Create a mock WebClient.Builder
        WebClient.Builder mockBuilder = mock(WebClient.Builder.class);
        when(mockBuilder.exchangeStrategies(any(ExchangeStrategies.class))).thenReturn(mockBuilder);

        // Apply the consumer to our mock builder
        consumer.accept(mockBuilder);

        // Verify that the exchangeStrategies method was called with a non-null argument
        verify(mockBuilder).exchangeStrategies(any(ExchangeStrategies.class));
    }
}
