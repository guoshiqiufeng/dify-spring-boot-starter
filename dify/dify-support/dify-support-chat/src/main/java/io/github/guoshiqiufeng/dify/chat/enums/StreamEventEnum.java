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
package io.github.guoshiqiufeng.dify.chat.enums;

import io.github.guoshiqiufeng.dify.chat.dto.response.message.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/15 19:47
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

    agent_message(EmptyData.class),

    agent_thought(EmptyData.class),

    message(EmptyData.class),

    message_end(EmptyData.class),

    message_replace(EmptyData.class),

    error(EmptyData.class),

    ping(EmptyData.class),

    ;

    private final Class<? extends CompletionData> clazz;
}
