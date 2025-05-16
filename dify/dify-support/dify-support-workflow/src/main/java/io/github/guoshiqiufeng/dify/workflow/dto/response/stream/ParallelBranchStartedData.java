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
