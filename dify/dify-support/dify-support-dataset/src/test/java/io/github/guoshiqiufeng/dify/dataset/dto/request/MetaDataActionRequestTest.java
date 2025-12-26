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
package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.dataset.enums.MetaDataActionEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MetaDataActionRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class MetaDataActionRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a MetaDataActionRequest instance
        MetaDataActionRequest request = new MetaDataActionRequest();

        // Set values for parent class fields
        String apiKey = "sk-12345678";
        request.setApiKey(apiKey);

        // Set values for fields
        String datasetId = "ds-12345";
        MetaDataActionEnum action = MetaDataActionEnum.enable;
        request.setDatasetId(datasetId);
        request.setAction(action);

        // Assert all values are set correctly
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(action, request.getAction());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a MetaDataActionRequest instance with sample data
        MetaDataActionRequest request = new MetaDataActionRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setAction(MetaDataActionEnum.enable);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"action\":"));
        assertTrue(json.contains("\"enable\""));

        // Deserialize back to object
        MetaDataActionRequest deserialized = objectMapper.readValue(json, MetaDataActionRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getAction(), deserialized.getAction());
    }

    /**
     * Test JSON deserialization
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // Test with enable action
        String jsonEnable = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"action\": \"enable\"\n" +
                "}";

        MetaDataActionRequest deserializedEnable = objectMapper.readValue(jsonEnable, MetaDataActionRequest.class);
        assertEquals("sk-12345678", deserializedEnable.getApiKey());
        assertEquals("ds-12345", deserializedEnable.getDatasetId());
        assertEquals(MetaDataActionEnum.enable, deserializedEnable.getAction());

        // Test with disable action
        String jsonDisable = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"action\": \"disable\"\n" +
                "}";

        MetaDataActionRequest deserializedDisable = objectMapper.readValue(jsonDisable, MetaDataActionRequest.class);
        assertEquals("sk-12345678", deserializedDisable.getApiKey());
        assertEquals("ds-12345", deserializedDisable.getDatasetId());
        assertEquals(MetaDataActionEnum.disable, deserializedDisable.getAction());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        MetaDataActionRequest request1 = new MetaDataActionRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setAction(MetaDataActionEnum.enable);

        MetaDataActionRequest request2 = new MetaDataActionRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setAction(MetaDataActionEnum.enable);

        // Create a different request (different action)
        MetaDataActionRequest request3 = new MetaDataActionRequest();
        request3.setApiKey("sk-12345678");
        request3.setDatasetId("ds-12345");
        request3.setAction(MetaDataActionEnum.disable);

        // Test equality
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        // Test hash code
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    /**
     * Test enum values
     */
    @Test
    public void testEnumValues() {
        // Test all possible enum values
        assertEquals(2, MetaDataActionEnum.values().length);
        assertEquals(MetaDataActionEnum.enable, MetaDataActionEnum.valueOf("enable"));
        assertEquals(MetaDataActionEnum.disable, MetaDataActionEnum.valueOf("disable"));
    }
} 