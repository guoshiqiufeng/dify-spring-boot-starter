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
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for creating Java HTTP client instances using OkHttp.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class JavaHttpClientFactory implements HttpClientFactory {

    private final OkHttpClient.Builder builder;

    private final JsonMapper jsonMapper;

    private final HttpHeaders defaultHeaders;

    private final List<Interceptor> interceptors;

    /**
     * Constructor with JsonMapper.
     *
     * @param jsonMapper the JSON mapper
     */
    public JavaHttpClientFactory(JsonMapper jsonMapper) {
        this(new OkHttpClient.Builder(), jsonMapper, new HttpHeaders(), new ArrayList<>());
    }

    /**
     * Constructor with JsonMapper.
     *
     * @param jsonMapper the JSON mapper
     */
    public JavaHttpClientFactory(OkHttpClient.Builder builder, JsonMapper jsonMapper) {
        this(builder, jsonMapper, new HttpHeaders(), new ArrayList<>());
    }

    private JavaHttpClientFactory(OkHttpClient.Builder builder, JsonMapper jsonMapper, HttpHeaders defaultHeaders, List<Interceptor> interceptors) {
        this.builder = builder;
        this.jsonMapper = jsonMapper;
        this.defaultHeaders = defaultHeaders;
        this.interceptors = interceptors;
    }

    @Override
    public HttpClient createClient(String baseUrl, DifyProperties.ClientConfig clientConfig) {
        if (!defaultHeaders.isEmpty() || !interceptors.isEmpty()) {
            return new JavaHttpClient(baseUrl, clientConfig, jsonMapper, defaultHeaders, interceptors);
        }
        return new JavaHttpClient(baseUrl, clientConfig, jsonMapper);
    }

    @Override
    public HttpClientFactory defaultHeader(String key, String value) {
        HttpHeaders newHeaders = new HttpHeaders(this.defaultHeaders);
        newHeaders.add(key, value);
        return new JavaHttpClientFactory(builder, jsonMapper, newHeaders, interceptors);
    }

    @Override
    public HttpClientFactory interceptor(Object interceptor) {
        if (!(interceptor instanceof Interceptor)) {
            throw new IllegalArgumentException("Interceptor must be an instance of okhttp3.Interceptor");
        }
        List<Interceptor> newInterceptors = new ArrayList<>(this.interceptors);
        newInterceptors.add((Interceptor) interceptor);
        return new JavaHttpClientFactory(builder, jsonMapper, defaultHeaders, newInterceptors);
    }
}
