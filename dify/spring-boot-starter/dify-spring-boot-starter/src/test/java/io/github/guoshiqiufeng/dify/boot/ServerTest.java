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
package io.github.guoshiqiufeng.dify.boot;

import cn.hutool.json.JSONUtil;
import io.github.guoshiqiufeng.dify.boot.base.BaseServerContainerTest;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponseVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/31 09:55
 */
@Slf4j
public class ServerTest extends BaseServerContainerTest {

    @Resource
    private DifyServer difyServer;

    @Test
    public void datasetApiKeyTest() {
        List<DatasetApiKeyResponseVO> datasetApiKey = difyServer.getDatasetApiKey();
        log.debug("{}", JSONUtil.toJsonStr(datasetApiKey));
        List<DatasetApiKeyResponseVO> datasetApiKeyResponseVOS = difyServer.initDatasetApiKey();
        log.debug("{}", JSONUtil.toJsonStr(datasetApiKeyResponseVOS));
    }

}
