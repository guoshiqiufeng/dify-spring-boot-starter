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

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponse;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/1 13:27
 */
@Slf4j
public abstract class BaseChatContainerTest implements RedisContainerTest {

    @Resource
    protected DifyServer difyServer;

    @Resource
    protected DifyProperties difyProperties;

    protected static String apiKey;

    @BeforeEach
    public void setUp() {
        if (apiKey != null) {
            return;
        }
        List<AppsResponse> apps = difyServer.apps("chat", "");
        assert !apps.isEmpty();
        List<ApiKeyResponse> appApiKey = difyServer.getAppApiKey(apps.getFirst().getId());
        if (appApiKey.isEmpty()) {
            appApiKey = difyServer.initAppApiKey(apps.getFirst().getId());
        }
        apiKey = appApiKey.getFirst().getToken();
    }

}
