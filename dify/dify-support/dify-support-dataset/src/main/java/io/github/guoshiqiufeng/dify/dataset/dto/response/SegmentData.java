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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 17:20
 */
@Data
public class SegmentData implements Serializable {


    private static final long serialVersionUID = -1501282407892329334L;

    private String id;

    private Integer position;

    @JsonAlias("document_id")
    private String documentId;

    private String content;

    private String answer;

    @JsonAlias("word_count")
    private Integer wordCount;

    private Integer tokens;

    private List<String> keywords;

    @JsonAlias("index_node_id")
    private String indexNodeId;

    @JsonAlias("index_node_hash")
    private String indexNodeHash;

    @JsonAlias("hit_count")
    private Integer hitCount;

    @JsonAlias("indexing_status")
    private String indexingStatus;

    private String error;

    private String enabled;

    @JsonAlias("disabled_at")
    private Long disabledAt;

    @JsonAlias("disabled_by")
    private String disabledBy;

    @JsonAlias("status")
    private String status;

    private Boolean archived;

    @JsonAlias("created_at")
    private Long createdAt;

    @JsonAlias("indexing_at")
    private Long indexingAt;

    @JsonAlias("completed_at")
    private Long completedAt;

    @JsonAlias("stopped_at")
    private Long stoppedAt;
}
