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
package io.github.guoshiqiufeng.dify.support.impl;

import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.map.MultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.web.client.*;

import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Base test class for client tests with HttpClient mocking.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/1/13
 */
@SuppressWarnings("unchecked")
public abstract class BaseClientTest {

    protected HttpClient httpClientMock;
    @SuppressWarnings("all")
    protected RequestHeadersUriSpec requestHeadersUriSpecMock;

    protected RequestBodyUriSpec requestBodyUriSpecMock;

    protected RequestBodySpec requestBodySpecMock;
    @SuppressWarnings("all")
    protected RequestHeadersSpec requestHeadersSpecMock;

    protected ResponseSpec responseSpecMock;

    public void setup() {
        httpClientMock = mock(HttpClient.class);
        requestHeadersUriSpecMock = mock(RequestHeadersUriSpec.class);
        requestBodyUriSpecMock = mock(RequestBodyUriSpec.class);
        requestBodySpecMock = mock(RequestBodySpec.class);
        requestHeadersSpecMock = mock(RequestHeadersSpec.class);
        responseSpecMock = mock(ResponseSpec.class);

        // Setup HttpClient mock behavior for GET
        when(httpClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString(), anyMap())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersUriSpecMock.uri(any(URI.class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenReturn(requestHeadersSpecMock);

        // Setup HttpClient mock behavior for POST
        when(httpClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
        when(requestBodyUriSpecMock.uri(anyString(), any(Object[].class))).thenReturn(requestBodySpecMock);
        when(requestBodyUriSpecMock.uri(anyString(), any(Object.class))).thenReturn(requestBodySpecMock);
        when(requestBodyUriSpecMock.uri(any(URI.class))).thenReturn(requestBodySpecMock);
        when(requestBodyUriSpecMock.uri(any(Function.class))).thenReturn(requestBodySpecMock);

        // Setup HttpClient mock behavior for PUT
        when(httpClientMock.put()).thenReturn(requestBodyUriSpecMock);

        // Setup HttpClient mock behavior for DELETE
        when(httpClientMock.delete()).thenReturn(requestHeadersUriSpecMock);

        // Setup HttpClient mock behavior for PATCH
        when(httpClientMock.patch()).thenReturn(requestBodyUriSpecMock);

        // Setup HttpClient mock behavior for HEAD
        when(httpClientMock.head()).thenReturn(requestHeadersUriSpecMock);

        // Setup HttpClient mock behavior for OPTIONS
        when(httpClientMock.options()).thenReturn(requestHeadersUriSpecMock);

        // Setup method() for custom HTTP methods
        when(httpClientMock.method(any())).thenReturn(requestBodyUriSpecMock);

        // Setup RequestHeadersSpec behavior
        when(requestHeadersSpecMock.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        doReturn(requestHeadersSpecMock).when(requestHeadersSpecMock).headers(any(Consumer.class));
        doReturn(requestHeadersSpecMock).when(requestHeadersSpecMock).cookies(any(Consumer.class));
        when(requestHeadersSpecMock.cookie(anyString(), anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.contentType(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        // Setup RequestBodySpec behavior
        when(requestBodySpecMock.header(anyString(), any(String[].class))).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.header(anyString(), anyString())).thenReturn(requestBodySpecMock);
        doReturn(requestBodySpecMock).when(requestBodySpecMock).headers(any(Consumer.class));
        doReturn(requestBodySpecMock).when(requestBodySpecMock).cookies(any(Consumer.class));
        when(requestBodySpecMock.cookie(anyString(), anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.contentType(anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.contentLength(anyLong())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.body(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.body(any(), any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock);

        // Setup ResponseSpec behavior
        when(responseSpecMock.onStatus(any())).thenReturn(responseSpecMock);
        // Mock response methods - these will be overridden in specific tests as needed
        // Note: body() returns T directly, not Mono<T>
        when(responseSpecMock.body(any(Class.class))).thenReturn(null);
        //when(responseSpecMock.body(any())).thenReturn(null);
        when(responseSpecMock.toEntity(any(Class.class))).thenReturn(null);
        //when(responseSpecMock.toEntity(any())).thenReturn(null);
        when(responseSpecMock.toBodilessEntity()).thenReturn(null);
        when(responseSpecMock.bodyToFlux(any(Class.class))).thenReturn(null);
        //when(responseSpecMock.bodyToFlux(any())).thenReturn(null);
    }
}
