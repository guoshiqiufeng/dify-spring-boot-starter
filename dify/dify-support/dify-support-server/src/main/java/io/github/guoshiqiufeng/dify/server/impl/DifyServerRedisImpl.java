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
package io.github.guoshiqiufeng.dify.server.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.cache.DifyRedisKey;
import io.github.guoshiqiufeng.dify.server.constant.ServerUriConstant;
import io.github.guoshiqiufeng.dify.server.dto.request.DifyLoginRequestVO;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import io.github.guoshiqiufeng.dify.server.utils.WebClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/4 13:46
 */
@Slf4j
public class DifyServerRedisImpl implements DifyServer {

    private final DifyProperties difyProperties;
    private final WebClient webClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 白名单
     */
    private static final List<String> WHITELISTING = List.of(ServerUriConstant.LOGIN, ServerUriConstant.REFRESH_TOKEN);

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Redis token 过期时间（分钟）
     */
    private static final long TOKEN_EXPIRE_MINUTES = 60;

    public DifyServerRedisImpl(DifyProperties difyProperties, RedisTemplate<String, String> redisTemplate, WebClient webClient) {
        this.difyProperties = difyProperties;
        this.webClient = webClient;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取所有应用
     */
    @Override
    public List<AppsResponseVO> apps(String name) {
        List<AppsResponseVO> result = new ArrayList<>();
        appPages(name, 1, result);
        return result;
    }

    /**
     * 获取应用列表
     */
    private void appPages(String name, int page, List<AppsResponseVO> result) {
        String uri = ServerUriConstant.APPS + "?name=" + name + "&page=" + page + "&limit=100";
        AppsResponseResultVO tmp = getRequest(uri, AppsResponseResultVO.class);
        if (tmp == null) {
            return;
        }

        result.addAll(tmp.getData());

        if (tmp.getHasMore()) {
            appPages(name, page + 1, result);
        }
    }

    /**
     * 获取应用
     */
    @Override
    public AppsResponseVO app(String appId) {
        return getRequest(ServerUriConstant.APPS + "/" + appId, AppsResponseVO.class);
    }

    /**
     * 获取应用api key
     */
    @Override
    public List<ApiKeyResponseVO> getAppApiKey(String id) {
        ApiKeyResultResponseVO tmp = getRequest(ServerUriConstant.APPS + "/" + id + "/api-keys", ApiKeyResultResponseVO.class);
        if (tmp == null) {
            return new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(tmp.getData())) {
            return initAppApiKey(id);
        }
        return tmp.getData();
    }

    /**
     * 处理返回单个对象或数组的情况
     *
     * @param uri          URI路径
     * @param requestBody  请求体
     * @param elementClass 元素类型
     * @param <T>          元素类型
     * @return 元素列表
     */
    private <T> List<T> postRequestForList(String uri, Object requestBody, Class<T> elementClass) {
        try {
            String requestBodyJson = requestBody instanceof String ? (String) requestBody :
                    objectMapper.writeValueAsString(requestBody);

            // 首先尝试将响应解析为单个对象
            T singleItem = null;
            try {
                singleItem = sendRequest(uri, requestSpec ->
                                requestSpec.post().uri(uri).bodyValue(requestBodyJson)
                        , elementClass);

                if (singleItem != null) {
                    return List.of(singleItem);
                }
            } catch (Exception e) {
                log.debug("解析为单个对象失败，尝试解析为列表 - URI:{}", uri);
            }

            // 如果解析为单个对象失败，则尝试解析为列表
            TypeReference<List<T>> typeReference = new TypeReference<List<T>>() {
            };
            List<T> items = sendRequest(uri, requestSpec ->
                            requestSpec.post().uri(uri).bodyValue(requestBodyJson)
                    , typeReference);

            return (items != null) ? items : List.of();

        } catch (JsonProcessingException e) {
            log.error("序列化请求数据失败 - URI:{}", uri, e);
            return List.of();
        }
    }

    /**
     * 初始化api key
     */
    @Override
    public List<ApiKeyResponseVO> initAppApiKey(String id) {
        String uri = StrUtil.format(ServerUriConstant.APPS + "/{}/api-keys", id);
        return postRequestForList(uri, "", ApiKeyResponseVO.class);
    }

    @Override
    public List<DatasetApiKeyResponseVO> getDatasetApiKey() {
        String uri = ServerUriConstant.DATASETS + "/api-keys";
        DatasetApiKeyResultVO result = getRequest(uri, DatasetApiKeyResultVO.class);
        return result != null ? result.getData() : null;
    }

    @Override
    public List<DatasetApiKeyResponseVO> initDatasetApiKey() {
        String uri = ServerUriConstant.DATASETS + "/api-keys";
        return postRequestForList(uri, "", DatasetApiKeyResponseVO.class);
    }

    /**
     * 获取token
     */
    public String getToken() {
        // 尝试从Redis获取访问令牌
        String accessToken = redisTemplate.opsForValue().get(DifyRedisKey.ACCESS_TOKEN);
        if (StrUtil.isNotEmpty(accessToken)) {
            return accessToken;
        }

        // 尝试刷新令牌
        String refreshToken = redisTemplate.opsForValue().get(DifyRedisKey.REFRESH_TOKEN);
        if (StrUtil.isNotEmpty(refreshToken)) {
            LoginResponseVO login = refreshToken(refreshToken);
            accessToken = saveToken(login);
            if (accessToken != null) {
                return accessToken;
            }
        }

        // 重新登录获取令牌
        return saveToken(login());
    }

    /**
     * 保存token
     */
    private String saveToken(LoginResponseVO login) {
        if (login == null) {
            return null;
        }

        String accessToken = login.getAccessToken();
        redisTemplate.opsForValue().set(DifyRedisKey.ACCESS_TOKEN, accessToken);
        redisTemplate.expire(DifyRedisKey.ACCESS_TOKEN, TOKEN_EXPIRE_MINUTES, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(DifyRedisKey.REFRESH_TOKEN, login.getRefreshToken());

        return accessToken;
    }

    /**
     * 登录
     */
    private LoginResponseVO login() {
        DifyProperties.Server server = difyProperties.getServer();
        if (server == null) {
            throw new RuntimeException("DifyProperties server is null");
        }

        DifyLoginRequestVO requestVO = DifyLoginRequestVO.build(server.getEmail(), server.getPassword());
        LoginResultResponseVO result = postRequest(ServerUriConstant.LOGIN, requestVO, LoginResultResponseVO.class);

        return processLoginResult(result);
    }

    /**
     * 刷新token
     */
    private LoginResponseVO refreshToken(String refreshToken) {
        Map<String, String> requestVO = Map.of("refresh_token", refreshToken);
        LoginResultResponseVO result = postRequest(ServerUriConstant.REFRESH_TOKEN, requestVO, LoginResultResponseVO.class);

        return processLoginResult(result);
    }

    /**
     * 处理登录结果
     */
    private LoginResponseVO processLoginResult(LoginResultResponseVO result) {
        if (result != null && DifyResult.SUCCESS.equals(result.getResult())) {
            return result.getData();
        }
        return null;
    }

    /**
     * GET请求
     */
    private <T> T getRequest(String uri, Class<T> responseType) {
        return sendRequest(uri, requestSpec ->
                        requestSpec.get().uri(uri)
                , responseType);
    }

    /**
     * GET请求（支持复杂泛型类型）
     */
    private <T> T getRequest(String uri, TypeReference<T> typeReference) {
        return sendRequest(uri, requestSpec ->
                        requestSpec.get().uri(uri)
                , typeReference);
    }

    /**
     * POST请求
     */
    private <T> T postRequest(String uri, Object requestBody, Class<T> responseType) {
        try {
            String requestBodyJson = requestBody instanceof String ? (String) requestBody :
                    objectMapper.writeValueAsString(requestBody);
            return sendRequest(uri, requestSpec ->
                            requestSpec.post().uri(uri).bodyValue(requestBodyJson)
                    , responseType);
        } catch (JsonProcessingException e) {
            log.error("序列化请求数据失败 - URI:{}", uri, e);
            return null;
        }
    }

    /**
     * POST请求（支持复杂泛型类型）
     */
    private <T> T postRequest(String uri, Object requestBody, TypeReference<T> typeReference) {
        try {
            String requestBodyJson = requestBody instanceof String ? (String) requestBody :
                    objectMapper.writeValueAsString(requestBody);
            return sendRequest(uri, requestSpec ->
                            requestSpec.post().uri(uri).bodyValue(requestBodyJson)
                    , typeReference);
        } catch (JsonProcessingException e) {
            log.error("序列化请求数据失败 - URI:{}", uri, e);
            return null;
        }
    }

    /**
     * 发送请求通用方法
     */
    private <T> T sendRequest(String uri, Function<WebClient, WebClient.RequestHeadersSpec<?>> requestConfigurer,
                              Class<T> responseType) {
        return sendRequest(uri, requestConfigurer, 0, responseType);
    }

    /**
     * 处理复杂泛型类型的请求
     */
    private <T> T sendRequest(String uri, Function<WebClient, WebClient.RequestHeadersSpec<?>> requestConfigurer,
                              TypeReference<T> typeReference) {
        return sendRequest(uri, requestConfigurer, 0, typeReference);
    }

    private <T> T sendRequest(String uri, Function<WebClient, WebClient.RequestHeadersSpec<?>> requestConfigurer,
                              int retryCount, Class<T> responseType) {
        if (retryCount >= MAX_RETRY_ATTEMPTS) {
            log.warn("请求超过最大重试次数: {}, uri: {}", MAX_RETRY_ATTEMPTS, uri);
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        if (!WHITELISTING.contains(uri)) {
            String accessToken = redisTemplate.opsForValue().get(DifyRedisKey.ACCESS_TOKEN);
            if (StrUtil.isEmpty(accessToken)) {
                getToken();
                return sendRequest(uri, requestConfigurer, retryCount + 1, responseType);
            }
            headers.setBearerAuth(accessToken);
        }

        String methodType = requestConfigurer.toString().contains("post") ? "POST" : "GET";
        log.debug("{} 请求开始 - URL:{}, 重试次数:{}, 请求头:{}", methodType, uri, retryCount, headers);

        try {
            Mono<String> response = requestConfigurer.apply(webClient)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                    .bodyToMono(String.class);

            String responseBody = response.block();
            log.debug("{} 请求完成 - URL:{}, 响应状态:成功, 响应数据:{}", methodType, uri, responseBody);

            if (responseBody != null) {
                if (responseType == String.class) {
                    return (T) responseBody;
                }
                try {
                    return objectMapper.readValue(responseBody, responseType);
                } catch (JsonProcessingException e) {
                    log.error("解析响应数据失败 - URI:{}, 类型:{}", uri, responseType.getName(), e);
                    return null;
                }
            }

            // 401 刷新token 重试
            if (!WHITELISTING.contains(uri)) {
                log.debug("{} 请求需要刷新Token - URL:{}", methodType, uri);
                String refreshToken = redisTemplate.opsForValue().get(DifyRedisKey.REFRESH_TOKEN);
                if (StrUtil.isNotEmpty(refreshToken)) {
                    LoginResponseVO login = refreshToken(refreshToken);
                    saveToken(login);
                    return sendRequest(uri, requestConfigurer, retryCount + 1, responseType);
                }
            }
        } catch (Exception e) {
            log.error("{} 请求失败 - URL:{}, 重试次数:{}, 错误信息:{}", methodType, uri, retryCount, e.getMessage(), e);
            return sendRequest(uri, requestConfigurer, retryCount + 1, responseType);
        }

        return null;
    }

    private <T> T sendRequest(String uri, Function<WebClient, WebClient.RequestHeadersSpec<?>> requestConfigurer,
                              int retryCount, TypeReference<T> typeReference) {
        if (retryCount >= MAX_RETRY_ATTEMPTS) {
            log.warn("请求超过最大重试次数: {}, uri: {}", MAX_RETRY_ATTEMPTS, uri);
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        if (!WHITELISTING.contains(uri)) {
            String accessToken = redisTemplate.opsForValue().get(DifyRedisKey.ACCESS_TOKEN);
            if (StrUtil.isEmpty(accessToken)) {
                getToken();
                return sendRequest(uri, requestConfigurer, retryCount + 1, typeReference);
            }
            headers.setBearerAuth(accessToken);
        }

        String methodType = requestConfigurer.toString().contains("post") ? "POST" : "GET";
        log.debug("{} 请求开始 - URL:{}, 重试次数:{}, 请求头:{}", methodType, uri, retryCount, headers);

        try {
            Mono<String> response = requestConfigurer.apply(webClient)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                    .bodyToMono(String.class);

            String responseBody = response.block();
            log.debug("{} 请求完成 - URL:{}, 响应状态:成功, 响应数据:{}", methodType, uri, responseBody);

            if (responseBody != null) {
                if (String.class.equals(typeReference.getType())) {
                    return (T) responseBody;
                }
                try {
                    // 检查响应是否是一个有效的JSON数组或对象
                    boolean expectingList = typeReference.getType().toString().contains("List");
                    boolean isJsonArray = responseBody.trim().startsWith("[");
                    boolean isJsonObject = responseBody.trim().startsWith("{");

                    if (expectingList && !isJsonArray && isJsonObject) {
                        log.debug("响应不是数组格式但正在尝试解析为列表 - URI:{}", uri);

                        // 尝试从响应对象中提取可能的数组字段
                        try {
                            Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
                            });
                            for (Map.Entry<String, Object> entry : responseMap.entrySet()) {
                                if (entry.getValue() instanceof List) {
                                    log.debug("找到数组字段: {} - URI:{}", entry.getKey(), uri);
                                    return (T) entry.getValue();
                                }
                            }
                        } catch (Exception e) {
                            log.debug("尝试提取数组字段失败 - URI:{}", uri, e);
                        }
                    }

                    return objectMapper.readValue(responseBody, typeReference);
                } catch (JsonProcessingException e) {
                    log.error("解析响应数据失败 - URI:{}, 类型:{}, 响应体:{}", uri, typeReference.getType().getTypeName(), responseBody, e);
                    return null;
                }
            }

            // 401 刷新token 重试
            if (!WHITELISTING.contains(uri)) {
                log.debug("{} 请求需要刷新Token - URL:{}", methodType, uri);
                String refreshToken = redisTemplate.opsForValue().get(DifyRedisKey.REFRESH_TOKEN);
                if (StrUtil.isNotEmpty(refreshToken)) {
                    LoginResponseVO login = refreshToken(refreshToken);
                    saveToken(login);
                    return sendRequest(uri, requestConfigurer, retryCount + 1, typeReference);
                }
            }
        } catch (Exception e) {
            log.error("{} 请求失败 - URL:{}, 重试次数:{}, 错误信息:{}", methodType, uri, retryCount, e.getMessage(), e);
            return sendRequest(uri, requestConfigurer, retryCount + 1, typeReference);
        }

        return null;
    }
}
