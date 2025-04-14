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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/11 09:46
 */
@Data
public class SegmentChildChunkResponse implements Serializable {
    private static final long serialVersionUID = 6161730057618771683L;

    private String id;

    @JsonProperty("segment_id")
    private String segmentId;

    private String content;

    @JsonProperty("word_count")
    private Integer wordCount;

    private Integer tokens;

    @JsonProperty("index_node_id")
    private String indexNodeId;

    @JsonProperty("index_node_hash")
    private String indexNodeHash;

    private String status;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("indexing_at")
    private Long indexingAt;

    @JsonProperty("completed_at")
    private Long completedAt;

    private String error;

    @JsonProperty("stopped_at")
    private Long stoppedAt;
}
