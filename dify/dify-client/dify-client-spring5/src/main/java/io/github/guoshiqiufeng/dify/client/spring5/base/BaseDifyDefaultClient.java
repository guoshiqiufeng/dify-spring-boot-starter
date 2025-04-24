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
package io.github.guoshiqiufeng.dify.client.spring5.base;

import io.github.guoshiqiufeng.dify.client.spring5.utils.DifyExchangeStrategies;
import io.github.guoshiqiufeng.dify.core.client.BaseDifyClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
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
public abstract class BaseDifyDefaultClient extends BaseDifyClient {

    protected final ResponseErrorHandler responseErrorHandler;

    protected final WebClient webClient;

    public BaseDifyDefaultClient() {
        this(DEFAULT_BASE_URL, new DifyProperties.ClientConfig());
    }

    public BaseDifyDefaultClient(String baseUrl) {
        this(baseUrl, new DifyProperties.ClientConfig());
    }

    public BaseDifyDefaultClient(DifyProperties.ClientConfig clientConfig) {
        this(DEFAULT_BASE_URL, clientConfig);
    }

    public BaseDifyDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig) {
        this(baseUrl, clientConfig, WebClient.builder());
    }

    public BaseDifyDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, WebClient.Builder webClientBuilder) {
        this(baseUrl, clientConfig, webClientBuilder, new DifyResponseErrorHandler());
    }

    public BaseDifyDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, WebClient.Builder webClientBuilder, ResponseErrorHandler responseErrorHandler) {
        this.responseErrorHandler = responseErrorHandler;

        Consumer<HttpHeaders> defaultHeaders = headers -> {
            headers.setContentType(MediaType.APPLICATION_JSON);
            List<MediaType> acceptableMediaTypes = new java.util.ArrayList<>();
            acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
            headers.setAccept(acceptableMediaTypes);
        };
        if (clientConfig != null && clientConfig.getSkipNull()) {
            DifyExchangeStrategies.exchangeStrategies().accept(webClientBuilder);
        }
        this.webClient = webClientBuilder.baseUrl(baseUrl).defaultHeaders(defaultHeaders).build();
    }

    private static class DifyResponseErrorHandler implements ResponseErrorHandler {


        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().isError();
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {

        }

        @Override
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

    /**
     * Gets the WebClient instance used by this client.
     * This method is primarily intended for testing purposes.
     *
     * @return the WebClient instance
     */
    public WebClient getWebClient() {
        return this.webClient;
    }
}
