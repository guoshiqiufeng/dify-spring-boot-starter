package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.guoshiqiufeng.dify.dataset.dto.RetrievalModel;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocFormEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/5/15 14:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetInfoResponse extends DatasetResponse {

    @JsonAlias("embeddingModel")
    @JsonProperty("embedding_model")
    private String embeddingModel;

    @JsonAlias("embeddingModelProvider")
    @JsonProperty("embedding_model_provider")
    private String embeddingModelProvider;

    @JsonAlias("embeddingAvailable")
    @JsonProperty("embedding_available")
    private Boolean embeddingAvailable;

    @JsonAlias("retrievalModelDict")
    @JsonProperty("retrieval_model_dict")
    private RetrievalModel retrievalModelDict;

    private List<String> tags;

    @JsonAlias("docForm")
    @JsonProperty("doc_form")
    private DocFormEnum docForm;

    @JsonProperty("external_knowledge_info")
    @JsonAlias("externalKnowledgeInfo")
    private ExternalKnowledgeInfo externalKnowledgeInfo;
}
