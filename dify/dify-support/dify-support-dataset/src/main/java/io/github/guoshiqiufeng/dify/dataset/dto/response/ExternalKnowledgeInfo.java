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
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/5/15 14:24
 */
@Data
public class ExternalKnowledgeInfo implements Serializable {
    private static final long serialVersionUID = -8388356622715394711L;

    /**
     * 外部知识库 ID
     */
    @JsonProperty("external_knowledge_id")
    @JsonAlias("externalKnowledgeId")
    private String externalKnowledgeId;

    /**
     * 外部知识库 API_ID
     */
    @JsonProperty("external_knowledge_api_id")
    @JsonAlias("externalKnowledgeApiId")
    private String externalKnowledgeApiId;

    @JsonProperty("external_knowledge_api_name")
    @JsonAlias("externalKnowledgeApiName")
    private String externalKnowledgeApiName;

    @JsonProperty("external_knowledge_api_endpoint")
    @JsonAlias("externalKnowledgeApiEndpoint")
    private String externalKnowledgeApiEndpoint;
}
