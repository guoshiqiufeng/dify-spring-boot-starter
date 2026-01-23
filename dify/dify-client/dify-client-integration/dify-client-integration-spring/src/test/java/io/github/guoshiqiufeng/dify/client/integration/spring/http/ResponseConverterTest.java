/*
 * Copyright (c) 2025-2026, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.client.integration.spring.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ResponseConverter
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class ResponseConverterTest {

    @Mock
    private JsonMapper jsonMapper;

    private ResponseConverter responseConverter;

    @BeforeEach
    void setUp() {
        responseConverter = new ResponseConverter(jsonMapper);
    }

    @Test
    void testConvertWithClassType() {
        // Arrange
        String jsonBody = "{\"name\":\"test\",\"value\":123}";
        TestDto expectedDto = new TestDto("test", 123);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonBody, headers, HttpStatus.OK);

        when(jsonMapper.fromJson(jsonBody, TestDto.class)).thenReturn(expectedDto);

        // Act
        io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity<TestDto> result = responseConverter.convert(responseEntity, TestDto.class);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals(expectedDto, result.getBody());
        assertEquals("application/json", result.getHeaders().get("Content-Type").get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testConvertWithTypeReference() {
        // Arrange
        String jsonBody = "[{\"name\":\"test1\"},{\"name\":\"test2\"}]";
        TypeReference<List<TestDto>> typeRef = new TypeReference<List<TestDto>>() {};
        List<TestDto> expectedList = List.of(new TestDto("test1", 1), new TestDto("test2", 2));
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonBody, HttpStatus.OK);

        when(jsonMapper.fromJson(eq(jsonBody), any(TypeReference.class))).thenReturn(expectedList);

        // Act
        io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity<List<TestDto>> result = responseConverter.convert(responseEntity, typeRef);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals(expectedList, result.getBody());
    }

    @Test
    void testDeserializeWithClassType() {
        // Arrange
        String jsonBody = "{\"name\":\"test\",\"value\":456}";
        TestDto expectedDto = new TestDto("test", 456);
        when(jsonMapper.fromJson(jsonBody, TestDto.class)).thenReturn(expectedDto);

        // Act
        TestDto result = responseConverter.deserialize(jsonBody, TestDto.class);

        // Assert
        assertEquals(expectedDto, result);
    }

    @Test
    void testDeserializeWithNullBody() {
        // Act
        TestDto result = responseConverter.deserialize(null, TestDto.class);

        // Assert
        assertNull(result);
    }

    @Test
    void testDeserializeWithEmptyBody() {
        // Act
        TestDto result = responseConverter.deserialize("", TestDto.class);

        // Assert
        assertNull(result);
    }

    @Test
    void testDeserializeStringType() {
        // Arrange
        String bodyString = "plain text response";

        // Act
        String result = responseConverter.deserialize(bodyString, String.class);

        // Assert
        assertEquals(bodyString, result);
    }

    @Test
    void testDeserializeThrowsException() {
        // Arrange
        String invalidJson = "invalid json";
        when(jsonMapper.fromJson(invalidJson, TestDto.class))
                .thenThrow(new RuntimeException("Parse error"));

        // Act & Assert
        HttpClientException exception = assertThrows(
                HttpClientException.class,
                () -> responseConverter.deserialize(invalidJson, TestDto.class)
        );
        assertTrue(exception.getMessage().contains("Failed to deserialize response"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testDeserializeWithTypeReference() {
        // Arrange
        String jsonBody = "{\"key\":\"value\"}";
        TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};
        Map<String, String> expectedMap = Map.of("key", "value");
        when(jsonMapper.fromJson(eq(jsonBody), any(TypeReference.class))).thenReturn(expectedMap);

        // Act
        Map<String, String> result = responseConverter.deserialize(jsonBody, typeRef);

        // Assert
        assertEquals(expectedMap, result);
    }

    @Test
    void testDeserializeWithTypeReferenceNullBody() {
        // Arrange
        TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};

        // Act
        Map<String, String> result = responseConverter.deserialize(null, typeRef);

        // Assert
        assertNull(result);
    }

    @Test
    void testDeserializeWithTypeReferenceEmptyBody() {
        // Arrange
        TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};

        // Act
        Map<String, String> result = responseConverter.deserialize("", typeRef);

        // Assert
        assertNull(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testDeserializeWithTypeReferenceThrowsException() {
        // Arrange
        String invalidJson = "invalid";
        TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};
        when(jsonMapper.fromJson(eq(invalidJson), any(TypeReference.class)))
                .thenThrow(new RuntimeException("Parse error"));

        // Act & Assert
        HttpClientException exception = assertThrows(
                HttpClientException.class,
                () -> responseConverter.deserialize(invalidJson, typeRef)
        );
        assertTrue(exception.getMessage().contains("Failed to deserialize response"));
    }

    @Test
    void testConvertPreservesMultipleHeaders() {
        // Arrange
        String jsonBody = "{}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-Custom-Header", "value1");
        headers.add("Authorization", "Bearer token");
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonBody, headers, HttpStatus.OK);

        when(jsonMapper.fromJson(jsonBody, TestDto.class)).thenReturn(new TestDto("test", 1));

        // Act
        io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity<TestDto> result = responseConverter.convert(responseEntity, TestDto.class);

        // Assert
        assertEquals("application/json", result.getHeaders().get("Content-Type").get(0));
        assertEquals("value1", result.getHeaders().get("X-Custom-Header").get(0));
        assertEquals("Bearer token", result.getHeaders().get("Authorization").get(0));
    }

    @Test
    void testConvertWithDifferentStatusCodes() {
        // Test multiple status codes
        HttpStatus[] statuses = {
                HttpStatus.CREATED,
                HttpStatus.ACCEPTED,
                HttpStatus.NO_CONTENT,
                HttpStatus.BAD_REQUEST,
                HttpStatus.NOT_FOUND,
                HttpStatus.INTERNAL_SERVER_ERROR
        };

        for (HttpStatus status : statuses) {
            // Arrange
            ResponseEntity<String> responseEntity = new ResponseEntity<>("{}", status);
            when(jsonMapper.fromJson("{}", TestDto.class)).thenReturn(new TestDto("test", 1));

            // Act
            io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity<TestDto> result = responseConverter.convert(responseEntity, TestDto.class);

            // Assert
            assertEquals(status.value(), result.getStatusCode(),
                    "Status code should match for " + status);
        }
    }

    // Test DTO class
    private static class TestDto {
        private String name;
        private int value;

        public TestDto(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestDto testDto = (TestDto) o;
            return value == testDto.value && name.equals(testDto.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode() + value;
        }
    }
}
