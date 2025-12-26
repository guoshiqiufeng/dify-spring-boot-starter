/*
 * Copyright (c) 2025-2026, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.springboot.autoconfigure;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.support.impl.workflow.DifyWorkflowDefaultClient;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import io.github.guoshiqiufeng.dify.workflow.impl.DifyWorkflowClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/18 16:17
 */
@Slf4j
@Configuration
@ConditionalOnClass({DifyWorkflowClient.class})
public class DifyWorkflowAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DifyWorkflowClient.class)
    public DifyWorkflowClient difyWorkflowClient(DifyProperties properties,
                                                 JsonMapper jsonMapper,
                                                 ObjectProvider<WebClient.Builder> webClientBuilderProvider,
                                                 ObjectProvider<RestClient.Builder> restClientBuilderProvider) {
        SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
                webClientBuilderProvider.getIfAvailable(WebClient::builder),
                restClientBuilderProvider.getIfAvailable(RestClient::builder),
                jsonMapper);
        HttpClient httpClient = httpClientFactory.createClient(properties.getUrl(), properties.getClientConfig());
        return new DifyWorkflowDefaultClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean({DifyWorkflow.class})
    public DifyWorkflowClientImpl difyWorkflowHandler(DifyWorkflowClient difyWorkflowClient) {
        return new DifyWorkflowClientImpl(difyWorkflowClient);
    }

}
