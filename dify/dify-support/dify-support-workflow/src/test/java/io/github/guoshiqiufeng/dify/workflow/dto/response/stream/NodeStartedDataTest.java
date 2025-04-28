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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link NodeStartedData}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class NodeStartedDataTest {

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

        // Act
        NodeStartedData data = new NodeStartedData();
        data.setId(id);
        data.setCreatedAt(createdAt);
        data.setNodeId(nodeId);
        data.setNodeType(nodeType);
        data.setTitle(title);
        data.setIndex(index);
        data.setPredecessorNodeId(predecessorNodeId);
        data.setInputs(inputs);

        // Assert
        assertEquals(id, data.getId());
        assertEquals(createdAt, data.getCreatedAt());
        assertEquals(nodeId, data.getNodeId());
        assertEquals(nodeType, data.getNodeType());
        assertEquals(title, data.getTitle());
        assertEquals(index, data.getIndex());
        assertEquals(predecessorNodeId, data.getPredecessorNodeId());
        assertEquals(inputs, data.getInputs());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        NodeStartedData data1 = new NodeStartedData();
        data1.setId("run-123");
        data1.setCreatedAt(1705395332L);
        data1.setNodeId("node-456");
        data1.setNodeType("llm");
        data1.setTitle("Process Text");
        data1.setIndex(1);

        NodeStartedData data2 = new NodeStartedData();
        data2.setId("run-123");
        data2.setCreatedAt(1705395332L);
        data2.setNodeId("node-456");
        data2.setNodeType("llm");
        data2.setTitle("Process Text");
        data2.setIndex(1);

        NodeStartedData data3 = new NodeStartedData();
        data3.setId("run-789");
        data3.setCreatedAt(1705395332L);
        data3.setNodeId("node-999");
        data3.setNodeType("code");
        data3.setTitle("Execute Code");
        data3.setIndex(2);

        // Assert
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1, data3);
        assertNotEquals(data1.hashCode(), data3.hashCode());
    }

    @Test
    public void testInheritance() {
        // Arrange
        NodeStartedData data = new NodeStartedData();

        // Assert
        assertInstanceOf(BaseWorkflowRunData.class, data);
    }

    @Test
    public void testJsonSerializationDeserialization() throws Exception {
        // Arrange
        NodeStartedData data = new NodeStartedData();
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

        // Act - Deserialize
        NodeStartedData deserializedData = objectMapper.readValue(json, NodeStartedData.class);

        // Assert - Deserialization
        assertEquals(data.getId(), deserializedData.getId());
        assertEquals(data.getCreatedAt(), deserializedData.getCreatedAt());
        assertEquals(data.getNodeId(), deserializedData.getNodeId());
        assertEquals(data.getNodeType(), deserializedData.getNodeType());
        assertEquals(data.getTitle(), deserializedData.getTitle());
        assertEquals(data.getIndex(), deserializedData.getIndex());
        assertEquals(data.getPredecessorNodeId(), deserializedData.getPredecessorNodeId());
        assertEquals("Hello, world!", deserializedData.getInputs().get("text"));
    }

    @Test
    public void testJsonDeserializationWithJsonAlias() throws Exception {
        // Arrange - Use JsonAlias field names
        String json = "{\"id\":\"run-123\",\"created_at\":1705395332,\"node_id\":\"node-456\"," +
                "\"node_type\":\"llm\",\"title\":\"Process Text\",\"index\":1," +
                "\"predecessor_node_id\":\"node-123\",\"inputs\":{\"text\":\"Hello, world!\"}}";

        // Act
        NodeStartedData data = objectMapper.readValue(json, NodeStartedData.class);

        // Assert
        assertEquals("run-123", data.getId());
        assertEquals(Long.valueOf(1705395332), data.getCreatedAt());
        assertEquals("node-456", data.getNodeId());
        assertEquals("llm", data.getNodeType());
        assertEquals("Process Text", data.getTitle());
        assertEquals(Integer.valueOf(1), data.getIndex());
        assertEquals("node-123", data.getPredecessorNodeId());
        assertEquals("Hello, world!", data.getInputs().get("text"));
    }

    @Test
    public void testToString() {
        // Arrange
        NodeStartedData data = new NodeStartedData();
        data.setId("run-123");
        data.setCreatedAt(1705395332L);
        data.setNodeId("node-456");
        data.setNodeType("llm");
        data.setTitle("Process Text");
        data.setIndex(1);

        // Act
        String toString = data.toString();

        // Assert
        assertTrue(toString.contains("id=run-123"));
        assertTrue(toString.contains("createdAt=1705395332"));
        assertTrue(toString.contains("nodeId=node-456"));
        assertTrue(toString.contains("nodeType=llm"));
        assertTrue(toString.contains("title=Process Text"));
        assertTrue(toString.contains("index=1"));
    }
}
