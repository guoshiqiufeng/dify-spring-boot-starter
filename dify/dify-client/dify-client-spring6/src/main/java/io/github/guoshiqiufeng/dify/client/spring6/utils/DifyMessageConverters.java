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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

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
            builder.messageConverters(converters -> {
                // 找到现有的Jackson converter
                Optional<MappingJackson2HttpMessageConverter> existingConverter = converters.stream()
                        .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                        .map(c -> (MappingJackson2HttpMessageConverter) c)
                        .findFirst();

                // 创建新的converter，基于现有配置
                MappingJackson2HttpMessageConverter newConverter;
                if (existingConverter.isPresent()) {
                    ObjectMapper newMapper = existingConverter.get().getObjectMapper().copy();
                    newMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    newMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    newConverter = new MappingJackson2HttpMessageConverter(newMapper);
                } else {
                    // 如果没有找到，创建新的
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    newConverter = new MappingJackson2HttpMessageConverter(objectMapper);
                }

                // 移除旧的，添加新的
                converters.removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
                converters.add(0, newConverter);
            });
        };
    }
}
