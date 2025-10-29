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
import io.github.guoshiqiufeng.dify.dataset.dto.RetrievalModel;
import io.github.guoshiqiufeng.dify.dataset.enums.SearchMethodEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.4.2
 * @since 2025/9/16 15:40
 */
@Data
public class RetrieveRetrievalModel implements Serializable {
    @JsonAlias("searchMethod")
    @JsonProperty("search_method")
    private SearchMethodEnum searchMethod;

    @JsonAlias("rerankingMode")
    @JsonProperty("reranking_mode")
    private RetrievalModel.RerankingModel rerankingMode;

    @JsonAlias("rerankingEnable")
    @JsonProperty("reranking_enable")
    private Boolean rerankingEnable;

    private Float weights;

    @JsonAlias("topK")
    @JsonProperty("top_k")
    private Integer topK;

    @JsonAlias("scoreThresholdEnabled")
    @JsonProperty("score_threshold_enabled")
    private Boolean scoreThresholdEnabled;

    @JsonAlias("scoreThreshold")
    @JsonProperty("score_threshold")
    private Float scoreThreshold;

    @JsonAlias("metadataFilteringConditions")
    @JsonProperty("metadata_filtering_conditions")
    private RetrievalModel.FilteringConditions metadataFilteringConditions;
}
