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
package io.github.guoshiqiufeng.dify.support.impl.server;

import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.map.MultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.core.utils.Assert;
import io.github.guoshiqiufeng.dify.core.utils.StrUtil;
import io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.client.RequestSupplier;
import io.github.guoshiqiufeng.dify.server.constant.ServerUriConstant;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.DifyLoginRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.DocumentRetryRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import io.github.guoshiqiufeng.dify.support.impl.base.BaseDifyDefaultClient;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30 13:47
 */
@Slf4j
public class DifyServerDefaultClient extends BaseDifyDefaultClient implements DifyServerClient {

    private final DifyProperties.Server difyServerProperties;

    private final BaseDifyServerToken difyServerToken;

    /**
     * Constructor with pre-configured HttpClient and custom token handler
     *
     * @param httpClient           the HTTP client instance
     * @param difyServerProperties the Dify server properties
     * @param difyServerToken      the token handler for authentication
     */
    public DifyServerDefaultClient(HttpClient httpClient, DifyProperties.Server difyServerProperties, BaseDifyServerToken difyServerToken) {
        super(httpClient);
        if (difyServerProperties == null) {
            difyServerProperties = new DifyProperties.Server();
        }
        if (difyServerToken == null) {
            difyServerToken = new DifyServerTokenDefault();
        }
        this.difyServerProperties = difyServerProperties;
        this.difyServerToken = difyServerToken;
    }

    /**
     * Constructor with pre-configured HttpClient and default token handler
     *
     * @param httpClient           the HTTP client instance
     * @param difyServerProperties the Dify server properties
     */
    public DifyServerDefaultClient(HttpClient httpClient, DifyProperties.Server difyServerProperties) {
        this(httpClient, difyServerProperties, new DifyServerTokenDefault());
    }

    /**
     * Constructor with HttpClientFactory configuration and custom token handler
     *
     * @param difyServerProperties the Dify server properties
     * @param difyServerToken      the token handler for authentication
     * @param baseUrl              the base URL for the Dify server
     * @param clientConfig         the HTTP client configuration
     * @param httpClientFactory    the HTTP client factory
     */
    public DifyServerDefaultClient(DifyProperties.Server difyServerProperties, BaseDifyServerToken difyServerToken,
                                   String baseUrl, DifyProperties.ClientConfig clientConfig,
                                   HttpClientFactory httpClientFactory) {
        super(baseUrl, clientConfig, httpClientFactory);
        Assert.notNull(difyServerProperties, "difyServerProperties cannot be null");
        Assert.notNull(difyServerToken, "difyServerToken cannot be null");
        this.difyServerProperties = difyServerProperties;
        this.difyServerToken = difyServerToken;
    }

