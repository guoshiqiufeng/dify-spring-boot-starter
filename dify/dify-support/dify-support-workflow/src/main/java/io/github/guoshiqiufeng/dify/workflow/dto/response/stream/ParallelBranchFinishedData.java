package io.github.guoshiqiufeng.dify.workflow.dto.response.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yanghq
 * @version 0.12.1
 * @since 2025/5/16 17:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParallelBranchFinishedData extends ParallelBranchStartedData {

    private String status;

    private String error;
}
