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
package io.github.guoshiqiufeng.dify.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.chat.impl.DifyChatDefaultImpl;
import io.github.guoshiqiufeng.dify.core.config.DifyServerProperties;
import io.github.guoshiqiufeng.dify.server.impl.DifyServerRedisImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/4 14:01
 */
@Configuration
public class DifyAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dify")
    @ConditionalOnMissingBean({DifyServerProperties.class})
    public DifyServerProperties difyProperties() {
        return new DifyServerProperties();
    }


    @Bean
    @ConditionalOnMissingBean({DifyChatDefaultImpl.class})
    public DifyChatDefaultImpl difyChatHandler(DifyServerProperties difyServerProperties, ObjectMapper objectMapper) {
        return new DifyChatDefaultImpl(difyServerProperties, objectMapper);
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    @ConditionalOnMissingBean({DifyServerRedisImpl.class})
    public DifyServerRedisImpl difyServerHandler(DifyServerProperties difyServerProperties, RedisTemplate<String, String> redisTemplate) {
        return new DifyServerRedisImpl(difyServerProperties, redisTemplate);
    }
}
