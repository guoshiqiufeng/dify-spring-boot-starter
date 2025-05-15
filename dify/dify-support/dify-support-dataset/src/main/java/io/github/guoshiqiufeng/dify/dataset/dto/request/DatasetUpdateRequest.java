package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.guoshiqiufeng.dify.dataset.dto.RetrievalModel;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.PermissionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/5/15 14:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetUpdateRequest extends BaseDatasetRequest implements Serializable {

    private static final long serialVersionUID = 2250982375704245540L;

    private String datasetId;

    /**
     * 索引模式
     */
    @JsonProperty("indexing_technique")
    @JsonAlias("indexingTechnique")
    private IndexingTechniqueEnum indexingTechnique;

    /**
     * 权限
     */
    private PermissionEnum permission = PermissionEnum.ONLY_ME;

    @JsonAlias("retrievalModel")
    @JsonProperty("retrieval_model")
    private RetrievalModel retrievalModel;

    @JsonAlias("embeddingModel")
    @JsonProperty("embedding_model")
    private String embeddingModel;

    @JsonAlias("embeddingModelProvider")
    @JsonProperty("embedding_model_provider")
    private String embeddingModelProvider;

    @JsonAlias("partialMemberList")
    @JsonProperty("partial_member_list")
    private List<String> partialMemberList;
}
