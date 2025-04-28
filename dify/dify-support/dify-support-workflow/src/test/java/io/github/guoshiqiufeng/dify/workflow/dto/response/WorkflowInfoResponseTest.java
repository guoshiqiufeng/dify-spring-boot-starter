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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link WorkflowInfoResponse}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
@SuppressWarnings("unchecked")
public class WorkflowInfoResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSettersAndGetters() {
        // Arrange
        String id = "run-123";
        String workflowId = "workflow-456";
        String status = "succeeded";
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("text", "Hello, world!");
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", "Processed: Hello, world!");
        String error = null;
        Float elapsedTime = 2.5f;
        Integer totalTokens = 150;
        Integer totalSteps = 3;
        Long createdAt = 1705395332L;
        Long finishedAt = 1705395335L;

        // Act
        WorkflowInfoResponse response = new WorkflowInfoResponse();
        response.setId(id);
        response.setWorkflowId(workflowId);
        response.setStatus(status);
        response.setInputs(inputs);
        response.setOutputs(outputs);
        response.setError(error);
        response.setElapsedTime(elapsedTime);
        response.setTotalTokens(totalTokens);
        response.setTotalSteps(totalSteps);
        response.setCreatedAt(createdAt);
        response.setFinishedAt(finishedAt);

        // Assert
        assertEquals(id, response.getId());
        assertEquals(workflowId, response.getWorkflowId());
        assertEquals(status, response.getStatus());
        assertEquals(inputs, response.getInputs());
        assertEquals(outputs, response.getOutputs());
        assertEquals(error, response.getError());
        assertEquals(elapsedTime, response.getElapsedTime());
        assertEquals(totalTokens, response.getTotalTokens());
        assertEquals(totalSteps, response.getTotalSteps());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(finishedAt, response.getFinishedAt());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        WorkflowInfoResponse response1 = new WorkflowInfoResponse();
        response1.setId("run-123");
        response1.setWorkflowId("workflow-456");
        response1.setStatus("succeeded");
        response1.setCreatedAt(1705395332L);
        response1.setFinishedAt(1705395335L);

        WorkflowInfoResponse response2 = new WorkflowInfoResponse();
        response2.setId("run-123");
        response2.setWorkflowId("workflow-456");
        response2.setStatus("succeeded");
        response2.setCreatedAt(1705395332L);
        response2.setFinishedAt(1705395335L);

        WorkflowInfoResponse response3 = new WorkflowInfoResponse();
        response3.setId("run-789");
        response3.setWorkflowId("workflow-999");
        response3.setStatus("failed");
        response3.setCreatedAt(1705395332L);
        response3.setFinishedAt(1705395340L);

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    public void testMapDeserializer() throws IOException {
        // Test scenario 1: Valid JSON map string
        String jsonMapString = "{\"key1\":\"value1\",\"key2\":123}";

        // Mock JsonParser and DeserializationContext
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        when(parser.getText()).thenReturn(jsonMapString);

        // Create deserializer instance
        WorkflowInfoResponse.MapDeserializer deserializer = new WorkflowInfoResponse.MapDeserializer();

        // Act
        Map<String, Object> result = deserializer.deserialize(parser, context);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals(123, result.get("key2"));

        // Test scenario 2: Empty string
        when(parser.getText()).thenReturn("");

        // Act
        Map<String, Object> emptyResult = deserializer.deserialize(parser, context);

        // Assert
        assertNull(emptyResult);

        // Test scenario 3: null
        when(parser.getText()).thenReturn(null);

        // Act
        Map<String, Object> nullResult = deserializer.deserialize(parser, context);

        // Assert
        assertNull(nullResult);

        // Test scenario 4: Invalid JSON string
        when(parser.getText()).thenReturn("not a valid json map");

        // Act & Assert
        assertThrows(IOException.class, () -> deserializer.deserialize(parser, context));
    }

    @Test
    public void testDateToTimestampDeserializerWithTimestamp() throws IOException {
        // Test with timestamp as string
        String timestamp = "1705395332";

        // Mock JsonParser and DeserializationContext
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        when(parser.getText()).thenReturn(timestamp);

        // Create deserializer instance
        WorkflowInfoResponse.DateToTimestampDeserializer deserializer = new WorkflowInfoResponse.DateToTimestampDeserializer();

        // Act
        Long result = deserializer.deserialize(parser, context);

        // Assert
        assertEquals(Long.valueOf(1705395332), result);
    }

