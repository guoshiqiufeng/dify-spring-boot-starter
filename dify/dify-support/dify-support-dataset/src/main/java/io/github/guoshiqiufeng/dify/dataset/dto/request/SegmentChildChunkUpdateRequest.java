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
package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/11 09:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SegmentChildChunkUpdateRequest extends BaseDatasetRequest implements Serializable {
    private static final long serialVersionUID = 7291412940243621065L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias("datasetId")
    private String datasetId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias("documentId")
    private String documentId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias("segmentId")
    private String segmentId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias("childChunkId")
    private String childChunkId;

    private String content;
}
