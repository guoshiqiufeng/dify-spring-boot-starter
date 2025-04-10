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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/18 17:29
 */
@Configuration
public class DifyPropertiesAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "dify")
    @ConditionalOnMissingBean({DifyProperties.class})
    public DifyProperties difyProperties() {
        return new DifyProperties();
    }


    @Bean
    @ConditionalOnMissingBean(DifyConnectionDetails.class)
    public PropertiesDifyConnectionDetails difyConnectionDetails(DifyProperties properties) {
        return new PropertiesDifyConnectionDetails(properties);
    }

    static class PropertiesDifyConnectionDetails implements DifyConnectionDetails {

        private final DifyProperties properties;

        PropertiesDifyConnectionDetails(DifyProperties properties) {
            this.properties = properties;
        }

        @Override
        public String getUrl() {
            return this.properties.getUrl();
        }

    }

}
