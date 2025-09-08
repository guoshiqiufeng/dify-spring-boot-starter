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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用反馈响应对象
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/9/8 15:30
 */
@Data
public class AppFeedbackResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 反馈ID
     */
    private String id;

    /**
     * 应用ID
     */
    @JsonProperty("app_id")
    private String appId;

    /**
     * 会话ID
     */
    @JsonProperty("conversation_id")
    private String conversationId;

    /**
     * 消息ID
     */
    @JsonProperty("message_id")
    private String messageId;

    /**
     * 评级 (like/dislike)
     */
    private String rating;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 来源
     */
    @JsonProperty("from_source")
    private String fromSource;

    /**
     * 来自终端用户ID
     */
    @JsonProperty("from_end_user_id")
    private String fromEndUserId;

    /**
     * 来自账户ID
     */
    @JsonProperty("from_account_id")
    private String fromAccountId;

    /**
     * 创建时间
     */
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
