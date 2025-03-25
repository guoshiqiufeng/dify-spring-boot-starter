package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 10:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MetaDataCreateRequest extends BaseDatasetRequest implements Serializable {
    private static final long serialVersionUID = 3246895121202690206L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias("datasetId")
    private String datasetId;

    /**
     * 元数据类型，必填
     */
    private String type;

    /**
     * 元数据名称，必填
     */
    private String name;
}
