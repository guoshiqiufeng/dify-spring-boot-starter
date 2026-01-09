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
package io.github.guoshiqiufeng.dify.support.impl.base;

import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import io.github.guoshiqiufeng.dify.core.client.BaseDifyClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.exception.DiftClientExceptionEnum;
import io.github.guoshiqiufeng.dify.core.exception.DifyClientException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

/**
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30 09:31
 */
@Slf4j
public class BaseDifyDefaultClient implements BaseDifyClient {

    protected final ResponseErrorHandler responseErrorHandler;

    protected final HttpClient httpClient;

    public BaseDifyDefaultClient(HttpClient httpClient) {
        this(httpClient, new DifyResponseErrorHandler());
    }

    public BaseDifyDefaultClient(HttpClient httpClient, ResponseErrorHandler responseErrorHandler) {
        this.responseErrorHandler = responseErrorHandler;
        this.httpClient = httpClient;
    }

    public BaseDifyDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, HttpClientFactory httpClientFactory) {
        this(baseUrl, clientConfig, httpClientFactory, new DifyResponseErrorHandler());
    }

    public BaseDifyDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, HttpClientFactory httpClientFactory, ResponseErrorHandler responseErrorHandler) {
        this.responseErrorHandler = responseErrorHandler;
        this.httpClient = httpClientFactory.createClient(baseUrl, clientConfig);
    }

    private static class DifyResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public Predicate<Integer> getStatusPredicate() {
            return status -> (status < 200 || status >= 300);
        }

        @Override
        public void handle(HttpResponse<?> response) throws Exception {
            int statusCode = response.getStatusCode();
            Object body = response.getBody();
            String message = body != null ? body.toString() : "";
            log.error("【Dify】请求错误，状态码：{}，错误信息：{}", statusCode, message);
            switch (statusCode) {
                case 401:
                    throw new DifyClientException(DiftClientExceptionEnum.UNAUTHORIZED);
                case 404:
                    throw new DifyClientException(DiftClientExceptionEnum.NOT_FOUND);
                default:
                    throw new RuntimeException(String.format("[%s] %s", statusCode, message));
            }
        }
    }
}
