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
package io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.dataset.enums.ModelFeatureEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.ModelStatusEnum;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TextEmbedding
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class TextEmbeddingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        TextEmbedding textEmbedding = new TextEmbedding();

        // Assert
        assertNotNull(textEmbedding);
        assertNull(textEmbedding.getProvider());
        assertNull(textEmbedding.getLabel());
        assertNull(textEmbedding.getIconSmall());
        assertNull(textEmbedding.getIconLarge());
        assertNull(textEmbedding.getStatus());
        assertNull(textEmbedding.getModels());
    }

    /**
     * Test all-args constructor
     */
    @Test
    public void testAllArgsConstructor() {
        // Arrange
        String provider = "openai";
        TextEmbeddingLabel label = new TextEmbeddingLabel("OpenAI 中文", "OpenAI");
        TextEmbeddingIcon iconSmall = new TextEmbeddingIcon("小图标中文路径", "small_icon_path");
        TextEmbeddingIcon iconLarge = new TextEmbeddingIcon("大图标中文路径", "large_icon_path");
        String status = "active";
        List<TextEmbeddingModel> models = new ArrayList<>();
        TextEmbeddingModel model = new TextEmbeddingModel();
        models.add(model);

        // Act
        TextEmbedding textEmbedding = new TextEmbedding(provider, label, iconSmall, iconLarge, status, models);

        // Assert
        assertEquals(provider, textEmbedding.getProvider());
        assertEquals(label, textEmbedding.getLabel());
        assertEquals(iconSmall, textEmbedding.getIconSmall());
        assertEquals(iconLarge, textEmbedding.getIconLarge());
        assertEquals(status, textEmbedding.getStatus());
        assertEquals(models, textEmbedding.getModels());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        TextEmbedding textEmbedding = new TextEmbedding();
        String provider = "openai";
        TextEmbeddingLabel label = new TextEmbeddingLabel("OpenAI 中文", "OpenAI");
        TextEmbeddingIcon iconSmall = new TextEmbeddingIcon("小图标中文路径", "small_icon_path");
        TextEmbeddingIcon iconLarge = new TextEmbeddingIcon("大图标中文路径", "large_icon_path");
        String status = "active";
        List<TextEmbeddingModel> models = new ArrayList<>();
        TextEmbeddingModel model = new TextEmbeddingModel();
        models.add(model);

        // Act
        textEmbedding.setProvider(provider);
        textEmbedding.setLabel(label);
        textEmbedding.setIconSmall(iconSmall);
        textEmbedding.setIconLarge(iconLarge);
        textEmbedding.setStatus(status);
        textEmbedding.setModels(models);

        // Assert
        assertEquals(provider, textEmbedding.getProvider());
        assertEquals(label, textEmbedding.getLabel());
        assertEquals(iconSmall, textEmbedding.getIconSmall());
        assertEquals(iconLarge, textEmbedding.getIconLarge());
        assertEquals(status, textEmbedding.getStatus());
        assertEquals(models, textEmbedding.getModels());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a complete TextEmbedding object with nested objects
        TextEmbeddingLabel label = new TextEmbeddingLabel("OpenAI 中文", "OpenAI");
        TextEmbeddingIcon iconSmall = new TextEmbeddingIcon("小图标中文路径", "small_icon_path");
        TextEmbeddingIcon iconLarge = new TextEmbeddingIcon("大图标中文路径", "large_icon_path");

        TextEmbeddingModelProperties modelProperties = new TextEmbeddingModelProperties(4096, 10);
        List<ModelFeatureEnum> features = List.of(ModelFeatureEnum.TOOL_CALL);

        TextEmbeddingModel model = new TextEmbeddingModel(
                "text-embedding-3-small",
                new TextEmbeddingLabel("模型中文名称", "Model Name"),
                "embedding",
                features,
                "system",
                modelProperties,
                false,
                ModelStatusEnum.ACTIVE,
                true
        );

        List<TextEmbeddingModel> models = new ArrayList<>();
        models.add(model);

        TextEmbedding textEmbedding = new TextEmbedding(
                "openai",
                label,
                iconSmall,
                iconLarge,
                "active",
                models
        );

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(textEmbedding);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"provider\":"));
        assertTrue(json.contains("\"label\":"));
        assertTrue(json.contains("\"iconSmall\":"));
        assertTrue(json.contains("\"iconLarge\":"));
        assertTrue(json.contains("\"status\":"));
        assertTrue(json.contains("\"models\":"));

        // Verify nested objects are serialized
        assertTrue(json.contains("\"zhHans\":"));
        assertTrue(json.contains("\"enUs\":"));
        assertTrue(json.contains("\"model\":"));
        assertTrue(json.contains("\"modelType\":"));
        assertTrue(json.contains("\"features\":"));
        assertTrue(json.contains("\"fetchFrom\":"));
        assertTrue(json.contains("\"modelProperties\":"));
        assertTrue(json.contains("\"deprecated\":"));
        assertTrue(json.contains("\"loadBalancingEnabled\":"));
        assertTrue(json.contains("\"contextSize\":"));
        assertTrue(json.contains("\"maxChunks\":"));

        // Deserialize back to object
        TextEmbedding deserialized = objectMapper.readValue(json, TextEmbedding.class);

        // Verify the deserialized object matches the original
        assertEquals(textEmbedding.getProvider(), deserialized.getProvider());
        assertEquals(textEmbedding.getStatus(), deserialized.getStatus());

        // Verify nested objects
        assertEquals(textEmbedding.getLabel().getZhHans(), deserialized.getLabel().getZhHans());
        assertEquals(textEmbedding.getLabel().getEnUs(), deserialized.getLabel().getEnUs());

        assertEquals(textEmbedding.getIconSmall().getZhHans(), deserialized.getIconSmall().getZhHans());
        assertEquals(textEmbedding.getIconSmall().getEnUs(), deserialized.getIconSmall().getEnUs());

        assertEquals(textEmbedding.getIconLarge().getZhHans(), deserialized.getIconLarge().getZhHans());
        assertEquals(textEmbedding.getIconLarge().getEnUs(), deserialized.getIconLarge().getEnUs());

        // Verify models list
        assertEquals(textEmbedding.getModels().size(), deserialized.getModels().size());

        // Verify first model
        TextEmbeddingModel originalModel = textEmbedding.getModels().get(0);
        TextEmbeddingModel deserializedModel = deserialized.getModels().get(0);

        assertEquals(originalModel.getModel(), deserializedModel.getModel());
        assertEquals(originalModel.getModelType(), deserializedModel.getModelType());
        assertEquals(originalModel.getFetchFrom(), deserializedModel.getFetchFrom());
        assertEquals(originalModel.getDeprecated(), deserializedModel.getDeprecated());
        assertEquals(originalModel.getStatus(), deserializedModel.getStatus());
        assertEquals(originalModel.getLoadBalancingEnabled(), deserializedModel.getLoadBalancingEnabled());

        // Verify model properties
        assertEquals(
                originalModel.getModelProperties().getContextSize(),
                deserializedModel.getModelProperties().getContextSize()
        );
        assertEquals(
                originalModel.getModelProperties().getMaxChunks(),
                deserializedModel.getModelProperties().getMaxChunks()
        );
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"provider\": \"openai\",\n" +
                "  \"label\": {\n" +
                "    \"zh_Hans\": \"OpenAI 中文\",\n" +
                "    \"en_US\": \"OpenAI\"\n" +
                "  },\n" +
                "  \"icon_small\": {\n" +
                "    \"zh_Hans\": \"小图标中文路径\",\n" +
                "    \"en_US\": \"small_icon_path\"\n" +
                "  },\n" +
                "  \"icon_large\": {\n" +
                "    \"zh_Hans\": \"大图标中文路径\",\n" +
                "    \"en_US\": \"large_icon_path\"\n" +
                "  },\n" +
                "  \"status\": \"active\",\n" +
                "  \"models\": [\n" +
                "    {\n" +
                "      \"model\": \"text-embedding-3-small\",\n" +
                "      \"label\": {\n" +
                "        \"zh_Hans\": \"模型中文名称\",\n" +
                "        \"en_US\": \"Model Name\"\n" +
                "      },\n" +
                "      \"model_type\": \"embedding\",\n" +
                "      \"features\": [\"tool-call\"],\n" +
                "      \"fetch_from\": \"system\",\n" +
                "      \"model_properties\": {\n" +
                "        \"context_size\": 4096,\n" +
                "        \"max_chunks\": 10\n" +
                "      },\n" +
                "      \"deprecated\": false,\n" +
                "      \"status\": \"active\",\n" +
                "      \"load_balancing_enabled\": true\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Deserialize with aliases
        TextEmbedding deserialized = objectMapper.readValue(jsonWithAliases, TextEmbedding.class);

        // Verify fields were correctly deserialized
        assertEquals("openai", deserialized.getProvider());
        assertEquals("OpenAI 中文", deserialized.getLabel().getZhHans());
        assertEquals("OpenAI", deserialized.getLabel().getEnUs());
        assertEquals("小图标中文路径", deserialized.getIconSmall().getZhHans());
        assertEquals("small_icon_path", deserialized.getIconSmall().getEnUs());
        assertEquals("大图标中文路径", deserialized.getIconLarge().getZhHans());
        assertEquals("large_icon_path", deserialized.getIconLarge().getEnUs());
        assertEquals("active", deserialized.getStatus());

        // Verify models list
        assertEquals(1, deserialized.getModels().size());

        // Verify model properties
        TextEmbeddingModel model = deserialized.getModels().get(0);
        assertEquals("text-embedding-3-small", model.getModel());
        assertEquals("模型中文名称", model.getLabel().getZhHans());
        assertEquals("Model Name", model.getLabel().getEnUs());
        assertEquals("embedding", model.getModelType());
        assertEquals(1, model.getFeatures().size());
        assertEquals(ModelFeatureEnum.TOOL_CALL, model.getFeatures().get(0));
        assertEquals("system", model.getFetchFrom());
        assertEquals(Integer.valueOf(4096), model.getModelProperties().getContextSize());
        assertEquals(Integer.valueOf(10), model.getModelProperties().getMaxChunks());
        assertFalse(model.getDeprecated());
        assertEquals(ModelStatusEnum.ACTIVE, model.getStatus());
        assertTrue(model.getLoadBalancingEnabled());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        TextEmbeddingLabel label1 = new TextEmbeddingLabel("OpenAI 中文", "OpenAI");
        TextEmbeddingIcon iconSmall1 = new TextEmbeddingIcon("小图标中文路径", "small_icon_path");
        TextEmbeddingIcon iconLarge1 = new TextEmbeddingIcon("大图标中文路径", "large_icon_path");
        List<TextEmbeddingModel> models1 = new ArrayList<>();

        TextEmbedding embedding1 = new TextEmbedding(
                "openai",
                label1,
                iconSmall1,
                iconLarge1,
                "active",
                models1
        );

        TextEmbeddingLabel label2 = new TextEmbeddingLabel("OpenAI 中文", "OpenAI");
        TextEmbeddingIcon iconSmall2 = new TextEmbeddingIcon("小图标中文路径", "small_icon_path");
        TextEmbeddingIcon iconLarge2 = new TextEmbeddingIcon("大图标中文路径", "large_icon_path");
        List<TextEmbeddingModel> models2 = new ArrayList<>();

        TextEmbedding embedding2 = new TextEmbedding(
                "openai",
                label2,
                iconSmall2,
                iconLarge2,
                "active",
                models2
        );

        // Create a different object
        TextEmbeddingLabel label3 = new TextEmbeddingLabel("Different 中文", "Different");
        TextEmbeddingIcon iconSmall3 = new TextEmbeddingIcon("不同小图标路径", "different_small_path");
        TextEmbeddingIcon iconLarge3 = new TextEmbeddingIcon("不同大图标路径", "different_large_path");
        List<TextEmbeddingModel> models3 = new ArrayList<>();
        models3.add(new TextEmbeddingModel());

        TextEmbedding embedding3 = new TextEmbedding(
                "different",
                label3,
                iconSmall3,
                iconLarge3,
                "inactive",
                models3
        );

        // Test equality
        assertEquals(embedding1, embedding2);
        assertNotEquals(embedding1, embedding3);

        // Test hash code
        assertEquals(embedding1.hashCode(), embedding2.hashCode());
        assertNotEquals(embedding1.hashCode(), embedding3.hashCode());
    }
}
