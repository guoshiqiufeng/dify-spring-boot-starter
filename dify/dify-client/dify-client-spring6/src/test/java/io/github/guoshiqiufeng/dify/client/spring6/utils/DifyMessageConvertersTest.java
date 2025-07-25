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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DifyMessageConverters}.
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/21 14:35
 */
public class DifyMessageConvertersTest {

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
        
        // Add another converter to verify ordering
        HttpMessageConverter<?> otherConverter = mock(HttpMessageConverter.class);
        converters.add(otherConverter);

        // Apply the captured consumer to our test list
        consumerCaptor.getValue().accept(converters);

        // Verify that the list now only has two converters (the original one was replaced)
        assertEquals(2, converters.size());
        
        // Verify the first converter is a MappingJackson2HttpMessageConverter
        assertTrue(converters.get(0) instanceof MappingJackson2HttpMessageConverter);
        
        // Get the ObjectMapper from the configured converter
        ObjectMapper configuredMapper = ((MappingJackson2HttpMessageConverter) converters.get(0)).getObjectMapper();
        
        // Verify that the Jackson converter is properly configured
        assertEquals(JsonInclude.Include.NON_NULL, configuredMapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion());
        assertFalse(configuredMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        
        // Verify the second converter is still the other mock converter
        assertSame(otherConverter, converters.get(1));
    }
    
    @Test
    @DisplayName("Test messageConvertersConsumer creates new converter when none exists")
    public void testMessageConvertersConfigurationNoExistingConverter() {
        // Create a consumer for message converters
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();

        // Create a mock RestClient.Builder
        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);

        // Create an ArgumentCaptor to capture the Consumer<List<HttpMessageConverter<?>>> argument
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<List<HttpMessageConverter<?>>>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);

        // Mock the behavior
        when(mockBuilder.messageConverters(consumerCaptor.capture())).thenReturn(mockBuilder);

        // Apply the consumer to the mock builder
        consumer.accept(mockBuilder);

        // Create a list without any Jackson converter
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        HttpMessageConverter<?> otherConverter = mock(HttpMessageConverter.class);
        converters.add(otherConverter);

        // Apply the captured consumer to our test list
        consumerCaptor.getValue().accept(converters);

        // Verify that the list now has two converters (added a new one)
        assertEquals(2, converters.size());
        
        // Verify the first converter is a MappingJackson2HttpMessageConverter
        assertTrue(converters.get(0) instanceof MappingJackson2HttpMessageConverter);
        
        // Get the ObjectMapper from the configured converter
        ObjectMapper configuredMapper = ((MappingJackson2HttpMessageConverter) converters.get(0)).getObjectMapper();
        
        // Verify that the Jackson converter is properly configured
        assertEquals(JsonInclude.Include.NON_NULL, configuredMapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion());
        assertFalse(configuredMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertFalse(configuredMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
        
        // Verify the second converter is still the other mock converter
        assertSame(otherConverter, converters.get(1));
    }
}
