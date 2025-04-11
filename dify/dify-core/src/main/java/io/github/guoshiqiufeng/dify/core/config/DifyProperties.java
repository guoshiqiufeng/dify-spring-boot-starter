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
package io.github.guoshiqiufeng.dify.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Dify 配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/12/31 13:49
 */
@Data
public class DifyProperties implements Serializable {

    private static final long serialVersionUID = 2857476370638253392L;
    /**
     * 服务地址
     */
    private String url;

    /**
     * 知识库
     */
    private Dataset dataset;

    /**
     * 后台服务
     */
    private Server server;

    /**
     * 请求配置
     */
    private ClientConfig clientConfig = new ClientConfig();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Dataset implements Serializable {
        private static final long serialVersionUID = -8070163136236819894L;
        private String apiKey;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Server implements Serializable {
        private static final long serialVersionUID = -8070163136236819894L;

        /**
         * email
         */
        private String email;

        /**
         * 密码
         */
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClientConfig implements Serializable {
        private static final long serialVersionUID = -8070163136236819894L;

        private Boolean skipNull = true;
    }
}
