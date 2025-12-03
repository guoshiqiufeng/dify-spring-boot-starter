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
package io.github.guoshiqiufeng.dify.client.spring7.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/11 13:42
 */
public class DifyMessageConverters {

    private DifyMessageConverters() {
    }

    public static Consumer<RestClient.Builder> messageConvertersConsumer() {
        return builder -> {
            // 创建自定义的JsonMapper
            JsonMapper objectMapper = JsonMapper.builder()
                    .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                    .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .build();

            // 使用自定义的Jackson converter
            JacksonJsonHttpMessageConverter converter = new JacksonJsonHttpMessageConverter(objectMapper);

            // 使用 configureMessageConverters 配置 JSON converter，同时保留默认的converters
            builder.configureMessageConverters(convertersBuilder ->
                convertersBuilder.registerDefaults().withJsonConverter(converter)
            );
        };
    }
}
