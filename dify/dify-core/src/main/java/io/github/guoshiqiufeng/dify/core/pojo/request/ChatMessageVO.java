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
package io.github.guoshiqiufeng.dify.core.pojo.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/1/8 09:26
 */
@Accessors(chain = true)
@Data
public class ChatMessageVO implements Serializable {
    private static final long serialVersionUID = 101472825936407801L;

    /**
     * 请求变量
     */
    private Map<String, Object> inputs;

    /**
     * 对话内容
     */
    private String query;

    /**
     * 模式
     */
    @JsonProperty("response_mode")
    @JsonAlias("responseMode")
    private ResponseModeEnum responseMode;

    /**
     * 会话 id
     */
    @JsonProperty("conversation_id")
    @JsonAlias("conversationId")
    private String conversationId;

    /**
     * 用户 id
     */
    private String user;

    private List<ChatMessageFile> files;

    /**
     * 是否自动生成会话标题
     */
    @JsonProperty("auto_generate_name")
    @JsonAlias("autoGenerateName")
    private Boolean autoGenerateName = true;

    @Data
    public static class ChatMessageFile implements Serializable {
        private static final long serialVersionUID = 3796193415966750860L;
        /**
         * 类型
         */
        private String type = "image";

        @JsonProperty("transfer_method")
        private String transferMethod = "remote_url";

        private String url;

        @JsonProperty("upload_file_id")
        private String uploadFileId;

    }
}