    @Test
    public void testDateToTimestampDeserializerWithDateString() throws IOException {
        // Test with date string in format: "EEE, dd MMM yyyy HH:mm:ss Z"
        String dateString = "Wed, 16 Jan 2024 12:15:32 +0000";

        // Calculate the expected timestamp (for January 16, 2024 12:15:32 UTC)
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        long expectedTimestamp;
        try {
            Date date = sdf.parse(dateString);
            expectedTimestamp = date.getTime();
        } catch (Exception e) {
            fail("Test setup failed: " + e.getMessage());
            return;
        }

        // Mock JsonParser and DeserializationContext
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        when(parser.getText()).thenReturn(dateString);

        // Create deserializer instance
        WorkflowInfoResponse.DateToTimestampDeserializer deserializer = new WorkflowInfoResponse.DateToTimestampDeserializer();

        // Act
        Long result = deserializer.deserialize(parser, context);

        // Assert
        assertEquals(expectedTimestamp, result);
    }

    @Test
    public void testDateToTimestampDeserializerWithEmptyString() throws IOException {
        // Test with empty string
        String dateString = "";

        // Mock JsonParser and DeserializationContext
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        when(parser.getText()).thenReturn(dateString);

        // Create deserializer instance
        WorkflowInfoResponse.DateToTimestampDeserializer deserializer = new WorkflowInfoResponse.DateToTimestampDeserializer();

        // Act
        Long result = deserializer.deserialize(parser, context);

        // Assert
        assertNull(result);
    }

    @Test
    public void testDateToTimestampDeserializerWithNullValue() throws IOException {
        // Test with null
        String dateString = null;

        // Mock JsonParser and DeserializationContext
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        when(parser.getText()).thenReturn(dateString);

        // Create deserializer instance
        WorkflowInfoResponse.DateToTimestampDeserializer deserializer = new WorkflowInfoResponse.DateToTimestampDeserializer();

        // Act
        Long result = deserializer.deserialize(parser, context);

        // Assert
        assertNull(result);
    }

    @Test
    public void testDateToTimestampDeserializerWithInvalidDateString() throws IOException {
        // Test with invalid date string
        String dateString = "not a valid date string";

        // Mock JsonParser and DeserializationContext
        JsonParser parser = mock(JsonParser.class);
        DeserializationContext context = mock(DeserializationContext.class);

        when(parser.getText()).thenReturn(dateString);

        // Create deserializer instance
        WorkflowInfoResponse.DateToTimestampDeserializer deserializer = new WorkflowInfoResponse.DateToTimestampDeserializer();

        // Act & Assert
        assertThrows(IOException.class, () -> deserializer.deserialize(parser, context));
    }

    @Test
    public void testJsonSerializationDeserialization() throws Exception {
        // Arrange
        WorkflowInfoResponse response = new WorkflowInfoResponse();
        response.setId("run-123");
        response.setWorkflowId("workflow-456");
        response.setStatus("succeeded");
        response.setElapsedTime(2.5f);
        response.setTotalTokens(150);
        response.setTotalSteps(3);
        response.setCreatedAt(1705395332L);
        response.setFinishedAt(1705395335L);

        // Act - Serialize
        String json = objectMapper.writeValueAsString(response);

        // Assert - Serialization
        assertTrue(json.contains("\"id\":\"run-123\""));
        assertTrue(json.contains("\"workflowId\":\"workflow-456\""));
        assertTrue(json.contains("\"status\":\"succeeded\""));
        assertTrue(json.contains("\"elapsedTime\":2.5"));
        assertTrue(json.contains("\"totalTokens\":150"));
        assertTrue(json.contains("\"totalSteps\":3"));
        assertTrue(json.contains("\"createdAt\":1705395332"));
        assertTrue(json.contains("\"finishedAt\":1705395335"));

        // Note: Full deserialization is not tested here because it requires complex mock setup
        // for custom deserializers. Unit tests for the deserializers are provided separately.
    }

    @Test
    public void testToString() {
        // Arrange
        WorkflowInfoResponse response = new WorkflowInfoResponse();
        response.setId("run-123");
        response.setWorkflowId("workflow-456");
        response.setStatus("succeeded");
        response.setElapsedTime(2.5f);
        response.setCreatedAt(1705395332L);
        response.setFinishedAt(1705395335L);

        // Act
        String toString = response.toString();

        // Assert
        assertTrue(toString.contains("id=run-123"));
        assertTrue(toString.contains("workflowId=workflow-456"));
        assertTrue(toString.contains("status=succeeded"));
        assertTrue(toString.contains("elapsedTime=2.5"));
        assertTrue(toString.contains("createdAt=1705395332"));
        assertTrue(toString.contains("finishedAt=1705395335"));
    }
}
