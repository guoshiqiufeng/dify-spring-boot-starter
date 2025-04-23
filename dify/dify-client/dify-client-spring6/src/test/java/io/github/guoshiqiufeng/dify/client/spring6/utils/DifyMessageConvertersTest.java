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
package io.github.guoshiqiufeng.dify.client.spring6.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/21 14:35
 */
public class DifyMessageConvertersTest {

//    @Test
//    @DisplayName("Test messageConvertersConsumer returns a valid RestClient.Builder consumer")
//    public void testMessageConvertersConsumer() {
//        // Get the RestClient.Builder consumer
//        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();
//
//        // Verify that the consumer is not null
//        assertNotNull(consumer);
//
//        // Create a mock RestClient.Builder
//        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);
//
//        // Mock the behavior to capture the messageConverters consumer
//        // Using a suppressed warning since we know the types are compatible
//        @SuppressWarnings("unchecked")
//        Consumer<List<HttpMessageConverter<?>>> anyConsumer = ArgumentMatchers.any(Consumer.class);
//        when(mockBuilder.messageConverters(anyConsumer)).thenReturn(mockBuilder);
//
//        // Apply the consumer to the mock builder
//        consumer.accept(mockBuilder);
//
//        // Verify that the messageConverters method was called once
//        verify(mockBuilder, times(1)).messageConverters(c-> {
//        });
//    }

    @Test
    @DisplayName("Test messageConvertersConsumer configures Jackson converter correctly")
    public void testMessageConvertersConfiguration() {
        // Create a consumer for message converters
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();

        // Create a mock RestClient.Builder
        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);

        // Create an ArgumentCaptor to capture the Consumer<List<HttpMessageConverter<?>>> argument
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<List<HttpMessageConverter<?>>>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);

        // Mock the behavior with a suppressed warning since we know the types are compatible
        when(mockBuilder.messageConverters(consumerCaptor.capture())).thenReturn(mockBuilder);

        // Apply the consumer to the mock builder
        consumer.accept(mockBuilder);

        // Create a list with a MappingJackson2HttpMessageConverter for testing
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(objectMapper);
        converters.add(jacksonConverter);

        // Apply the captured consumer to our test list
        consumerCaptor.getValue().accept(converters);

        // Verify that the ObjectMapper's serialization inclusion is set to NON_NULL
        // Using the non-deprecated API in newer Jackson versions
        assertEquals(JsonInclude.Include.NON_NULL, objectMapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion());
    }
}
