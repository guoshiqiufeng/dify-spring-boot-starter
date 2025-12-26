/*
 * Copyright (c) 2025-2026, fubluesky (fubluesky@foxmail.com)
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

    @JsonAlias("retrievalModelDict")
    @JsonProperty("retrieval_model_dict")
    private RetrievalModel retrievalModelDict;

    private List<Tag> tags;

    @JsonAlias("docForm")
    @JsonProperty("doc_form")
    private DocFormEnum docForm;

    @JsonProperty("external_knowledge_info")
    @JsonAlias("externalKnowledgeInfo")
    private ExternalKnowledgeInfo externalKnowledgeInfo;
}
