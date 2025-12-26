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
package io.github.guoshiqiufeng.dify.boot.base;

import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.impl.DifyDatasetClientImpl;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponse;
import io.github.guoshiqiufeng.dify.support.impl.dataset.DifyDatasetDefaultClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
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
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DifyProperties difyProperties;

    protected DifyDataset difyDataset;

    @BeforeEach
    public void setUp() {
        String apiKey = initializeApiKeyWithCache();

        HttpClient client = new SpringHttpClientFactory(
                WebClient.builder().defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey),
                null,
                new JacksonJsonMapper()
        ).createClient(difyProperties.getUrl(),
                difyProperties.getClientConfig());

        DifyDatasetClient difyDatasetClient = new DifyDatasetDefaultClient(client);

        this.difyDataset = new DifyDatasetClientImpl(difyDatasetClient);
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

        List<DatasetApiKeyResponse> apiKeys = difyServer.getDatasetApiKey();
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
                .map(DatasetApiKeyResponse::getToken)
                .orElseGet(() -> {
                    List<DatasetApiKeyResponse> newKeys = difyServer.initDatasetApiKey();
                    if (CollectionUtils.isEmpty(newKeys)) {
                        throw new IllegalStateException("Failed to initialize API Key");
                    }
                    return newKeys.getFirst().getToken();
                });
    }

}
