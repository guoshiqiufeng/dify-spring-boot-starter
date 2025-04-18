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
package io.github.guoshiqiufeng.dify.client.spring6.utils;

import cn.hutool.core.util.StrUtil;
import io.github.guoshiqiufeng.dify.dataset.dto.request.BaseDatasetRequest;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/17 11:41
 */
public class DatasetHeaderUtils {

    public static Consumer<HttpHeaders> getHttpHeadersConsumer(BaseDatasetRequest request) {
        return headers -> {
            if (StrUtil.isNotEmpty(request.getApiKey())) {
                headers.setBearerAuth(request.getApiKey());
            }
        };
    }

    public static Consumer<HttpHeaders> getHttpHeadersConsumer(String apikey) {
        return headers -> {
            if (StrUtil.isNotEmpty(apikey)) {
                headers.setBearerAuth(apikey);
            }
        };
    }
}
