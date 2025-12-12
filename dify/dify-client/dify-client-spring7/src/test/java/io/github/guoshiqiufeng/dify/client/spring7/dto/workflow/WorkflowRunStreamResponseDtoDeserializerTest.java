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
package io.github.guoshiqiufeng.dify.client.spring7.dto.workflow;


import io.github.guoshiqiufeng.dify.workflow.dto.response.stream.NodeStartedData;
import io.github.guoshiqiufeng.dify.workflow.dto.response.stream.WorkflowFinishedData;
import io.github.guoshiqiufeng.dify.workflow.dto.response.stream.WorkflowStartedData;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link WorkflowRunStreamResponseDtoDeserializer}.
 *
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/12 10:00
 */
class WorkflowRunStreamResponseDtoDeserializerTest {

    private WorkflowRunStreamResponseDtoDeserializer deserializer;
    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        deserializer = new WorkflowRunStreamResponseDtoDeserializer();
        jsonMapper = new JsonMapper();
    }

    @Test
    void testDeserializeWithoutEventField() throws IOException {
        // Given
        String json = "{\"task_id\":\"task123\",\"workflow_run_id\":\"run456\"}";
        JsonParser parser = jsonMapper.tokenStreamFactory().createParser(ObjectReadContext.empty(), json.getBytes());

        // When
        WorkflowRunStreamResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("task123", result.getData().getTaskId());
        assertEquals("run456", result.getData().getWorkflowRunId());
        assertNull(result.getData().getEvent());
    }

    @Test
    void testDeserializeWithInvalidEvent() throws IOException {
        // Given
        String json = "{\"event\":\"INVALID_EVENT\",\"task_id\":\"task123\"}";
        JsonParser parser = jsonMapper.tokenStreamFactory().createParser(ObjectReadContext.empty(), json.getBytes());

        // When
        WorkflowRunStreamResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("task123", result.getData().getTaskId());
        assertNull(result.getData().getEvent());
    }

    @Test
    void testDeserializeWithValidEvent() throws Exception {
        // Given
        String json = "{" +
                "\"event\":\"workflow_started\"," +
                "\"task_id\":\"task123\"," +
                "\"workflow_run_id\":\"run456\"," +
                "\"data\":{" +
                "\"id\":\"workflow789\"," +
                "\"name\":\"test workflow\"," +
                "\"status\":\"running\"" +
                "}" +
                "}";
        JsonParser parser = jsonMapper.tokenStreamFactory().createParser(ObjectReadContext.empty(), json.getBytes());

        // When
        WorkflowRunStreamResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(StreamEventEnum.workflow_started, result.getData().getEvent());
        assertEquals("task123", result.getData().getTaskId());
        assertEquals("run456", result.getData().getWorkflowRunId());
        assertNotNull(result.getData().getData());
        assertInstanceOf(WorkflowStartedData.class, result.getData().getData());
    }

    @Test
    void testDeserializeWithWorkflowFinishedEvent() throws Exception {
        // Given
        String json = "{" +
                "\"event\":\"workflow_finished\"," +
                "\"task_id\":\"task123\"," +
                "\"workflow_run_id\":\"run456\"," +
                "\"data\":{" +
                "\"outputs\":{\"result\":\"success\"}," +
                "\"error\":null," +
                "\"status\":\"completed\"" +
                "}" +
                "}";
        JsonParser parser = jsonMapper.tokenStreamFactory().createParser(ObjectReadContext.empty(), json.getBytes());

        // When
        WorkflowRunStreamResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(StreamEventEnum.workflow_finished, result.getData().getEvent());
        assertEquals("task123", result.getData().getTaskId());
        assertEquals("run456", result.getData().getWorkflowRunId());
        assertNotNull(result.getData().getData());
        assertInstanceOf(WorkflowFinishedData.class, result.getData().getData());
    }

    @Test
    void testDeserializeWithNodeStartedEvent() throws Exception {
        // Given
        String json = "{" +
                "\"event\":\"node_started\"," +
                "\"task_id\":\"task123\"," +
                "\"workflow_run_id\":\"run456\"," +
                "\"data\":{" +
                "\"node_id\":\"node789\"," +
                "\"node_type\":\"llm\"," +
                "\"index\":1" +
                "}" +
                "}";
        JsonParser parser = jsonMapper.tokenStreamFactory().createParser(ObjectReadContext.empty(), json.getBytes());

        // When
        WorkflowRunStreamResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(StreamEventEnum.node_started, result.getData().getEvent());
        assertEquals("task123", result.getData().getTaskId());
        assertEquals("run456", result.getData().getWorkflowRunId());
        assertNotNull(result.getData().getData());
        assertInstanceOf(NodeStartedData.class, result.getData().getData());
    }

    @Test
    void testDeserializeWithNullDataField() throws Exception {
        // Given
        String json = "{" +
                "\"event\":\"workflow_started\"," +
                "\"task_id\":\"task123\"," +
                "\"data\":null" +
                "}";
        JsonParser parser = jsonMapper.tokenStreamFactory().createParser(ObjectReadContext.empty(), json.getBytes());

        // When
        WorkflowRunStreamResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(StreamEventEnum.workflow_started, result.getData().getEvent());
        assertEquals("task123", result.getData().getTaskId());
        assertNull(result.getData().getData());
    }

    @Test
    void testDeserializeWithEmptyDataField() throws Exception {
        // Given
        String json = "{" +
                "\"event\":\"workflow_started\"," +
                "\"task_id\":\"task123\"," +
                "\"data\":{}" +
                "}";
        JsonParser parser = jsonMapper.tokenStreamFactory().createParser(ObjectReadContext.empty(), json.getBytes());

        // When
        WorkflowRunStreamResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(StreamEventEnum.workflow_started, result.getData().getEvent());
        assertEquals("task123", result.getData().getTaskId());
        assertNotNull(result.getData().getData());
        assertInstanceOf(WorkflowStartedData.class, result.getData().getData());
    }
}
