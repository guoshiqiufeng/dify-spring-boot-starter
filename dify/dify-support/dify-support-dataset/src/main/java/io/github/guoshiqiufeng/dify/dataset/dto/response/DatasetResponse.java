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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.PermissionEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 13:15
 */
@Data
public class DatasetResponse implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 权限
     */
    private PermissionEnum permission;

    /**
     * 数据源类型
     */
    @JsonAlias("data_source_type")
    private String dataSourceType;

    /**
     * 索引技术
     */
    @JsonAlias("indexing_technique")
    private IndexingTechniqueEnum indexingTechnique;

    /**
     * 应用数量
     */
    @JsonAlias("app_count")
    private Integer appCount;

    /**
     * 文档数量
     */
    @JsonAlias("document_count")
    private Integer documentCount;

    /**
     * 单词数量
     */
    @JsonAlias("word_count")
    private Integer wordCount;

    /**
     * 创建人
     */
    @JsonAlias("created_by")
    private String createdBy;

    /**
     * 创建时间
     */
    @JsonAlias("created_at")
    private Long createdAt;

    /**
     * 更新人
     */
    @JsonAlias("updated_by")
    private String updatedBy;

    /**
     * 更新时间
     */
    @JsonAlias("updated_at")
    private Long updatedAt;

    @JsonAlias("embeddingModel")
    @JsonProperty("embedding_model")
    private String embeddingModel;

    @JsonAlias("embeddingModelProvider")
    @JsonProperty("embedding_model_provider")
    private String embeddingModelProvider;

    @JsonAlias("embeddingAvailable")
    @JsonProperty("embedding_available")
    private Boolean embeddingAvailable;
}
