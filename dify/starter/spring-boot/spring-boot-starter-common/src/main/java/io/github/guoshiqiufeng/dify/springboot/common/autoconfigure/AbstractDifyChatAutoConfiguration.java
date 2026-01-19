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
package io.github.guoshiqiufeng.dify.springboot.common.autoconfigure;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.chat.impl.DifyChatClientImpl;
import io.github.guoshiqiufeng.dify.chat.pipeline.ChatMessagePipelineModel;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineHandler;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineModel;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineProcess;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineTemplate;
import io.github.guoshiqiufeng.dify.support.impl.chat.DifyChatDefaultClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dify Chat 自动配置抽象基类
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/3/18 16:17
 */
@Slf4j
@ConditionalOnClass({DifyChatClient.class})
public abstract class AbstractDifyChatAutoConfiguration {

    /**
     * 创建 SpringHttpClientFactory
     * 子类需要实现此方法以提供特定版本的 HttpClientFactory
     */
    protected abstract SpringHttpClientFactory createHttpClientFactory(
            DifyProperties properties,
            JsonMapper jsonMapper);

    @Bean
    @ConditionalOnMissingBean(DifyChatClient.class)
    public DifyChatClient difyChatClient(DifyProperties properties, JsonMapper jsonMapper) {
        SpringHttpClientFactory httpClientFactory = createHttpClientFactory(properties, jsonMapper);
        HttpClient httpClient = httpClientFactory.createClient(properties.getUrl(), properties.getClientConfig());
        return new DifyChatDefaultClient(httpClient);
    }

    @Bean
    @ConditionalOnMissingBean({DifyChat.class})
    public DifyChatClientImpl difyChatHandler(DifyChatClient difyChatClient) {
        return new DifyChatClientImpl(difyChatClient);
    }

    @Bean("chatTemplate")
    @ConditionalOnMissingBean(name = "chatTemplate")
    public PipelineTemplate<ChatMessagePipelineModel> chatTemplate(
            List<PipelineProcess<ChatMessagePipelineModel>> processList) {
        PipelineTemplate<ChatMessagePipelineModel> processTemplate = new PipelineTemplate<>();
        processTemplate.setProcessList(processList);
        return processTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(PipelineHandler.class)
    public PipelineHandler pipelineHandler(
            List<PipelineProcess<ChatMessagePipelineModel>> produceProcessList) {
        PipelineHandler processHandler = new PipelineHandler();
        Map<String, PipelineTemplate<? extends PipelineModel>> templateConfig = new HashMap<>(1);
        templateConfig.put("CHAT", chatTemplate(produceProcessList));
        processHandler.setTemplateConfig(templateConfig);
        return processHandler;
    }
}
