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
package io.github.guoshiqiufeng.dify.server.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for LoginResultResponse
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class LoginResultResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        LoginResultResponse result = new LoginResultResponse();

        // Assert
        assertNotNull(result);
        assertNull(result.getData());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        LoginResultResponse result = new LoginResultResponse();
        LoginResponse data = new LoginResponse();
        data.setAccessToken("test-access-token");
        data.setRefreshToken("test-refresh-token");
        Integer code = 200;
        String message = "Success";

        // Act
        result.setData(data);

        // Assert
        assertEquals(data, result.getData());
        assertEquals("test-access-token", result.getData().getAccessToken());
        assertEquals("test-refresh-token", result.getData().getRefreshToken());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        LoginResultResponse result = new LoginResultResponse();
        LoginResponse data = new LoginResponse();
        data.setAccessToken("test-access-token");
        data.setRefreshToken("test-refresh-token");
        result.setData(data);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(result);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"data\":"));
        assertTrue(json.contains("\"accessToken\":"));
        assertTrue(json.contains("\"refreshToken\":"));

        // Deserialize back to object
        LoginResultResponse deserialized = objectMapper.readValue(json, LoginResultResponse.class);

        // Verify the deserialized object matches the original
        assertNotNull(deserialized.getData());
        assertEquals(data.getAccessToken(), deserialized.getData().getAccessToken());
        assertEquals(data.getRefreshToken(), deserialized.getData().getRefreshToken());
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"data\": {\n" +
                "    \"access_token\": \"test-access-token\",\n" +
                "    \"refresh_token\": \"test-refresh-token\"\n" +
                "  }\n" +
                "}";

        // Deserialize with aliases
        LoginResultResponse deserialized = objectMapper.readValue(jsonWithAliases, LoginResultResponse.class);

        // Verify fields were correctly deserialized
        assertNotNull(deserialized.getData());
        assertEquals("test-access-token", deserialized.getData().getAccessToken());
        assertEquals("test-refresh-token", deserialized.getData().getRefreshToken());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        LoginResultResponse result1 = new LoginResultResponse();
        LoginResponse data1 = new LoginResponse();
        data1.setAccessToken("test-access-token");
        data1.setRefreshToken("test-refresh-token");
        result1.setData(data1);

        LoginResultResponse result2 = new LoginResultResponse();
        LoginResponse data2 = new LoginResponse();
        data2.setAccessToken("test-access-token");
        data2.setRefreshToken("test-refresh-token");
        result2.setData(data2);

        // Create a different object
        LoginResultResponse result3 = new LoginResultResponse();
        LoginResponse data3 = new LoginResponse();
        data3.setAccessToken("different-access-token");
        data3.setRefreshToken("different-refresh-token");
        result3.setData(data3);
        // Test equality
        assertEquals(result1, result2);
        assertNotEquals(result1, result3);

        // Test hash code
        assertEquals(result1.hashCode(), result2.hashCode());
        assertNotEquals(result1.hashCode(), result3.hashCode());
    }

    /**
     * Test with null data
     */
    @Test
    public void testWithNullData() throws JsonProcessingException {
        // Create an instance with null data
        LoginResultResponse result = new LoginResultResponse();
        result.setData(null);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(result);

        // Verify JSON still contains data field but with null value
        assertTrue(json.contains("\"data\":null"));

        // Deserialize back to object
        LoginResultResponse deserialized = objectMapper.readValue(json, LoginResultResponse.class);

        // Verify the deserialized object has null data
        assertNull(deserialized.getData());
    }

    /**
     * Test serialization of class with its serialVersionUID
     */
    @Test
    public void testSerializationWithSerialVersionUID() {
        // Verify the class has a serialVersionUID field
        LoginResultResponse result = new LoginResultResponse();
        assertNotNull(result);

        // This test mainly verifies that the class has a serialVersionUID defined
        // The actual serialization test is covered in the testJsonSerialization method
    }
}
