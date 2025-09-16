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
