package io.github.guoshiqiufeng.dify.chat.dto.response.message;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/15 17:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkflowStartedData extends CompletionData {

    private String id;

    @JsonAlias("workflow_id")
    private String workflowId;

    @JsonAlias("sequence_number")
    private Integer sequenceNumber;

    @JsonAlias("created_at")
    private Long createdAt;

}
