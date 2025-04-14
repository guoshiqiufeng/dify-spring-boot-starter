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
import io.github.guoshiqiufeng.dify.dataset.dto.request.document.ProcessRule;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocFormEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 15:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentUpdateByTextRequest extends BaseDatasetRequest implements Serializable {


    @JsonAlias("datasetId")
    private String datasetId;


    @JsonAlias("documentId")
    private String documentId;

    private String name;

    private String text;

    /**
     * 文档类型（选填）
     */
    @JsonProperty("doc_type")
    @JsonAlias("docType")
    private DocTypeEnum docType;

    /**
     * 文档元数据（如提供文档类型则必填）
     */
    @JsonProperty("doc_metadata")
    @JsonAlias("docMetadata")
    private List<MetaData> docMetadata;

    /**
     * 索引模式
     */
    @JsonProperty("indexing_technique")
    @JsonAlias("indexingTechnique")
    private IndexingTechniqueEnum indexingTechnique;

    @JsonProperty("doc_form")
    @JsonAlias("docForm")
    private DocFormEnum docForm;

    @JsonProperty("doc_language")
    @JsonAlias("docLanguage")
    private String docLanguage;

    @JsonProperty("process_rule")
    @JsonAlias("processRule")
    private ProcessRule processRule;

}
