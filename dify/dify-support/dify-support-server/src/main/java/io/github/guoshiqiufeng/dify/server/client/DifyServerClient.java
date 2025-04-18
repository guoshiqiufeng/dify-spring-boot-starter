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
package io.github.guoshiqiufeng.dify.server.client;

import io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponseVO;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponseVO;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponseVO;
import io.github.guoshiqiufeng.dify.server.dto.response.LoginResponseVO;

import java.util.List;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/9 10:14
 */
public interface DifyServerClient {

    List<AppsResponseVO> apps(String mode, String name);

    AppsResponseVO app(String appId);

    List<ApiKeyResponseVO> getAppApiKey(String appId);

    List<ApiKeyResponseVO> initAppApiKey(String appId);

    List<DatasetApiKeyResponseVO> getDatasetApiKey();

    List<DatasetApiKeyResponseVO> initDatasetApiKey();

    LoginResponseVO login();

    LoginResponseVO refreshToken(String refreshToken);
}
