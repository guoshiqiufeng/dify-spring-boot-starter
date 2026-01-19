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

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dify Codec 自动配置基类
 * <p>
 * 默认初始化 Jackson 的 JsonMapper 实现
 * Jackson 支持 @JsonProperty, @JsonAlias 等注解，与项目中的 DTO 定义兼容
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-30
 */
@Slf4j
@Configuration
@ConditionalOnClass({JsonMapper.class, JacksonJsonMapper.class})
public class DifyCodecAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JsonMapper.class)
    public JsonMapper jsonMapper() {
        return JacksonJsonMapper.getInstance();
    }
}
