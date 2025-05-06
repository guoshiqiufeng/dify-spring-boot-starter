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
package io.github.guoshiqiufeng.dify.chat.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AppAnnotationReplyResponse
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class AppAnnotationReplyResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        AppAnnotationReplyResponse response = new AppAnnotationReplyResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getJobId());
        assertNull(response.getJobStatus());
        assertNull(response.getErrorMsg());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        AppAnnotationReplyResponse response = new AppAnnotationReplyResponse();
        String jobId = "job-12345";
        String jobStatus = "processing";
        String errorMsg = "Some error message";

        // Act
        response.setJobId(jobId);
        response.setJobStatus(jobStatus);
        response.setErrorMsg(errorMsg);

        // Assert
        assertEquals(jobId, response.getJobId());
        assertEquals(jobStatus, response.getJobStatus());
        assertEquals(errorMsg, response.getErrorMsg());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        AppAnnotationReplyResponse response = new AppAnnotationReplyResponse();
        response.setJobId("job-12345");
        response.setJobStatus("completed");
        response.setErrorMsg(null);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"jobId\":"));
        assertTrue(json.contains("\"jobStatus\":"));

        // Since errorMsg is null, it might not be included in the JSON
        if (json.contains("\"errorMsg\":")) {
            assertTrue(json.contains("\"errorMsg\":null"));
        }

        // Deserialize back to object
        AppAnnotationReplyResponse deserialized = objectMapper.readValue(json, AppAnnotationReplyResponse.class);

        // Verify the deserialized object matches the original
        assertEquals(response.getJobId(), deserialized.getJobId());
        assertEquals(response.getJobStatus(), deserialized.getJobStatus());
        assertEquals(response.getErrorMsg(), deserialized.getErrorMsg());
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"job_id\": \"job-12345\",\n" +
                "  \"job_status\": \"processing\",\n" +
                "  \"error_msg\": \"Some error occurred\"\n" +
                "}";

        // Deserialize with aliases
        AppAnnotationReplyResponse deserialized = objectMapper.readValue(jsonWithAliases, AppAnnotationReplyResponse.class);

        // Verify fields were correctly deserialized
        assertEquals("job-12345", deserialized.getJobId());
        assertEquals("processing", deserialized.getJobStatus());
        assertEquals("Some error occurred", deserialized.getErrorMsg());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        AppAnnotationReplyResponse response1 = new AppAnnotationReplyResponse();
        response1.setJobId("job-12345");
        response1.setJobStatus("processing");
        response1.setErrorMsg("Some error message");

        AppAnnotationReplyResponse response2 = new AppAnnotationReplyResponse();
        response2.setJobId("job-12345");
        response2.setJobStatus("processing");
        response2.setErrorMsg("Some error message");

        // Create a different object
        AppAnnotationReplyResponse response3 = new AppAnnotationReplyResponse();
        response3.setJobId("job-67890");
        response3.setJobStatus("completed");
        response3.setErrorMsg(null);

        // Test equality
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);

        // Test hash code
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
}
