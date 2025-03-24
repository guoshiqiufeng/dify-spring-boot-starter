package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 10:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentMetaDataUpdateRequest extends BaseDatasetRequest implements Serializable {
    private static final long serialVersionUID = -7793337783067712263L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String datasetId;

    @JsonProperty("operation_data")
    private List<OperationData> operationData;


    @Data
    public static class OperationData implements Serializable {

        private static final long serialVersionUID = 6071302031098356179L;

        private String documentId;

        @JsonProperty("metadata_list")
        private List<MetaData> metadataList;
    }

    @Data
    public static class MetaData implements Serializable {

        private static final long serialVersionUID = 1083376788375284035L;

        private String id;

        private String type;

        private String name;
    }
}
