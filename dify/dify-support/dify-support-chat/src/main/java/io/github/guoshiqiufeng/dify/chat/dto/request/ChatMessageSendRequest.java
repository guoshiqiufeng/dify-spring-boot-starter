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
package io.github.guoshiqiufeng.dify.chat.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 聊天对话内容
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/3/4 10:25
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ChatMessageSendRequest extends BaseChatRequest implements Serializable {

    /**
     * 聊天对话编号
     */
    private String conversationId;

    /**
     * 聊天内容
     */
    @NotEmpty(message = "聊天内容不能为空")
    private String content;

    /**
     * 文件
     */
    private List<ChatMessageFile> files;

    /**
     * 自定义参数
     */
    private Map<String, Object> inputs;


    @Data
    public static class ChatMessageFile {
        private String type = "image";
        private String transferMethod = "remote_url";
        private String url;
    }
}
