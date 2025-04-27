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
package io.github.guoshiqiufeng.dify.chat.dto.response.message;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link NodeStartedData}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class NodeStartedDataTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        NodeStartedData nodeStartedData = new NodeStartedData();
        String id = "test-id";
        String nodeId = "test-node-id";
        String nodeType = "llm";
        String title = "Language Model";
        Integer index = 1;
        String predecessorNodeId = "previous-node-id";
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("prompt", "Hello, world!");
        inputs.put("temperature", 0.7);
        Long createdAt = 1619517600000L; // 2021-04-27 12:00:00 UTC

        // Act
        nodeStartedData.setId(id);
        nodeStartedData.setNodeId(nodeId);
        nodeStartedData.setNodeType(nodeType);
        nodeStartedData.setTitle(title);
        nodeStartedData.setIndex(index);
        nodeStartedData.setPredecessorNodeId(predecessorNodeId);
        nodeStartedData.setInputs(inputs);
        nodeStartedData.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, nodeStartedData.getId());
        assertEquals(nodeId, nodeStartedData.getNodeId());
        assertEquals(nodeType, nodeStartedData.getNodeType());
        assertEquals(title, nodeStartedData.getTitle());
        assertEquals(index, nodeStartedData.getIndex());
        assertEquals(predecessorNodeId, nodeStartedData.getPredecessorNodeId());
        assertEquals(inputs, nodeStartedData.getInputs());
        assertEquals(createdAt, nodeStartedData.getCreatedAt());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        NodeStartedData nodeStartedData = new NodeStartedData();

        // Assert
        assertNull(nodeStartedData.getId());
        assertNull(nodeStartedData.getNodeId());
        assertNull(nodeStartedData.getNodeType());
        assertNull(nodeStartedData.getTitle());
        assertNull(nodeStartedData.getIndex());
        assertNull(nodeStartedData.getPredecessorNodeId());
        assertNull(nodeStartedData.getInputs());
        assertNull(nodeStartedData.getCreatedAt());
    }

    @Test
    public void testToString() {
        // Arrange
        NodeStartedData nodeStartedData = new NodeStartedData();
        nodeStartedData.setId("test-id");
        nodeStartedData.setNodeId("test-node-id");
        nodeStartedData.setNodeType("llm");
        nodeStartedData.setTitle("Language Model");
        nodeStartedData.setIndex(1);
        nodeStartedData.setPredecessorNodeId("previous-node-id");

        // Act
        String toString = nodeStartedData.toString();

        // Assert
        assertTrue(toString.contains("id=test-id"));
        assertTrue(toString.contains("nodeId=test-node-id"));
        assertTrue(toString.contains("nodeType=llm"));
        assertTrue(toString.contains("title=Language Model"));
        assertTrue(toString.contains("index=1"));
        assertTrue(toString.contains("predecessorNodeId=previous-node-id"));
    }
}
