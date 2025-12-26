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
package io.github.guoshiqiufeng.dify.workflow.dto.response.stream;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.github.guoshiqiufeng.dify.workflow.dto.response.MetaData;
import lombok.Data;

import java.util.Map;

/**
 * @author yanghq
 * @version 0.12.1
 * @since 2025/5/16 17:50
 */
@Data
public class AgentLogData {
    @JsonAlias("node_execution_id")
    private String nodeExecutionId;

    private String id;

    private String label;

    @JsonAlias("parent_id")
    private String parentId;

    private String error;

    private String status;

    private Map<String, Object> data;

    private MetaData metadata;

    @JsonAlias("node_id")
    private String nodeId;
}
