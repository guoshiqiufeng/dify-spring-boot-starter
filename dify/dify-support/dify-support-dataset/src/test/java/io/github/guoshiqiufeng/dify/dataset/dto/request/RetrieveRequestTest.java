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
package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.dataset.dto.RetrievalModel;
import io.github.guoshiqiufeng.dify.dataset.enums.RerankingModeEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.SearchMethodEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RetrieveRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class RetrieveRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a RetrieveRequest instance
        RetrieveRequest retrieveRequest = new RetrieveRequest();

        // Set values for parent class fields
        String apiKey = "sk-12345678";
        retrieveRequest.setApiKey(apiKey);

        // Set values for fields
        String datasetId = "ds-12345";
        String query = "How does machine learning work?";
        retrieveRequest.setDatasetId(datasetId);
        retrieveRequest.setQuery(query);

        // Create and set RetrievalModel
        RetrieveRetrievalModel retrievalModel = new RetrieveRetrievalModel();
        retrievalModel.setSearchMethod(SearchMethodEnum.hybrid_search);
        //retrievalModel.setRerankingMode(RerankingModeEnum.weighted_score);
        retrievalModel.setRerankingEnable(true);
        retrievalModel.setTopK(5);
        retrieveRequest.setRetrievalModel(retrievalModel);

        // Assert all values are set correctly
        assertEquals(apiKey, retrieveRequest.getApiKey());
        assertEquals(datasetId, retrieveRequest.getDatasetId());
        assertEquals(query, retrieveRequest.getQuery());
        assertNotNull(retrieveRequest.getRetrievalModel());
        assertEquals(SearchMethodEnum.hybrid_search, retrieveRequest.getRetrievalModel().getSearchMethod());
        //assertEquals(RerankingModeEnum.weighted_score, retrieveRequest.getRetrievalModel().getRerankingMode());
        assertTrue(retrieveRequest.getRetrievalModel().getRerankingEnable());
        assertEquals(5, retrieveRequest.getRetrievalModel().getTopK());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a RetrieveRequest instance with sample data
        RetrieveRequest retrieveRequest = new RetrieveRequest();
        retrieveRequest.setApiKey("sk-12345678");
        retrieveRequest.setDatasetId("ds-12345");
        retrieveRequest.setQuery("How does machine learning work?");

        RetrieveRetrievalModel retrievalModel = new RetrieveRetrievalModel();
        retrievalModel.setSearchMethod(SearchMethodEnum.semantic_search);
        retrievalModel.setTopK(3);
        retrieveRequest.setRetrievalModel(retrievalModel);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(retrieveRequest);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"query\":"));
        assertTrue(json.contains("\"retrieval_model\":"));  // Note: this should use snake_case due to @JsonProperty
        assertTrue(json.contains("\"search_method\":"));
        assertTrue(json.contains("\"top_k\":"));

        // Deserialize back to object
        RetrieveRequest deserialized = objectMapper.readValue(json, RetrieveRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(retrieveRequest.getApiKey(), deserialized.getApiKey());
        assertEquals(retrieveRequest.getDatasetId(), deserialized.getDatasetId());
        assertEquals(retrieveRequest.getQuery(), deserialized.getQuery());
        assertNotNull(deserialized.getRetrievalModel());
        assertEquals(retrieveRequest.getRetrievalModel().getSearchMethod(), deserialized.getRetrievalModel().getSearchMethod());
        assertEquals(retrieveRequest.getRetrievalModel().getTopK(), deserialized.getRetrievalModel().getTopK());
    }

    /**
     * Test JSON deserialization with different property name formats
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // Test with snake_case JSON format
        String snakeJson = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"query\": \"How does machine learning work?\",\n" +
                "  \"retrieval_model\": {\n" +
                "    \"search_method\": \"hybrid_search\",\n" +
                "    \"reranking_enable\": true,\n" +
                "    \"top_k\": 5\n" +
                "  }\n" +
                "}";

        // Deserialize from snake_case JSON
        RetrieveRequest snakeDeserialized = objectMapper.readValue(snakeJson, RetrieveRequest.class);
        validateDeserializedRequest(snakeDeserialized);

        // Test with camelCase JSON format (using JsonAlias)
        String camelJson = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"query\": \"How does machine learning work?\",\n" +
                "  \"retrievalModel\": {\n" +
                "    \"searchMethod\": \"hybrid_search\",\n" +
                "    \"rerankingEnable\": true,\n" +
                "    \"topK\": 5\n" +
                "  }\n" +
                "}";

        // Deserialize from camelCase JSON
        RetrieveRequest camelDeserialized = objectMapper.readValue(camelJson, RetrieveRequest.class);
        validateDeserializedRequest(camelDeserialized);
    }

    /**
     * Helper method to validate a deserialized RetrieveRequest
     */
    private void validateDeserializedRequest(RetrieveRequest request) {
        // Validate main fields
        assertEquals("sk-12345678", request.getApiKey());
        assertEquals("ds-12345", request.getDatasetId());
        assertEquals("How does machine learning work?", request.getQuery());

        // Validate RetrievalModel
        assertNotNull(request.getRetrievalModel());
        assertEquals(SearchMethodEnum.hybrid_search, request.getRetrievalModel().getSearchMethod());
        // assertEquals(RerankingModeEnum.weighted_score, request.getRetrievalModel().getRerankingMode());
        assertTrue(request.getRetrievalModel().getRerankingEnable());
        assertEquals(5, request.getRetrievalModel().getTopK());
    }
}
