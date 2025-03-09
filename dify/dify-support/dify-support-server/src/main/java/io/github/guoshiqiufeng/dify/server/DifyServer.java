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
package io.github.guoshiqiufeng.dify.server;

import io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponseVO;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponseVO;

import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/4 15:05
 */
public interface DifyServer {

    /**
     * 获取所有应用
     */
    List<AppsResponseVO> apps(String name);

    /**
     * 获取应用
     */
    AppsResponseVO app(String appId);

    /**
     * 获取应用api key
     */
    List<ApiKeyResponseVO> getApiKey(String id);

    List<ApiKeyResponseVO> initApiKey(String id);

}
