package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 10:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MetaDataUpdateRequest extends BaseDatasetRequest implements Serializable {

    private static final long serialVersionUID = -7443098127777945554L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias("datasetId")
    private String datasetId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias("metaDataId")
    private String metaDataId;

    private String name;
}
