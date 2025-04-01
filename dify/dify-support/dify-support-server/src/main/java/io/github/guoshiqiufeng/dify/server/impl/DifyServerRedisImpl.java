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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

    public DifyServerRedisImpl(DifyProperties difyProperties, RedisTemplate<String, String> redisTemplate, WebClient webClient) {
        this.difyProperties = difyProperties;
        this.webClient = webClient;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 白名单
     */
    private static final List<String> WHITELISTING = List.of(ServerUriConstant.LOGIN, ServerUriConstant.REFRESH_TOKEN);

    /**
     * 获取所有应用
     */
    @Override
    public List<AppsResponseVO> apps(String name) {
        int page = 1;
        return appPages(name, page, new ArrayList<>());
    }

    /**
     * 获取应用列表
     */
    private List<AppsResponseVO> appPages(String name, int page, List<AppsResponseVO> result) {
        String json = getRequest(ServerUriConstant.APPS + "?name=" + name + "&page=" + page + "&limit=100");
        if (StrUtil.isNotEmpty(json)) {
            AppsResponseResultVO tmp = JSONUtil.toBean(json, AppsResponseResultVO.class);
            result.addAll(tmp.getData());
            if (tmp.getHasMore()) {
                return appPages(name, page++, result);
            }
            return result;
        }
        return new ArrayList<>();
    }

    /**
     * 获取应用
     */
    @Override
    public AppsResponseVO app(String appId) {
        String json = getRequest(ServerUriConstant.APPS + "/" + appId);
        if (StrUtil.isNotEmpty(json)) {
            return JSONUtil.toBean(json, AppsResponseVO.class);
        }
        return null;
    }

    /**
     * 获取应用api key
     */
    @Override
    public List<ApiKeyResponseVO> getApiKey(String id) {
        String json = getRequest(ServerUriConstant.APPS + "/" + id + "/api-keys");
        if (StrUtil.isNotEmpty(json)) {
            ApiKeyResultResponseVO tmp = JSONUtil.toBean(json, ApiKeyResultResponseVO.class);
            if (tmp != null && CollectionUtils.isEmpty(tmp.getData())) {
                return initApiKey(id);
            }
            return tmp.getData();
        }
        return new ArrayList<>();
    }

    /**
     * 初始化api key
     */
    @Override
    public List<ApiKeyResponseVO> initApiKey(String id) {
        String uri = ServerUriConstant.APPS + "/{}/api-keys";
        uri = StrUtil.format(uri, id);
        String json = postRequest(uri, "");
        if (StrUtil.isNotEmpty(json)) {
            return JSONUtil.toList(json, ApiKeyResponseVO.class);
        }
        return null;
    }

    @Override
    public List<DatasetApiKeyResponseVO> getDatasetApiKey() {
        String uri = ServerUriConstant.DATASETS + "/api-keys";
        String json = getRequest(uri);
        if (StrUtil.isNotEmpty(json)) {
            return Optional.of(JSONUtil.toBean(json, DatasetApiKeyResultVO.class))
                    .orElse(new DatasetApiKeyResultVO())
                    .getData();
        }
        return null;
    }

    @Override
    public List<DatasetApiKeyResponseVO> initDatasetApiKey() {
        String uri = ServerUriConstant.DATASETS + "/api-keys";
        String json = postRequest(uri, "");
        if (StrUtil.isNotEmpty(json)) {
            return Optional.of(JSONUtil.toBean(json, DatasetApiKeyResultVO.class))
                    .orElse(new DatasetApiKeyResultVO())
                    .getData();
        }
        return null;
    }

    /**
     * 获取token
     */
    public String getToken() {
        String accessToken = redisTemplate.opsForValue().get(DifyRedisKey.ACCESS_TOKEN);
        if (StrUtil.isNotEmpty(accessToken)) {
            return accessToken;
        }
        String refreshToken = redisTemplate.opsForValue().get(DifyRedisKey.REFRESH_TOKEN);
        if (StrUtil.isNotEmpty(refreshToken)) {
            LoginResponseVO login = refreshToken(refreshToken);
            accessToken = saveToken(login);
            if (accessToken != null) {
                return accessToken;
            }
        }
        // 获取token
        LoginResponseVO login = login();
        accessToken = saveToken(login);
        return accessToken;
    }

    /**
     * 保存token
     */
    private String saveToken(LoginResponseVO login) {
        String accessToken;
        if (login != null) {
            accessToken = login.getAccessToken();
            redisTemplate.opsForValue().set(DifyRedisKey.ACCESS_TOKEN, accessToken);
            redisTemplate.expire(DifyRedisKey.ACCESS_TOKEN, 60, TimeUnit.MINUTES);
            redisTemplate.opsForValue().set(DifyRedisKey.REFRESH_TOKEN, login.getRefreshToken());
            return accessToken;
        }
        return null;
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
        if (StrUtil.isNotEmpty(json)) {
            LoginResultResponseVO result = JSONUtil.toBean(json, LoginResultResponseVO.class);
            if (result != null && DifyResult.SUCCESS.equals(result.getResult())) {
                return result.getData();
            }
        }
        return null;
    }

    /**
     * 刷新token
     */
    private LoginResponseVO refreshToken(String refreshToken) {
        Map<String, String> requestVO = Map.of("refresh_token", refreshToken);
        String json = postRequest(ServerUriConstant.REFRESH_TOKEN, JSONUtil.toJsonStr(requestVO));
        if (StrUtil.isNotEmpty(json)) {
            LoginResultResponseVO result = JSONUtil.toBean(json, LoginResultResponseVO.class);
            if (result != null && DifyResult.SUCCESS.equals(result.getResult())) {
                return result.getData();
            }
        }
        return null;
    }

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * 获取请求
     */
    private String getRequest(String uri) {
        return getRequest(uri, 0);
    }

    private String getRequest(String uri, int retryCount) {
        if (retryCount >= MAX_RETRY_ATTEMPTS) {
            log.warn("请求超过最大重试次数: {}, uri: {}", MAX_RETRY_ATTEMPTS, uri);
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        if (!WHITELISTING.contains(uri)) {
            String accessToken = redisTemplate.opsForValue().get(DifyRedisKey.ACCESS_TOKEN);
            if (StrUtil.isNotEmpty(accessToken)) {
                headers.setBearerAuth(accessToken);
            } else {
                getToken();
                return getRequest(uri, retryCount + 1);
            }
        }

        log.debug("请求地址:{}, 重试次数:{}", uri, retryCount);

        try {
            Mono<String> response = webClient.get()
                    .uri(uri)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .bodyToMono(String.class);

            String responseBody = response.block();
            log.debug("响应数据:{}", responseBody);

            if (responseBody != null) {
                return responseBody;
            }

            // 401 刷新token 重试
            if (!WHITELISTING.contains(uri)) {
                String refreshToken = redisTemplate.opsForValue().get(DifyRedisKey.REFRESH_TOKEN);
                if (StrUtil.isNotEmpty(refreshToken)) {
                    LoginResponseVO login = refreshToken(refreshToken);
                    saveToken(login);
                    return getRequest(uri, retryCount + 1);
                }
            }
        } catch (Exception e) {
            log.error("请求失败, uri: {}, 重试次数: {}, 错误信息: {}", uri, retryCount, e.getMessage());
            return getRequest(uri, retryCount + 1);
        }

        return null;
    }

    /**
     * post获取请求
     */
    private String postRequest(String uri, String json) {
        return postRequest(uri, json, 0);
    }

    private String postRequest(String uri, String json, int retryCount) {
        if (retryCount >= MAX_RETRY_ATTEMPTS) {
            log.warn("请求超过最大重试次数: {}, uri: {}", MAX_RETRY_ATTEMPTS, uri);
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        if (!WHITELISTING.contains(uri)) {
            String accessToken = redisTemplate.opsForValue().get(DifyRedisKey.ACCESS_TOKEN);
            if (StrUtil.isNotEmpty(accessToken)) {
                headers.setBearerAuth(accessToken);
            } else {
                getToken();
                return postRequest(uri, json, retryCount + 1);
            }
        }

        log.debug("postRequest 请求地址:{}, 请求参数:{}", uri, json);

        try {
            Mono<String> response = webClient.post()
                    .uri(uri)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(String.class);

            String responseBody = response.block();
            log.debug("postRequest 响应数据:{}", responseBody);

            if (responseBody != null) {
                return responseBody;
            }

            if (!WHITELISTING.contains(uri)) {
                String refreshToken = redisTemplate.opsForValue().get(DifyRedisKey.REFRESH_TOKEN);
                if (StrUtil.isNotEmpty(refreshToken)) {
                    LoginResponseVO login = refreshToken(refreshToken);
                    saveToken(login);
                    return postRequest(uri, json, retryCount + 1);
                }
            }
        } catch (Exception e) {
            log.error("请求失败, uri: {}, 重试次数: {}, 错误信息: {}", uri, retryCount, e.getMessage());
            return postRequest(uri, json, retryCount + 1);
        }

        return null;
    }
}
