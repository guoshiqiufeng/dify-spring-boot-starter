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
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.dataset.impl.DifyDatasetDefaultImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/18 16:17
 */
@Slf4j
@Configuration
public class DifyDatasetAutoConfiguration {

    @Bean(name = "difyDatasetWebClient")
    @ConditionalOnMissingBean(name = "difyDatasetWebClient")
    public WebClient difyDatasetWebClient(DifyProperties properties) {
        if (properties == null) {
            log.error("Dify properties must not be null");
            return null;
        }
        String apiKey = Optional.ofNullable(properties.getDataset())
                .map(DifyProperties.Dataset::getApiKey)
                .orElse("");

        return WebClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean({DifyDatasetDefaultImpl.class})
    public DifyDatasetDefaultImpl difyDatasetHandler(ObjectMapper objectMapper,
                                                     @Qualifier("difyDatasetWebClient") WebClient difyDatasetWebClient) {
        return new DifyDatasetDefaultImpl(objectMapper, difyDatasetWebClient);
    }

}
