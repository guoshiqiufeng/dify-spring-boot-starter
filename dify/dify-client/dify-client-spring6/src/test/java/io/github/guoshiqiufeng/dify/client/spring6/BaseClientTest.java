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
package io.github.guoshiqiufeng.dify.client.spring6;

import lombok.Getter;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/25 10:25
 */
@SuppressWarnings("unchecked")
public abstract class BaseClientTest {

    protected RestClientMock restClientMock = new RestClientMock();

    protected WebClientMock webClientMock = new WebClientMock();

    public void setup() {
        restClientMock.init();
        webClientMock.init();
    }

    @Getter
    public static class RestClientMock {
        protected RestClient restClient;

        protected RestClient.Builder restClientBuilder;

        protected RestClient.RequestHeadersSpec requestHeadersSpec;

        protected RestClient.RequestBodyUriSpec requestBodyUriSpec;

        protected RestClient.RequestBodySpec requestBodySpec;

        protected RestClient.ResponseSpec responseSpec;

        protected RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

        public RestClientMock() {
            // Create fresh mocks
            restClient = Mockito.mock(RestClient.class);
            restClientBuilder = Mockito.mock(RestClient.Builder.class);
            requestHeadersSpec = Mockito.mock(RestClient.RequestHeadersSpec.class);
            requestBodyUriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
            requestBodySpec = Mockito.mock(RestClient.RequestBodySpec.class);
            responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
            requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        }

        public void init() {
            // Clear any previous interactions with the mocks
            Mockito.reset(restClient, restClientBuilder, requestHeadersSpec,
                    requestBodyUriSpec, requestBodySpec, responseSpec, requestHeadersUriSpec);

            // Setup RestClient.Builder behavior for proper chaining
            when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
            when(restClientBuilder.defaultHeaders(any())).thenReturn(restClientBuilder);
            when(restClientBuilder.defaultCookies(any())).thenReturn(restClientBuilder);
            when(restClientBuilder.build()).thenReturn(restClient);

            // Setup RestClient behavior chain for POST
            when(restClient.post()).thenReturn(requestBodyUriSpec);
            when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
            when(requestBodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestBodySpec);
            when(requestBodyUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestBodySpec);

            // Handle header method properly
            when(requestBodySpec.header(anyString(), any(String[].class))).thenReturn(requestBodySpec);
            when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
            // Fix for headers(Consumer) method
            doReturn(requestBodySpec).when(requestBodySpec).headers(any(Consumer.class));
            when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);

            // Critical fix for the body method - try multiple approaches
            // Using doReturn instead of when for body methods
            doReturn(requestBodySpec).when(requestBodySpec).body(any());
            doReturn(requestBodySpec).when(requestBodySpec).body(any(Object.class));
            // Also try with specific class to cover all bases
            when(requestBodySpec.body(any())).thenReturn(requestBodySpec);

            // Ensure retrieve() works properly on all mock objects
            when(requestBodySpec.retrieve()).thenReturn(responseSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

            // Setup RestClient behavior chain for GET
            when(restClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
            when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
            when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);

            // Fix for RequestHeadersSpec methods
            when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
            // Critical fix: Add proper mocking for headers method on RequestHeadersSpec
            doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any(Consumer.class));
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

            // Setup RestClient behavior chain for DELETE
            when(restClient.delete()).thenReturn(requestHeadersUriSpec);
            // Handle method for various HTTP methods
            when(restClient.method(any())).thenReturn(requestBodyUriSpec);

            // Setup RestClient behavior chain for PATCH
            when(restClient.patch()).thenReturn(requestBodyUriSpec);

            // Ensure response handling works
            when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
            when(responseSpec.onStatus(any())).thenReturn(responseSpec);
        }
    }

    @Getter
    public static class WebClientMock {
        protected WebClient webClient;

        protected WebClient.Builder webClientBuilder;

        protected WebClient.RequestHeadersSpec requestHeadersSpec;

        protected WebClient.RequestBodyUriSpec requestBodyUriSpec;

        protected WebClient.RequestBodySpec requestBodySpec;

        protected WebClient.ResponseSpec responseSpec;

        protected WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

        public WebClientMock() {
            webClient = mock(WebClient.class);
            webClientBuilder = mock(WebClient.Builder.class);
            requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
            requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
            requestBodySpec = mock(WebClient.RequestBodySpec.class);
            responseSpec = mock(WebClient.ResponseSpec.class);
            requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        }

        public void init() {
            // Clear any previous interactions
            Mockito.reset(webClient, webClientBuilder, requestHeadersSpec,
                    requestBodyUriSpec, requestBodySpec, responseSpec, requestHeadersUriSpec);

            // Setup WebClient.Builder behavior for proper chaining
            when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
            when(webClientBuilder.defaultHeaders(any())).thenReturn(webClientBuilder);
            when(webClientBuilder.defaultCookies(any())).thenReturn(webClientBuilder);
            when(webClientBuilder.build()).thenReturn(webClient);

            // Setup WebClient behavior chain for POST
            when(webClient.post()).thenReturn(requestBodyUriSpec);
            when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
            when(requestBodyUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestBodySpec);
            when(requestBodyUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestBodySpec);

            // Handle header method properly
            when(requestBodySpec.header(anyString(), any(String[].class))).thenReturn(requestBodySpec);
            when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
            when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);

            when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
            when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

            // Setup WebClient behavior chain for GET
            when(webClient.get()).thenReturn(requestHeadersUriSpec);
            when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
            when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);

            when(requestHeadersSpec.header(anyString(), any(String[].class))).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
            when(requestHeadersSpec.headers(any(Consumer.class))).thenReturn(requestHeadersSpec);

            when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

            // Setup WebClient behavior chain for DELETE
            when(webClient.delete()).thenReturn(requestHeadersUriSpec);

            // Setup WebClient behavior chain for PATCH
            when(webClient.patch()).thenReturn(requestBodyUriSpec);

            // Make sure retrieve() always returns responseSpec
            when(requestBodySpec.retrieve()).thenReturn(responseSpec);

            // Ensure response handling works
            when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        }
    }
}
