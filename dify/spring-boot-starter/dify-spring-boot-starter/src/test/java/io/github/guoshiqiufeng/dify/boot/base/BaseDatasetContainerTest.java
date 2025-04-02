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
package io.github.guoshiqiufeng.dify.boot.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.impl.DifyDatasetDefaultImpl;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponseVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/1 13:27
 */
@Slf4j
public abstract class BaseDatasetContainerTest implements RedisContainerTest {

    private static final String API_KEY_CACHE_KEY = "dify:api:key";
    private static final Duration CACHE_EXPIRATION = Duration.ofHours(1);

    @Resource
    protected DifyServer difyServer;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DifyProperties difyProperties;

    protected DifyDataset difyDataset;

    @BeforeEach
    public void setUp() {
        String apiKey = initializeApiKeyWithCache();

        WebClient datasetWebClient = createDatasetWebClient(apiKey);

        this.difyDataset = new DifyDatasetDefaultImpl(objectMapper, datasetWebClient);
    }

    private String initializeApiKeyWithCache() {
        String cachedToken = stringRedisTemplate.opsForValue().get(API_KEY_CACHE_KEY);
        if (StringUtils.hasText(cachedToken)) {
            log.debug("Using cached API Key");
            return cachedToken;
        }

        synchronized (this) {
            cachedToken = stringRedisTemplate.opsForValue().get(API_KEY_CACHE_KEY);
            if (StringUtils.hasText(cachedToken)) {
                return cachedToken;
            }

            String newToken = fetchOrCreateApiKey();

            try {
                stringRedisTemplate.opsForValue().set(API_KEY_CACHE_KEY, newToken, CACHE_EXPIRATION);
                log.debug("API Key cached with expiration: {}", CACHE_EXPIRATION);
            } catch (Exception e) {
                log.error("Failed to cache API Key", e);
            }
            return newToken;
        }
    }

    /**
     * 初始化API Key
     */
    private String fetchOrCreateApiKey() {
        if (difyServer == null) {
            throw new IllegalStateException("DifyServer is not initialized");
        }

        List<DatasetApiKeyResponseVO> apiKeys = difyServer.getDatasetApiKey();
        if (apiKeys == null) {
            log.debug("No existing API keys found, creating new key");
            apiKeys = difyServer.initDatasetApiKey();
            if (CollectionUtils.isEmpty(apiKeys)) {
                throw new IllegalStateException("Failed to initialize API Key");
            }
            return apiKeys.getFirst().getToken();
        }

        return apiKeys.stream()
                .findFirst()
                .map(DatasetApiKeyResponseVO::getToken)
                .orElseGet(() -> {
                    List<DatasetApiKeyResponseVO> newKeys = difyServer.initDatasetApiKey();
                    if (CollectionUtils.isEmpty(newKeys)) {
                        throw new IllegalStateException("Failed to initialize API Key");
                    }
                    return newKeys.getFirst().getToken();
                });
    }

    /**
     * 创建WebClient
     */
    private WebClient createDatasetWebClient(String apiKey) {
        return WebClient.builder()
                .baseUrl(difyProperties.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(createHttpClientConnector())
                .build();
    }

    /**
     * 创建HTTP连接器
     */
    private ReactorClientHttpConnector createHttpClientConnector() {
        return new ReactorClientHttpConnector(
                HttpClient.create()
                        .protocol(HttpProtocol.HTTP11)
                        .responseTimeout(Duration.ofSeconds(10))
        );
    }
}
