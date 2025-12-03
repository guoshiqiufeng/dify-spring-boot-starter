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
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.function.Consumer;

/**
 * @author yanghq
 * @version 1.6.0
 * @since 2025/12/2 17:26
 */
public class DifyExchangeStrategies {

    private DifyExchangeStrategies() {
    }

    public static Consumer<WebClient.Builder> exchangeStrategies() {
        JsonMapper objectMapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        // Updated from Jackson2JsonEncoder to JacksonJsonEncoder for Jackson 3 compatibility
        JacksonJsonEncoder encoder = new JacksonJsonEncoder(objectMapper, MediaType.APPLICATION_JSON);
        JacksonJsonDecoder decoder = new JacksonJsonDecoder(objectMapper, MediaType.APPLICATION_JSON);

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer -> {
                    // Method names updated from jackson2JsonEncoder to jsonEncoder
                    clientCodecConfigurer.defaultCodecs().jacksonJsonEncoder(encoder);
                    clientCodecConfigurer.defaultCodecs().jacksonJsonDecoder(decoder);
                })
                .build();
        return builder -> builder.exchangeStrategies(strategies);
    }
}
