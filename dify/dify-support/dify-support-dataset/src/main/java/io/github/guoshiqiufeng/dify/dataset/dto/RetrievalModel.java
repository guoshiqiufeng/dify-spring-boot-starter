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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.guoshiqiufeng.dify.dataset.enums.RerankingModeEnum;
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

    @JsonAlias("searchMethod")
    @JsonProperty("search_method")
    private SearchMethodEnum searchMethod;

    @JsonAlias("rerankingMode")
    @JsonProperty("reranking_mode")
    private RerankingModeEnum rerankingMode;

    @JsonAlias("rerankingEnable")
    @JsonProperty("reranking_enable")
    private Boolean rerankingEnable;

    @JsonAlias("rerankingModel")
    @JsonProperty("reranking_model")
    private RerankingModel rerankingModel;

    private RerankingModelWeight weights;

    @JsonAlias("topK")
    @JsonProperty("top_k")
    private Integer topK;

    @JsonAlias("scoreThresholdEnabled")
    @JsonProperty("score_threshold_enabled")
    private Boolean scoreThresholdEnabled;

    @JsonAlias("scoreThreshold")
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

    @Data
    public static class RerankingModelWeight implements Serializable {

        private static final long serialVersionUID = 3614734284341383364L;

        @JsonAlias("weightType")
        @JsonProperty("weight_type")
        private String weightType = "customized";

        @JsonAlias("vectorSetting")
        @JsonProperty("vector_setting")
        private VectorSetting vectorSetting;

        @JsonAlias("keywordSetting")
        @JsonProperty("keyword_setting")
        private KeywordSetting keywordSetting;

    }

    @Data
    public static class VectorSetting implements Serializable {

        private static final long serialVersionUID = 3614734284341383364L;

        @JsonAlias("vectorWeight")
        @JsonProperty("vector_weight")
        private Float vectorWeight;

        @JsonAlias("embeddingModelName")
        @JsonProperty("embedding_model_name")
        private String embeddingModelName;

        @JsonAlias("embeddingProviderName")
        @JsonProperty("embedding_provider_name")
        private String embeddingProviderName;

    }

    @Data
    public static class KeywordSetting implements Serializable {

        private static final long serialVersionUID = 3614734284341383364L;

        @JsonAlias("keywordWeight")
        @JsonProperty("keyword_weight")
        private Float keywordWeight;


    }
}
