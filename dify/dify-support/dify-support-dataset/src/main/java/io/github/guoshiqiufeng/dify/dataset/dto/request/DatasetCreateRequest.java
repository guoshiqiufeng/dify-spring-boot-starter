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
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.PermissionEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.ProviderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 13:22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetCreateRequest extends BaseDatasetRequest implements Serializable {

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库描述
     */
    private String description;

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

    private ProviderEnum provider;

    /**
     * 外部知识库 API_ID
     */
    @JsonProperty("external_knowledge_api_id")
    @JsonAlias("externalKnowledgeApiId")
    private String externalKnowledgeApiId;

    /**
     * 外部知识库 ID
     */
    @JsonProperty("external_knowledge_id")
    @JsonAlias("externalKnowledgeId")
    private String externalKnowledgeId;


}
