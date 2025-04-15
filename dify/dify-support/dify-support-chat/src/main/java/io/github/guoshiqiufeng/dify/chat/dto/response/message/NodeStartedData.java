package io.github.guoshiqiufeng.dify.chat.dto.response.message;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/15 17:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeStartedData extends CompletionData {

    private String id;

    @JsonAlias("node_id")
    private String nodeId;

    @JsonAlias("nodeType")
    private String nodeType;

    private String title;

    private Integer index;

    @JsonAlias("predecessor_node_id")
    private String predecessorNodeId;

    private Map<String, Object> inputs;

    @JsonAlias("created_at")
    private Long createdAt;

}
