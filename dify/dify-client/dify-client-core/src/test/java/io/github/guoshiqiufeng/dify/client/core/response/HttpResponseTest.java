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
package io.github.guoshiqiufeng.dify.client.core.response;

import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HttpResponse
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class HttpResponseTest {

    @Test
    void testConstructorWithMapHeaders() {
        // Arrange
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        headers.put("Content-Length", Collections.singletonList("123"));
        String body = "test body";

        // Act
        HttpResponse<String> response = new HttpResponse<>(200, headers, body);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals(body, response.getBody());
        assertNotNull(response.getHeaders());
        assertEquals("application/json", response.getFirstHeader("Content-Type"));
        assertEquals("123", response.getFirstHeader("Content-Length"));
    }

    @Test
    void testConstructorWithHttpHeaders() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Set-Cookie", "session=abc");
        headers.add("Set-Cookie", "token=xyz");
        String body = "test body";

        // Act
        HttpResponse<String> response = new HttpResponse<>(201, headers, body);

        // Assert
        assertEquals(201, response.getStatusCode());
        assertEquals(body, response.getBody());
        assertEquals("application/json", response.getFirstHeader("Content-Type"));
        List<String> cookies = response.getHeader("Set-Cookie");
        assertEquals(2, cookies.size());
        assertTrue(cookies.contains("session=abc"));
        assertTrue(cookies.contains("token=xyz"));
    }

    @Test
    void testConstructorWithNullHeaders() {
        // Act
        HttpResponse<String> response = new HttpResponse<>(200, (HttpHeaders) null, "body");

        // Assert
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getHeaders());
        assertTrue(response.getHeaders().isEmpty());
    }

    @Test
    void testGetHeader() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "text/html");
        HttpResponse<String> response = new HttpResponse<>(200, headers, "body");

        // Act & Assert
        List<String> contentType = response.getHeader("Content-Type");
        assertEquals(1, contentType.size());
        assertEquals("application/json", contentType.get(0));

        List<String> accept = response.getHeader("Accept");
        assertEquals(1, accept.size());
        assertEquals("text/html", accept.get(0));
    }

    @Test
    void testGetHeaderCaseInsensitive() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpResponse<String> response = new HttpResponse<>(200, headers, "body");

        // Act & Assert
        assertEquals("application/json", response.getFirstHeader("content-type"));
        assertEquals("application/json", response.getFirstHeader("CONTENT-TYPE"));
        assertEquals("application/json", response.getFirstHeader("Content-Type"));
    }

    @Test
    void testGetOrEmpty() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpResponse<String> response = new HttpResponse<>(200, headers, "body");

        // Act & Assert
        List<String> existing = response.getOrEmpty("Content-Type");
        assertEquals(1, existing.size());
        assertEquals("application/json", existing.get(0));

        List<String> nonExisting = response.getOrEmpty("Non-Existing");
        assertNotNull(nonExisting);
        assertTrue(nonExisting.isEmpty());
    }

    @Test
    void testGetFirstHeader() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "session=abc");
        headers.add("Set-Cookie", "token=xyz");
        HttpResponse<String> response = new HttpResponse<>(200, headers, "body");

        // Act & Assert
        assertEquals("session=abc", response.getFirstHeader("Set-Cookie"));
        assertNull(response.getFirstHeader("Non-Existing"));
    }

    @Test
    void testIsSuccessful() {
        // 2xx status codes should be successful
        assertTrue(new HttpResponse<>(200, new HttpHeaders(), null).isSuccessful());
        assertTrue(new HttpResponse<>(201, new HttpHeaders(), null).isSuccessful());
        assertTrue(new HttpResponse<>(204, new HttpHeaders(), null).isSuccessful());
        assertTrue(new HttpResponse<>(299, new HttpHeaders(), null).isSuccessful());

        // Non-2xx status codes should not be successful
        assertFalse(new HttpResponse<>(199, new HttpHeaders(), null).isSuccessful());
        assertFalse(new HttpResponse<>(300, new HttpHeaders(), null).isSuccessful());
        assertFalse(new HttpResponse<>(400, new HttpHeaders(), null).isSuccessful());
        assertFalse(new HttpResponse<>(404, new HttpHeaders(), null).isSuccessful());
        assertFalse(new HttpResponse<>(500, new HttpHeaders(), null).isSuccessful());
    }

    @Test
    void testBuilder() {
        // Act
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(200)
                .header("Content-Type", "application/json")
                .header("Accept", "text/html")
                .body("test body")
                .build();

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("test body", response.getBody());
        assertEquals("application/json", response.getFirstHeader("Content-Type"));
        assertEquals("text/html", response.getFirstHeader("Accept"));
    }

    @Test
    void testBuilderWithMultipleHeaderValues() {
        // Act
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(200)
                .header("Set-Cookie", Arrays.asList("session=abc", "token=xyz"))
                .body("test body")
                .build();

        // Assert
        List<String> cookies = response.getHeader("Set-Cookie");
        assertEquals(2, cookies.size());
        assertTrue(cookies.contains("session=abc"));
        assertTrue(cookies.contains("token=xyz"));
    }

    @Test
    void testBuilderWithHeadersMap() {
        // Arrange
        Map<String, List<String>> headersMap = new HashMap<>();
        headersMap.put("Content-Type", Collections.singletonList("application/json"));
        headersMap.put("Accept", Collections.singletonList("text/html"));

        // Act
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(200)
                .headers(headersMap)
                .body("test body")
                .build();

        // Assert
        assertEquals("application/json", response.getFirstHeader("Content-Type"));
        assertEquals("text/html", response.getFirstHeader("Accept"));
    }

    @Test
    void testBuilderWithHttpHeaders() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "text/html");

        // Act
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(200)
                .headers(headers)
                .body("test body")
                .build();

        // Assert
        assertEquals("application/json", response.getFirstHeader("Content-Type"));
        assertEquals("text/html", response.getFirstHeader("Accept"));
    }

    @Test
    void testBuilderDefaultStatusCode() {
        // Act
        HttpResponse<String> response = HttpResponse.<String>builder()
                .body("test body")
                .build();

        // Assert
        assertEquals(200, response.getStatusCode());
        assertEquals("test body", response.getBody());
    }

    @Test
    void testBuilderWithNullBody() {
        // Act
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(204)
                .build();

        // Assert
        assertEquals(204, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testBuilderChaining() {
        // Act
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(201)
                .header("Content-Type", "application/json")
                .header("Location", "/api/users/123")
                .header("Set-Cookie", "session=abc")
                .header("Set-Cookie", "token=xyz")
                .body("{\"id\":123}")
                .build();

        // Assert
        assertEquals(201, response.getStatusCode());
        assertEquals("{\"id\":123}", response.getBody());
        assertEquals("application/json", response.getFirstHeader("Content-Type"));
        assertEquals("/api/users/123", response.getFirstHeader("Location"));
        assertEquals(2, response.getHeader("Set-Cookie").size());
    }

    @Test
    void testToString() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpResponse<String> response = new HttpResponse<>(200, headers, "test body");

        // Act
        String result = response.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("statusCode=200"));
        assertTrue(result.contains("body=test body"));
    }

    @Test
    void testGenericTypes() {
        // Test with different body types
        HttpResponse<Integer> intResponse = new HttpResponse<>(200, new HttpHeaders(), 42);
        assertEquals(42, intResponse.getBody());

        HttpResponse<List<String>> listResponse = new HttpResponse<>(200, new HttpHeaders(), Arrays.asList("a", "b"));
        assertEquals(2, listResponse.getBody().size());

        HttpResponse<Map<String, Object>> mapResponse = new HttpResponse<>(200, new HttpHeaders(), new HashMap<>());
        assertNotNull(mapResponse.getBody());
    }
}
