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
package io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.dataset.enums.ModelFeatureEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.ModelStatusEnum;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TextEmbeddingModel
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class TextEmbeddingModelTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        TextEmbeddingModel model = new TextEmbeddingModel();

        // Assert
        assertNotNull(model);
        assertNull(model.getModel());
        assertNull(model.getLabel());
        assertNull(model.getModelType());
        assertNull(model.getFeatures());
        assertNull(model.getFetchFrom());
        assertNull(model.getModelProperties());
        assertNull(model.getDeprecated());
        assertNull(model.getStatus());
        assertNull(model.getLoadBalancingEnabled());
    }

    /**
     * Test all-args constructor
     */
    @Test
    public void testAllArgsConstructor() {
        // Arrange
        String modelName = "text-embedding-3-small";
        TextEmbeddingLabel label = new TextEmbeddingLabel("模型中文名称", "Model Name");
        String modelType = "embedding";
        List<ModelFeatureEnum> features = List.of(ModelFeatureEnum.TOOL_CALL);
        String fetchFrom = "system";
        TextEmbeddingModelProperties modelProperties = new TextEmbeddingModelProperties(4096, 10);
        Boolean deprecated = false;
        ModelStatusEnum status = ModelStatusEnum.ACTIVE;
        Boolean loadBalancingEnabled = true;

        // Act
        TextEmbeddingModel model = new TextEmbeddingModel(
                modelName,
                label,
                modelType,
                features,
                fetchFrom,
                modelProperties,
                deprecated,
                status,
                loadBalancingEnabled
        );

        // Assert
        assertEquals(modelName, model.getModel());
        assertEquals(label, model.getLabel());
        assertEquals(modelType, model.getModelType());
        assertEquals(features, model.getFeatures());
        assertEquals(fetchFrom, model.getFetchFrom());
        assertEquals(modelProperties, model.getModelProperties());
        assertEquals(deprecated, model.getDeprecated());
        assertEquals(status, model.getStatus());
        assertEquals(loadBalancingEnabled, model.getLoadBalancingEnabled());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        TextEmbeddingModel model = new TextEmbeddingModel();
        String modelName = "text-embedding-3-small";
        TextEmbeddingLabel label = new TextEmbeddingLabel("模型中文名称", "Model Name");
        String modelType = "embedding";
        List<ModelFeatureEnum> features = List.of(ModelFeatureEnum.TOOL_CALL);
        String fetchFrom = "system";
        TextEmbeddingModelProperties modelProperties = new TextEmbeddingModelProperties(4096, 10);
        Boolean deprecated = false;
        ModelStatusEnum status = ModelStatusEnum.ACTIVE;
        Boolean loadBalancingEnabled = true;

        // Act
        model.setModel(modelName);
        model.setLabel(label);
        model.setModelType(modelType);
        model.setFeatures(features);
        model.setFetchFrom(fetchFrom);
        model.setModelProperties(modelProperties);
        model.setDeprecated(deprecated);
        model.setStatus(status);
        model.setLoadBalancingEnabled(loadBalancingEnabled);

        // Assert
        assertEquals(modelName, model.getModel());
        assertEquals(label, model.getLabel());
        assertEquals(modelType, model.getModelType());
        assertEquals(features, model.getFeatures());
        assertEquals(fetchFrom, model.getFetchFrom());
        assertEquals(modelProperties, model.getModelProperties());
        assertEquals(deprecated, model.getDeprecated());
        assertEquals(status, model.getStatus());
        assertEquals(loadBalancingEnabled, model.getLoadBalancingEnabled());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a complete TextEmbeddingModel object with nested objects
        TextEmbeddingLabel label = new TextEmbeddingLabel("模型中文名称", "Model Name");
        TextEmbeddingModelProperties modelProperties = new TextEmbeddingModelProperties(4096, 10);
        List<ModelFeatureEnum> features = List.of(ModelFeatureEnum.TOOL_CALL);

        TextEmbeddingModel model = new TextEmbeddingModel(
                "text-embedding-3-small",
                label,
                "embedding",
                features,
                "system",
                modelProperties,
                false,
                ModelStatusEnum.ACTIVE,
                true
        );

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(model);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"model\":"));
        assertTrue(json.contains("\"label\":"));
        assertTrue(json.contains("\"modelType\":"));
        assertTrue(json.contains("\"features\":"));
        assertTrue(json.contains("\"fetchFrom\":"));
        assertTrue(json.contains("\"modelProperties\":"));
        assertTrue(json.contains("\"deprecated\":"));
        assertTrue(json.contains("\"status\":"));
        assertTrue(json.contains("\"loadBalancingEnabled\":"));

        // Verify nested objects are serialized
        assertTrue(json.contains("\"zhHans\":"));
        assertTrue(json.contains("\"enUs\":"));
        assertTrue(json.contains("\"contextSize\":"));
        assertTrue(json.contains("\"maxChunks\":"));

        // Deserialize back to object
        TextEmbeddingModel deserialized = objectMapper.readValue(json, TextEmbeddingModel.class);

        // Verify the deserialized object matches the original
        assertEquals(model.getModel(), deserialized.getModel());
        assertEquals(model.getModelType(), deserialized.getModelType());
        assertEquals(model.getFetchFrom(), deserialized.getFetchFrom());
        assertEquals(model.getDeprecated(), deserialized.getDeprecated());
        assertEquals(model.getStatus(), deserialized.getStatus());
        assertEquals(model.getLoadBalancingEnabled(), deserialized.getLoadBalancingEnabled());

        // Verify nested objects
        assertEquals(model.getLabel().getZhHans(), deserialized.getLabel().getZhHans());
        assertEquals(model.getLabel().getEnUs(), deserialized.getLabel().getEnUs());

        assertEquals(model.getModelProperties().getContextSize(), deserialized.getModelProperties().getContextSize());
        assertEquals(model.getModelProperties().getMaxChunks(), deserialized.getModelProperties().getMaxChunks());

        // Verify features list
        assertEquals(model.getFeatures().size(), deserialized.getFeatures().size());
        assertEquals(model.getFeatures().get(0), deserialized.getFeatures().get(0));
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"model\": \"text-embedding-3-small\",\n" +
                "  \"label\": {\n" +
                "    \"zh_Hans\": \"模型中文名称\",\n" +
                "    \"en_US\": \"Model Name\"\n" +
                "  },\n" +
                "  \"model_type\": \"embedding\",\n" +
                "  \"features\": [\"tool-call\"],\n" +
                "  \"fetch_from\": \"system\",\n" +
                "  \"model_properties\": {\n" +
                "    \"context_size\": 4096,\n" +
                "    \"max_chunks\": 10\n" +
                "  },\n" +
                "  \"deprecated\": false,\n" +
                "  \"status\": \"active\",\n" +
                "  \"load_balancing_enabled\": true\n" +
                "}";

        // Deserialize with aliases
        TextEmbeddingModel deserialized = objectMapper.readValue(jsonWithAliases, TextEmbeddingModel.class);

        // Verify fields were correctly deserialized
        assertEquals("text-embedding-3-small", deserialized.getModel());
        assertEquals("模型中文名称", deserialized.getLabel().getZhHans());
        assertEquals("Model Name", deserialized.getLabel().getEnUs());
        assertEquals("embedding", deserialized.getModelType());
        assertEquals(1, deserialized.getFeatures().size());
        assertEquals(ModelFeatureEnum.TOOL_CALL, deserialized.getFeatures().get(0));
        assertEquals("system", deserialized.getFetchFrom());
        assertEquals(Integer.valueOf(4096), deserialized.getModelProperties().getContextSize());
        assertEquals(Integer.valueOf(10), deserialized.getModelProperties().getMaxChunks());
        assertFalse(deserialized.getDeprecated());
        assertEquals(ModelStatusEnum.ACTIVE, deserialized.getStatus());
        assertTrue(deserialized.getLoadBalancingEnabled());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        TextEmbeddingLabel label1 = new TextEmbeddingLabel("模型中文名称", "Model Name");
        TextEmbeddingModelProperties properties1 = new TextEmbeddingModelProperties(4096, 10);
        List<ModelFeatureEnum> features1 = List.of(ModelFeatureEnum.TOOL_CALL);

        TextEmbeddingModel model1 = new TextEmbeddingModel(
                "text-embedding-3-small",
                label1,
                "embedding",
                features1,
                "system",
                properties1,
                false,
                ModelStatusEnum.ACTIVE,
                true
        );

        TextEmbeddingLabel label2 = new TextEmbeddingLabel("模型中文名称", "Model Name");
        TextEmbeddingModelProperties properties2 = new TextEmbeddingModelProperties(4096, 10);
        List<ModelFeatureEnum> features2 = List.of(ModelFeatureEnum.TOOL_CALL);

        TextEmbeddingModel model2 = new TextEmbeddingModel(
                "text-embedding-3-small",
                label2,
                "embedding",
                features2,
                "system",
                properties2,
                false,
                ModelStatusEnum.ACTIVE,
                true
        );

        // Create a different object
        TextEmbeddingLabel label3 = new TextEmbeddingLabel("不同名称", "Different Name");
        TextEmbeddingModelProperties properties3 = new TextEmbeddingModelProperties(8192, 20);
        List<ModelFeatureEnum> features3 = List.of(ModelFeatureEnum.TOOL_CALL);

        TextEmbeddingModel model3 = new TextEmbeddingModel(
                "text-embedding-3-large",
                label3,
                "different-type",
                features3,
                "different",
                properties3,
                true,
                ModelStatusEnum.ACTIVE,
                false
        );

        // Test equality
        assertEquals(model1, model2);
        assertNotEquals(model1, model3);

        // Test hash code
        assertEquals(model1.hashCode(), model2.hashCode());
        assertNotEquals(model1.hashCode(), model3.hashCode());
    }
}
