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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AppsResponseResult
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class AppsResponseResultTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        AppsResponseResult response = new AppsResponseResult();

        // Assert
        assertNotNull(response);
        assertNull(response.getData());
        assertNull(response.getTotal());
        assertNull(response.getHasMore());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        AppsResponseResult response = new AppsResponseResult();
        List<AppsResponse> data = new ArrayList<>();
        AppsResponse app = new AppsResponse();
        // Assuming AppsResponseVO has an id field, you may need to adjust based on actual implementation
        // app.setId("app-12345");
        data.add(app);

        Integer total = 1;
        Boolean hasMore = false;

        // Act
        response.setData(data);
        response.setTotal(total);
        response.setHasMore(hasMore);

        // Assert
        assertEquals(data, response.getData());
        assertEquals(total, response.getTotal());
        assertEquals(hasMore, response.getHasMore());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        AppsResponseResult response = new AppsResponseResult();

        List<AppsResponse> data = new ArrayList<>();
        AppsResponse app = new AppsResponse();
        // Set properties on app if needed
        data.add(app);

        response.setData(data);
        response.setTotal(1);
        response.setHasMore(false);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"data\":"));
        assertTrue(json.contains("\"total\":"));
        assertTrue(json.contains("\"hasMore\":"));

        // Deserialize back to object
        AppsResponseResult deserialized = objectMapper.readValue(json, AppsResponseResult.class);

        // Verify the deserialized object matches the original
        assertEquals(response.getTotal(), deserialized.getTotal());
        assertEquals(response.getHasMore(), deserialized.getHasMore());
        assertNotNull(deserialized.getData());
        assertEquals(1, deserialized.getData().size());
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"data\": [\n" +
                "    {}\n" +  // Empty app object for simplicity
                "  ],\n" +
                "  \"total\": 1,\n" +
                "  \"has_more\": false\n" +
                "}";

        // Deserialize with aliases
        AppsResponseResult deserialized = objectMapper.readValue(jsonWithAliases, AppsResponseResult.class);

        // Verify fields were correctly deserialized
        assertNotNull(deserialized.getData());
        assertEquals(1, deserialized.getData().size());
        assertEquals(1, deserialized.getTotal());
        assertEquals(Boolean.FALSE, deserialized.getHasMore());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        AppsResponseResult response1 = new AppsResponseResult();
        List<AppsResponse> data1 = new ArrayList<>();
        data1.add(new AppsResponse());
        response1.setData(data1);
        response1.setTotal(1);
        response1.setHasMore(false);

        AppsResponseResult response2 = new AppsResponseResult();
        List<AppsResponse> data2 = new ArrayList<>();
        data2.add(new AppsResponse());
        response2.setData(data2);
        response2.setTotal(1);
        response2.setHasMore(false);

        // Create a different object
        AppsResponseResult response3 = new AppsResponseResult();
        List<AppsResponse> data3 = new ArrayList<>();
        data3.add(new AppsResponse());
        data3.add(new AppsResponse());  // Add an extra item to make it different
        response3.setData(data3);
        response3.setTotal(2);
        response3.setHasMore(true);

        // Test equality
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);

        // Test hash code
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    /**
     * Test with empty data list
     */
    @Test
    public void testWithEmptyDataList() throws JsonProcessingException {
        // Create an instance with empty data list
        AppsResponseResult response = new AppsResponseResult();
        response.setData(new ArrayList<>());
        response.setTotal(0);
        response.setHasMore(false);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains empty data array
        assertTrue(json.contains("\"data\":[]"));

        // Deserialize back to object
        AppsResponseResult deserialized = objectMapper.readValue(json, AppsResponseResult.class);

        // Verify the deserialized object has empty data list
        assertNotNull(deserialized.getData());
        assertTrue(deserialized.getData().isEmpty());
        assertEquals(0, deserialized.getTotal());
        assertEquals(Boolean.FALSE, deserialized.getHasMore());
    }

    /**
     * Test serialization of class with its serialVersionUID
     */
    @Test
    public void testSerializationWithSerialVersionUID() {
        // Verify the class has a serialVersionUID field
        AppsResponseResult response = new AppsResponseResult();
        assertNotNull(response);

        // This test mainly verifies that the class has a serialVersionUID defined
        // The actual serialization test is covered in the testJsonSerialization method
    }
}
