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
package io.github.guoshiqiufeng.dify.workflow.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/11 14:36
 */
@Data
public class WorkflowInfoResponse implements Serializable {
    private static final long serialVersionUID = -3258145450973500655L;
    /**
     * workflow 执行 ID
     */
    private String id;

    @JsonAlias("workflow_id")
    private String workflowId;

    /**
     * 执行状态, running / succeeded / failed / stopped
     */
    private String status;

    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, Object> inputs;

    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, Object> outputs;

    private String error;

    @JsonAlias("elapsed_time")
    private Float elapsedTime;

    @JsonAlias("total_tokens")
    private Integer totalTokens;

    @JsonAlias("total_steps")
    private Integer totalSteps;

    @JsonAlias("created_at")
    @JsonDeserialize(using = DateToTimestampDeserializer.class)
    private Long createdAt;

    @JsonAlias("finished_at")
    @JsonDeserialize(using = DateToTimestampDeserializer.class)
    private Long finishedAt;

    /**
     * 自定义反序列化器
     */
    public static class MapDeserializer extends JsonDeserializer<Map<String, Object>> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public Map<String, Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String value = p.getText();
            if (value == null || value.isEmpty()) {
                return null;
            }
            try {
                return objectMapper.readValue(value, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                });
            } catch (Exception e) {
                throw new IOException("Failed to deserialize string to Map: " + value, e);
            }
        }
    }

    public static class DateToTimestampDeserializer extends JsonDeserializer<Long> {
        private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z";

        private static final ThreadLocal<SimpleDateFormat> SDF_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // 确保时区为UTC
            return sdf;
        });

        @Override
        public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String dateStr = p.getText();
            if (dateStr == null || dateStr.isEmpty()) {
                return null;
            }
            try {
                return Long.parseLong(dateStr.trim());
            } catch (NumberFormatException ignored) {
            }
            try {
                Date date = SDF_THREAD_LOCAL.get().parse(dateStr);
                return date.getTime();
            } catch (ParseException e) {
                throw new IOException("Failed to parse date string: " + dateStr, e);
            }
        }
    }
}
