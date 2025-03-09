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
package io.github.guoshiqiufeng.dify.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/4 10:29
 */
@Data
public class ChatMessageSendResponse implements Serializable {

    private String event;

    /**
     * 会话 id
     */
    @JsonAlias("conversation_id")
    private String conversationId;

    /**
     * 消息id
     */
    @JsonAlias("message_id")
    private String messageId;

    @JsonAlias("created_at")
    private Long createdAt;

    @JsonAlias("task_id")
    private String taskId;

    private String id;

    /**
     * 回答
     */
    private String answer;

    @JsonAlias("from_variable_selector")
    private Object fromVariableSelector;
}
