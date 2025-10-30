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
package io.github.guoshiqiufeng.dify.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * Response DTO for chat conversation data
 *
 * @author yanghq
 * @version 1.5.0
 * @since 2025/10/30
 */
@Data
public class ChatConversationResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String status;
    private String fromSource;

    @JsonProperty("from_end_user_id")
    private String fromEndUserId;

    @JsonProperty("from_end_user_session_id")
    private String fromEndUserSessionId;

    @JsonProperty("from_account_id")
    private String fromAccountId;

    @JsonProperty("from_account_name")
    private String fromAccountName;

    private String name;
    private String summary;

    @JsonProperty("read_at")
    private Long readAt;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("updated_at")
    private Long updatedAt;

    private Boolean annotated;

    @JsonProperty("model_config")
    private Map<String, Object> modelConfig;

    @JsonProperty("message_count")
    private Integer messageCount;

    @JsonProperty("user_feedback_stats")
    private FeedbackStats userFeedbackStats;

    @JsonProperty("admin_feedback_stats")
    private FeedbackStats adminFeedbackStats;

    @JsonProperty("status_count")
    private StatusCount statusCount;

    /**
     * Feedback statistics DTO
     */
    @Data
    public static class FeedbackStats implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer like;
        private Integer dislike;
    }

    /**
     * Status count DTO
     */
    @Data
    public static class StatusCount implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer success;
        private Integer failed;
        private Integer partialSuccess;
    }
}
