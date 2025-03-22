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
     * 获取所有应用列表。
     *
     * @param name 应用名称，用于过滤应用列表（可选，传入null时表示不过滤）
     * @return 返回符合条件的应用列表，每个应用封装为 {@link AppsResponseVO} 对象
     * @throws IllegalArgumentException 如果传入的参数不符合预期格式或范围
     */
    List<AppsResponseVO> apps(String name);

    /**
     * 根据应用ID获取单个应用的详细信息。
     *
     * @param appId 应用的唯一标识符，不能为空
     * @return 返回封装了应用详细信息的 {@link AppsResponseVO} 对象
     * @throws NullPointerException 如果 appId 为 null
     */
    AppsResponseVO app(String appId);

    /**
     * 根据应用ID获取该应用的所有API Key列表。
     *
     * @param id 应用的唯一标识符，不能为空
     * @return 返回封装了API Key信息的列表，每个API Key封装为 {@link ApiKeyResponseVO} 对象
     * @throws NullPointerException 如果 id 为 null
     */
    List<ApiKeyResponseVO> getApiKey(String id);

    /**
     * 初始化应用的API Key列表。
     *
     * @param id 应用的唯一标识符，不能为空
     * @return 返回初始化后的API Key列表，每个API Key封装为 {@link ApiKeyResponseVO} 对象
     * @throws NullPointerException 如果 id 为 null
     */
    List<ApiKeyResponseVO> initApiKey(String id);

}
