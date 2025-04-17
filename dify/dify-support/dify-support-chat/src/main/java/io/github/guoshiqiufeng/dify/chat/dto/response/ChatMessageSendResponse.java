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
import java.util.List;

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

    private Metadata metadata;


    @Data
    public static class Metadata implements Serializable {

        private static final long serialVersionUID = 5739363389399951537L;

        private Usage usage;

        @JsonAlias("retriever_resources")
        private List<RetrieverResources> retrieverResources;

    }

    @Data
    public static class RetrieverResources implements Serializable {

        private static final long serialVersionUID = 1023974561298465172L;

        @JsonAlias("position")
        private Integer position;

        @JsonAlias("dataset_id")
        private String datasetId;

        @JsonAlias("dataset_name")
        private String datasetName;

        @JsonAlias("document_id")
        private String documentId;

        @JsonAlias("document_name")
        private String documentName;

        @JsonAlias("segment_id")
        private String segmentId;

        private Float score;

        private String content;
    }

    @Data
    public static class Usage implements Serializable {

        private static final long serialVersionUID = -164237129958299138L;

        @JsonAlias("prompt_tokens")
        private Integer promptTokens;

        @JsonAlias("prompt_unit_price")
        private String promptUnitPrice;

        @JsonAlias("prompt_price_unit")
        private String promptPriceUnit;

        @JsonAlias("prompt_price")
        private String promptPrice;

        @JsonAlias("completion_tokens")
        private Integer completionTokens;

        @JsonAlias("completion_unit_price")
        private String completionUnitPrice;

        @JsonAlias("completion_price_unit")
        private String completionPriceUnit;

        @JsonAlias("completion_price")
        private String completionPrice;

        @JsonAlias("total_tokens")
        private Integer totalTokens;

        @JsonAlias("total_price")
        private String totalPrice;

        @JsonAlias("currency")
        private String currency;

        @JsonAlias("latency")
        private Double latency;
    }
}
