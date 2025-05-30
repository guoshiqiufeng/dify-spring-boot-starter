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
import io.github.guoshiqiufeng.dify.dataset.dto.request.document.ProcessRule;
import io.github.guoshiqiufeng.dify.dataset.dto.request.file.FileOperation;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocFormEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DocumentUpdateByFileRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/28
 */
public class DocumentUpdateByFileRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a DocumentUpdateByFileRequest instance
        DocumentUpdateByFileRequest request = new DocumentUpdateByFileRequest();

        // Set values for parent class fields
        String apiKey = "sk-12345678";
        request.setApiKey(apiKey);

        // Set values for fields
        String datasetId = "ds-12345";
        String name = "Updated Document";
        String documentId = "doc-67890";
        MultipartFile file = new MockMultipartFile("testFile", "updated.txt", "text/plain", "updated content".getBytes());
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
        request.setName(name);
        request.setDocumentId(documentId);
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
        assertEquals(name, request.getName());
        assertEquals(documentId, request.getDocumentId());
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
        DocumentUpdateByFileRequest request = new DocumentUpdateByFileRequest();

        // Verify it implements FileOperation
        assertInstanceOf(FileOperation.class, request);

        // Test setting and getting file
        MultipartFile file = new MockMultipartFile("testFile", "updated.txt", "text/plain", "updated content".getBytes());
        request.setFile(file);
        assertEquals(file, request.getFile());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a request instance with sample data
        DocumentUpdateByFileRequest request = new DocumentUpdateByFileRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setName("Updated Document");
        request.setDocumentId("doc-67890");
        DocTypeEnum docType = DocTypeEnum.others;
        List<MetaData> docMetadata = Arrays.asList(
                MetaData.builder().type("string").value("Test Author").build(),
                MetaData.builder().type("string").value("2025-04-28").build()
        );
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);
        request.setDocForm(DocFormEnum.text_model);
        request.setDocLanguage("en");
        request.setProcessRule(new ProcessRule());
        request.setRetrievalModel(new RetrievalModel());
        request.setEmbeddingModel("test-model");
        request.setEmbeddingModelProvider("test-provider");

        // Set file to null for serialization (MultipartFile is not serializable)
        request.setFile(null);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names (snake_case due to @JsonProperty)
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"name\":"));
        assertTrue(json.contains("\"documentId\":"));
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
        DocumentUpdateByFileRequest deserialized = objectMapper.readValue(json, DocumentUpdateByFileRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getName(), deserialized.getName());
        assertEquals(request.getDocumentId(), deserialized.getDocumentId());
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
                "  \"name\": \"Updated Document\",\n" +
                "  \"documentId\": \"doc-67890\",\n" +
                "  \"doc_type\": \"others\",\n" +
                "  \"doc_metadata\": [\n" +
                "    {\n" +
                "      \"name\": \"author\",\n" +
                "      \"value\": \"Updated Author\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"indexing_technique\": \"high_quality\",\n" +
                "  \"doc_form\": \"text_model\",\n" +
                "  \"doc_language\": \"en\",\n" +
                "  \"embedding_model\": \"test-model\",\n" +
                "  \"embedding_model_provider\": \"test-provider\"\n" +
                "}";

        // Deserialize from snake_case JSON
        DocumentUpdateByFileRequest snakeDeserialized = objectMapper.readValue(snakeJson, DocumentUpdateByFileRequest.class);
        assertEquals("sk-12345678", snakeDeserialized.getApiKey());
        assertEquals("ds-12345", snakeDeserialized.getDatasetId());
        assertEquals("Updated Document", snakeDeserialized.getName());
        assertEquals("doc-67890", snakeDeserialized.getDocumentId());
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
                "  \"name\": \"Updated Document\",\n" +
                "  \"documentId\": \"doc-67890\",\n" +
                "  \"docType\": \"others\",\n" +
                "  \"docMetadata\": [\n" +
                "    {\n" +
                "      \"name\": \"author\",\n" +
                "      \"value\": \"Updated Author\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"indexingTechnique\": \"high_quality\",\n" +
                "  \"docForm\": \"text_model\",\n" +
                "  \"docLanguage\": \"en\",\n" +
                "  \"embeddingModel\": \"test-model\",\n" +
                "  \"embeddingModelProvider\": \"test-provider\"\n" +
                "}";

        // Deserialize from camelCase JSON
        DocumentUpdateByFileRequest camelDeserialized = objectMapper.readValue(camelJson, DocumentUpdateByFileRequest.class);
        assertEquals("sk-12345678", camelDeserialized.getApiKey());
        assertEquals("ds-12345", camelDeserialized.getDatasetId());
        assertEquals("Updated Document", camelDeserialized.getName());
        assertEquals("doc-67890", camelDeserialized.getDocumentId());
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
        DocumentUpdateByFileRequest request1 = new DocumentUpdateByFileRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setName("Updated Document");
        request1.setDocumentId("doc-67890");
        request1.setDocType(DocTypeEnum.others);

        DocumentUpdateByFileRequest request2 = new DocumentUpdateByFileRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setName("Updated Document");
        request2.setDocumentId("doc-67890");
        request2.setDocType(DocTypeEnum.others);

        // Create a different request
        DocumentUpdateByFileRequest request3 = new DocumentUpdateByFileRequest();
        request3.setApiKey("sk-12345678");
        request3.setDatasetId("ds-12345");
        request3.setName("Updated Document");
        request3.setDocumentId("doc-54321"); // Different document ID
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
        DocumentUpdateByFileRequest request = new DocumentUpdateByFileRequest();
        assertInstanceOf(BaseDatasetRequest.class, request);
    }

    /**
     * Test Serializable interface implementation
     */
    @Test
    public void testSerializable() {
        DocumentUpdateByFileRequest request = new DocumentUpdateByFileRequest();
        assertInstanceOf(Serializable.class, request);
    }
}
