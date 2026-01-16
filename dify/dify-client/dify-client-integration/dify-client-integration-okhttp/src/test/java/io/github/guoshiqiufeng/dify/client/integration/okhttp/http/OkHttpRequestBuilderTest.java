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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.map.LinkedMultiValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OkHttpRequestBuilder
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class OkHttpRequestBuilderTest {

    @Mock
    private JavaHttpClient client;

    @Mock
    private JsonMapper jsonMapper;

    private OkHttpRequestBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new OkHttpRequestBuilder(client, jsonMapper, "GET");
    }

    @Test
    void testConstructor() {
        assertNotNull(builder);
    }

    @Test
    void testUriWithString() {
        var result = builder.uri("/api/test");
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testUriWithParams() {
        var result = builder.uri("/api/users/{id}", 123);
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testUriWithConsumer() {
        var result = builder.uri(uriBuilder -> {
            uriBuilder.path("/api/test");
            uriBuilder.queryParam("page", "1");
        });
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testHeader() {
        var result = builder.header("Content-Type", "application/json");
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testHeadersWithMap() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");
        headers.put("X-Custom", "value");

        var result = builder.headers(headers);
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testHeadersWithConsumer() {
        var result = builder.headers(headers -> {
            headers.add("Content-Type", "application/json");
            headers.add("Authorization", "Bearer token");
        });
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testCookiesWithConsumer() {
        var result = builder.cookies(cookies -> {
            cookies.add("sessionId", "abc123");
            cookies.add("userId", "user456");
        });
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testQueryParam() {
        var result = builder.queryParam("page", "1");
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testQueryParams() {
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("limit", "10");

        var result = builder.queryParams(params);
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testBody() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "test");

        var result = builder.body(body);
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testMultipart() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("file", "data");

        var result = builder.multipart(formData);
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testMethodChaining() {
        var result = builder
                .uri("/api/test")
                .header("Content-Type", "application/json")
                .queryParam("page", "1")
                .body(new HashMap<>());

        assertSame(builder, result);
    }

    @Test
    void testRetrieve() {
        var responseSpec = builder.retrieve();
        assertNotNull(responseSpec);
    }

    @Test
    void testHeadersConsumerWithNullValues() {
        var result = builder.headers(headers -> {
            headers.add("Content-Type", null);
            headers.add("Authorization", "Bearer token");
        });
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testHeadersConsumerWithEmptyValues() {
        var result = builder.headers(headers -> {
            // Add empty list - should not add to headers map
        });
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testCookiesConsumerWithNullValues() {
        var result = builder.cookies(cookies -> {
            cookies.add("sessionId", null);
            cookies.add("userId", "user456");
        });
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testCookiesConsumerWithEmptyValues() {
        var result = builder.cookies(cookies -> {
            // Add empty list - should not add to cookies map
        });
        assertNotNull(result);
        assertSame(builder, result);
    }
}
