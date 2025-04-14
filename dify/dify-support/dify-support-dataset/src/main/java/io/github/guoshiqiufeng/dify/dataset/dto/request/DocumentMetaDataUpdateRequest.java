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


    @JsonAlias("datasetId")
    private String datasetId;

    @JsonProperty("operation_data")
    @JsonAlias("operationData")
    private List<OperationData> operationData;


    @Data
    public static class OperationData implements Serializable {

        private static final long serialVersionUID = 6071302031098356179L;

        @JsonProperty("document_id")
        @JsonAlias("documentId")
        private String documentId;

        @JsonProperty("metadata_list")
        @JsonAlias("metadataList")
        private List<MetaData> metadataList;
    }


}
