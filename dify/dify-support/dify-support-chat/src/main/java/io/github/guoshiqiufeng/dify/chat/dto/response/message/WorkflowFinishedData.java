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
