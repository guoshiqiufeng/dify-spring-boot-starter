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
package io.github.guoshiqiufeng.dify.dataset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.guoshiqiufeng.dify.dataset.enums.SearchMethodEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 14:57
 */
@Data
public class RetrievalModel implements Serializable {

    @JsonProperty("search_method")
    private SearchMethodEnum searchMethod;

    @JsonProperty("reranking_enable")
    private Boolean rerankingEnable;


    @JsonProperty("reranking_model")
    private RerankingModel rerankingModel;

    private Float weights;

    @JsonProperty("top_k")
    private Integer topK;

    @JsonProperty("score_threshold_enabled")
    private Boolean scoreThresholdEnabled;

    @JsonProperty("score_threshold")
    private Float scoreThreshold;

    @Data
    public static class RerankingModel implements Serializable {

        private static final long serialVersionUID = -7080215137958419497L;

        @JsonProperty("reranking_provider_name")
        private String rerankingProviderName;

        @JsonProperty("reranking_model_name")
        private String rerankingModelName;

    }
}
