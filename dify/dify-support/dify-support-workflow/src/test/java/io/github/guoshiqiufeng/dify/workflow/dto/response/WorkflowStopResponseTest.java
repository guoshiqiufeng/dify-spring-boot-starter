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
package io.github.guoshiqiufeng.dify.workflow.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link WorkflowStopResponse}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class WorkflowStopResponseTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        WorkflowStopResponse response = new WorkflowStopResponse();
        String result = "Workflow stopped successfully";

        // Act
        response.setResult(result);

        // Assert
        assertEquals(result, response.getResult());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        WorkflowStopResponse response = new WorkflowStopResponse();

        // Assert
        assertNull(response.getResult());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        WorkflowStopResponse response1 = new WorkflowStopResponse();
        response1.setResult("Workflow stopped");

        WorkflowStopResponse response2 = new WorkflowStopResponse();
        response2.setResult("Workflow stopped");

        WorkflowStopResponse response3 = new WorkflowStopResponse();
        response3.setResult("Failed to stop workflow");

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        WorkflowStopResponse response = new WorkflowStopResponse();
        response.setResult("Workflow stopped successfully");

        // Act
        String toString = response.toString();

        // Assert
        assertTrue(toString.contains("result=Workflow stopped successfully"));
    }
} 