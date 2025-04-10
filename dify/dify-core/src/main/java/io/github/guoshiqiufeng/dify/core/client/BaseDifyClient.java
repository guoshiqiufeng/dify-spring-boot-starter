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
package io.github.guoshiqiufeng.dify.core.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/7 16:10
 */
@Slf4j
public abstract class BaseDifyClient {

    public static final String REQUEST_BODY_NULL_ERROR = "The request body can not be null.";

    private static final String DEFAULT_BASE_URL = "http://localhost";

    protected final ResponseErrorHandler responseErrorHandler;

    protected final RestClient restClient;

    protected final WebClient webClient;

    public BaseDifyClient() {
        this(DEFAULT_BASE_URL);
    }

    public BaseDifyClient(String baseUrl) {
        this(baseUrl, RestClient.builder(), WebClient.builder());
    }

    public BaseDifyClient(String baseUrl, RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
        this(baseUrl, restClientBuilder, webClientBuilder, new DifyChatResponseErrorHandler());
    }

    public BaseDifyClient(String baseUrl, RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder, ResponseErrorHandler responseErrorHandler) {
        this.responseErrorHandler = responseErrorHandler;

        Consumer<HttpHeaders> defaultHeaders = headers -> {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        };

        this.restClient = restClientBuilder.baseUrl(baseUrl).defaultHeaders(defaultHeaders).build();

        this.webClient = webClientBuilder.baseUrl(baseUrl).defaultHeaders(defaultHeaders).build();
    }

    private static class DifyChatResponseErrorHandler implements ResponseErrorHandler {


        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().isError();
        }


        public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
            if (response.getStatusCode().isError()) {
                int statusCode = response.getStatusCode().value();
                String statusText = response.getStatusText();
                String message = StreamUtils.copyToString(response.getBody(), java.nio.charset.StandardCharsets.UTF_8);
                log.warn(String.format("URI: %s, Method: %s, Status: [%s] %s - %s", url, method, statusCode, statusText, message));
                throw new RuntimeException(String.format("[%s] %s - %s", statusCode, statusText, message));
            }
        }

    }
}
