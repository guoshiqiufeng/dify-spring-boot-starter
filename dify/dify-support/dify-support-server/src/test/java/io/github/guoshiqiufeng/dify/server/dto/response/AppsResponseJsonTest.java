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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for JSON serialization/deserialization of {@link AppsResponse}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
@SuppressWarnings("unchecked")
public class AppsResponseJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonSerialization() throws Exception {
        // Arrange
        AppsResponse vo = new AppsResponse();
        vo.setId("app-123");
        vo.setName("Test App");
        vo.setMaxActiveRequests(5);
        vo.setEnableSite(true);
        vo.setEnableApi(false);

        // Act
        String json = objectMapper.writeValueAsString(vo);

        // Assert
        assertTrue(json.contains("\"id\":\"app-123\""));
        assertTrue(json.contains("\"name\":\"Test App\""));
        assertTrue(json.contains("\"maxActiveRequests\":5"));
        assertTrue(json.contains("\"enableSite\":true"));
        assertTrue(json.contains("\"enableApi\":false"));
    }

    @Test
    public void testJsonDeserializationWithStandardFields() throws Exception {
        // Arrange
        String json = "{\"id\":\"app-123\",\"name\":\"Test App\",\"description\":\"Test Description\",\"mode\":\"completion\"}";

        // Act
        AppsResponse vo = objectMapper.readValue(json, AppsResponse.class);

        // Assert
        assertEquals("app-123", vo.getId());
        assertEquals("Test App", vo.getName());
        assertEquals("Test Description", vo.getDescription());
        assertEquals("completion", vo.getMode());
    }

    @Test
    public void testJsonDeserializationWithJsonAlias() throws Exception {
        // Arrange - Use JsonAlias field names
        String json = "{\"id\":\"app-123\",\"name\":\"Test App\",\"max_active_requests\":10,\"icon_type\":\"emoji\"," +
                "\"icon_background\":\"#FFFFFF\",\"icon_url\":\"https://example.com/icon.png\"," +
                "\"enable_site\":true,\"enable_api\":false,\"model_config\":{},\"api_base_url\":\"https://api.example.com\"," +
                "\"use_icon_as_answer_icon\":true,\"created_by\":\"user-123\",\"created_at\":1619712000," +
                "\"updated_by\":\"user-456\",\"updated_at\":1619798400,\"deleted_tools\":[\"tool1\"]}";

        // Act
        AppsResponse vo = objectMapper.readValue(json, AppsResponse.class);

        // Assert
        assertEquals("app-123", vo.getId());
        assertEquals("Test App", vo.getName());
        assertEquals(Integer.valueOf(10), vo.getMaxActiveRequests());
        assertEquals("emoji", vo.getIconType());
        assertEquals("#FFFFFF", vo.getIconBackground());
        assertEquals("https://example.com/icon.png", vo.getIconUrl());
        assertTrue(vo.getEnableSite());
        assertFalse(vo.getEnableApi());
        assertNotNull(vo.getModelConfig());
        assertEquals("https://api.example.com", vo.getApiBaseUrl());
        assertTrue(vo.getUseIconAsAnswerIcon());
        assertEquals("user-123", vo.getCreatedBy());
        assertEquals(Long.valueOf(1619712000), vo.getCreatedAt());
        assertEquals("user-456", vo.getUpdatedBy());
        assertEquals(Long.valueOf(1619798400), vo.getUpdatedAt());
        assertEquals(Collections.singletonList("tool1"), vo.getDeletedTools());
    }

    @Test
    public void testNestedModelConfigDeserializationWithJsonAlias() throws Exception {
        // Arrange
        String json = "{\"model_config\":{\"model\":{\"provider\":\"openai\",\"name\":\"gpt-4\",\"mode\":\"chat\"," +
                "\"completion_params\":{\"stop\":[\"###\",\"END\"],\"format\":\"json\"}}," +
                "\"pre_prompt\":\"You are a helpful assistant\",\"created_by\":\"user-123\",\"created_at\":1619712000," +
                "\"updated_by\":\"user-456\",\"updated_at\":1619798400}}";

        // Act
        AppsResponse vo = objectMapper.readValue(json, AppsResponse.class);
        Map<String, Object> modelConfigMap = vo.getModelConfig();

        // Assert
        assertNotNull(modelConfigMap);
        // We can't directly access the inner classes from the Map, so we check the JSON structure
        String modelConfigJson = objectMapper.writeValueAsString(modelConfigMap);
        assertTrue(modelConfigJson.contains("\"model\""));
        assertTrue(modelConfigJson.contains("\"pre_prompt\":\"You are a helpful assistant\""));
        assertTrue(modelConfigJson.contains("\"created_by\":\"user-123\""));
        assertTrue(modelConfigJson.contains("\"created_at\":1619712000"));
        assertTrue(modelConfigJson.contains("\"updated_by\":\"user-456\""));
        assertTrue(modelConfigJson.contains("\"updated_at\":1619798400"));

        // Verify the model part
        assertTrue(modelConfigJson.contains("\"provider\":\"openai\""));
        assertTrue(modelConfigJson.contains("\"name\":\"gpt-4\""));
        assertTrue(modelConfigJson.contains("\"mode\":\"chat\""));

        // Verify the completion_params part
        assertTrue(modelConfigJson.contains("\"completion_params\""));
        assertTrue(modelConfigJson.contains("\"stop\":[\"###\",\"END\"]"));
        assertTrue(modelConfigJson.contains("\"format\":\"json\""));
    }

    @Test
    public void testFullRoundTripSerialization() throws Exception {
        // Arrange - Create a complex AppsResponseVO object with nested classes
        Map<String, Object> modelConfigMap = new HashMap<>();
        modelConfigMap.put("pre_prompt", "You are a helpful assistant");

        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("provider", "openai");
        modelMap.put("name", "gpt-4");
        modelMap.put("mode", "chat");

        Map<String, Object> completionParamsMap = new HashMap<>();
        completionParamsMap.put("stop", Arrays.asList("###", "END"));
        completionParamsMap.put("format", "json");

        modelMap.put("completion_params", completionParamsMap);
        modelConfigMap.put("model", modelMap);

        AppsResponse originalVO = new AppsResponse();
        originalVO.setId("app-123");
        originalVO.setName("Test App");
        originalVO.setModelConfig(modelConfigMap);
        originalVO.setMaxActiveRequests(10);
        originalVO.setEnableSite(true);
        originalVO.setCreatedAt(1619712000L);

        // Act - Serialize then deserialize
        String json = objectMapper.writeValueAsString(originalVO);
        AppsResponse deserializedVO = objectMapper.readValue(json, AppsResponse.class);

        // Assert
        assertEquals(originalVO.getId(), deserializedVO.getId());
        assertEquals(originalVO.getName(), deserializedVO.getName());
        assertEquals(originalVO.getMaxActiveRequests(), deserializedVO.getMaxActiveRequests());
        assertEquals(originalVO.getEnableSite(), deserializedVO.getEnableSite());
        assertEquals(originalVO.getCreatedAt(), deserializedVO.getCreatedAt());

        // For complex nested objects, check the model config map has equivalent data
        assertNotNull(deserializedVO.getModelConfig());
        Map<String, Object> deserializedModelConfig = deserializedVO.getModelConfig();
        assertEquals(modelConfigMap.size(), deserializedModelConfig.size());
        assertEquals(modelConfigMap.get("pre_prompt"), deserializedModelConfig.get("pre_prompt"));

        // Check that the model map is also correctly preserved
        Map<String, Object> deserializedModel = (Map<String, Object>) deserializedModelConfig.get("model");
        assertNotNull(deserializedModel);
        assertEquals(modelMap.get("provider"), deserializedModel.get("provider"));
        assertEquals(modelMap.get("name"), deserializedModel.get("name"));
        assertEquals(modelMap.get("mode"), deserializedModel.get("mode"));

        // Check the completion params
        Map<String, Object> deserializedCompletionParams = (Map<String, Object>) deserializedModel.get("completion_params");
        assertNotNull(deserializedCompletionParams);
        assertEquals(completionParamsMap.get("format"), deserializedCompletionParams.get("format"));
    }

    @Test
    public void testJsonDeserializationWithMissingFields() throws Exception {
        // Arrange
        String json = "{\"id\":\"app-123\",\"name\":\"Test App\"}";

        // Act
        AppsResponse vo = objectMapper.readValue(json, AppsResponse.class);

        // Assert
        assertEquals("app-123", vo.getId());
        assertEquals("Test App", vo.getName());
        assertNull(vo.getMaxActiveRequests());
        assertNull(vo.getDescription());
        assertNull(vo.getMode());
        assertNull(vo.getIconType());
        assertNull(vo.getIcon());
        assertNull(vo.getIconBackground());
        assertNull(vo.getIconUrl());
        assertNull(vo.getEnableSite());
        assertNull(vo.getEnableApi());
        assertNull(vo.getModelConfig());
        assertNull(vo.getWorkflow());
        assertNull(vo.getSite());
        assertNull(vo.getApiBaseUrl());
        assertNull(vo.getUseIconAsAnswerIcon());
        assertNull(vo.getCreatedBy());
        assertNull(vo.getCreatedAt());
        assertNull(vo.getUpdatedBy());
        assertNull(vo.getUpdatedAt());
        assertNull(vo.getTags());
        assertNull(vo.getDeletedTools());
    }
}
