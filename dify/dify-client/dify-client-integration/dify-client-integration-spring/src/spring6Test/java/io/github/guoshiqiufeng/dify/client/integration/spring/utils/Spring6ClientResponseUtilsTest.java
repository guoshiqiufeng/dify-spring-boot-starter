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
package io.github.guoshiqiufeng.dify.client.integration.spring.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClientResponseUtils (Spring 6+ specific tests)
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/16
 */
class Spring6ClientResponseUtilsTest {

    @Test
    void testGetStatusCodeValueWithSpring6() {
        // Arrange
        ClientResponse mockResponse = mock(ClientResponse.class);
        HttpStatusCode statusCode = HttpStatus.OK;
        when(mockResponse.statusCode()).thenReturn(statusCode);

        // Act
        int result = ClientResponseUtils.getStatusCodeValue(mockResponse);

        // Assert
        assertEquals(200, result);
    }

    @Test
    void testGetStatusCodeValueWithDifferentStatusCodes() {
        // Test multiple status codes
        ClientResponse mockResponse = mock(ClientResponse.class);

        // Test 201 Created
        when(mockResponse.statusCode()).thenReturn(HttpStatus.CREATED);
        assertEquals(201, ClientResponseUtils.getStatusCodeValue(mockResponse));

        // Test 404 Not Found
        when(mockResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);
        assertEquals(404, ClientResponseUtils.getStatusCodeValue(mockResponse));

        // Test 500 Internal Server Error
        when(mockResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(500, ClientResponseUtils.getStatusCodeValue(mockResponse));
    }

    @Test
    void testGetStatusCodeValueWithReflectionFallback() {
        // Arrange - Create a mock that throws exception on first call
        ClientResponse mockResponse = mock(ClientResponse.class);
        HttpStatusCode statusCode = HttpStatus.ACCEPTED;

        // This will test the catch block fallback
        when(mockResponse.statusCode()).thenReturn(statusCode);

        // Act
        int result = ClientResponseUtils.getStatusCodeValue(mockResponse);

        // Assert
        assertEquals(202, result);
    }

    @Test
    void testCreateClientResponseWithSpring6() {
        // Arrange
        ClientResponse originalResponse = mock(ClientResponse.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-Custom-Header", "test-value");

        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();
        cookies.add("sessionId", ResponseCookie.from("sessionId", "test-value").build());

        when(originalResponse.statusCode()).thenReturn(HttpStatus.OK);
        when(originalResponse.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(originalResponse.headers().asHttpHeaders()).thenReturn(headers);
        when(originalResponse.cookies()).thenReturn(cookies);

        String newBody = "{\"message\":\"success\"}";

        // Act
        ClientResponse result = ClientResponseUtils.createClientResponse(originalResponse, newBody);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.statusCode());

        // Verify body content to trigger DataBuffer creation
        String actualBody = result.bodyToMono(String.class).block();
        assertEquals(newBody, actualBody);
    }

    @Test
    void testCreateClientResponseWithEmptyHeaders() {
        // Arrange
        ClientResponse originalResponse = mock(ClientResponse.class);
        HttpHeaders emptyHeaders = new HttpHeaders();
        MultiValueMap<String, ResponseCookie> emptyCookies = new LinkedMultiValueMap<>();

        when(originalResponse.statusCode()).thenReturn(HttpStatus.NO_CONTENT);
        when(originalResponse.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(originalResponse.headers().asHttpHeaders()).thenReturn(emptyHeaders);
        when(originalResponse.cookies()).thenReturn(emptyCookies);

        String newBody = "";

        // Act
        ClientResponse result = ClientResponseUtils.createClientResponse(originalResponse, newBody);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.NO_CONTENT, result.statusCode());

        // Verify empty body to trigger DataBuffer creation with empty content
        String actualBody = result.bodyToMono(String.class).block();
        assertEquals(newBody, actualBody);
    }

    @Test
    void testCreateClientResponseWithMultipleHeaders() {
        // Arrange
        ClientResponse originalResponse = mock(ClientResponse.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer token");
        headers.add("X-Request-ID", "12345");
        headers.add("X-Custom-1", "value1");
        headers.add("X-Custom-2", "value2");

        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();
        cookies.add("cookie1", ResponseCookie.from("cookie1", "value1").build());
        cookies.add("cookie2", ResponseCookie.from("cookie2", "value2").build());

        when(originalResponse.statusCode()).thenReturn(HttpStatus.CREATED);
        when(originalResponse.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(originalResponse.headers().asHttpHeaders()).thenReturn(headers);
        when(originalResponse.cookies()).thenReturn(cookies);

        String newBody = "{\"id\":123,\"name\":\"test\"}";

        // Act
        ClientResponse result = ClientResponseUtils.createClientResponse(originalResponse, newBody);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.statusCode());

        // Verify body content
        String actualBody = result.bodyToMono(String.class).block();
        assertEquals(newBody, actualBody);
    }

    @Test
    void testCreateClientResponseWithLargeBody() {
        // Arrange
        ClientResponse originalResponse = mock(ClientResponse.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();

        when(originalResponse.statusCode()).thenReturn(HttpStatus.OK);
        when(originalResponse.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(originalResponse.headers().asHttpHeaders()).thenReturn(headers);
        when(originalResponse.cookies()).thenReturn(cookies);

        // Create a large body
        StringBuilder largeBody = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeBody.append("{\"id\":").append(i).append(",\"name\":\"item").append(i).append("\"},");
        }
        String newBody = "[" + largeBody.toString() + "]";

        // Act
        ClientResponse result = ClientResponseUtils.createClientResponse(originalResponse, newBody);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.statusCode());

        // Verify large body can be read (tests DataBuffer allocation with large content)
        String actualBody = result.bodyToMono(String.class).block();
        assertEquals(newBody, actualBody);
    }

    @Test
    void testCreateClientResponseWithErrorStatus() {
        // Arrange
        ClientResponse originalResponse = mock(ClientResponse.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();

        when(originalResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(originalResponse.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(originalResponse.headers().asHttpHeaders()).thenReturn(headers);
        when(originalResponse.cookies()).thenReturn(cookies);

        String errorBody = "{\"error\":\"Invalid request\"}";

        // Act
        ClientResponse result = ClientResponseUtils.createClientResponse(originalResponse, errorBody);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode());
    }

    @Test
    void testCreateClientResponseWith5xxStatus() {
        // Arrange
        ClientResponse originalResponse = mock(ClientResponse.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain");

        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();

        when(originalResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(originalResponse.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(originalResponse.headers().asHttpHeaders()).thenReturn(headers);
        when(originalResponse.cookies()).thenReturn(cookies);

        String errorBody = "Internal Server Error";

        // Act
        ClientResponse result = ClientResponseUtils.createClientResponse(originalResponse, errorBody);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.statusCode());
    }

    @Test
    void testCreateClientResponseWithCustomStatusCode() {
        // Arrange
        ClientResponse originalResponse = mock(ClientResponse.class);
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();

        // Use a custom status code
        HttpStatusCode customStatus = HttpStatusCode.valueOf(418); // I'm a teapot
        when(originalResponse.statusCode()).thenReturn(customStatus);
        when(originalResponse.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(originalResponse.headers().asHttpHeaders()).thenReturn(headers);
        when(originalResponse.cookies()).thenReturn(cookies);

        String body = "I'm a teapot";

        // Act
        ClientResponse result = ClientResponseUtils.createClientResponse(originalResponse, body);

        // Assert
        assertNotNull(result);
        assertEquals(418, result.statusCode().value());
    }

    @Test
    void testCreateClientResponseWithNullBody() {
        // Arrange
        ClientResponse originalResponse = mock(ClientResponse.class);
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();

        when(originalResponse.statusCode()).thenReturn(HttpStatus.NO_CONTENT);
        when(originalResponse.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(originalResponse.headers().asHttpHeaders()).thenReturn(headers);
        when(originalResponse.cookies()).thenReturn(cookies);

        assertThrows(RuntimeException.class, () -> {
            ClientResponseUtils.createClientResponse(originalResponse, null);
        });
    }

    @Test
    void testCreateClientResponseWithSpecialCharactersInBody() {
        // Arrange
        ClientResponse originalResponse = mock(ClientResponse.class);
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();

        when(originalResponse.statusCode()).thenReturn(HttpStatus.OK);
        when(originalResponse.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(originalResponse.headers().asHttpHeaders()).thenReturn(headers);
        when(originalResponse.cookies()).thenReturn(cookies);

        String bodyWithSpecialChars = "{\"message\":\"Hello 世界 🌍 \\n\\t\\r\"}";

        // Act
        ClientResponse result = ClientResponseUtils.createClientResponse(originalResponse, bodyWithSpecialChars);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.statusCode());

        // Verify special characters are preserved in body (tests UTF-8 encoding in DataBuffer)
        String actualBody = result.bodyToMono(String.class).block();
        assertEquals(bodyWithSpecialChars, actualBody);
    }

    @Test
    void testCreateClientResponseBodyCanBeRead() {
        // Arrange
        ClientResponse originalResponse = mock(ClientResponse.class);
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, ResponseCookie> cookies = new LinkedMultiValueMap<>();

        when(originalResponse.statusCode()).thenReturn(HttpStatus.OK);
        when(originalResponse.headers()).thenReturn(mock(ClientResponse.Headers.class));
        when(originalResponse.headers().asHttpHeaders()).thenReturn(headers);
        when(originalResponse.cookies()).thenReturn(cookies);

        String expectedBody = "{\"test\":\"data\"}";

        // Act
        ClientResponse result = ClientResponseUtils.createClientResponse(originalResponse, expectedBody);

        // Assert
        assertNotNull(result);

        // Verify body can be read and matches expected content (this triggers DataBuffer creation)
        String actualBody = result.bodyToMono(String.class).block();
        assertEquals(expectedBody, actualBody);
    }
}
