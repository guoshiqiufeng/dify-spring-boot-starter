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
import io.github.guoshiqiufeng.dify.core.pojo.DifyFile;
import io.github.guoshiqiufeng.dify.dataset.dto.RetrievalModel;
import io.github.guoshiqiufeng.dify.dataset.dto.request.document.ProcessRule;
import io.github.guoshiqiufeng.dify.dataset.dto.request.file.FileOperation;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocFormEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocTypeEnum;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DocumentCreateByFileRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/28
 */
public class DocumentCreateByFileRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a DocumentCreateByFileRequest instance
        DocumentCreateByFileRequest request = new DocumentCreateByFileRequest();

        // Set values for parent class fields
        String apiKey = "sk-12345678";
        request.setApiKey(apiKey);

        // Set values for fields
        String datasetId = "ds-12345";
        String originalDocumentId = "doc-67890";
        DifyFile file = new DifyFile("test.txt", "text/plain", "test content".getBytes());
        DocTypeEnum docType = DocTypeEnum.others;
        List<MetaData> docMetadata = Arrays.asList(
                MetaData.builder().type("string").value("Test Author").build(),
                MetaData.builder().type("string").value("2025-04-28").build()
        );
        IndexingTechniqueEnum indexingTechnique = IndexingTechniqueEnum.HIGH_QUALITY;
        DocFormEnum docForm = DocFormEnum.text_model;
        String docLanguage = "en";
        ProcessRule processRule = new ProcessRule();
        RetrievalModel retrievalModel = new RetrievalModel();
        String embeddingModel = "test-model";
        String embeddingModelProvider = "test-provider";

        request.setDatasetId(datasetId);
        request.setOriginalDocumentId(originalDocumentId);
        request.setFile(file);
        request.setDocType(docType);
        request.setDocMetadata(docMetadata);
        request.setIndexingTechnique(indexingTechnique);
        request.setDocForm(docForm);
        request.setDocLanguage(docLanguage);
        request.setProcessRule(processRule);
        request.setRetrievalModel(retrievalModel);
        request.setEmbeddingModel(embeddingModel);
        request.setEmbeddingModelProvider(embeddingModelProvider);

        // Assert all values are set correctly
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(originalDocumentId, request.getOriginalDocumentId());
        assertEquals(file, request.getFile());
        assertEquals(docType, request.getDocType());
        assertEquals(docMetadata, request.getDocMetadata());
        assertEquals(indexingTechnique, request.getIndexingTechnique());
        assertEquals(docForm, request.getDocForm());
        assertEquals(docLanguage, request.getDocLanguage());
        assertEquals(processRule, request.getProcessRule());
        assertEquals(retrievalModel, request.getRetrievalModel());
        assertEquals(embeddingModel, request.getEmbeddingModel());
        assertEquals(embeddingModelProvider, request.getEmbeddingModelProvider());
    }

    /**
     * Test FileOperation interface implementation
     */
    @Test
    public void testFileOperationInterface() {
        // Create request
        DocumentCreateByFileRequest request = new DocumentCreateByFileRequest();

        // Verify it implements FileOperation
        assertInstanceOf(FileOperation.class, request);

        // Test setting and getting file
        DifyFile file = new DifyFile("test.txt", "text/plain", "test content".getBytes());
        request.setFile(file);
        assertEquals(file, request.getFile());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a request instance with sample data
        DocumentCreateByFileRequest request = new DocumentCreateByFileRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setOriginalDocumentId("doc-67890");
        request.setDocType(DocTypeEnum.others);
        request.setDocMetadata(Collections.singletonList(
                MetaData.builder().type("string").value("Test Author").build()
        ));
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);
        request.setDocForm(DocFormEnum.text_model);
        request.setDocLanguage("en");
        request.setProcessRule(new ProcessRule());
        request.setRetrievalModel(new RetrievalModel());
        request.setEmbeddingModel("test-model");
        request.setEmbeddingModelProvider("test-provider");

        // Set file to null for serialization
        request.setFile(null);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names (snake_case due to @JsonProperty)
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"original_document_id\":"));
        assertTrue(json.contains("\"doc_type\":"));
        assertTrue(json.contains("\"doc_metadata\":"));
        assertTrue(json.contains("\"indexing_technique\":"));
        assertTrue(json.contains("\"doc_form\":"));
        assertTrue(json.contains("\"doc_language\":"));
        assertTrue(json.contains("\"process_rule\":"));
        assertTrue(json.contains("\"retrieval_model\":"));
        assertTrue(json.contains("\"embedding_model\":"));
        assertTrue(json.contains("\"embedding_model_provider\":"));

        // Deserialize back to object
        DocumentCreateByFileRequest deserialized = objectMapper.readValue(json, DocumentCreateByFileRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getOriginalDocumentId(), deserialized.getOriginalDocumentId());
        assertEquals(request.getDocType(), deserialized.getDocType());
        assertEquals(request.getIndexingTechnique(), deserialized.getIndexingTechnique());
        assertEquals(request.getDocForm(), deserialized.getDocForm());
        assertEquals(request.getDocLanguage(), deserialized.getDocLanguage());
        assertNull(deserialized.getFile()); // File is not serialized
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
                "  \"original_document_id\": \"doc-67890\",\n" +
                "  \"doc_type\": \"others\",\n" +
                "  \"doc_metadata\": [\n" +
                "    {\n" +
                "      \"name\": \"author\",\n" +
                "      \"value\": \"Test Author\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"indexing_technique\": \"high_quality\",\n" +
                "  \"doc_form\": \"text_model\",\n" +
                "  \"doc_language\": \"en\",\n" +
                "  \"embedding_model\": \"test-model\",\n" +
                "  \"embedding_model_provider\": \"test-provider\"\n" +
                "}";

        // Deserialize from snake_case JSON
        DocumentCreateByFileRequest snakeDeserialized = objectMapper.readValue(snakeJson, DocumentCreateByFileRequest.class);
        assertEquals("sk-12345678", snakeDeserialized.getApiKey());
        assertEquals("ds-12345", snakeDeserialized.getDatasetId());
        assertEquals("doc-67890", snakeDeserialized.getOriginalDocumentId());
        assertEquals(DocTypeEnum.others, snakeDeserialized.getDocType());
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, snakeDeserialized.getIndexingTechnique());
        assertEquals(DocFormEnum.text_model, snakeDeserialized.getDocForm());
        assertEquals("en", snakeDeserialized.getDocLanguage());
        assertEquals("test-model", snakeDeserialized.getEmbeddingModel());
        assertEquals("test-provider", snakeDeserialized.getEmbeddingModelProvider());

        // Test with camelCase JSON format (using JsonAlias)
        String camelJson = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"originalDocumentId\": \"doc-67890\",\n" +
                "  \"docType\": \"others\",\n" +
                "  \"docMetadata\": [\n" +
                "    {\n" +
                "      \"name\": \"author\",\n" +
                "      \"value\": \"Test Author\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"indexingTechnique\": \"high_quality\",\n" +
                "  \"docForm\": \"text_model\",\n" +
                "  \"docLanguage\": \"en\",\n" +
                "  \"embeddingModel\": \"test-model\",\n" +
                "  \"embeddingModelProvider\": \"test-provider\"\n" +
                "}";

        // Deserialize from camelCase JSON
        DocumentCreateByFileRequest camelDeserialized = objectMapper.readValue(camelJson, DocumentCreateByFileRequest.class);
        assertEquals("sk-12345678", camelDeserialized.getApiKey());
        assertEquals("ds-12345", camelDeserialized.getDatasetId());
        assertEquals("doc-67890", camelDeserialized.getOriginalDocumentId());
        assertEquals(DocTypeEnum.others, camelDeserialized.getDocType());
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, camelDeserialized.getIndexingTechnique());
        assertEquals(DocFormEnum.text_model, camelDeserialized.getDocForm());
        assertEquals("en", camelDeserialized.getDocLanguage());
        assertEquals("test-model", camelDeserialized.getEmbeddingModel());
        assertEquals("test-provider", camelDeserialized.getEmbeddingModelProvider());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        DocumentCreateByFileRequest request1 = new DocumentCreateByFileRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setOriginalDocumentId("doc-67890");
        request1.setDocType(DocTypeEnum.others);
        request1.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);

        DocumentCreateByFileRequest request2 = new DocumentCreateByFileRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setOriginalDocumentId("doc-67890");
        request2.setDocType(DocTypeEnum.others);
        request2.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);

        // Create a different request
        DocumentCreateByFileRequest request3 = new DocumentCreateByFileRequest();
        request3.setApiKey("sk-12345678");
        request3.setDatasetId("ds-54321");  // Different dataset ID
        request3.setOriginalDocumentId("doc-67890");
        request3.setDocType(DocTypeEnum.others);
        request3.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);

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
        DocumentCreateByFileRequest request = new DocumentCreateByFileRequest();
        assertInstanceOf(BaseDatasetRequest.class, request);
    }

    /**
     * Test Serializable interface implementation
     */
    @Test
    public void testSerializable() {
        DocumentCreateByFileRequest request = new DocumentCreateByFileRequest();
        assertInstanceOf(Serializable.class, request);
    }
}
