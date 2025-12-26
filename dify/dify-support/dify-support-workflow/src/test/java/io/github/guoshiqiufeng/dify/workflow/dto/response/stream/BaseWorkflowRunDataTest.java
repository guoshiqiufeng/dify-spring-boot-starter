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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link BaseWorkflowRunData}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class BaseWorkflowRunDataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Concrete implementation for testing the abstract class
    private static class TestBaseWorkflowRunData extends BaseWorkflowRunData {
    }

    @Test
    public void testSettersAndGetters() {
        // Arrange
        String id = "run-123";
        Long createdAt = 1705395332L;

        // Act
        TestBaseWorkflowRunData data = new TestBaseWorkflowRunData();
        data.setId(id);
        data.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, data.getId());
        assertEquals(createdAt, data.getCreatedAt());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        TestBaseWorkflowRunData data1 = new TestBaseWorkflowRunData();
        data1.setId("run-123");
        data1.setCreatedAt(1705395332L);

        TestBaseWorkflowRunData data2 = new TestBaseWorkflowRunData();
        data2.setId("run-123");
        data2.setCreatedAt(1705395332L);

        TestBaseWorkflowRunData data3 = new TestBaseWorkflowRunData();
        data3.setId("run-456");
        data3.setCreatedAt(1705395332L);

        // Assert
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1, data3);
        assertNotEquals(data1.hashCode(), data3.hashCode());
    }

    @Test
    public void testJsonSerializationDeserialization() throws Exception {
        // Arrange
        TestBaseWorkflowRunData data = new TestBaseWorkflowRunData();
        data.setId("run-123");
        data.setCreatedAt(1705395332L);

        // Act - Serialize
        String json = objectMapper.writeValueAsString(data);

        // Assert - Serialization
        assertTrue(json.contains("\"id\":\"run-123\""));
        assertTrue(json.contains("\"createdAt\":1705395332"));

        // Act - Deserialize
        TestBaseWorkflowRunData deserializedData = objectMapper.readValue(json, TestBaseWorkflowRunData.class);

        // Assert - Deserialization
        assertEquals(data.getId(), deserializedData.getId());
        assertEquals(data.getCreatedAt(), deserializedData.getCreatedAt());
    }

    @Test
    public void testJsonDeserializationWithJsonAlias() throws Exception {
        // Arrange - Use JsonAlias field names
        String json = "{\"id\":\"run-123\",\"created_at\":1705395332}";

        // Act
        TestBaseWorkflowRunData data = objectMapper.readValue(json, TestBaseWorkflowRunData.class);

        // Assert
        assertEquals("run-123", data.getId());
        assertEquals(Long.valueOf(1705395332), data.getCreatedAt());
    }

    @Test
    public void testToString() {
        // Arrange
        TestBaseWorkflowRunData data = new TestBaseWorkflowRunData();
        data.setId("run-123");
        data.setCreatedAt(1705395332L);

        // Act
        String toString = data.toString();

        // Assert
        assertTrue(toString.contains("id=run-123"));
        assertTrue(toString.contains("createdAt=1705395332"));
    }
} 