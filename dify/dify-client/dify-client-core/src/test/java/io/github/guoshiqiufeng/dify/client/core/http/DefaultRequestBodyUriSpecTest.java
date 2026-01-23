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

import io.github.guoshiqiufeng.dify.client.core.util.LinkedMultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.util.MultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestBodySpec;
import io.github.guoshiqiufeng.dify.client.core.web.client.ResponseSpec;
import io.github.guoshiqiufeng.dify.client.core.web.util.UriBuilder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DefaultRequestBodyUriSpec
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/12
 */
@SuppressWarnings("unchecked")
class DefaultRequestBodyUriSpecTest {

    private HttpRequestBuilder mockRequestBuilder;
    private DefaultRequestBodyUriSpec spec;

    @BeforeEach
    void setUp() {
        mockRequestBuilder = mock(HttpRequestBuilder.class);
        spec = new DefaultRequestBodyUriSpec(mockRequestBuilder);
    }

    @Test
    void testConstructorWithNullBuilder() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DefaultRequestBodyUriSpec(null);
        });
    }

    @Test
    void testUriWithURI() throws Exception {
        URI uri = new URI("http://example.com/users/123");

        RequestBodySpec result = spec.uri(uri);

        assertSame(spec, result);
        verify(mockRequestBuilder).uri(uri.toString());
    }

    @Test
    void testUriWithNullURI() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.uri((URI) null);
        });
    }

    @Test
    void testUriWithStringAndVariables() {
        String uri = "/users/{id}";
        Object[] variables = {123};

        RequestBodySpec result = spec.uri(uri, variables);

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

        RequestBodySpec result = spec.uri(uri, variables);

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

        RequestBodySpec result = spec.uri(uri, variables);

        assertSame(spec, result);
        verify(mockRequestBuilder).uri(uri);
    }

    @Test
    void testUriWithNullMap() {
        String uri = "/users";

        RequestBodySpec result = spec.uri(uri, (Map<String, Object>) null);

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
    @SuppressWarnings("all")
    void testUriWithFunction() {
        Function<UriBuilder, URI> uriFunction = builder -> {
            builder.path("/users");
            builder.queryParam("page", "1");
            return builder.build();
        };

        RequestBodySpec result = spec.uri(uriFunction);

        assertSame(spec, result);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<UriBuilder>> captor = ArgumentCaptor.forClass((Class) Consumer.class);
        verify(mockRequestBuilder).uri(captor.capture());
        assertNotNull(captor.getValue());
    }

    @Test
    void testUriWithNullFunction() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.uri((Function<UriBuilder, URI>) null);
        });
    }

    @Test
    void testUriWithVariablesAndFunction() {
        String uri = "/users/{id}";
        Object[] variables = {123};
        Function<UriBuilder, URI> uriFunction = builder -> {
            builder.queryParam("page", "1");
            return builder.build();
        };

        doAnswer(invocation -> {
            Consumer<UriBuilder> consumer = invocation.getArgument(0);
            UriBuilder mockBuilder = mock(UriBuilder.class);
            when(mockBuilder.path(anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build(any())).thenReturn(URI.create("http://example.com"));
            consumer.accept(mockBuilder);
            verify(mockBuilder).path(uri);
            verify(mockBuilder).build(variables);
            return null;
        }).when(mockRequestBuilder).uri(any(Consumer.class));

        RequestBodySpec result = spec.uri(uri, variables, uriFunction);

        assertSame(spec, result);
        verify(mockRequestBuilder).uri(any(Consumer.class));
    }

    @Test
    void testUriWithNullVariablesAndFunction() {
        String uri = "/users";
        Function<UriBuilder, URI> uriFunction = builder -> {
            builder.queryParam("page", "1");
            return builder.build();
        };

        doAnswer(invocation -> {
            Consumer<UriBuilder> consumer = invocation.getArgument(0);
            UriBuilder mockBuilder = mock(UriBuilder.class);
            when(mockBuilder.path(anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(URI.create("http://example.com"));
            consumer.accept(mockBuilder);
            verify(mockBuilder).path(uri);
            verify(mockBuilder, never()).build(any(Object[].class));
            return null;
        }).when(mockRequestBuilder).uri(any(Consumer.class));

        RequestBodySpec result = spec.uri(uri, null, uriFunction);

        assertSame(spec, result);
        verify(mockRequestBuilder).uri(any(Consumer.class));
    }

    @Test
    void testUriWithEmptyVariablesAndFunction() {
        String uri = "/users";
        Object[] variables = {};
        Function<UriBuilder, URI> uriFunction = builder -> {
            builder.queryParam("page", "1");
            return builder.build();
        };

        doAnswer(invocation -> {
            Consumer<UriBuilder> consumer = invocation.getArgument(0);
            UriBuilder mockBuilder = mock(UriBuilder.class);
            when(mockBuilder.path(anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(URI.create("http://example.com"));
            consumer.accept(mockBuilder);
            verify(mockBuilder).path(uri);
            verify(mockBuilder, never()).build(any(Object[].class));
            return null;
        }).when(mockRequestBuilder).uri(any(Consumer.class));

        RequestBodySpec result = spec.uri(uri, variables, uriFunction);

        assertSame(spec, result);
        verify(mockRequestBuilder).uri(any(Consumer.class));
    }

    @Test
    void testUriWithVariablesAndFunctionNullUri() {
        Object[] variables = {123};
        Function<UriBuilder, URI> uriFunction = UriBuilder::build;

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
        String headerName = "Content-Type";
        String headerValue = "application/json";

        RequestBodySpec result = spec.header(headerName, headerValue);

        assertSame(spec, result);
        verify(mockRequestBuilder).header(headerName, headerValue);
    }

    @Test
    void testHeaderWithMultipleValues() {
        String headerName = "Accept";
        String[] headerValues = {"application/json", "text/html"};

        RequestBodySpec result = spec.header(headerName, headerValues);

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
        String headerName = "Content-Type";

        RequestBodySpec result = spec.header(headerName, (String[]) null);

        assertSame(spec, result);
        verify(mockRequestBuilder, never()).header(anyString(), anyString());
    }

    @Test
    void testHeadersWithConsumer() {
        Consumer<HttpHeaders> headersConsumer = headers -> {
            headers.add("Content-Type", "application/json");
            headers.add("Accept", "text/html");
        };

        RequestBodySpec result = spec.headers(headersConsumer);

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
    @SuppressWarnings("all")
    void testCookie() {
        String name = "sessionId";
        String value = "abc123";

        RequestBodySpec result = spec.cookie(name, value);

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

        RequestBodySpec result = spec.cookie(name, null);

        assertSame(spec, result);
        verify(mockRequestBuilder, never()).cookies(any());
    }

    @Test
    void testCookiesWithConsumer() {
        Consumer<MultiValueMap<String, String>> cookiesConsumer = cookies -> {
            cookies.add("sessionId", "abc123");
            cookies.add("token", "xyz789");
        };

        RequestBodySpec result = spec.cookies(cookiesConsumer);

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

        RequestBodySpec result = spec.contentType(contentType);

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
    void testContentLength() {
        long contentLength = 1024L;

        RequestBodySpec result = spec.contentLength(contentLength);

        assertSame(spec, result);
        verify(mockRequestBuilder).header("Content-Length", "1024");
    }

    @Test
    void testContentLengthWithZero() {
        RequestBodySpec result = spec.contentLength(0);

        assertSame(spec, result);
        verify(mockRequestBuilder).header("Content-Length", "0");
    }

    @Test
    void testContentLengthWithNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.contentLength(-1);
        });
    }

    @Test
    void testBody() {
        TestPojo body = new TestPojo("John", 30);

        RequestBodySpec result = spec.body(body);

        assertSame(spec, result);
        verify(mockRequestBuilder).body(body);
    }

    @Test
    void testBodyWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            spec.body(null);
        });
    }

    @Test
    void testBodyWithTypeReference() {
        TestPojo body = new TestPojo("John", 30);
        TypeReference<TestPojo> typeRef = new TypeReference<TestPojo>() {
        };

        RequestBodySpec result = spec.body(body, typeRef);

        assertSame(spec, result);
        verify(mockRequestBuilder).body(body);
    }

    @Test
    void testBodyWithTypeReferenceNullBody() {
        TypeReference<TestPojo> typeRef = new TypeReference<TestPojo>() {
        };

        assertThrows(IllegalArgumentException.class, () -> {
            spec.body(null, typeRef);
        });
    }

    @Test
    void testBodyWithTypeReferenceNullTypeRef() {
        TestPojo body = new TestPojo("John", 30);

        assertThrows(IllegalArgumentException.class, () -> {
            spec.body(body, null);
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
        TestPojo body = new TestPojo("John", 30);
        ResponseSpec mockResponseSpec = mock(ResponseSpec.class);
        when(mockRequestBuilder.retrieve()).thenReturn(mockResponseSpec);

        ResponseSpec result = spec
                .uri("/users/{id}", 123)
                .header("Content-Type", "application/json")
                .contentType("application/json")
                .contentLength(100)
                .cookie("sessionId", "abc123")
                .body(body)
                .retrieve();

        assertSame(mockResponseSpec, result);
        verify(mockRequestBuilder).uri("/users/{id}", 123);
        verify(mockRequestBuilder, atLeastOnce()).header(anyString(), anyString());
        verify(mockRequestBuilder).contentType("application/json");
        verify(mockRequestBuilder).body(body);
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

    // Test POJO class
    @Setter
    @Getter
    public static class TestPojo {
        private String name;
        private int age;

        public TestPojo() {
        }

        public TestPojo(String name, int age) {
            this.name = name;
            this.age = age;
        }

    }
}
