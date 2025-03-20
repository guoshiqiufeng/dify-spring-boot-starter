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
package io.github.guoshiqiufeng.dify.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.chat.impl.DifyChatDefaultImpl;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/18 16:17
 */
@Slf4j
@Configuration
public class DifyChatAutoConfiguration {

    @Bean(name = "difyChatWebClient")
    @ConditionalOnMissingBean(name = "difyChatWebClient")
    public WebClient difyChatWebClient(DifyProperties properties) {
        if (properties == null) {
            log.error("Dify server properties must not be null");
            return null;
        }

        return WebClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean({DifyChatDefaultImpl.class})
    public DifyChatDefaultImpl difyChatHandler(ObjectMapper objectMapper,
                                               @Qualifier("difyChatWebClient") WebClient difyChatWebClient) {
        return new DifyChatDefaultImpl(objectMapper, difyChatWebClient);
    }

}
