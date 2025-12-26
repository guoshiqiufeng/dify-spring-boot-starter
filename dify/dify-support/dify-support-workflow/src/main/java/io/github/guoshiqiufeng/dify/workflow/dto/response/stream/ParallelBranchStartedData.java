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
import lombok.Data;

/**
 * @author yanghq
 * @version 0.12.1
 * @since 2025/5/16 17:38
 */
@Data
public class ParallelBranchStartedData {


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
