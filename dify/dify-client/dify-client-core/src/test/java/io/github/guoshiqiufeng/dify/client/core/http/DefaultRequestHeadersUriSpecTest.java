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
package io.github.guoshiqiufeng.dify.client.core.http;

import io.github.guoshiqiufeng.dify.client.core.map.LinkedMultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.map.MultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.web.client.ResponseSpec;
import io.github.guoshiqiufeng.dify.client.core.web.util.UriBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DefaultRequestHeadersUriSpec
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/12
 */
class DefaultRequestHeadersUriSpecTest {

    private HttpRequestBuilder mockRequestBuilder;
    private DefaultRequestHeadersUriSpec spec;

    @BeforeEach
    void setUp() {
        mockRequestBuilder = mock(HttpRequestBuilder.class);
        spec = new DefaultRequestHeadersUriSpec(mockRequestBuilder);
    }

    @Test
    void testConstructorWithNullBuilder() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DefaultRequestHeadersUriSpec(null);
        });
    }

    @Test
    void testUriWithStringAndVariables() {
        String uri = "/users/{id}";
        Object[] variables = {123};

        DefaultRequestHeadersUriSpec result = spec.uri(uri, variables);

        assertSame(spec, result);
        verify(mockRequestBuilder).uri(uri, variables);
    }

    @Test
    void testUriWithNullString() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.uri(null, 123);
        });
    }

    @Test
    void testUriWithMap() {
        String uri = "/users/{id}/posts/{postId}";
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", 123);
        variables.put("postId", 456);

        DefaultRequestHeadersUriSpec result = spec.uri(uri, variables);

        assertSame(spec, result);
        ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
        verify(mockRequestBuilder).uri(eq(uri), captor.capture());

        Object[] capturedArgs = captor.getValue();
        assertEquals(2, capturedArgs.length);
        assertEquals(123, capturedArgs[0]);
        assertEquals(456, capturedArgs[1]);
    }

    @Test
    void testUriWithMapNullUri() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", 123);

        assertThrows(IllegalArgumentException.class, () -> {
            spec.uri(null, variables);
        });
    }

    @Test
    void testUriWithEmptyMap() {
        String uri = "/users";
        Map<String, Object> variables = new HashMap<>();

        DefaultRequestHeadersUriSpec result = spec.uri(uri, variables);

        assertSame(spec, result);
        verify(mockRequestBuilder).uri(uri);
    }

    @Test
    void testUriWithMapMissingVariable() {
        String uri = "/users/{id}/posts/{postId}";
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", 123);
        // Missing postId

        assertThrows(IllegalArgumentException.class, () -> {
            spec.uri(uri, variables);
        });
    }

    @Test
    void testUriWithFunction() {
        Function<UriBuilder, String> uriFunction = builder -> {
            builder.path("/users");
            builder.queryParam("page", "1");
            return builder.build();
        };

        DefaultRequestHeadersUriSpec result = spec.uri(uriFunction);

        assertSame(spec, result);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<UriBuilder>> captor = ArgumentCaptor.forClass((Class) Consumer.class);
        verify(mockRequestBuilder).uri(captor.capture());
        assertNotNull(captor.getValue());
    }

    @Test
    void testUriWithNullFunction() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.uri((Function<UriBuilder, String>) null);
        });
    }

    @Test
    void testUriWithVariablesAndFunction() {
        String uri = "/users/{id}";
        Object[] variables = {123};
        Function<UriBuilder, String> uriFunction = builder -> {
            builder.queryParam("page", "1");
            return builder.build();
        };

        DefaultRequestHeadersUriSpec result = spec.uri(uri, variables, uriFunction);

        assertSame(spec, result);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<UriBuilder>> captor = ArgumentCaptor.forClass((Class) Consumer.class);
        verify(mockRequestBuilder).uri(captor.capture());
        assertNotNull(captor.getValue());
    }

    @Test
    void testUriWithVariablesAndFunctionNullUri() {
        Object[] variables = {123};
        Function<UriBuilder, String> uriFunction = builder -> builder.build();

        assertThrows(IllegalArgumentException.class, () -> {
            spec.uri(null, variables, uriFunction);
        });
    }

    @Test
    void testUriWithVariablesAndFunctionNullFunction() {
        String uri = "/users/{id}";
        Object[] variables = {123};

        assertThrows(IllegalArgumentException.class, () -> {
            spec.uri(uri, variables, null);
        });
    }

    @Test
    void testHeaderWithSingleValue() {
        String headerName = "Accept";
        String headerValue = "application/json";

        DefaultRequestHeadersUriSpec result = spec.header(headerName, headerValue);

        assertSame(spec, result);
        verify(mockRequestBuilder).header(headerName, headerValue);
    }

    @Test
    void testHeaderWithMultipleValues() {
        String headerName = "Accept";
        String[] headerValues = {"application/json", "text/html"};

        DefaultRequestHeadersUriSpec result = spec.header(headerName, headerValues);

        assertSame(spec, result);
        verify(mockRequestBuilder, times(2)).header(eq(headerName), anyString());
    }

    @Test
    void testHeaderWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.header(null, "value");
        });
    }

    @Test
    void testHeaderWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.header("", "value");
        });
    }

    @Test
    void testHeaderWithNullValues() {
        String headerName = "Accept";

        DefaultRequestHeadersUriSpec result = spec.header(headerName, (String[]) null);

        assertSame(spec, result);
        verify(mockRequestBuilder, never()).header(anyString(), anyString());
    }

    @Test
    void testHeadersWithConsumer() {
        Consumer<HttpHeaders> headersConsumer = headers -> {
            headers.add("Accept", "application/json");
            headers.add("Accept-Language", "en-US");
        };

        DefaultRequestHeadersUriSpec result = spec.headers(headersConsumer);

        assertSame(spec, result);
        verify(mockRequestBuilder).headers(headersConsumer);
    }

    @Test
    void testHeadersWithNullConsumer() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.headers((Consumer<HttpHeaders>) null);
        });
    }

    @Test
    void testCookie() {
        String name = "sessionId";
        String value = "abc123";

        DefaultRequestHeadersUriSpec result = spec.cookie(name, value);

        assertSame(spec, result);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<MultiValueMap<String, String>>> captor = ArgumentCaptor.forClass((Class) Consumer.class);
        verify(mockRequestBuilder).cookies(captor.capture());

        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();
        captor.getValue().accept(cookies);
        assertEquals("abc123", cookies.getFirst("sessionId"));
    }

    @Test
    void testCookieWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.cookie(null, "value");
        });
    }

    @Test
    void testCookieWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.cookie("", "value");
        });
    }

    @Test
    void testCookieWithNullValue() {
        String name = "sessionId";

        DefaultRequestHeadersUriSpec result = spec.cookie(name, null);

        assertSame(spec, result);
        verify(mockRequestBuilder, never()).cookies(any());
    }

    @Test
    void testCookiesWithConsumer() {
        Consumer<MultiValueMap<String, String>> cookiesConsumer = cookies -> {
            cookies.add("sessionId", "abc123");
            cookies.add("token", "xyz789");
        };

        DefaultRequestHeadersUriSpec result = spec.cookies(cookiesConsumer);

        assertSame(spec, result);
        verify(mockRequestBuilder).cookies(cookiesConsumer);
    }

    @Test
    void testCookiesWithNullConsumer() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.cookies(null);
        });
    }

    @Test
    void testContentType() {
        String contentType = "application/json";

        DefaultRequestHeadersUriSpec result = spec.contentType(contentType);

        assertSame(spec, result);
        verify(mockRequestBuilder).contentType(contentType);
    }

    @Test
    void testContentTypeWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.contentType(null);
        });
    }

    @Test
    void testContentTypeWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.contentType("");
        });
    }

    @Test
    void testRetrieve() {
        ResponseSpec mockResponseSpec = mock(ResponseSpec.class);
        when(mockRequestBuilder.retrieve()).thenReturn(mockResponseSpec);

        ResponseSpec result = spec.retrieve();

        assertSame(mockResponseSpec, result);
        verify(mockRequestBuilder).retrieve();
    }

    @Test
    void testGetRequestBuilder() {
        HttpRequestBuilder builder = spec.getRequestBuilder();
        assertSame(mockRequestBuilder, builder);
    }

    @Test
    void testFluentChaining() {
        ResponseSpec mockResponseSpec = mock(ResponseSpec.class);
        when(mockRequestBuilder.retrieve()).thenReturn(mockResponseSpec);

        ResponseSpec result = spec
                .uri("/users/{id}", 123)
                .header("Accept", "application/json")
                .contentType("application/json")
                .cookie("sessionId", "abc123")
                .retrieve();

        assertSame(mockResponseSpec, result);
        verify(mockRequestBuilder).uri("/users/{id}", 123);
        verify(mockRequestBuilder, atLeastOnce()).header(anyString(), anyString());
        verify(mockRequestBuilder).contentType("application/json");
        verify(mockRequestBuilder).retrieve();
    }

    @Test
    void testComplexUriWithMultipleVariables() {
        String uri = "/api/{version}/users/{userId}/posts/{postId}";
        Map<String, Object> variables = new HashMap<>();
        variables.put("version", "v1");
        variables.put("userId", 123);
        variables.put("postId", 456);

        spec.uri(uri, variables);

        ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
        verify(mockRequestBuilder).uri(eq(uri), captor.capture());

        Object[] capturedArgs = captor.getValue();
        assertEquals(3, capturedArgs.length);
        assertEquals("v1", capturedArgs[0]);
        assertEquals(123, capturedArgs[1]);
        assertEquals(456, capturedArgs[2]);
    }

    @Test
    void testHeaderWithMixedNullValues() {
        String headerName = "Accept";
        String[] headerValues = {"application/json", null, "text/html"};

        spec.header(headerName, headerValues);

        // Should only add non-null values
        verify(mockRequestBuilder, times(2)).header(eq(headerName), anyString());
        verify(mockRequestBuilder).header(headerName, "application/json");
        verify(mockRequestBuilder).header(headerName, "text/html");
    }

    @Test
    void testTypicalGetRequest() {
        ResponseSpec mockResponseSpec = mock(ResponseSpec.class);
        when(mockRequestBuilder.retrieve()).thenReturn(mockResponseSpec);

        // Simulate a typical GET request
        ResponseSpec result = spec
                .uri("/api/users/{id}", 123)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer token123")
                .cookie("sessionId", "abc123")
                .retrieve();

        assertSame(mockResponseSpec, result);
        verify(mockRequestBuilder).uri("/api/users/{id}", 123);
        verify(mockRequestBuilder).header("Accept", "application/json");
        verify(mockRequestBuilder).header("Authorization", "Bearer token123");
        verify(mockRequestBuilder).retrieve();
    }

    @Test
    void testUriWithQueryParameters() {
        Function<UriBuilder, String> uriFunction = builder -> {
            builder.path("/api/users");
            builder.queryParam("page", "1");
            builder.queryParam("size", "10");
            builder.queryParam("sort", "name");
            return builder.build();
        };

        spec.uri(uriFunction);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<UriBuilder>> captor = ArgumentCaptor.forClass((Class) Consumer.class);
        verify(mockRequestBuilder).uri(captor.capture());
        assertNotNull(captor.getValue());
    }

    @Test
    void testMultipleCookies() {
        spec.cookie("sessionId", "abc123")
            .cookie("token", "xyz789")
            .cookie("preference", "dark-mode");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<MultiValueMap<String, String>>> captor = ArgumentCaptor.forClass((Class) Consumer.class);
        verify(mockRequestBuilder, times(3)).cookies(captor.capture());

        // Verify each cookie was added
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();
        for (Consumer<MultiValueMap<String, String>> consumer : captor.getAllValues()) {
            consumer.accept(cookies);
        }

        assertEquals("abc123", cookies.getFirst("sessionId"));
        assertEquals("xyz789", cookies.getFirst("token"));
        assertEquals("dark-mode", cookies.getFirst("preference"));
    }

    @Test
    void testHeadersConsumerModifiesHeaders() {
        Consumer<HttpHeaders> headersConsumer = headers -> {
            headers.add("Accept", "application/json");
            headers.add("Accept-Language", "en-US");
            headers.set("User-Agent", "TestClient/1.0");
        };

        spec.headers(headersConsumer);

        verify(mockRequestBuilder).headers(headersConsumer);
    }

    @Test
    void testUriWithNullMapValue() {
        String uri = "/users/{id}";
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", null);

        assertThrows(IllegalArgumentException.class, () -> {
            spec.uri(uri, variables);
        });
    }

    @Test
    void testEmptyHeaderValues() {
        String headerName = "Accept";
        String[] headerValues = {};

        DefaultRequestHeadersUriSpec result = spec.header(headerName, headerValues);

        assertSame(spec, result);
        verify(mockRequestBuilder, never()).header(anyString(), anyString());
    }
}
