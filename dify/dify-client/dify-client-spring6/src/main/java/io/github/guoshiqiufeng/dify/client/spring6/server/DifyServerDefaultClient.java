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
package io.github.guoshiqiufeng.dify.client.spring6.server;

import io.github.guoshiqiufeng.dify.client.spring6.base.BaseDifyDefaultClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.client.RequestSupplier;
import io.github.guoshiqiufeng.dify.server.constant.ServerUriConstant;
import io.github.guoshiqiufeng.dify.server.dto.request.DifyLoginRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/9 10:14
 */
@Slf4j
public class DifyServerDefaultClient extends BaseDifyDefaultClient implements DifyServerClient {

    private final DifyProperties.Server difyServerProperties;

    private final BaseDifyServerToken difyServerToken;

    public DifyServerDefaultClient(DifyProperties.Server difyServerProperties) {
        this(difyServerProperties, new DifyServerTokenDefault());
    }

    public DifyServerDefaultClient(DifyProperties.Server difyServerProperties, BaseDifyServerToken difyServerToken) {
        super();
        this.difyServerProperties = difyServerProperties;
        this.difyServerToken = difyServerToken;
    }

    public DifyServerDefaultClient(DifyProperties.Server difyServerProperties, BaseDifyServerToken difyServerToken, String baseUrl) {
        super(baseUrl);
        this.difyServerProperties = difyServerProperties;
        this.difyServerToken = difyServerToken;
    }

    public DifyServerDefaultClient(DifyProperties.Server difyServerProperties, BaseDifyServerToken difyServerToken, String baseUrl, DifyProperties.ClientConfig clientConfig, RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
        super(baseUrl, clientConfig, restClientBuilder, webClientBuilder);
        this.difyServerProperties = difyServerProperties;
        this.difyServerToken = difyServerToken;
    }

    @Override
    public List<AppsResponse> apps(String mode, String name) {
        List<AppsResponse> result = new ArrayList<>();
        appPages(mode, name, 1, result);
        return result;
    }

    @Override
    public AppsResponse app(String appId) {
        return executeWithRetry(
                () -> restClient.get()
                        .uri(ServerUriConstant.APPS + "/{appId}", appId)
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(AppsResponse.class)
        );
    }

    @Override
    public List<ApiKeyResponse> getAppApiKey(String appId) {
        ApiKeyResultResponse tmp = executeWithRetry(
                () -> restClient.get()
                        .uri(ServerUriConstant.APPS + "/{appId}/api-keys", appId)
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(ApiKeyResultResponse.class)
        );
        if (tmp == null) {
            return new ArrayList<>();
        }
        return tmp.getData();
    }

    @Override
    public List<ApiKeyResponse> initAppApiKey(String appId) {
        ApiKeyResponse apiKeyResponseVO = executeWithRetry(
                () -> restClient.post()
                        .uri(ServerUriConstant.APPS + "/{appId}/api-keys", appId)
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(ApiKeyResponse.class)
        );
        return apiKeyResponseVO != null ? new ArrayList<>(List.of(apiKeyResponseVO)) : null;
    }

    @Override
    public List<DatasetApiKeyResponse> getDatasetApiKey() {
        DatasetApiKeyResult result = executeWithRetry(
                () -> restClient.get()
                        .uri(ServerUriConstant.DATASETS + "/api-keys")
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(DatasetApiKeyResult.class)
        );
        return result != null ? result.getData() : null;
    }

    @Override
    public List<DatasetApiKeyResponse> initDatasetApiKey() {
        DatasetApiKeyResponse result = executeWithRetry(
                () -> restClient.post()
                        .uri(ServerUriConstant.DATASETS + "/api-keys")
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(DatasetApiKeyResponse.class)
        );
        return result != null ? new ArrayList<>(List.of(result)) : null;
    }

    private void appPages(String mode, String name, int page, List<AppsResponse> result) {
        AppsResponseResult response = executeWithRetry(
                () -> restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(ServerUriConstant.APPS)
                                .queryParam("page", page)
                                .queryParam("limit", 100)
                                .queryParamIfPresent("mode", Optional.ofNullable(mode).filter(m -> !m.isEmpty()))
                                .queryParamIfPresent("name", Optional.ofNullable(name).filter(m -> !m.isEmpty()))
                                .build())
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(AppsResponseResult.class)
        );

        if (response == null) {
            return;
        }
        List<AppsResponse> data = response.getData();
        if (data != null) {
            result.addAll(data);
        }

        if (Boolean.TRUE.equals(response.getHasMore())) {
            appPages(mode, name, page + 1, result);
        }
    }

    private void addAuthorizationHeader(HttpHeaders headers) {
        difyServerToken.addAuthorizationHeader(headers, this);
    }

    private <T> T executeWithRetry(RequestSupplier<T> supplier) {
        return difyServerToken.executeWithRetry(supplier, this);
    }

    @Override
    public LoginResponse login() {
        Assert.notNull(difyServerProperties, "The difyServerProperties can not be null.");
        DifyLoginRequest requestVO = DifyLoginRequest.build(difyServerProperties.getEmail(), difyServerProperties.getPassword());
        LoginResultResponse result = restClient.post()
                .uri(ServerUriConstant.LOGIN)
                .body(requestVO)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(LoginResultResponse.class);
        return processLoginResult(result);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        Map<String, String> requestVO = Map.of("refresh_token", refreshToken);
        LoginResultResponse result = restClient.post()
                .uri(ServerUriConstant.REFRESH_TOKEN)
                .body(requestVO)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(LoginResultResponse.class);
        return processLoginResult(result);
    }

    /**
     * Processes login/refresh response and extracts data
     *
     * @param result the login result
     * @return the login response data or null
     */
    private LoginResponse processLoginResult(LoginResultResponse result) {
        return Optional.ofNullable(result)
                .filter(r -> DifyResult.SUCCESS.equals(r.getResult()))
                .map(LoginResultResponse::getData)
                .orElse(null);
    }
}
