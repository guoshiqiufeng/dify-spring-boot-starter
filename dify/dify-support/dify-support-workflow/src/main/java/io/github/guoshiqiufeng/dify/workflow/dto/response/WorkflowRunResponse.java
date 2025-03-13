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
package io.github.guoshiqiufeng.dify.workflow.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/11 14:36
 */
@Data
public class WorkflowRunResponse implements Serializable {
    private static final long serialVersionUID = 7374887951820521203L;

    @JsonAlias("workflow_run_id")
    private String workflowRunId;

    @JsonAlias("task_id")
    private String taskId;

    private WorkflowRunData data;

    @Data
    public static class WorkflowRunData implements Serializable {
        private static final long serialVersionUID = -808300279940666254L;

        /**
         * workflow 执行 ID
         */
        private String id;

        @JsonAlias("workflow_id")
        private String workflowId;

        /**
         * 执行状态, running / succeeded / failed / stopped
         */
        private String status;

        private Map<String, Object> outputs;

        private String error;

        @JsonAlias("elapsed_time")
        private Float elapsedTime;

        @JsonAlias("total_tokens")
        private Integer totalTokens;

        @JsonAlias("total_steps")
        private Integer totalSteps;

        @JsonAlias("created_at")
        private Long createdAt;

        @JsonAlias("finished_at")
        private Long finishedAt;


    }
}
