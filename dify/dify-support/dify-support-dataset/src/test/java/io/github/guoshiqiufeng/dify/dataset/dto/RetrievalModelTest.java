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
package io.github.guoshiqiufeng.dify.dataset.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.dataset.dto.RetrievalModel;
import io.github.guoshiqiufeng.dify.dataset.enums.RerankingModeEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.SearchMethodEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RetrievalModel
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class RetrievalModelTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods for RetrievalModel
     */
    @Test
    public void testGetterAndSetter() {
        // Create a RetrievalModel instance
        RetrievalModel retrievalModel = new RetrievalModel();

        // Set values for all fields
        retrievalModel.setSearchMethod(SearchMethodEnum.hybrid_search);
        retrievalModel.setRerankingMode(RerankingModeEnum.weighted_score);
        retrievalModel.setRerankingEnable(true);
        retrievalModel.setTopK(5);
        retrievalModel.setScoreThresholdEnabled(true);
        retrievalModel.setScoreThreshold(0.75f);

        // Create and set RerankingModel
        RetrievalModel.RerankingModel rerankingModel = new RetrievalModel.RerankingModel();
        rerankingModel.setRerankingProviderName("OpenAI");
        rerankingModel.setRerankingModelName("gpt-4");
        retrievalModel.setRerankingModel(rerankingModel);

        // Create and set RerankingModelWeight
        RetrievalModel.RerankingModelWeight weights = new RetrievalModel.RerankingModelWeight();
        weights.setWeightType("customized");

        // Create and set VectorSetting
        RetrievalModel.VectorSetting vectorSetting = new RetrievalModel.VectorSetting();
        vectorSetting.setVectorWeight(0.8f);
        vectorSetting.setEmbeddingModelName("text-embedding-ada-002");
        vectorSetting.setEmbeddingProviderName("OpenAI");
        weights.setVectorSetting(vectorSetting);

        // Create and set KeywordSetting
        RetrievalModel.KeywordSetting keywordSetting = new RetrievalModel.KeywordSetting();
        keywordSetting.setKeywordWeight(0.2f);
        weights.setKeywordSetting(keywordSetting);

        retrievalModel.setWeights(weights);

        // Assert all values are set correctly
        assertEquals(SearchMethodEnum.hybrid_search, retrievalModel.getSearchMethod());
        assertEquals(RerankingModeEnum.weighted_score, retrievalModel.getRerankingMode());
        assertTrue(retrievalModel.getRerankingEnable());
        assertEquals(5, retrievalModel.getTopK());
        assertTrue(retrievalModel.getScoreThresholdEnabled());
        assertEquals(0.75f, retrievalModel.getScoreThreshold(), 0.001);

        // Assert RerankingModel is set correctly
        assertNotNull(retrievalModel.getRerankingModel());
        assertEquals("OpenAI", retrievalModel.getRerankingModel().getRerankingProviderName());
        assertEquals("gpt-4", retrievalModel.getRerankingModel().getRerankingModelName());

        // Assert RerankingModelWeight is set correctly
        assertNotNull(retrievalModel.getWeights());
        assertEquals("customized", retrievalModel.getWeights().getWeightType());

        // Assert VectorSetting is set correctly
        assertNotNull(retrievalModel.getWeights().getVectorSetting());
        assertEquals(0.8f, retrievalModel.getWeights().getVectorSetting().getVectorWeight(), 0.001);
        assertEquals("text-embedding-ada-002", retrievalModel.getWeights().getVectorSetting().getEmbeddingModelName());
        assertEquals("OpenAI", retrievalModel.getWeights().getVectorSetting().getEmbeddingProviderName());

        // Assert KeywordSetting is set correctly
        assertNotNull(retrievalModel.getWeights().getKeywordSetting());
        assertEquals(0.2f, retrievalModel.getWeights().getKeywordSetting().getKeywordWeight(), 0.001);
    }

    /**
     * Test serialization and deserialization with Jackson
     * This test verifies the JSON Property annotations work correctly for serialization
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a RetrievalModel instance with sample data
        RetrievalModel retrievalModel = new RetrievalModel();
        retrievalModel.setSearchMethod(SearchMethodEnum.semantic_search);
        retrievalModel.setRerankingMode(RerankingModeEnum.reranking_model);
        retrievalModel.setRerankingEnable(true);
        retrievalModel.setTopK(10);
        retrievalModel.setScoreThresholdEnabled(false);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(retrievalModel);

        // Verify JSON contains snake_case property names
        assertTrue(json.contains("\"search_method\":"));
        assertTrue(json.contains("\"reranking_mode\":"));
        assertTrue(json.contains("\"reranking_enable\":"));
        assertTrue(json.contains("\"top_k\":"));
        assertTrue(json.contains("\"score_threshold_enabled\":"));

        // Deserialize back to object
        RetrievalModel deserialized = objectMapper.readValue(json, RetrievalModel.class);

        // Verify the deserialized object matches the original
        assertEquals(retrievalModel.getSearchMethod(), deserialized.getSearchMethod());
        assertEquals(retrievalModel.getRerankingMode(), deserialized.getRerankingMode());
        assertEquals(retrievalModel.getRerankingEnable(), deserialized.getRerankingEnable());
        assertEquals(retrievalModel.getTopK(), deserialized.getTopK());
        assertEquals(retrievalModel.getScoreThresholdEnabled(), deserialized.getScoreThresholdEnabled());
    }

    /**
     * Test JSON deserialization with JsonAlias and JsonProperty
     * This test verifies that both camelCase and snake_case property names work for deserialization
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // Test with snake_case JSON format
        String snakeJson = "{" +
                "\"search_method\":\"hybrid_search\"," +
                "\"reranking_mode\":\"weighted_score\"," +
                "\"reranking_enable\":true," +
                "\"top_k\":5," +
                "\"score_threshold_enabled\":true," +
                "\"score_threshold\":0.75," +
                "\"reranking_model\":{" +
                "  \"reranking_provider_name\":\"OpenAI\"," +
                "  \"reranking_model_name\":\"gpt-4\"" +
                "}," +
                "\"weights\":{" +
                "  \"weight_type\":\"customized\"," +
                "  \"vector_setting\":{" +
                "    \"vector_weight\":0.8," +
                "    \"embedding_model_name\":\"text-embedding-ada-002\"," +
                "    \"embedding_provider_name\":\"OpenAI\"" +
                "  }," +
                "  \"keyword_setting\":{" +
                "    \"keyword_weight\":0.2" +
                "  }" +
                "}" +
                "}";

        // Deserialize from snake_case JSON
        RetrievalModel snakeDeserialized = objectMapper.readValue(snakeJson, RetrievalModel.class);
        validateDeserializedModel(snakeDeserialized);

        // Test with camelCase JSON format (using JsonAlias)
        String camelJson = "{" +
                "\"searchMethod\":\"hybrid_search\"," +
                "\"rerankingMode\":\"weighted_score\"," +
                "\"rerankingEnable\":true," +
                "\"topK\":5," +
                "\"scoreThresholdEnabled\":true," +
                "\"scoreThreshold\":0.75," +
                "\"rerankingModel\":{" +
                "  \"reranking_provider_name\":\"OpenAI\"," +
                "  \"reranking_model_name\":\"gpt-4\"" +
                "}," +
                "\"weights\":{" +
                "  \"weightType\":\"customized\"," +
                "  \"vectorSetting\":{" +
                "    \"vectorWeight\":0.8," +
                "    \"embeddingModelName\":\"text-embedding-ada-002\"," +
                "    \"embeddingProviderName\":\"OpenAI\"" +
                "  }," +
                "  \"keywordSetting\":{" +
                "    \"keywordWeight\":0.2" +
                "  }" +
                "}" +
                "}";

        // Deserialize from camelCase JSON
        RetrievalModel camelDeserialized = objectMapper.readValue(camelJson, RetrievalModel.class);
        validateDeserializedModel(camelDeserialized);
    }

    /**
     * Helper method to validate a deserialized RetrievalModel
     */
    private void validateDeserializedModel(RetrievalModel model) {
        // Validate main fields
        assertEquals(SearchMethodEnum.hybrid_search, model.getSearchMethod());
        assertEquals(RerankingModeEnum.weighted_score, model.getRerankingMode());
        assertTrue(model.getRerankingEnable());
        assertEquals(5, model.getTopK());
        assertTrue(model.getScoreThresholdEnabled());
        assertEquals(0.75f, model.getScoreThreshold(), 0.001);

        // Validate RerankingModel
        assertNotNull(model.getRerankingModel());
        assertEquals("OpenAI", model.getRerankingModel().getRerankingProviderName());
        assertEquals("gpt-4", model.getRerankingModel().getRerankingModelName());

        // Validate RerankingModelWeight
        assertNotNull(model.getWeights());
        assertEquals("customized", model.getWeights().getWeightType());

        // Validate VectorSetting
        assertNotNull(model.getWeights().getVectorSetting());
        assertEquals(0.8f, model.getWeights().getVectorSetting().getVectorWeight(), 0.001);
        assertEquals("text-embedding-ada-002", model.getWeights().getVectorSetting().getEmbeddingModelName());
        assertEquals("OpenAI", model.getWeights().getVectorSetting().getEmbeddingProviderName());

        // Validate KeywordSetting
        assertNotNull(model.getWeights().getKeywordSetting());
        assertEquals(0.2f, model.getWeights().getKeywordSetting().getKeywordWeight(), 0.001);
    }
} 