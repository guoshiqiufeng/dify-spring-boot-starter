package io.github.guoshiqiufeng.dify.workflow.dto.response.stream;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.github.guoshiqiufeng.dify.workflow.dto.response.MetaData;
import lombok.Data;

import java.util.Map;

/**
 * @author yanghq
 * @version 0.12.1
 * @since 2025/5/16 17:50
 */
@Data
public class AgentLogData {
    @JsonAlias("node_execution_id")
    private String nodeExecutionId;

    private String id;

    private String label;

    @JsonAlias("parent_id")
    private String parentId;

    private String error;

    private String status;

    private Map<String, Object> data;

    private MetaData metadata;

    @JsonAlias("node_id")
    private String nodeId;
}
