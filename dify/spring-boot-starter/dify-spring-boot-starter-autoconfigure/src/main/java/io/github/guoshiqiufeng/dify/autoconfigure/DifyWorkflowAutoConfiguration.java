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

//    @Bean(name = "difyWorkflowWebClient")
//    @ConditionalOnMissingBean(name = "difyWorkflowWebClient")
//    public WebClient difyWorkflowWebClient(DifyProperties properties) {
//        if (properties == null) {
//            log.error("Dify properties must not be null");
//            return null;
//        }
//        HttpClient httpClient = HttpClient.create()
//                .protocol(HttpProtocol.HTTP11);
//
//        return WebClient.builder()
//                .baseUrl(properties.getUrl())
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .build();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean({DifyWorkflowDefaultImpl.class})
//    public DifyWorkflowDefaultImpl difyWorkflowHandler(ObjectMapper objectMapper,
//                                                       @Qualifier("difyWorkflowWebClient") WebClient difyDatasetWebClient) {
//        return new DifyWorkflowDefaultImpl(objectMapper, difyDatasetWebClient);
//    }

    @Bean
    @ConditionalOnMissingBean(DifyWorkflowClient.class)
    public DifyWorkflowClient difyWorkflowClient(DifyProperties properties,
                                                 ObjectProvider<RestClient.Builder> restClientBuilderProvider,
                                                 ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        return new DifyWorkflowClient(properties.getUrl(),
                restClientBuilderProvider.getIfAvailable(RestClient::builder),
                webClientBuilderProvider.getIfAvailable(WebClient::builder));
    }

    @Bean
    @ConditionalOnMissingBean({DifyWorkflow.class})
    public DifyWorkflowClientImpl difyWorkflowHandler(DifyWorkflowClient difyWorkflowClient) {
        return new DifyWorkflowClientImpl(difyWorkflowClient);
    }

}
