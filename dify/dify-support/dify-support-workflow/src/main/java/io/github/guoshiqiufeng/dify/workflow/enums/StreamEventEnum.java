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
package io.github.guoshiqiufeng.dify.workflow.enums;

import io.github.guoshiqiufeng.dify.workflow.dto.response.stream.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 09:47
 */
@AllArgsConstructor
@Getter
public enum StreamEventEnum {

    /**
     * workflow started
     */
    workflow_started(WorkflowStartedData.class),

    /**
     * node started
     */
    node_started(NodeStartedData.class),

    text_chunk(Map.class),

    /**
     * node finished
     */
    node_finished(NodeFinishedData.class),

    /**
     * workflow finished
     */
    workflow_finished(WorkflowFinishedData.class),

    /**
     * parallel_branch_started
     */
    parallel_branch_started(ParallelBranchStartedData.class),

    /**
     * parallel_branch_finished
     */
    parallel_branch_finished(ParallelBranchFinishedData.class),

    /**
     * agent_log
     */
    agent_log(AgentLogData.class),

    ;

    private final Class<?> clazz;
}
