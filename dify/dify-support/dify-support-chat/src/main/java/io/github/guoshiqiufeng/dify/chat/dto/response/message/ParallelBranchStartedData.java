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

/**
 * @author yanghq
 * @version 1.0.3
 * @since 2025/5/29 10:30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParallelBranchStartedData extends CompletionData {


    @JsonAlias("parallel_id")
    private String parallelId;

    @JsonAlias("parallel_branch_id")
    private String parallelBranchId;

    @JsonAlias("parent_parallel_id")
    private String parentParallelId;

    @JsonAlias("parent_parallel_start_node_id")
    private String parentParallelStartNodeId;

    @JsonAlias("iteration_id")
    private String iterationId;

    @JsonAlias("loop_id")
    private String loopId;

    /**
     * created_at (timestamp) 创建时间
     */
    @JsonAlias("created_at")
    private Long createdAt;
}
