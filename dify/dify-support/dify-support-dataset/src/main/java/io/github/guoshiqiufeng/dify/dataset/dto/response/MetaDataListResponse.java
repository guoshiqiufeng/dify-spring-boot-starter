package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 10:58
 */
@Data
public class MetaDataListResponse implements Serializable {
    private static final long serialVersionUID = 1633421634204998066L;

    @JsonAlias("built_in_field_name")
    private Boolean builtInFieldEnabled;

    @JsonAlias("doc_metadata")
    private List<DocMetadata> docMetadata;

    @Data
    public static class DocMetadata implements Serializable {

        private static final long serialVersionUID = -5140673844526720576L;

        private String id;
        private String type;
        private String name;
        @JsonAlias("use_count")
        private Integer userCount;
    }
}
