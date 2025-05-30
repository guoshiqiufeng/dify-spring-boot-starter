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
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/16 17:26
 */
public class DifyExchangeStrategies {

    private DifyExchangeStrategies() {}

    public static Consumer<WebClient.Builder> exchangeStrategies() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 忽略序列化null字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Jackson2JsonEncoder encoder = new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON);
        Jackson2JsonDecoder decoder = new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON);

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer -> {
                    clientCodecConfigurer.defaultCodecs().jackson2JsonEncoder(encoder);
                    clientCodecConfigurer.defaultCodecs().jackson2JsonDecoder(decoder);
                })
                .build();
        return builder -> builder.exchangeStrategies(strategies);
    }
}
