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
package io.github.guoshiqiufeng.dify.workflow.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.guoshiqiufeng.dify.workflow.dto.response.jackson.WorkflowRunStreamResponseDeserializer;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/11 14:36
 */
@Data
//@JsonDeserialize(using = WorkflowRunStreamResponseDeserializer.class)
public class WorkflowRunStreamResponse implements Serializable {
    private static final long serialVersionUID = 7374887951820521203L;

    /**
     * workflow_started、node_started、node_finished、text_chunk、node_finished
     */
    private StreamEventEnum event;

    @JsonAlias("workflow_run_id")
    private String workflowRunId;

    @JsonAlias("task_id")
    private String taskId;

    private Object data;

}
