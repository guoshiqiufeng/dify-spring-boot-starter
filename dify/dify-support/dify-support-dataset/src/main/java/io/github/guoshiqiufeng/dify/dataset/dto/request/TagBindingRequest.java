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
import java.util.List;

/**
 * @author yanghq
 * @version 1.3.0
 * @since 2025-07-26 15:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TagBindingRequest extends BaseDatasetRequest implements Serializable {

    @JsonProperty("tag_ids")
    @JsonAlias("tagIds")
    private List<String> tagIds;

    /**
     * targetId: datasetId
     */
    @JsonProperty("target_id")
    @JsonAlias("targetId")
    private String targetId;

}
