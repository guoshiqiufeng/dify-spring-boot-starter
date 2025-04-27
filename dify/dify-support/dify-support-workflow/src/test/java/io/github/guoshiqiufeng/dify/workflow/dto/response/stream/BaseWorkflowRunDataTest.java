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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link BaseWorkflowRunData}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class BaseWorkflowRunDataTest {

    // Create a concrete implementation of the abstract class for testing
    private static class ConcreteBaseWorkflowRunData extends BaseWorkflowRunData {
        // No additional implementation needed for testing
    }

    @Test
    public void testGetterAndSetter() {
        // Arrange
        ConcreteBaseWorkflowRunData data = new ConcreteBaseWorkflowRunData();
        String id = "test-id";
        Long createdAt = 1619517600000L; // 2021-04-27 12:00:00 UTC

        // Act
        data.setId(id);
        data.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, data.getId());
        assertEquals(createdAt, data.getCreatedAt());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        ConcreteBaseWorkflowRunData data = new ConcreteBaseWorkflowRunData();

        // Assert
        assertNull(data.getId());
        assertNull(data.getCreatedAt());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        ConcreteBaseWorkflowRunData data1 = new ConcreteBaseWorkflowRunData();
        data1.setId("id-1");
        data1.setCreatedAt(1619517600000L);

        ConcreteBaseWorkflowRunData data2 = new ConcreteBaseWorkflowRunData();
        data2.setId("id-1");
        data2.setCreatedAt(1619517600000L);

        ConcreteBaseWorkflowRunData data3 = new ConcreteBaseWorkflowRunData();
        data3.setId("id-2");
        data3.setCreatedAt(1619604000000L);

        // Assert
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1, data3);
        assertNotEquals(data1.hashCode(), data3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        ConcreteBaseWorkflowRunData data = new ConcreteBaseWorkflowRunData();
        data.setId("test-id");
        data.setCreatedAt(1619517600000L);

        // Act
        String toString = data.toString();

        // Assert
        assertTrue(toString.contains("id=test-id"));
        assertTrue(toString.contains("createdAt=1619517600000"));
    }
} 