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
package io.github.guoshiqiufeng.dify.client.spring5;

import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/24 17:07
 */
@SuppressWarnings("unchecked")
public abstract class BaseClientTest {

    protected WebClient webClientMock;

    protected WebClient.Builder webClientBuilderMock;

    protected WebClient.RequestHeadersSpec requestHeadersSpecMock;

    protected WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    protected WebClient.RequestBodySpec requestBodySpecMock;

    protected WebClient.ResponseSpec responseSpecMock;

    protected WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    public void setup() {
        webClientMock = mock(WebClient.class);
        webClientBuilderMock = mock(WebClient.Builder.class);
        requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
        requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        responseSpecMock = mock(WebClient.ResponseSpec.class);
        requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);

        // Setup WebClient.Builder behavior for proper chaining
        when(webClientBuilderMock.baseUrl(anyString())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.defaultHeaders(any())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.defaultCookies(any())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.build()).thenReturn(webClientMock);

        // Setup WebClient mock behavior chain for POST
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.headers(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.contentType(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        // Setup WebClient mock behavior chain for GET
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.headers(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);

    }
}
