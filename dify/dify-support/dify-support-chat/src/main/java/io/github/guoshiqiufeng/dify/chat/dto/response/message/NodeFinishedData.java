package io.github.guoshiqiufeng.dify.chat.dto.response.message;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/15 17:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeFinishedData extends NodeStartedData {

    @JsonAlias("process_data")
    private Map<String, Object> processData;

    private Map<String, Object> outputs;

    private String status;
    private String error;

    @JsonAlias("elapsed_time")
    private Integer elapsedTime;

    @JsonAlias("execution_metadata")
    private ExecutionMetadata executionMetadata;

    @Data
    public static class ExecutionMetadata {
        @JsonAlias("total_tokens")
        private Integer totalTokens;

        @JsonAlias("total_price")
        private BigDecimal totalPrice;

        /**
         * optional e.g. USD / RMB
         */
        private String currency;

    }
}
