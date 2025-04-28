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
package io.github.guoshiqiufeng.dify.workflow.dto.response.stream;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 10:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NodeFinishedData extends NodeStartedData {

    /**
     * Optional node process data
     */
    @JsonAlias("process_data")
    private Map<String, Object> processData;

    private Map<String, Object> outputs;

    /**
     * 执行状态, running / succeeded / failed / stopped
     */
    private String status;

    private String error;

    @JsonAlias("elapsed_time")
    private Float elapsedTime;

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
