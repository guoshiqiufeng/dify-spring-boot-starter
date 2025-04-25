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
package io.github.guoshiqiufeng.dify.chat.enums;

import io.github.guoshiqiufeng.dify.chat.dto.response.message.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link StreamEventEnum}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/15
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StreamEventEnumTest {

    @Test
    @DisplayName("Test workflow_started enum value and its class association")
    public void testWorkflowStartedEnum() {
        // Test the enum value
        assertEquals("workflow_started", StreamEventEnum.workflow_started.name());

        // Test the associated class
        assertEquals(NodeStartedData.class, StreamEventEnum.workflow_started.getClazz());
    }

    @Test
    @DisplayName("Test node_started enum value and its class association")
    public void testNodeStartedEnum() {
        // Test the enum value
        assertEquals("node_started", StreamEventEnum.node_started.name());

        // Test the associated class
        assertEquals(WorkflowStartedData.class, StreamEventEnum.node_started.getClazz());
    }

    @Test
    @DisplayName("Test node_finished enum value and its class association")
    public void testNodeFinishedEnum() {
        // Test the enum value
        assertEquals("node_finished", StreamEventEnum.node_finished.name());

        // Test the associated class
        assertEquals(NodeFinishedData.class, StreamEventEnum.node_finished.getClazz());
    }

    @Test
    @DisplayName("Test workflow_finished enum value and its class association")
    public void testWorkflowFinishedEnum() {
        // Test the enum value
        assertEquals("workflow_finished", StreamEventEnum.workflow_finished.name());

        // Test the associated class
        assertEquals(WorkflowFinishedData.class, StreamEventEnum.workflow_finished.getClazz());
    }

    @Test
    @DisplayName("Test enum count")
    public void testEnumCount() {
        // Verify the expected number of enum values
        assertEquals(4, StreamEventEnum.values().length);
    }

    @Test
    @DisplayName("Test all enum values exist")
    public void testAllEnumValuesExist() {
        // Check that all expected enum values exist
        assertNotNull(StreamEventEnum.valueOf("workflow_started"));
        assertNotNull(StreamEventEnum.valueOf("node_started"));
        assertNotNull(StreamEventEnum.valueOf("node_finished"));
        assertNotNull(StreamEventEnum.valueOf("workflow_finished"));
    }

    @Test
    @DisplayName("Test all enum values have non-null class associations")
    public void testAllEnumValuesHaveNonNullClasses() {
        // Check that all enum values have non-null class associations
        for (StreamEventEnum event : StreamEventEnum.values()) {
            assertNotNull(event.getClazz(), "Class association for " + event + " should not be null");
            assertTrue(CompletionData.class.isAssignableFrom(event.getClazz()),
                    "Class for " + event + " should extend CompletionData");
        }
    }
}
