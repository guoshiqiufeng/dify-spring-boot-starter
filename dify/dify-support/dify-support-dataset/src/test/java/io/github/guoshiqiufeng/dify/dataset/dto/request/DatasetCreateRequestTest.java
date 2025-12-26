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
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.PermissionEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.ProviderEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for DatasetCreateRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class DatasetCreateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a DatasetCreateRequest instance
        DatasetCreateRequest request = new DatasetCreateRequest();

        // Set values for parent class fields
        String apiKey = "sk-12345678";
        request.setApiKey(apiKey);

        // Set values for fields
        String name = "Test Knowledge Base";
        String description = "This is a test knowledge base for machine learning";
        request.setName(name);
        request.setDescription(description);
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);
        request.setPermission(PermissionEnum.ALL_TEAM_MEMBERS);
        request.setProvider(ProviderEnum.vendor);

        String externalKnowledgeApiId = "api-12345";
        String externalKnowledgeId = "ext-12345";
        request.setExternalKnowledgeApiId(externalKnowledgeApiId);
        request.setExternalKnowledgeId(externalKnowledgeId);

        // Assert all values are set correctly
        assertEquals(apiKey, request.getApiKey());
        assertEquals(name, request.getName());
        assertEquals(description, request.getDescription());
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, request.getIndexingTechnique());
        assertEquals(PermissionEnum.ALL_TEAM_MEMBERS, request.getPermission());
        assertEquals(ProviderEnum.vendor, request.getProvider());
        assertEquals(externalKnowledgeApiId, request.getExternalKnowledgeApiId());
        assertEquals(externalKnowledgeId, request.getExternalKnowledgeId());
    }

    /**
     * Test default values
     */
    @Test
    public void testDefaultValues() {
        // Create a DatasetCreateRequest instance
        DatasetCreateRequest request = new DatasetCreateRequest();

        // Permission should default to ONLY_ME
        assertEquals(PermissionEnum.ONLY_ME, request.getPermission());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a DatasetCreateRequest instance with sample data
        DatasetCreateRequest request = new DatasetCreateRequest();
        request.setApiKey("sk-12345678");
        request.setName("Test Knowledge Base");
        request.setDescription("A test knowledge base");
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);
        request.setPermission(PermissionEnum.ONLY_ME);
        request.setProvider(ProviderEnum.external);
        request.setExternalKnowledgeApiId("api-12345");
        request.setExternalKnowledgeId("ext-12345");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names including snake_case for annotated fields
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"name\":"));
        assertTrue(json.contains("\"description\":"));
        assertTrue(json.contains("\"indexing_technique\":"));  // Snake case due to @JsonProperty
        assertTrue(json.contains("\"permission\":"));
        assertTrue(json.contains("\"provider\":"));
        assertTrue(json.contains("\"external_knowledge_api_id\":"));  // Snake case due to @JsonProperty
        assertTrue(json.contains("\"external_knowledge_id\":"));  // Snake case due to @JsonProperty

        // Verify enum values are correctly serialized
        assertTrue(json.contains("\"high_quality\""));  // Serialized value from IndexingTechniqueEnum
        assertTrue(json.contains("\"only_me\""));  // Serialized value from PermissionEnum
        assertTrue(json.contains("\"external\""));  // Serialized value from ProviderEnum

        // Deserialize back to object
        DatasetCreateRequest deserialized = objectMapper.readValue(json, DatasetCreateRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getName(), deserialized.getName());
        assertEquals(request.getDescription(), deserialized.getDescription());
        assertEquals(request.getIndexingTechnique(), deserialized.getIndexingTechnique());
        assertEquals(request.getPermission(), deserialized.getPermission());
        assertEquals(request.getProvider(), deserialized.getProvider());
        assertEquals(request.getExternalKnowledgeApiId(), deserialized.getExternalKnowledgeApiId());
        assertEquals(request.getExternalKnowledgeId(), deserialized.getExternalKnowledgeId());
    }

    /**
     * Test JSON deserialization with different property name formats
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // Test with snake_case JSON format
        String snakeJson = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"name\": \"Test Knowledge Base\",\n" +
                "  \"description\": \"A test knowledge base\",\n" +
                "  \"indexing_technique\": \"high_quality\",\n" +
                "  \"permission\": \"all_team_members\",\n" +
                "  \"provider\": \"vendor\",\n" +
                "  \"external_knowledge_api_id\": \"api-12345\",\n" +
                "  \"external_knowledge_id\": \"ext-12345\"\n" +
                "}";

        // Deserialize from snake_case JSON
        DatasetCreateRequest snakeDeserialized = objectMapper.readValue(snakeJson, DatasetCreateRequest.class);
        validateDeserializedRequest(snakeDeserialized);

        // Test with camelCase JSON format (using JsonAlias)
        String camelJson = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"name\": \"Test Knowledge Base\",\n" +
                "  \"description\": \"A test knowledge base\",\n" +
                "  \"indexingTechnique\": \"high_quality\",\n" +
                "  \"permission\": \"all_team_members\",\n" +
                "  \"provider\": \"vendor\",\n" +
                "  \"externalKnowledgeApiId\": \"api-12345\",\n" +
                "  \"externalKnowledgeId\": \"ext-12345\"\n" +
                "}";

        // Deserialize from camelCase JSON
        DatasetCreateRequest camelDeserialized = objectMapper.readValue(camelJson, DatasetCreateRequest.class);
        validateDeserializedRequest(camelDeserialized);
    }

    /**
     * Helper method to validate a deserialized DatasetCreateRequest
     */
    private void validateDeserializedRequest(DatasetCreateRequest request) {
        assertEquals("sk-12345678", request.getApiKey());
        assertEquals("Test Knowledge Base", request.getName());
        assertEquals("A test knowledge base", request.getDescription());
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, request.getIndexingTechnique());
        assertEquals(PermissionEnum.ALL_TEAM_MEMBERS, request.getPermission());
        assertEquals(ProviderEnum.vendor, request.getProvider());
        assertEquals("api-12345", request.getExternalKnowledgeApiId());
        assertEquals("ext-12345", request.getExternalKnowledgeId());
    }
} 