    /**
     * Constructor with HttpClientFactory configuration and default token handler
     *
     * @param difyServerProperties the Dify server properties
     * @param baseUrl              the base URL for the Dify server
     * @param clientConfig         the HTTP client configuration
     * @param httpClientFactory    the HTTP client factory
     */
    public DifyServerDefaultClient(DifyProperties.Server difyServerProperties, String baseUrl, DifyProperties.ClientConfig clientConfig,
                                   HttpClientFactory httpClientFactory) {
        this(difyServerProperties, new DifyServerTokenDefault(), baseUrl, clientConfig, httpClientFactory);
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
                () -> httpClient.get()
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
                () -> httpClient.get()
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
                () -> httpClient.get()
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
                () -> httpClient.post()
                        .uri(ServerUriConstant.APPS + "/{appId}/api-keys", appId)
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(ApiKeyResponse.class)
        );
        if (apiKeyResponseVO != null) {
            List<ApiKeyResponse> apiKeyResponses = new ArrayList<>();
            apiKeyResponses.add(apiKeyResponseVO);
            return apiKeyResponses;
        } else {
            return null;
        }
    }

    @Override
    public void deleteAppApiKey(String appId, String apiKeyId) {
        executeWithRetry(
                () -> httpClient.delete()
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
                () -> httpClient.get()
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
                () -> httpClient.post()
                        .uri(ServerUriConstant.DATASETS + "/api-keys")
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(DatasetApiKeyResponse.class)
        );
        if (result != null) {
            List<DatasetApiKeyResponse> datasetApiKeyResponses = new ArrayList<>();
            datasetApiKeyResponses.add(result);
            return datasetApiKeyResponses;
        } else {
            return null;
        }
    }

    @Override
    public void deleteDatasetApiKey(String apiKeyId) {
        executeWithRetry(
                () -> httpClient.delete()
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
                () -> httpClient.get()
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
                        .body(new TypeReference<DifyPageResult<ChatConversationResponse>>() {
                        })
        );
    }

    @Override
    public List<DailyConversationsResponse> dailyConversations(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    DailyConversationsResultResponse response = httpClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.DAILY_CONVERSATIONS)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new TypeReference<DailyConversationsResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<DailyEndUsersResponse> dailyEndUsers(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    DailyEndUsersResultResponse response = httpClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.DAILY_END_USERS)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new TypeReference<DailyEndUsersResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<AverageSessionInteractionsResponse> averageSessionInteractions(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    AverageSessionInteractionsResultResponse response = httpClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.AVERAGE_SESSION_INTERACTIONS)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new TypeReference<AverageSessionInteractionsResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<TokensPerSecondResponse> tokensPerSecond(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    TokensPerSecondResultResponse response = httpClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.TOKENS_PER_SECOND)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new TypeReference<TokensPerSecondResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<UserSatisfactionRateResponse> userSatisfactionRate(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    UserSatisfactionRateResultResponse response = httpClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.USER_SATISFACTION_RATE)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new TypeReference<UserSatisfactionRateResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<TokenCostsResponse> tokenCosts(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    TokenCostsResultResponse response = httpClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.TOKEN_COSTS)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new TypeReference<TokenCostsResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public List<DailyMessagesResponse> dailyMessages(String appId, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return executeWithRetry(
                () -> {
                    DailyMessagesResultResponse response = httpClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path(ServerUriConstant.DAILY_MESSAGES)
                                    .queryParam("start", start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .queryParam("end", end.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                    .build(appId))
                            .headers(this::addAuthorizationHeader)
                            .cookies(this::addAuthorizationCookies)
                            .retrieve()
                            .onStatus(responseErrorHandler)
                            .body(new TypeReference<DailyMessagesResultResponse>() {
                            });
                    return response != null ? response.getData() : Collections.emptyList();
                }
        );
    }

    @Override
    public DocumentIndexingStatusResponse getDatasetIndexingStatus(String datasetId) {
        return executeWithRetry(() ->
                httpClient
                        .get()
                        .uri(ServerUriConstant.DATASET_INDEXING_STATUS, datasetId)
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(DocumentIndexingStatusResponse.class));
    }

    @Override
    public DocumentIndexingStatusResponse.ProcessingStatus getDocumentIndexingStatus(String datasetId, String documentId) {
        return executeWithRetry(() -> {
            Map<String, Object> uriVariables = new HashMap<>();
            uriVariables.put("datasetId", datasetId);
            uriVariables.put("documentId", documentId);
            return httpClient
                    .get()
                    .uri(ServerUriConstant.DOCUMENT_INDEXING_STATUS, uriVariables)
                    .headers(this::addAuthorizationHeader)
                    .cookies(this::addAuthorizationCookies)
                    .retrieve()
                    .onStatus(responseErrorHandler)
                    .body(DocumentIndexingStatusResponse.ProcessingStatus.class);
        });
    }

    @Override
    public DatasetErrorDocumentsResponse getDatasetErrorDocuments(String datasetId) {
        return executeWithRetry(() ->
                httpClient
                        .get()
                        .uri(ServerUriConstant.DATASET_ERROR_DOCUMENTS, datasetId)
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(DatasetErrorDocumentsResponse.class));
    }

    @Override
    public void retryDocumentIndexing(DocumentRetryRequest request) {
        // 创建只包含document_ids的请求体
        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("document_ids", request.getDocumentIds());

        executeWithRetry(() ->
                httpClient
                        .post()
                        .uri(ServerUriConstant.DOCUMENT_RETRY, request.getDatasetId())
                        .headers(this::addAuthorizationHeader)
                        .cookies(this::addAuthorizationCookies)
                        .body(requestBody)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .toBodilessEntity());
    }

    private void appPages(String mode, String name, int page, List<AppsResponse> result) {
        AppsResponseResult response = executeWithRetry(
                () -> httpClient.get()
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

    private void addAuthorizationHeader(io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders headers) {
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
        if (difyServerProperties.getPasswordEncryption() && !StrUtil.isEmpty(difyServerProperties.getPassword())) {
            requestVO.setPassword(Base64.getEncoder().encodeToString(difyServerProperties.getPassword().getBytes()));
        }
        // 发送请求并获取完整 ResponseEntity
        ResponseEntity<LoginResultResponse> responseEntity = httpClient.post()
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
        Map<String, String> requestVO = new HashMap<>(1);
        requestVO.put("refresh_token", refreshToken);
        ResponseEntity<LoginResultResponse> responseEntity = httpClient.post()
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
