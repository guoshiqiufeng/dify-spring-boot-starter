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
package io.github.guoshiqiufeng.dify.client.spring5.server;

import io.github.guoshiqiufeng.dify.client.spring5.base.BaseDifyDefaultClient;
import io.github.guoshiqiufeng.dify.client.spring5.utils.WebClientUtil;
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
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

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

    public DifyServerDefaultClient(DifyProperties.Server difyServerProperties, BaseDifyServerToken difyServerToken, String baseUrl, DifyProperties.ClientConfig clientConfig, WebClient.Builder webClientBuilder) {
        super(baseUrl, clientConfig, webClientBuilder);
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
                () -> webClient.get()
                        .uri(ServerUriConstant.APPS + "/{appId}", appId)
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                        .bodyToMono(AppsResponse.class)
                        .block()
        );
    }

    @Override
    public List<ApiKeyResponse> getAppApiKey(String appId) {
        ApiKeyResultResponse tmp = executeWithRetry(
                () -> webClient.get()
                        .uri(ServerUriConstant.APPS + "/{appId}/api-keys", appId)
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                        .bodyToMono(ApiKeyResultResponse.class)
                        .block()
        );
        if (tmp == null) {
            return new ArrayList<>();
        }
        return tmp.getData();
    }

    @Override
    public List<ApiKeyResponse> initAppApiKey(String appId) {
        ApiKeyResponse apiKeyResponseVO = executeWithRetry(
                () -> webClient.post()
                        .uri(ServerUriConstant.APPS + "/{appId}/api-keys", appId)
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                        .bodyToMono(ApiKeyResponse.class)
                        .block()
        );
        ArrayList<ApiKeyResponse> objects = new ArrayList<>();
        objects.add(apiKeyResponseVO);
        return apiKeyResponseVO != null ? objects : null;
    }

    @Override
    public List<DatasetApiKeyResponse> getDatasetApiKey() {
        DatasetApiKeyResult result = executeWithRetry(
                () -> webClient.get()
                        .uri(ServerUriConstant.DATASETS + "/api-keys")
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                        .bodyToMono(DatasetApiKeyResult.class)
                        .block()
        );
        return result != null ? result.getData() : null;
    }

    @Override
    public List<DatasetApiKeyResponse> initDatasetApiKey() {
        DatasetApiKeyResponse result = executeWithRetry(
                () -> webClient.post()
                        .uri(ServerUriConstant.DATASETS + "/api-keys")
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                        .bodyToMono(DatasetApiKeyResponse.class)
                        .block()
        );
        ArrayList<DatasetApiKeyResponse> data = new ArrayList<>();
        data.add(result);
        return result != null ? data : null;
    }

    private void appPages(String mode, String name, int page, List<AppsResponse> result) {
        AppsResponseResult response = executeWithRetry(
                () -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(ServerUriConstant.APPS)
                                .queryParam("page", page)
                                .queryParam("limit", 100)
                                .queryParamIfPresent("mode", Optional.ofNullable(mode).filter(m -> !m.isEmpty()))
                                .queryParamIfPresent("name", Optional.ofNullable(name).filter(m -> !m.isEmpty()))
                                .build())
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                        .bodyToMono(AppsResponseResult.class)
                        .block()
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
        LoginResultResponse result = webClient.post()
                .uri(ServerUriConstant.LOGIN)
                .bodyValue(requestVO)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(LoginResultResponse.class)
                .block();
        return processLoginResult(result);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        Map<String, String> requestVO = new HashMap<>(1);
        requestVO.put("refresh_token", refreshToken);
        LoginResultResponse result = webClient.post()
                .uri(ServerUriConstant.REFRESH_TOKEN)
                .bodyValue(requestVO)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(LoginResultResponse.class)
                .block();
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
