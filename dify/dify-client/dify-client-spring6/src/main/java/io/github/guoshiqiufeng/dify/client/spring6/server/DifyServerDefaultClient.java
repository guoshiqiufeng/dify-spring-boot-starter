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
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.client.RequestSupplier;
import io.github.guoshiqiufeng.dify.server.constant.ServerUriConstant;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.DifyLoginRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;
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
    public AppsResponseResult apps(AppsRequest appsRequest) {
        return executeWithRetry(
                () -> restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(ServerUriConstant.APPS)
                                .queryParam("page", appsRequest.getPage())
                                .queryParam("limit", appsRequest.getLimit())
                                .queryParamIfPresent("mode", Optional.ofNullable(appsRequest.getMode()).filter(m -> !m.isEmpty()))
                                .queryParamIfPresent("name", Optional.ofNullable(appsRequest.getName()).filter(m -> !m.isEmpty()))
                                .queryParamIfPresent("is_created_by_me", Optional.ofNullable(appsRequest.getIsCreatedByMe()))
                                .build())
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(AppsResponseResult.class)
        );
    }

    @Override
    public AppsResponse app(String appId) {
        return executeWithRetry(
                () -> restClient.get()
                        .uri(ServerUriConstant.APPS + "/{appId}", appId)
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
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
                        .cookies(this::addAuthorizationCookies)
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
                        .cookies(this::addAuthorizationCookies)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(ApiKeyResponse.class)
        );
        return apiKeyResponseVO != null ? new ArrayList<>(List.of(apiKeyResponseVO)) : null;
    }

    @Override
    public void deleteAppApiKey(String appId, String apiKeyId) {
        executeWithRetry(
                () -> restClient.delete()
                        .uri(ServerUriConstant.APP_API_KEYS + "/{apiKeyId}", appId, apiKeyId)
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(Void.class)
        );
    }

    @Override
    public List<DatasetApiKeyResponse> getDatasetApiKey() {
        DatasetApiKeyResult result = executeWithRetry(
                () -> restClient.get()
                        .uri(ServerUriConstant.DATASETS + "/api-keys")
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
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
                        .cookies(this::addAuthorizationCookies)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(DatasetApiKeyResponse.class)
        );
        return result != null ? new ArrayList<>(List.of(result)) : null;
    }

    @Override
    public void deleteDatasetApiKey(String apiKeyId) {
        executeWithRetry(
                () -> restClient.delete()
                        .uri(ServerUriConstant.DATASET_API_KEYS, apiKeyId)
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(Void.class)
        );
    }

    @Override
    public DifyPageResult<ChatConversationResponse> chatConversations(ChatConversationsRequest request) {
        return executeWithRetry(
                () -> restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path(ServerUriConstant.APPS + "/{appId}/chat-conversations")
                                .queryParam("page", request.getPage())
                                .queryParam("limit", request.getLimit())
                                .queryParamIfPresent("start", Optional.ofNullable(request.getStart()))
                                .queryParamIfPresent("end", Optional.ofNullable(request.getEnd()))
                                .queryParamIfPresent("sort_by", Optional.ofNullable(request.getSortBy()))
                                .queryParamIfPresent("annotation_status", Optional.ofNullable(request.getAnnotationStatus()))
                                .build(request.getAppId()))
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(new org.springframework.core.ParameterizedTypeReference<DifyPageResult<ChatConversationResponse>>() {
                        })
        );
    }

    @Override
    public List<DailyConversationsResponse> dailyConversations(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    DailyConversationsResultResponse response = restClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.DAILY_CONVERSATIONS)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new org.springframework.core.ParameterizedTypeReference<DailyConversationsResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<DailyEndUsersResponse> dailyEndUsers(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    DailyEndUsersResultResponse response = restClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.DAILY_END_USERS)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new org.springframework.core.ParameterizedTypeReference<DailyEndUsersResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<AverageSessionInteractionsResponse> averageSessionInteractions(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    AverageSessionInteractionsResultResponse response = restClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.AVERAGE_SESSION_INTERACTIONS)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new org.springframework.core.ParameterizedTypeReference<AverageSessionInteractionsResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<TokensPerSecondResponse> tokensPerSecond(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    TokensPerSecondResultResponse response = restClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.TOKENS_PER_SECOND)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new org.springframework.core.ParameterizedTypeReference<TokensPerSecondResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<UserSatisfactionRateResponse> userSatisfactionRate(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    UserSatisfactionRateResultResponse response = restClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.USER_SATISFACTION_RATE)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new org.springframework.core.ParameterizedTypeReference<UserSatisfactionRateResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<TokenCostsResponse> tokenCosts(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    TokenCostsResultResponse response = restClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.TOKEN_COSTS)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new org.springframework.core.ParameterizedTypeReference<TokenCostsResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<DailyMessagesResponse> dailyMessages(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    DailyMessagesResultResponse response = restClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.DAILY_MESSAGES)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new org.springframework.core.ParameterizedTypeReference<DailyMessagesResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
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
                        .cookies(this::addAuthorizationCookies)
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

    private void addAuthorizationCookies(MultiValueMap<String, String> cookies) {
        difyServerToken.addAuthorizationCookies(cookies, this);
    }

    private <T> T executeWithRetry(RequestSupplier<T> supplier) {
        return difyServerToken.executeWithRetry(supplier, this);
    }

    @Override
    public LoginResponse login() {
        Assert.notNull(difyServerProperties, "The difyServerProperties can not be null.");
        DifyLoginRequest requestVO = DifyLoginRequest.build(
                difyServerProperties.getEmail(),
                difyServerProperties.getPassword()
        );
        if (difyServerProperties.getPasswordEncryption() && !ObjectUtils.isEmpty(difyServerProperties.getPassword())) {
            requestVO.setPassword(Base64.getEncoder().encodeToString(difyServerProperties.getPassword().getBytes()));
        }
        // 发送请求并获取完整 ResponseEntity
        ResponseEntity<LoginResultResponse> responseEntity = restClient.post()
                .uri(ServerUriConstant.LOGIN)
                .body(requestVO)
                .retrieve()
                .onStatus(responseErrorHandler)
                .toEntity(LoginResultResponse.class);
        // 获取 Set-Cookie 头
        List<String> setCookies = responseEntity.getHeaders().getOrEmpty("Set-Cookie");
        // 处理结果
        LoginResultResponse result = responseEntity.getBody();
        return processLoginResult(result, setCookies);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        Map<String, String> requestVO = Map.of("refresh_token", refreshToken);
        ResponseEntity<LoginResultResponse> responseEntity = restClient.post()
                .uri(ServerUriConstant.REFRESH_TOKEN)
                // support Https
                .cookie("__Host-refresh_token", refreshToken)
                .cookie("refresh_token", refreshToken)
                .body(requestVO)
                .retrieve()
                .onStatus(responseErrorHandler)
                .toEntity(LoginResultResponse.class);
        // 获取 Set-Cookie 头
        List<String> setCookies = responseEntity.getHeaders().getOrEmpty("Set-Cookie");
        // 处理结果
        LoginResultResponse result = responseEntity.getBody();
        return processLoginResult(result, setCookies);
    }

    /**
     * Processes login/refresh response and extracts data
     *
     * @param result the login result
     * @return the login response data or null
     */
    @Deprecated
    private LoginResponse processLoginResult(LoginResultResponse result) {
        return Optional.ofNullable(result)
                .filter(r -> DifyResult.SUCCESS.equals(r.getResult()))
                .map(LoginResultResponse::getData)
                .orElse(null);
    }

    /**
     * Processes login/refresh response and extracts data
     *
     * @param result           the login result
     * @param setCookieHeaders the Set-Cookie headers from the response
     * @return the login response data with cookies or null
     */
    private LoginResponse processLoginResult(LoginResultResponse result, List<String> setCookieHeaders) {
        LoginResponse loginResponse = Optional.ofNullable(result)
                .filter(r -> DifyResult.SUCCESS.equals(r.getResult()))
                .map(LoginResultResponse::getData)
                .orElse(null);

        if (loginResponse == null && setCookieHeaders != null) {
            loginResponse = new LoginResponse();
            for (String cookieHeader : setCookieHeaders) {
                String accessToken = extractToken(cookieHeader, "access_token");
                String refreshToken = extractToken(cookieHeader, "refresh_token");
                String csrfToken = extractToken(cookieHeader, "csrf_token");
                if (accessToken != null) {
                    loginResponse.setAccessToken(accessToken);
                }
                if (refreshToken != null) {
                    loginResponse.setRefreshToken(refreshToken);
                }
                if (csrfToken != null) {
                    loginResponse.setCsrfToken(csrfToken);
                }
            }
        }

        return loginResponse;
    }

    /**
     * Extracts the token value for a specified key from the Cookie header.
     * Supports both the standard key prefix and the secure "__Host-" prefix
     * for compatibility. If the extracted value is empty (e.g., "access_token=;"),
     * it returns null.
     *
     * @param cookieHeader The raw Cookie header string.
     * @param key          The name of the token to extract.
     * @return The token value if present and non-empty; null otherwise.
     */
    private String extractToken(String cookieHeader, String key) {
        String prefix = key + "=";
        String hostPrefix = "__Host-" + key + "=";

        String value = null;
        if (cookieHeader.startsWith(prefix)) {
            value = extractTokenValue(cookieHeader, prefix);
        } else if (cookieHeader.startsWith(hostPrefix)) {
            value = extractTokenValue(cookieHeader, hostPrefix);
        }

        if (value != null && !value.isEmpty()) {
            return value;
        }
        return null;
    }

    /**
     * Extracts the token value from a cookie header string
     *
     * @param cookieHeader the cookie header string
     * @param prefix       the token prefix (e.g. "access_token=")
     * @return the extracted token value
     */
    private String extractTokenValue(String cookieHeader, String prefix) {
        int startIndex = cookieHeader.indexOf(prefix) + prefix.length();
        int endIndex = cookieHeader.indexOf(";", startIndex);
        if (endIndex == -1) {
            endIndex = cookieHeader.length();
        }
        return cookieHeader.substring(startIndex, endIndex);
    }
}
