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
import cn.hutool.json.JSONUtil;
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
import java.util.Optional;
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
        String json = getRequest(uri);
        if (StrUtil.isEmpty(json)) {
            return;
        }

        AppsResponseResultVO tmp = JSONUtil.toBean(json, AppsResponseResultVO.class);
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
        String json = getRequest(ServerUriConstant.APPS + "/" + appId);
        return StrUtil.isNotEmpty(json) ? JSONUtil.toBean(json, AppsResponseVO.class) : null;
    }

    /**
     * 获取应用api key
     */
    @Override
    public List<ApiKeyResponseVO> getAppApiKey(String id) {
        String json = getRequest(ServerUriConstant.APPS + "/" + id + "/api-keys");
        if (StrUtil.isEmpty(json)) {
            return new ArrayList<>();
        }

        ApiKeyResultResponseVO tmp = JSONUtil.toBean(json, ApiKeyResultResponseVO.class);
        if (tmp == null) {
            return new ArrayList<>();
        }

        if (CollectionUtils.isEmpty(tmp.getData())) {
            return initAppApiKey(id);
        }
        return tmp.getData();
    }

    /**
     * 初始化api key
     */
    @Override
    public List<ApiKeyResponseVO> initAppApiKey(String id) {
        String uri = StrUtil.format(ServerUriConstant.APPS + "/{}/api-keys", id);
        String json = postRequest(uri, "");

        return Optional.of(JSONUtil.toBean(json, ApiKeyResponseVO.class))
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    public List<DatasetApiKeyResponseVO> getDatasetApiKey() {
        String uri = ServerUriConstant.DATASETS + "/api-keys";
        String json = getRequest(uri);
        if (StrUtil.isEmpty(json)) {
            return null;
        }

        return Optional.of(JSONUtil.toBean(json, DatasetApiKeyResultVO.class))
                .orElse(new DatasetApiKeyResultVO())
                .getData();
    }

    @Override
    public List<DatasetApiKeyResponseVO> initDatasetApiKey() {
        String uri = ServerUriConstant.DATASETS + "/api-keys";
        String json = postRequest(uri, "");
        if (StrUtil.isEmpty(json)) {
            return List.of();
        }

        return Optional.of(JSONUtil.toBean(json, DatasetApiKeyResponseVO.class))
                .map(List::of)
                .orElse(List.of());
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
        String json = postRequest(ServerUriConstant.LOGIN, JSONUtil.toJsonStr(requestVO));

        return processLoginResponse(json);
    }

    /**
     * 刷新token
     */
    private LoginResponseVO refreshToken(String refreshToken) {
        Map<String, String> requestVO = Map.of("refresh_token", refreshToken);
        String json = postRequest(ServerUriConstant.REFRESH_TOKEN, JSONUtil.toJsonStr(requestVO));

        return processLoginResponse(json);
    }

    /**
     * 处理登录或刷新令牌响应
     */
    private LoginResponseVO processLoginResponse(String json) {
        if (StrUtil.isEmpty(json)) {
            return null;
        }

        LoginResultResponseVO result = JSONUtil.toBean(json, LoginResultResponseVO.class);
        if (result != null && DifyResult.SUCCESS.equals(result.getResult())) {
            return result.getData();
        }

        return null;
    }

    /**
     * GET请求
     */
    private String getRequest(String uri) {
        return sendRequest(uri, requestSpec ->
                requestSpec.get().uri(uri)
        );
    }

    /**
     * POST请求
     */
    private String postRequest(String uri, String json) {
        return sendRequest(uri, requestSpec ->
                requestSpec.post().uri(uri).bodyValue(json)
        );
    }

    /**
     * 发送请求通用方法
     */
    private String sendRequest(String uri, Function<WebClient, WebClient.RequestHeadersSpec<?>> requestConfigurer) {
        return sendRequest(uri, requestConfigurer, 0);
    }

    private String sendRequest(String uri, Function<WebClient, WebClient.RequestHeadersSpec<?>> requestConfigurer, int retryCount) {
        if (retryCount >= MAX_RETRY_ATTEMPTS) {
            log.warn("请求超过最大重试次数: {}, uri: {}", MAX_RETRY_ATTEMPTS, uri);
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        if (!WHITELISTING.contains(uri)) {
            String accessToken = redisTemplate.opsForValue().get(DifyRedisKey.ACCESS_TOKEN);
            if (StrUtil.isEmpty(accessToken)) {
                getToken();
                return sendRequest(uri, requestConfigurer, retryCount + 1);
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
                return responseBody;
            }

            // 401 刷新token 重试
            if (!WHITELISTING.contains(uri)) {
                log.debug("{} 请求需要刷新Token - URL:{}", methodType, uri);
                String refreshToken = redisTemplate.opsForValue().get(DifyRedisKey.REFRESH_TOKEN);
                if (StrUtil.isNotEmpty(refreshToken)) {
                    LoginResponseVO login = refreshToken(refreshToken);
                    saveToken(login);
                    return sendRequest(uri, requestConfigurer, retryCount + 1);
                }
            }
        } catch (Exception e) {
            log.error("{} 请求失败 - URL:{}, 重试次数:{}, 错误信息:{}", methodType, uri, retryCount, e.getMessage(), e);
            return sendRequest(uri, requestConfigurer, retryCount + 1);
        }

        return null;
    }
}
