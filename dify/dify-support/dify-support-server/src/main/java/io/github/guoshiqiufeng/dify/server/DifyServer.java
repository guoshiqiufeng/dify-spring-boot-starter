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
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponseVO;

import java.util.List;

/**
 * Dify服务接口，提供与Dify平台交互的方法
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/3/4 15:05
 */
public interface DifyServer {

    /**
     * 获取所有应用列表
     *
     * @param mode 模式 chat\agent-chat\completion\advanced-chat\workflow
     * @param name 应用名称，用于过滤应用列表（可选，传入空字符串时表示不过滤）
     * @return 返回符合条件的应用列表，每个应用封装为 {@link AppsResponseVO} 对象
     * @throws IllegalArgumentException 如果传入的参数不符合预期格式或范围
     */
    List<AppsResponseVO> apps(String mode, String name);

    /**
     * 根据应用ID获取单个应用的详细信息
     *
     * @param appId 应用的唯一标识符，不能为空
     * @return 返回封装了应用详细信息的 {@link AppsResponseVO} 对象，如果未找到应用则返回null
     * @throws NullPointerException 如果 appId 为 null
     */
    AppsResponseVO app(String appId);

    /**
     * 根据应用ID获取该应用的所有API Key列表
     *
     * @param appId 应用的唯一标识符，不能为空
     * @return 返回封装了API Key信息的列表，每个API Key封装为 {@link ApiKeyResponseVO} 对象
     * @throws NullPointerException 如果 id 为 null
     */
    List<ApiKeyResponseVO> getAppApiKey(String appId);

    /**
     * 初始化应用的API Key
     * 如果应用尚未创建API Key，此方法将创建并返回新的API Key
     *
     * @param appId 应用的唯一标识符，不能为空
     * @return 返回初始化后的API Key列表，每个API Key封装为 {@link ApiKeyResponseVO} 对象
     * @throws NullPointerException 如果 id 为 null
     */
    List<ApiKeyResponseVO> initAppApiKey(String appId);

    /**
     * 获取知识库的API Key列表
     * 用于访问Dify平台上的知识库资源
     *
     * @return 返回知识库API Key列表，每个API Key封装为 {@link DatasetApiKeyResponseVO} 对象，
     * 如果未找到则返回null
     */
    List<DatasetApiKeyResponseVO> getDatasetApiKey();

    /**
     * 初始化知识库的API Key
     * 如果知识库尚未创建API Key，此方法将创建并返回新的API Key
     *
     * @return 返回初始化后的知识库API Key列表，每个API Key封装为 {@link DatasetApiKeyResponseVO} 对象，
     * 如果初始化失败则返回空列表
     */
    List<DatasetApiKeyResponseVO> initDatasetApiKey();
}
