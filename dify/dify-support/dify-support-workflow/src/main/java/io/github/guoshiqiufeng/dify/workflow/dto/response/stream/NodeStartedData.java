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
package io.github.guoshiqiufeng.dify.workflow.dto.response.stream;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 10:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeStartedData extends BaseWorkflowRunData {

    /**
     * ID of node
     */
    @JsonAlias("node_id")
    private String nodeId;

    /**
     * type of node
     */
    @JsonAlias("node_type")
    private String nodeType;

    /**
     * name of node
     */
    private String title;

    /**
     * Execution sequence number, used to display Tracing Node sequence
     */
    private Integer index;

    /**
     * optional Prefix node ID, used for canvas display execution path
     */
    @JsonAlias("predecessor_node_id")
    private String predecessorNodeId;

    /**
     * Contents of all preceding node variables used in the node
     */
    private Map<String, Object> inputs;
}
