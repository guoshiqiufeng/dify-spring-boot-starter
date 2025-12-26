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
import io.github.guoshiqiufeng.dify.dataset.dto.request.document.ProcessRule;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocFormEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocTypeEnum;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DocumentUpdateByTextRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/28
 */
@SuppressWarnings("deprecation")
public class DocumentUpdateByTextRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a DocumentUpdateByTextRequest instance
        DocumentUpdateByTextRequest request = new DocumentUpdateByTextRequest();

        // Set values for parent class fields
        String apiKey = "sk-12345678";
        request.setApiKey(apiKey);

        // Set values for fields
        String datasetId = "ds-12345";
        String documentId = "doc-67890";
        String name = "Updated Text Document";
        String text = "This is updated document text content for testing.";
        DocTypeEnum docType = DocTypeEnum.others;
        List<MetaData> docMetadata = Arrays.asList(
                MetaData.builder().type("string").value("Test Author").build(),
                MetaData.builder().type("string").value("2025-04-28").build()
        );
        IndexingTechniqueEnum indexingTechnique = IndexingTechniqueEnum.HIGH_QUALITY;
        DocFormEnum docForm = DocFormEnum.text_model;
        String docLanguage = "en";
        ProcessRule processRule = new ProcessRule();

        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setName(name);
        request.setText(text);
        request.setDocType(docType);
        request.setDocMetadata(docMetadata);
        request.setIndexingTechnique(indexingTechnique);
        request.setDocForm(docForm);
        request.setDocLanguage(docLanguage);
        request.setProcessRule(processRule);

        // Assert all values are set correctly
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(documentId, request.getDocumentId());
        assertEquals(name, request.getName());
        assertEquals(text, request.getText());
        assertEquals(docType, request.getDocType());
        assertEquals(docMetadata, request.getDocMetadata());
        assertEquals(indexingTechnique, request.getIndexingTechnique());
        assertEquals(docForm, request.getDocForm());
        assertEquals(docLanguage, request.getDocLanguage());
        assertEquals(processRule, request.getProcessRule());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a request instance with sample data
        DocumentUpdateByTextRequest request = new DocumentUpdateByTextRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setDocumentId("doc-67890");
        request.setName("Updated Text Document");
        request.setText("This is updated document text content for testing.");
        DocTypeEnum docType = DocTypeEnum.others;
        List<MetaData> docMetadata = Arrays.asList(
                MetaData.builder().type("string").value("Test Author").build(),
                MetaData.builder().type("string").value("2025-04-28").build()
        );
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);
        request.setDocForm(DocFormEnum.text_model);
        request.setDocLanguage("en");
        request.setProcessRule(new ProcessRule());

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names (snake_case due to @JsonProperty)
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"documentId\":"));
        assertTrue(json.contains("\"name\":"));
        assertTrue(json.contains("\"text\":"));
        assertTrue(json.contains("\"doc_type\":"));
        assertTrue(json.contains("\"doc_metadata\":"));
        assertTrue(json.contains("\"indexing_technique\":"));
        assertTrue(json.contains("\"doc_form\":"));
        assertTrue(json.contains("\"doc_language\":"));
        assertTrue(json.contains("\"process_rule\":"));

        // Deserialize back to object
        DocumentUpdateByTextRequest deserialized = objectMapper.readValue(json, DocumentUpdateByTextRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getDocumentId(), deserialized.getDocumentId());
        assertEquals(request.getName(), deserialized.getName());
        assertEquals(request.getText(), deserialized.getText());
        assertEquals(request.getDocType(), deserialized.getDocType());
        assertEquals(request.getIndexingTechnique(), deserialized.getIndexingTechnique());
        assertEquals(request.getDocForm(), deserialized.getDocForm());
        assertEquals(request.getDocLanguage(), deserialized.getDocLanguage());
    }

    /**
     * Test JSON deserialization with both snake_case and camelCase property names
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // Test with snake_case JSON format
        String snakeJson = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"documentId\": \"doc-67890\",\n" +
                "  \"name\": \"Updated Text Document\",\n" +
                "  \"text\": \"This is updated document text content for testing.\",\n" +
                "  \"doc_type\": \"others\",\n" +
                "  \"doc_metadata\": [\n" +
                "    {\n" +
                "      \"name\": \"author\",\n" +
                "      \"value\": \"Updated Author\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"indexing_technique\": \"high_quality\",\n" +
                "  \"doc_form\": \"text_model\",\n" +
                "  \"doc_language\": \"en\"\n" +
                "}";

        // Deserialize from snake_case JSON
        DocumentUpdateByTextRequest snakeDeserialized = objectMapper.readValue(snakeJson, DocumentUpdateByTextRequest.class);
        assertEquals("sk-12345678", snakeDeserialized.getApiKey());
        assertEquals("ds-12345", snakeDeserialized.getDatasetId());
        assertEquals("doc-67890", snakeDeserialized.getDocumentId());
        assertEquals("Updated Text Document", snakeDeserialized.getName());
        assertEquals("This is updated document text content for testing.", snakeDeserialized.getText());
        assertEquals(DocTypeEnum.others, snakeDeserialized.getDocType());
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, snakeDeserialized.getIndexingTechnique());
        assertEquals(DocFormEnum.text_model, snakeDeserialized.getDocForm());
        assertEquals("en", snakeDeserialized.getDocLanguage());

        // Test with camelCase JSON format (using JsonAlias)
        String camelJson = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"documentId\": \"doc-67890\",\n" +
                "  \"name\": \"Updated Text Document\",\n" +
                "  \"text\": \"This is updated document text content for testing.\",\n" +
                "  \"docType\": \"others\",\n" +
                "  \"docMetadata\": [\n" +
                "    {\n" +
                "      \"name\": \"author\",\n" +
                "      \"value\": \"Updated Author\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"indexingTechnique\": \"high_quality\",\n" +
                "  \"docForm\": \"text_model\",\n" +
                "  \"docLanguage\": \"en\"\n" +
                "}";

        // Deserialize from camelCase JSON
        DocumentUpdateByTextRequest camelDeserialized = objectMapper.readValue(camelJson, DocumentUpdateByTextRequest.class);
        assertEquals("sk-12345678", camelDeserialized.getApiKey());
        assertEquals("ds-12345", camelDeserialized.getDatasetId());
        assertEquals("doc-67890", camelDeserialized.getDocumentId());
        assertEquals("Updated Text Document", camelDeserialized.getName());
        assertEquals("This is updated document text content for testing.", camelDeserialized.getText());
        assertEquals(DocTypeEnum.others, camelDeserialized.getDocType());
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, camelDeserialized.getIndexingTechnique());
        assertEquals(DocFormEnum.text_model, camelDeserialized.getDocForm());
        assertEquals("en", camelDeserialized.getDocLanguage());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        DocumentUpdateByTextRequest request1 = new DocumentUpdateByTextRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setDocumentId("doc-67890");
        request1.setName("Updated Text Document");
        request1.setText("This is updated document text content for testing.");
        request1.setDocType(DocTypeEnum.others);

        DocumentUpdateByTextRequest request2 = new DocumentUpdateByTextRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setDocumentId("doc-67890");
        request2.setName("Updated Text Document");
        request2.setText("This is updated document text content for testing.");
        request2.setDocType(DocTypeEnum.others);

        // Create a different request
        DocumentUpdateByTextRequest request3 = new DocumentUpdateByTextRequest();
        request3.setApiKey("sk-12345678");
        request3.setDatasetId("ds-12345");
        request3.setDocumentId("doc-67890");
        request3.setName("Updated Text Document");
        request3.setText("Different text content."); // Different text content
        request3.setDocType(DocTypeEnum.others);

        // Test equality
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        // Test hash code
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    /**
     * Test inheritance from BaseDatasetRequest
     */
    @Test
    public void testInheritance() {
        DocumentUpdateByTextRequest request = new DocumentUpdateByTextRequest();
        assertInstanceOf(BaseDatasetRequest.class, request);
    }

    /**
     * Test Serializable interface implementation
     */
    @Test
    public void testSerializable() {
        DocumentUpdateByTextRequest request = new DocumentUpdateByTextRequest();
        assertInstanceOf(Serializable.class, request);
    }
}
