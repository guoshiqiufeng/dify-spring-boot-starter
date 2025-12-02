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

import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DifyMessageConvertersTest {

    static class TestObject {
        public String stringValue;
        public String nullValue;

        public TestObject(String stringValue, String nullValue) {
            this.stringValue = stringValue;
            this.nullValue = nullValue;
        }
    }

    @Test
    @DisplayName("Test messageConvertersConsumer configures Jackson converter correctly when existing converter is present")
    void testMessageConvertersConfigurationWithExistingConverter() {
        // Given
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();
        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);

        // Create an ArgumentCaptor to capture the Consumer<List<HttpMessageConverter<?>>> argument
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<List<HttpMessageConverter<?>>>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);

        // Mock the behavior
        when(mockBuilder.messageConverters(consumerCaptor.capture())).thenReturn(mockBuilder);

        // When
        consumer.accept(mockBuilder);

        // Create a list with an existing JacksonJsonHttpMessageConverter for testing
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        JsonMapper originalMapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.ALWAYS))
                .build();
        JacksonJsonHttpMessageConverter existingConverter = new JacksonJsonHttpMessageConverter(originalMapper);
        converters.add(existingConverter);

        // Add another converter to verify ordering
        HttpMessageConverter<?> otherConverter = mock(HttpMessageConverter.class);
        converters.add(otherConverter);

        // Apply the captured consumer to our test list
        consumerCaptor.getValue().accept(converters);

        // Then
        assertEquals(2, converters.size());

        // Verify the first converter is a JacksonJsonHttpMessageConverter (the new one)
        assertInstanceOf(JacksonJsonHttpMessageConverter.class, converters.getFirst());

        // The new converter should be at position 0
        JacksonJsonHttpMessageConverter configuredConverter = (JacksonJsonHttpMessageConverter) converters.get(0);
        JsonMapper configuredMapper = configuredConverter.getMapper();

        // Verify that the Jackson converter is properly configured with NON_NULL inclusion
        // Test by serializing an object with null values to see if they are excluded
        var testObj = new TestObject("test", null);
        try {
            String json = configuredMapper.writeValueAsString(testObj);
            assertFalse(json.contains("nullValue"));
        } catch (Exception e) {
            fail("Serialization failed: " + e.getMessage());
        }

        assertFalse(configuredMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));

        // Verify the second converter is still the other mock converter
        assertSame(otherConverter, converters.get(1));
    }

    @Test
    @DisplayName("Test messageConvertersConsumer creates new converter when no Jackson converter exists")
    void testMessageConvertersConfigurationNoExistingJacksonConverter() {
        // Given
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();
        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);

        // Create an ArgumentCaptor to capture the Consumer<List<HttpMessageConverter<?>>> argument
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<List<HttpMessageConverter<?>>>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);

        // Mock the behavior
        when(mockBuilder.messageConverters(consumerCaptor.capture())).thenReturn(mockBuilder);

        // When
        consumer.accept(mockBuilder);

        // Create a list without any Jackson converter
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        HttpMessageConverter<?> otherConverter = mock(HttpMessageConverter.class);
        converters.add(otherConverter);

        // Apply the captured consumer to our test list
        consumerCaptor.getValue().accept(converters);

        // Then
        assertEquals(2, converters.size());

        // Verify the first converter is a JacksonJsonHttpMessageConverter (the new one)
        assertInstanceOf(JacksonJsonHttpMessageConverter.class, converters.getFirst());

        JacksonJsonHttpMessageConverter newConverter = (JacksonJsonHttpMessageConverter) converters.get(0);
        JsonMapper configuredMapper = newConverter.getMapper();

        // Verify that the new Jackson converter is properly configured
        // Test by serializing an object with null values to see if they are excluded
        var testObj = new TestObject("test", null);
        try {
            String json = configuredMapper.writeValueAsString(testObj);
            assertFalse(json.contains("nullValue"));
        } catch (Exception e) {
            fail("Serialization failed: " + e.getMessage());
        }
        assertFalse(configuredMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));

        // Verify the second converter is still the other mock converter
        assertSame(otherConverter, converters.get(1));
    }

    @Test
    @DisplayName("Test messageConvertersConsumer adds converter at the beginning of the list")
    void testConverterAddedAtBeginning() {
        // Given
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();
        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<List<HttpMessageConverter<?>>>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);

        when(mockBuilder.messageConverters(consumerCaptor.capture())).thenReturn(mockBuilder);
        consumer.accept(mockBuilder);

        // Create a list with multiple existing converters
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        HttpMessageConverter<?> converter1 = mock(HttpMessageConverter.class);
        HttpMessageConverter<?> converter2 = mock(HttpMessageConverter.class);
        converters.add(converter1);
        converters.add(converter2);

        // When
        consumerCaptor.getValue().accept(converters);

        // Then
        assertEquals(3, converters.size());
        assertInstanceOf(JacksonJsonHttpMessageConverter.class, converters.get(0));
        assertSame(converter1, converters.get(1));
        assertSame(converter2, converters.get(2));
    }

    @Test
    @DisplayName("Test messageConvertersConsumer replaces all existing Jackson converters")
    void testAllExistingJacksonConvertersReplaced() {
        // Given
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();
        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<List<HttpMessageConverter<?>>>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);

        when(mockBuilder.messageConverters(consumerCaptor.capture())).thenReturn(mockBuilder);
        consumer.accept(mockBuilder);

        // Create a list with multiple Jackson converters and other converters
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        JsonMapper mapper1 = JsonMapper.builder().build();
        JsonMapper mapper2 = JsonMapper.builder().build();
        JacksonJsonHttpMessageConverter jacksonConverter1 = new JacksonJsonHttpMessageConverter(mapper1);
        JacksonJsonHttpMessageConverter jacksonConverter2 = new JacksonJsonHttpMessageConverter(mapper2);
        HttpMessageConverter<?> otherConverter = mock(HttpMessageConverter.class);

        converters.add(jacksonConverter1);
        converters.add(otherConverter);
        converters.add(jacksonConverter2);

        // When
        consumerCaptor.getValue().accept(converters);

        // Then
        assertEquals(2, converters.size()); // Should have 1 new Jackson converter + 1 other converter
        assertInstanceOf(JacksonJsonHttpMessageConverter.class, converters.get(0));
        assertSame(otherConverter, converters.get(1));

        // Verify the new converter is properly configured
        JsonMapper configuredMapper = ((JacksonJsonHttpMessageConverter) converters.get(0)).getMapper();
        // Test by serializing an object with null values to see if they are excluded
        var testObj = new TestObject("test", null);
        try {
            String json = configuredMapper.writeValueAsString(testObj);
            assertFalse(json.contains("nullValue"));
        } catch (Exception e) {
            fail("Serialization failed: " + e.getMessage());
        }
        assertFalse(configuredMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    @Test
    @DisplayName("Test messageConvertersConsumer maintains proper ordering with multiple converter types")
    void testConverterOrderingWithMultipleTypes() {
        // Given
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();
        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<List<HttpMessageConverter<?>>>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);

        when(mockBuilder.messageConverters(consumerCaptor.capture())).thenReturn(mockBuilder);
        consumer.accept(mockBuilder);

        // Create a list with various converter types in a specific order
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        HttpMessageConverter<?> stringConverter = mock(HttpMessageConverter.class);
        JsonMapper originalMapper = JsonMapper.builder().build();
        JacksonJsonHttpMessageConverter originalJacksonConverter = new JacksonJsonHttpMessageConverter(originalMapper);
        HttpMessageConverter<?> xmlConverter = mock(HttpMessageConverter.class);

        converters.add(stringConverter);
        converters.add(originalJacksonConverter);
        converters.add(xmlConverter);

        // When
        consumerCaptor.getValue().accept(converters);

        // Then
        assertEquals(3, converters.size());
        assertInstanceOf(JacksonJsonHttpMessageConverter.class, converters.get(0)); // New Jackson converter at position 0
        assertSame(stringConverter, converters.get(1)); // Other converters shifted
        assertSame(xmlConverter, converters.get(2));
    }

    @Test
    @DisplayName("Test messageConvertersConsumer produces properly configured JsonMapper")
    void testJsonMapperConfiguration() {
        // Given
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();
        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<List<HttpMessageConverter<?>>>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);

        when(mockBuilder.messageConverters(consumerCaptor.capture())).thenReturn(mockBuilder);
        consumer.accept(mockBuilder);

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        consumerCaptor.getValue().accept(converters);

        // Then
        assertInstanceOf(JacksonJsonHttpMessageConverter.class, converters.getFirst());
        JsonMapper configuredMapper = ((JacksonJsonHttpMessageConverter) converters.getFirst()).getMapper();

        // Verify all expected configurations are applied
        // Test by serializing an object with null values to see if they are excluded
        var testObj = new TestObject("test", null);
        try {
            String json = configuredMapper.writeValueAsString(testObj);
            assertFalse(json.contains("nullValue"));
        } catch (Exception e) {
            fail("Serialization failed: " + e.getMessage());
        }
        assertFalse(configuredMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    @Test
    @DisplayName("Test utility class constructor is private")
    void testPrivateConstructor() throws Exception {
        // Verify that the constructor is private
        java.lang.reflect.Constructor<DifyMessageConverters> constructor = DifyMessageConverters.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
    }
}
