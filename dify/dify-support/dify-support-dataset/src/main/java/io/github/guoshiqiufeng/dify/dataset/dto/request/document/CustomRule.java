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
package io.github.guoshiqiufeng.dify.dataset.dto.request.document;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.guoshiqiufeng.dify.dataset.enums.document.ParentModeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 15:23
 */
@Data
public class CustomRule implements Serializable {
    private static final long serialVersionUID = 2662516999304017667L;

    @JsonAlias("preProcessingRules")
    @JsonProperty("pre_processing_rules")
    private List<PreProcessingRule> preProcessingRules;

    /**
     * 分段规则
     */
    private Segmentation segmentation;

    /**
     * 父分段的召回模式
     */
    @JsonAlias("parentMode")
    @JsonProperty("parent_mode")
    private ParentModeEnum parentMode;

    @JsonAlias("subChunkSegmentation")
    @JsonProperty("subchunk_segmentation")
    private SubChunkSegmentation subChunkSegmentation;
}
