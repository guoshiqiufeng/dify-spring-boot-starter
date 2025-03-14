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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/14 14:02
 */
@Data
public class DocumentIndexingStatusResponse implements Serializable {
    private static final long serialVersionUID = 8090939188285176936L;

    private List<ProcessingStatus> data;

    @Data
    public static class ProcessingStatus implements Serializable {
        private static final long serialVersionUID = 6083630613281543124L;
        private String id;

        /**
         * 索引状态，支持 JSON 中的 "indexing_status" 映射。
         */
        @JsonAlias("indexing_status")
        private String indexingStatus;

        /**
         * 处理开始时间的时间戳，支持 JSON 中的 "processing_started_at" 映射。
         */
        @JsonAlias("processing_started_at")
        private Long processingStartedAt;

        /**
         * 解析完成时间的时间戳，支持 JSON 中的 "parsing_completed_at" 映射。
         */
        @JsonAlias("parsing_completed_at")
        private Long parsingCompletedAt;

        /**
         * 清洗完成时间的时间戳，支持 JSON 中的 "cleaning_completed_at" 映射。
         */
        @JsonAlias("cleaning_completed_at")
        private Long cleaningCompletedAt;

        /**
         * 分片完成时间的时间戳，支持 JSON 中的 "splitting_completed_at" 映射。
         */
        @JsonAlias("splitting_completed_at")
        private Long splittingCompletedAt;

        /**
         * 整体处理完成时间的时间戳，支持 JSON 中的 "completed_at" 映射。
         */
        @JsonAlias("completed_at")
        private Long completedAt;

        /**
         * 暂停时间的时间戳，支持 JSON 中的 "paused_at" 映射。
         */
        @JsonAlias("paused_at")
        private Long pausedAt;

        /**
         * 错误信息，支持 JSON 中的 "error" 映射。
         */
        @JsonAlias("error")
        private String error;

        /**
         * 停止时间的时间戳，支持 JSON 中的 "stopped_at" 映射。
         */
        @JsonAlias("stopped_at")
        private Long stoppedAt;

        /**
         * 已完成的分片数量，支持 JSON 中的 "completed_segments" 映射。
         */
        @JsonAlias("completed_segments")
        private Integer completedSegments;

        /**
         * 总分片数量，支持 JSON 中的 "total_segments" 映射。
         */
        @JsonAlias("total_segments")
        private Integer totalSegments;
    }
}
