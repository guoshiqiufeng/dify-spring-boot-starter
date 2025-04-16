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
package io.github.guoshiqiufeng.dify.chat.dto.response.message;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/15 17:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkflowFinishedData extends CompletionData {

    private String id;

    @JsonAlias("workflow_id")
    private String workflowId;

    private Map<String, Object> outputs;

    private String status;
    private String error;

    @JsonAlias("elapsed_time")
    private Integer elapsedTime;

    @JsonAlias("total_tokens")
    private Integer totalTokens;

    @JsonAlias("total_steps")
    private Integer totalSteps;


    @JsonAlias("created_at")
    private Long createdAt;
}
