/*
 * Copyright (c) 2025-2026, fubluesky (fubluesky@foxmail.com)
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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link NodeFinishedData}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class NodeFinishedDataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSettersAndGetters() {
        // Arrange
        String id = "run-123";
        Long createdAt = 1705395332L;
        String nodeId = "node-456";
        String nodeType = "llm";
        String title = "Process Text";
        Integer index = 1;
        String predecessorNodeId = "node-123";
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("text", "Hello, world!");

        // NodeFinishedData specific fields
        Map<String, Object> processData = new HashMap<>();
        processData.put("steps", 3);
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", "Processed: Hello, world!");
        String status = "succeeded";
        String error = null;
        Float elapsedTime = 1.5f;

        // Create and setup execution metadata
        NodeFinishedData.ExecutionMetadata executionMetadata = new NodeFinishedData.ExecutionMetadata();
        executionMetadata.setTotalTokens(100);
        executionMetadata.setTotalPrice(new BigDecimal("0.002"));
        executionMetadata.setCurrency("USD");

        // Act
        NodeFinishedData data = new NodeFinishedData();
        data.setId(id);
        data.setCreatedAt(createdAt);
        data.setNodeId(nodeId);
        data.setNodeType(nodeType);
        data.setTitle(title);
        data.setIndex(index);
        data.setPredecessorNodeId(predecessorNodeId);
        data.setInputs(inputs);

        data.setProcessData(processData);
        data.setOutputs(outputs);
        data.setStatus(status);
        data.setError(error);
        data.setElapsedTime(elapsedTime);
        data.setExecutionMetadata(executionMetadata);

        // Assert
        assertEquals(id, data.getId());
        assertEquals(createdAt, data.getCreatedAt());
        assertEquals(nodeId, data.getNodeId());
        assertEquals(nodeType, data.getNodeType());
        assertEquals(title, data.getTitle());
        assertEquals(index, data.getIndex());
        assertEquals(predecessorNodeId, data.getPredecessorNodeId());
        assertEquals(inputs, data.getInputs());

        assertEquals(processData, data.getProcessData());
        assertEquals(outputs, data.getOutputs());
        assertEquals(status, data.getStatus());
        assertEquals(error, data.getError());
        assertEquals(elapsedTime, data.getElapsedTime());

        assertSame(executionMetadata, data.getExecutionMetadata());
        assertEquals(Integer.valueOf(100), data.getExecutionMetadata().getTotalTokens());
        assertEquals(new BigDecimal("0.002"), data.getExecutionMetadata().getTotalPrice());
        assertEquals("USD", data.getExecutionMetadata().getCurrency());
    }

    @Test
    public void testExecutionMetadataSettersAndGetters() {
        // Arrange
        Integer totalTokens = 100;
        BigDecimal totalPrice = new BigDecimal("0.002");
        String currency = "USD";

        // Act
        NodeFinishedData.ExecutionMetadata metadata = new NodeFinishedData.ExecutionMetadata();
        metadata.setTotalTokens(totalTokens);
        metadata.setTotalPrice(totalPrice);
        metadata.setCurrency(currency);

        // Assert
        assertEquals(totalTokens, metadata.getTotalTokens());
        assertEquals(totalPrice, metadata.getTotalPrice());
        assertEquals(currency, metadata.getCurrency());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        NodeFinishedData data1 = new NodeFinishedData();
        data1.setId("run-123");
        data1.setCreatedAt(1705395332L);
        data1.setNodeId("node-456");
        data1.setNodeType("llm");
        data1.setStatus("succeeded");

        NodeFinishedData.ExecutionMetadata metadata1 = new NodeFinishedData.ExecutionMetadata();
        metadata1.setTotalTokens(100);
        metadata1.setTotalPrice(new BigDecimal("0.002"));
        metadata1.setCurrency("USD");
        data1.setExecutionMetadata(metadata1);

        NodeFinishedData data2 = new NodeFinishedData();
        data2.setId("run-123");
        data2.setCreatedAt(1705395332L);
        data2.setNodeId("node-456");
        data2.setNodeType("llm");
        data2.setStatus("succeeded");

        NodeFinishedData.ExecutionMetadata metadata2 = new NodeFinishedData.ExecutionMetadata();
        metadata2.setTotalTokens(100);
        metadata2.setTotalPrice(new BigDecimal("0.002"));
        metadata2.setCurrency("USD");
        data2.setExecutionMetadata(metadata2);

        NodeFinishedData data3 = new NodeFinishedData();
        data3.setId("run-789");
        data3.setCreatedAt(1705395332L);
        data3.setNodeId("node-999");
        data3.setNodeType("code");
        data3.setStatus("failed");

        NodeFinishedData.ExecutionMetadata metadata3 = new NodeFinishedData.ExecutionMetadata();
        metadata3.setTotalTokens(50);
        metadata3.setTotalPrice(new BigDecimal("0.001"));
        metadata3.setCurrency("EUR");
        data3.setExecutionMetadata(metadata3);

        // Assert
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1, data3);
        assertNotEquals(data1.hashCode(), data3.hashCode());

        // Test ExecutionMetadata equals and hashCode
        assertEquals(metadata1, metadata2);
        assertEquals(metadata1.hashCode(), metadata2.hashCode());
        assertNotEquals(metadata1, metadata3);
        assertNotEquals(metadata1.hashCode(), metadata3.hashCode());
    }

    @Test
    public void testInheritance() {
        // Arrange
        NodeFinishedData data = new NodeFinishedData();

        // Assert
        assertTrue(data instanceof NodeStartedData);
        assertTrue(data instanceof BaseWorkflowRunData);
    }

    @Test
    public void testJsonSerializationDeserialization() throws Exception {
        // Arrange
        NodeFinishedData data = new NodeFinishedData();
        data.setId("run-123");
        data.setCreatedAt(1705395332L);
        data.setNodeId("node-456");
        data.setNodeType("llm");
        data.setTitle("Process Text");
        data.setIndex(1);
        data.setPredecessorNodeId("node-123");

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("text", "Hello, world!");
        data.setInputs(inputs);

        Map<String, Object> processData = new HashMap<>();
        processData.put("steps", 3);
        data.setProcessData(processData);

        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", "Processed: Hello, world!");
        data.setOutputs(outputs);

        data.setStatus("succeeded");
        data.setElapsedTime(1.5f);

        NodeFinishedData.ExecutionMetadata metadata = new NodeFinishedData.ExecutionMetadata();
        metadata.setTotalTokens(100);
        metadata.setTotalPrice(new BigDecimal("0.002"));
        metadata.setCurrency("USD");
        data.setExecutionMetadata(metadata);

        // Act - Serialize
        String json = objectMapper.writeValueAsString(data);

        // Assert - Serialization
        assertTrue(json.contains("\"id\":\"run-123\""));
        assertTrue(json.contains("\"createdAt\":1705395332"));
        assertTrue(json.contains("\"nodeId\":\"node-456\""));
        assertTrue(json.contains("\"nodeType\":\"llm\""));
        assertTrue(json.contains("\"title\":\"Process Text\""));
        assertTrue(json.contains("\"index\":1"));
        assertTrue(json.contains("\"predecessorNodeId\":\"node-123\""));
        assertTrue(json.contains("\"inputs\":{\"text\":\"Hello, world!\"}"));
        assertTrue(json.contains("\"processData\":{\"steps\":3}"));
        assertTrue(json.contains("\"outputs\":{\"result\":\"Processed: Hello, world!\"}"));
        assertTrue(json.contains("\"status\":\"succeeded\""));
        assertTrue(json.contains("\"elapsedTime\":1.5"));
        assertTrue(json.contains("\"executionMetadata\":{"));
        assertTrue(json.contains("\"totalTokens\":100"));
        assertTrue(json.contains("\"totalPrice\":0.002"));
        assertTrue(json.contains("\"currency\":\"USD\""));

        // Act - Deserialize
        NodeFinishedData deserializedData = objectMapper.readValue(json, NodeFinishedData.class);

        // Assert - Deserialization
        assertEquals(data.getId(), deserializedData.getId());
        assertEquals(data.getCreatedAt(), deserializedData.getCreatedAt());
        assertEquals(data.getNodeId(), deserializedData.getNodeId());
        assertEquals(data.getNodeType(), deserializedData.getNodeType());
        assertEquals(data.getTitle(), deserializedData.getTitle());
        assertEquals(data.getIndex(), deserializedData.getIndex());
        assertEquals(data.getPredecessorNodeId(), deserializedData.getPredecessorNodeId());
        assertEquals("Hello, world!", deserializedData.getInputs().get("text"));
        assertEquals(3, deserializedData.getProcessData().get("steps"));
        assertEquals("Processed: Hello, world!", deserializedData.getOutputs().get("result"));
        assertEquals(data.getStatus(), deserializedData.getStatus());
        assertEquals(data.getElapsedTime(), deserializedData.getElapsedTime());

        assertEquals(data.getExecutionMetadata().getTotalTokens(), deserializedData.getExecutionMetadata().getTotalTokens());
        assertEquals(data.getExecutionMetadata().getTotalPrice(), deserializedData.getExecutionMetadata().getTotalPrice());
        assertEquals(data.getExecutionMetadata().getCurrency(), deserializedData.getExecutionMetadata().getCurrency());
    }

    @Test
    public void testJsonDeserializationWithJsonAlias() throws Exception {
        // Arrange - Use JsonAlias field names
        String json = "{\"id\":\"run-123\",\"created_at\":1705395332,\"node_id\":\"node-456\"," +
                "\"node_type\":\"llm\",\"title\":\"Process Text\",\"index\":1," +
                "\"predecessor_node_id\":\"node-123\",\"inputs\":{\"text\":\"Hello, world!\"}," +
                "\"process_data\":{\"steps\":3},\"outputs\":{\"result\":\"Processed: Hello, world!\"}," +
                "\"status\":\"succeeded\",\"elapsed_time\":1.5," +
                "\"execution_metadata\":{\"total_tokens\":100,\"total_price\":0.002,\"currency\":\"USD\"}}";

        // Act
        NodeFinishedData data = objectMapper.readValue(json, NodeFinishedData.class);

        // Assert
        assertEquals("run-123", data.getId());
        assertEquals(Long.valueOf(1705395332), data.getCreatedAt());
        assertEquals("node-456", data.getNodeId());
        assertEquals("llm", data.getNodeType());
        assertEquals("Process Text", data.getTitle());
        assertEquals(Integer.valueOf(1), data.getIndex());
        assertEquals("node-123", data.getPredecessorNodeId());
        assertEquals("Hello, world!", data.getInputs().get("text"));
        assertEquals(3, data.getProcessData().get("steps"));
        assertEquals("Processed: Hello, world!", data.getOutputs().get("result"));
        assertEquals("succeeded", data.getStatus());
        assertEquals(1.5f, data.getElapsedTime());

        assertEquals(Integer.valueOf(100), data.getExecutionMetadata().getTotalTokens());
        assertEquals(new BigDecimal("0.002"), data.getExecutionMetadata().getTotalPrice());
        assertEquals("USD", data.getExecutionMetadata().getCurrency());
    }

    @Test
    public void testToString() {
        // Arrange
        NodeFinishedData data = new NodeFinishedData();
        data.setId("run-123");
        data.setCreatedAt(1705395332L);
        data.setNodeId("node-456");
        data.setNodeType("llm");
        data.setTitle("Process Text");
        data.setStatus("succeeded");

        NodeFinishedData.ExecutionMetadata metadata = new NodeFinishedData.ExecutionMetadata();
        metadata.setTotalTokens(100);
        data.setExecutionMetadata(metadata);

        // Act
        String toString = data.toString();

        // Assert
        assertTrue(toString.contains("id=run-123"));
        assertTrue(toString.contains("createdAt=1705395332"));
        assertTrue(toString.contains("nodeId=node-456"));
        assertTrue(toString.contains("nodeType=llm"));
        assertTrue(toString.contains("title=Process Text"));
        assertTrue(toString.contains("status=succeeded"));
        assertTrue(toString.contains("executionMetadata="));
        assertTrue(toString.contains("totalTokens=100"));
    }

    @Test
    public void testExecutionMetadataToString() {
        // Arrange
        NodeFinishedData.ExecutionMetadata metadata = new NodeFinishedData.ExecutionMetadata();
        metadata.setTotalTokens(100);
        metadata.setTotalPrice(new BigDecimal("0.002"));
        metadata.setCurrency("USD");

        // Act
        String toString = metadata.toString();

        // Assert
        assertTrue(toString.contains("totalTokens=100"));
        assertTrue(toString.contains("totalPrice=0.002"));
        assertTrue(toString.contains("currency=USD"));
    }
}
