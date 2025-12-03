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
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    @DisplayName("Test messageConvertersConsumer configures Jackson converter correctly")
    void testMessageConvertersConfiguration() {
        // Given
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();
        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);

        // Create an ArgumentCaptor to capture the Consumer<HttpMessageConverters.ClientBuilder> argument
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<HttpMessageConverters.ClientBuilder>> consumerCaptor =
                ArgumentCaptor.forClass(Consumer.class);

        // Mock the behavior
        when(mockBuilder.configureMessageConverters(consumerCaptor.capture())).thenReturn(mockBuilder);

        // When
        consumer.accept(mockBuilder);

        // Then
        verify(mockBuilder).configureMessageConverters(any());
        assertNotNull(consumerCaptor.getValue());
    }

    @Test
    @DisplayName("Test JsonMapper is configured with NON_NULL inclusion")
    void testJsonMapperNonNullConfiguration() {
        // Given
        JsonMapper objectMapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        JacksonJsonHttpMessageConverter converter = new JacksonJsonHttpMessageConverter(objectMapper);
        JsonMapper configuredMapper = converter.getMapper();

        // When - Test by serializing an object with null values
        var testObj = new TestObject("test", null);

        // Then
        try {
            String json = configuredMapper.writeValueAsString(testObj);
            assertFalse(json.contains("nullValue"), "Null values should be excluded from JSON");
            assertTrue(json.contains("stringValue"), "Non-null values should be included in JSON");
        } catch (Exception e) {
            fail("Serialization failed: " + e.getMessage());
        }

        assertFalse(configuredMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES),
                "FAIL_ON_UNKNOWN_PROPERTIES should be disabled");
    }

    @Test
    @DisplayName("Test messageConvertersConsumer returns non-null consumer")
    void testMessageConvertersConsumerNotNull() {
        // When
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();

        // Then
        assertNotNull(consumer, "Consumer should not be null");
    }

    @Test
    @DisplayName("Test consumer can be applied to RestClient.Builder")
    void testConsumerCanBeApplied() {
        // Given
        Consumer<RestClient.Builder> consumer = DifyMessageConverters.messageConvertersConsumer();
        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);
        when(mockBuilder.configureMessageConverters(any())).thenReturn(mockBuilder);

        // When/Then - Should not throw exception
        assertDoesNotThrow(() -> consumer.accept(mockBuilder));
        verify(mockBuilder).configureMessageConverters(any());
    }

    @Test
    @DisplayName("Test JsonMapper configuration with unknown properties")
    void testJsonMapperUnknownPropertiesHandling() {
        // Given
        JsonMapper objectMapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        // When - Deserialize JSON with unknown properties
        String jsonWithUnknownProperty = "{\"stringValue\":\"test\",\"unknownProperty\":\"value\"}";

        // Then - Should not throw exception
        assertDoesNotThrow(() -> {
            TestObject result = objectMapper.readValue(jsonWithUnknownProperty, TestObject.class);
            assertEquals("test", result.stringValue);
        }, "Should handle unknown properties without throwing exception");
    }

    @Test
    @DisplayName("Test utility class constructor is private")
    void testPrivateConstructor() throws Exception {
        // Verify that the constructor is private
        java.lang.reflect.Constructor<DifyMessageConverters> constructor = DifyMessageConverters.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
    }

    @Test
    @DisplayName("Test JacksonJsonHttpMessageConverter is created with custom JsonMapper")
    void testJacksonConverterCreation() {
        // Given
        JsonMapper objectMapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        // When
        JacksonJsonHttpMessageConverter converter = new JacksonJsonHttpMessageConverter(objectMapper);

        // Then
        assertNotNull(converter);
        assertNotNull(converter.getMapper());
        assertSame(objectMapper, converter.getMapper());
    }
}
