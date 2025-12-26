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
package io.github.guoshiqiufeng.dify.chat.dto.response.message;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link NodeFinishedData}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class NodeFinishedDataTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        NodeFinishedData nodeFinishedData = new NodeFinishedData();
        Map<String, Object> processData = new HashMap<>();
        processData.put("status", "in_progress");
        processData.put("progress", 75);

        Map<String, Object> outputs = new HashMap<>();
        outputs.put("text", "The answer is 42");
        outputs.put("confidence", 0.95);

        String status = "completed";
        String error = null;
        Integer elapsedTime = 1200;

        NodeFinishedData.ExecutionMetadata executionMetadata = new NodeFinishedData.ExecutionMetadata();
        executionMetadata.setTotalTokens(250);
        executionMetadata.setTotalPrice(new BigDecimal("0.05"));
        executionMetadata.setCurrency("USD");

        // Act
        nodeFinishedData.setProcessData(processData);
        nodeFinishedData.setOutputs(outputs);
        nodeFinishedData.setStatus(status);
        nodeFinishedData.setError(error);
        nodeFinishedData.setElapsedTime(elapsedTime);
        nodeFinishedData.setExecutionMetadata(executionMetadata);

        // Assert
        assertEquals(processData, nodeFinishedData.getProcessData());
        assertEquals(outputs, nodeFinishedData.getOutputs());
        assertEquals(status, nodeFinishedData.getStatus());
        assertEquals(error, nodeFinishedData.getError());
        assertEquals(elapsedTime, nodeFinishedData.getElapsedTime());
        assertEquals(executionMetadata, nodeFinishedData.getExecutionMetadata());
        assertEquals(250, nodeFinishedData.getExecutionMetadata().getTotalTokens());
        assertEquals(new BigDecimal("0.05"), nodeFinishedData.getExecutionMetadata().getTotalPrice());
        assertEquals("USD", nodeFinishedData.getExecutionMetadata().getCurrency());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        NodeFinishedData nodeFinishedData = new NodeFinishedData();

        // Assert
        assertNull(nodeFinishedData.getProcessData());
        assertNull(nodeFinishedData.getOutputs());
        assertNull(nodeFinishedData.getStatus());
        assertNull(nodeFinishedData.getError());
        assertNull(nodeFinishedData.getElapsedTime());
        assertNull(nodeFinishedData.getExecutionMetadata());
    }

    @Test
    public void testExecutionMetadataGetterAndSetter() {
        // Arrange
        NodeFinishedData.ExecutionMetadata executionMetadata = new NodeFinishedData.ExecutionMetadata();
        Integer totalTokens = 300;
        BigDecimal totalPrice = new BigDecimal("0.06");
        String currency = "EUR";

        // Act
        executionMetadata.setTotalTokens(totalTokens);
        executionMetadata.setTotalPrice(totalPrice);
        executionMetadata.setCurrency(currency);

        // Assert
        assertEquals(totalTokens, executionMetadata.getTotalTokens());
        assertEquals(totalPrice, executionMetadata.getTotalPrice());
        assertEquals(currency, executionMetadata.getCurrency());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        NodeFinishedData nodeFinishedData = new NodeFinishedData();
        String id = "test-id";
        String nodeId = "test-node-id";
        String nodeType = "llm";
        String title = "Language Model";
        Integer index = 1;

        // Act
        nodeFinishedData.setId(id);
        nodeFinishedData.setNodeId(nodeId);
        nodeFinishedData.setNodeType(nodeType);
        nodeFinishedData.setTitle(title);
        nodeFinishedData.setIndex(index);

        // Assert
        assertEquals(id, nodeFinishedData.getId());
        assertEquals(nodeId, nodeFinishedData.getNodeId());
        assertEquals(nodeType, nodeFinishedData.getNodeType());
        assertEquals(title, nodeFinishedData.getTitle());
        assertEquals(index, nodeFinishedData.getIndex());
    }

    @Test
    public void testToString() {
        // Arrange
        NodeFinishedData nodeFinishedData = new NodeFinishedData();
        nodeFinishedData.setId("test-id");
        nodeFinishedData.setNodeId("test-node-id");
        nodeFinishedData.setStatus("completed");
        nodeFinishedData.setElapsedTime(1000);

        NodeFinishedData.ExecutionMetadata executionMetadata = new NodeFinishedData.ExecutionMetadata();
        executionMetadata.setTotalTokens(200);
        executionMetadata.setTotalPrice(new BigDecimal("0.04"));
        executionMetadata.setCurrency("USD");
        nodeFinishedData.setExecutionMetadata(executionMetadata);

        // Act
        String toString = nodeFinishedData.toString();

        // Assert
        assertTrue(toString.contains("id=test-id"));
        assertTrue(toString.contains("nodeId=test-node-id"));
        assertTrue(toString.contains("status=completed"));
        assertTrue(toString.contains("elapsedTime=1000"));
        assertTrue(toString.contains("executionMetadata="));
    }

    @Test
    public void testExecutionMetadataEqualsAndHashCode() {
        // Arrange
        NodeFinishedData.ExecutionMetadata metadata1 = new NodeFinishedData.ExecutionMetadata();
        metadata1.setTotalTokens(100);
        metadata1.setTotalPrice(new BigDecimal("0.02"));
        metadata1.setCurrency("USD");

        NodeFinishedData.ExecutionMetadata metadata2 = new NodeFinishedData.ExecutionMetadata();
        metadata2.setTotalTokens(100);
        metadata2.setTotalPrice(new BigDecimal("0.02"));
        metadata2.setCurrency("USD");

        NodeFinishedData.ExecutionMetadata metadata3 = new NodeFinishedData.ExecutionMetadata();
        metadata3.setTotalTokens(200);
        metadata3.setTotalPrice(new BigDecimal("0.04"));
        metadata3.setCurrency("EUR");

        // Assert
        assertEquals(metadata1, metadata2);
        assertEquals(metadata1.hashCode(), metadata2.hashCode());
        assertNotEquals(metadata1, metadata3);
        assertNotEquals(metadata1.hashCode(), metadata3.hashCode());
    }

    @Test
    public void testExecutionMetadataToString() {
        // Arrange
        NodeFinishedData.ExecutionMetadata metadata = new NodeFinishedData.ExecutionMetadata();
        metadata.setTotalTokens(100);
        metadata.setTotalPrice(new BigDecimal("0.02"));
        metadata.setCurrency("USD");

        // Act
        String toString = metadata.toString();

        // Assert
        assertTrue(toString.contains("totalTokens=100"));
        assertTrue(toString.contains("totalPrice=0.02"));
        assertTrue(toString.contains("currency=USD"));
    }
}
