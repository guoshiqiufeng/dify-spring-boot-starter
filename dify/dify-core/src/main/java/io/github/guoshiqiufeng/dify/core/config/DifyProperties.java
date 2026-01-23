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
package io.github.guoshiqiufeng.dify.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
    private Dataset dataset = new Dataset();

    /**
     * 后台服务
     */
    private Server server = new Server();

    /**
     * 请求配置
     */
    private ClientConfig clientConfig = new ClientConfig();

    private StatusConfig status = new StatusConfig();

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

        /**
         * 密码加密
         * <p>dify 1.11.2以下需要关闭</p>
         * <p>
         * `@version` 1.7.1
         */
        private Boolean passwordEncryption = true;

        public Server(String email, String password) {
            this.email = email;
            this.password = password;
            this.passwordEncryption = false;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClientConfig implements Serializable {
        private static final long serialVersionUID = -8070163136236819894L;

        private Boolean skipNull = true;

        private Boolean logging = true;

        /**
         * 连接超时时间（秒），默认 30 秒
         */
        private Integer connectTimeout = 30;

        /**
         * 读取超时时间（秒），默认 30 秒
         */
        private Integer readTimeout = 30;

        /**
         * 写入超时时间（秒），默认 30 秒
         */
        private Integer writeTimeout = 30;

        public ClientConfig(Boolean skipNull, Boolean logging) {
            this.skipNull = skipNull;
            this.logging = logging;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusConfig implements Serializable {
        private static final long serialVersionUID = -9124280674952624154L;

        @Builder.Default
        private Boolean healthIndicatorEnabled = false;

        @Builder.Default
        private Boolean healthIndicatorInitByServer = true;

        /**
         * all apikey
         */
        private String apiKey;

        /**
         * Dataset API key (for DifyDataset client)
         */
        private String datasetApiKey;

        /**
         * Chat API key (for DifyChat client)
         */
        private List<String> chatApiKey;

        /**
         * Workflow API key (for DifyWorkflow client)
         */
        private List<String> workflowApiKey;
    }
}
