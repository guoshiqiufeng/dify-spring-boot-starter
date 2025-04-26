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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the functionality of DocumentInfo class
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 16:02
 */
public class DocumentInfoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test basic properties setting and getting of DocumentInfo")
    public void testBasicProperties() {
        // Create test object
        DocumentInfo documentInfo = new DocumentInfo();

        // Set properties
        documentInfo.setId("doc-123");
        documentInfo.setPosition(1);
        documentInfo.setDataSourceType("file");
        Map<String, Object> dataSourceInfo = new HashMap<>();
        dataSourceInfo.put("filename", "test.pdf");
        dataSourceInfo.put("size", 1024);
        documentInfo.setDataSourceInfo(dataSourceInfo);
        documentInfo.setDatasetProcessRuleId("rule-456");
        documentInfo.setName("Test Document");
        documentInfo.setCreatedFrom("upload");
        documentInfo.setCreatedBy("user1");
        documentInfo.setCreatedAt(1615379400000L);
        documentInfo.setTokens(1500);
        documentInfo.setIndexingStatus("completed");
        documentInfo.setError(null);
        documentInfo.setEnabled("true");
        documentInfo.setDisabledAt(null);
        documentInfo.setDisabledBy(null);
        documentInfo.setArchived("false");
        documentInfo.setDisplayStatus("normal");
        documentInfo.setWordCount("500");
        documentInfo.setHitCount("10");
        documentInfo.setDocForm("pdf");

        // Verify properties
        assertEquals("doc-123", documentInfo.getId());
        assertEquals(Integer.valueOf(1), documentInfo.getPosition());
        assertEquals("file", documentInfo.getDataSourceType());
        assertEquals(dataSourceInfo, documentInfo.getDataSourceInfo());
        assertEquals("rule-456", documentInfo.getDatasetProcessRuleId());
        assertEquals("Test Document", documentInfo.getName());
        assertEquals("upload", documentInfo.getCreatedFrom());
        assertEquals("user1", documentInfo.getCreatedBy());
        assertEquals(Long.valueOf(1615379400000L), documentInfo.getCreatedAt());
        assertEquals(Integer.valueOf(1500), documentInfo.getTokens());
        assertEquals("completed", documentInfo.getIndexingStatus());
        assertNull(documentInfo.getError());
        assertEquals("true", documentInfo.getEnabled());
        assertNull(documentInfo.getDisabledAt());
        assertNull(documentInfo.getDisabledBy());
        assertEquals("false", documentInfo.getArchived());
        assertEquals("normal", documentInfo.getDisplayStatus());
        assertEquals("500", documentInfo.getWordCount());
        assertEquals("10", documentInfo.getHitCount());
        assertEquals("pdf", documentInfo.getDocForm());
    }

    @Test
    @DisplayName("Test JSON serialization and deserialization of DocumentInfo")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // Create test object
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc-123");
        documentInfo.setName("Test Document");
        documentInfo.setIndexingStatus("completed");
        documentInfo.setTokens(1500);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(documentInfo);

        // Verify JSON contains expected fields
        assertTrue(json.contains("\"id\":\"doc-123\""));
        assertTrue(json.contains("\"name\":\"Test Document\""));
        assertTrue(json.contains("\"indexingStatus\":\"completed\""));
        assertTrue(json.contains("\"tokens\":1500"));

        // Deserialize from JSON
        DocumentInfo deserializedDocumentInfo = objectMapper.readValue(json, DocumentInfo.class);

        // Verify deserialized object
        assertEquals("doc-123", deserializedDocumentInfo.getId());
        assertEquals("Test Document", deserializedDocumentInfo.getName());
        assertEquals("completed", deserializedDocumentInfo.getIndexingStatus());
        assertEquals(Integer.valueOf(1500), deserializedDocumentInfo.getTokens());
    }

    @Test
    @DisplayName("Test JSON deserialization of DocumentInfo using snake case naming")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // JSON using snake case naming
        String snakeCaseJson = "{\n" +
                "  \"id\": \"doc-123\",\n" +
                "  \"position\": 1,\n" +
                "  \"data_source_type\": \"file\",\n" +
                "  \"data_source_info\": {\n" +
                "    \"filename\": \"test.pdf\",\n" +
                "    \"size\": 1024\n" +
                "  },\n" +
                "  \"dataset_process_rule_id\": \"rule-456\",\n" +
                "  \"name\": \"Test Document\",\n" +
                "  \"created_from\": \"upload\",\n" +
                "  \"created_by\": \"user1\",\n" +
                "  \"created_at\": 1615379400000,\n" +
                "  \"tokens\": 1500,\n" +
                "  \"indexing_status\": \"completed\",\n" +
                "  \"enabled\": \"true\",\n" +
                "  \"archived\": \"false\",\n" +
                "  \"display_status\": \"normal\",\n" +
                "  \"word_count\": \"500\",\n" +
                "  \"hit_count\": \"10\",\n" +
                "  \"doc_form\": \"pdf\"\n" +
                "}";

        // Deserialize from snake case JSON
        DocumentInfo deserializedDocumentInfo = objectMapper.readValue(snakeCaseJson, DocumentInfo.class);

        // Verify snake case field mapping
        assertEquals("doc-123", deserializedDocumentInfo.getId());
        assertEquals(Integer.valueOf(1), deserializedDocumentInfo.getPosition());
        assertEquals("file", deserializedDocumentInfo.getDataSourceType());  // Verify data_source_type mapping
        assertNotNull(deserializedDocumentInfo.getDataSourceInfo());  // Verify data_source_info mapping
        assertEquals("test.pdf", deserializedDocumentInfo.getDataSourceInfo().get("filename"));
        assertEquals(1024, deserializedDocumentInfo.getDataSourceInfo().get("size"));
        assertEquals("rule-456", deserializedDocumentInfo.getDatasetProcessRuleId());  // Verify dataset_process_rule_id mapping
        assertEquals("Test Document", deserializedDocumentInfo.getName());
        assertEquals("upload", deserializedDocumentInfo.getCreatedFrom());  // Verify created_from mapping
        assertEquals("user1", deserializedDocumentInfo.getCreatedBy());  // Verify created_by mapping
        assertEquals(Long.valueOf(1615379400000L), deserializedDocumentInfo.getCreatedAt());  // Verify created_at mapping
        assertEquals(Integer.valueOf(1500), deserializedDocumentInfo.getTokens());
        assertEquals("completed", deserializedDocumentInfo.getIndexingStatus());  // Verify indexing_status mapping
        assertEquals("true", deserializedDocumentInfo.getEnabled());
        assertEquals("false", deserializedDocumentInfo.getArchived());
        assertEquals("normal", deserializedDocumentInfo.getDisplayStatus());  // Verify display_status mapping
        assertEquals("500", deserializedDocumentInfo.getWordCount());  // Verify word_count mapping
        assertEquals("10", deserializedDocumentInfo.getHitCount());  // Verify hit_count mapping
        assertEquals("pdf", deserializedDocumentInfo.getDocForm());  // Verify doc_form mapping
    }

    @Test
    @DisplayName("Test equality and hash code of DocumentInfo")
    public void testEqualsAndHashCode() {
        // Create two objects with same content
        DocumentInfo documentInfo1 = new DocumentInfo();
        documentInfo1.setId("doc-123");
        documentInfo1.setName("Test Document");
        documentInfo1.setIndexingStatus("completed");

        DocumentInfo documentInfo2 = new DocumentInfo();
        documentInfo2.setId("doc-123");
        documentInfo2.setName("Test Document");
        documentInfo2.setIndexingStatus("completed");

        // Test equality
        assertEquals(documentInfo1, documentInfo2);
        assertEquals(documentInfo2, documentInfo1);

        // Test hash code
        assertEquals(documentInfo1.hashCode(), documentInfo2.hashCode());

        // Create object with different content
        DocumentInfo documentInfo3 = new DocumentInfo();
        documentInfo3.setId("doc-456");  // Different ID
        documentInfo3.setName("Test Document");
        documentInfo3.setIndexingStatus("completed");

        // Test inequality
        assertNotEquals(documentInfo1, documentInfo3);
        assertNotEquals(documentInfo3, documentInfo1);
    }

    @Test
    @DisplayName("Test default values of DocumentInfo")
    public void testDefaultValues() {
        // Create new object to verify default values
        DocumentInfo documentInfo = new DocumentInfo();

        // Verify default values
        assertNull(documentInfo.getId());
        assertNull(documentInfo.getPosition());
        assertNull(documentInfo.getDataSourceType());
        assertNull(documentInfo.getDataSourceInfo());
        assertNull(documentInfo.getDatasetProcessRuleId());
        assertNull(documentInfo.getName());
        assertNull(documentInfo.getCreatedFrom());
        assertNull(documentInfo.getCreatedBy());
        assertNull(documentInfo.getCreatedAt());
        assertNull(documentInfo.getTokens());
        assertNull(documentInfo.getIndexingStatus());
        assertNull(documentInfo.getError());
        assertNull(documentInfo.getEnabled());
        assertNull(documentInfo.getDisabledAt());
        assertNull(documentInfo.getDisabledBy());
        assertNull(documentInfo.getArchived());
        assertNull(documentInfo.getDisplayStatus());
        assertNull(documentInfo.getWordCount());
        assertNull(documentInfo.getHitCount());
        assertNull(documentInfo.getDocForm());
    }

    @Test
    @DisplayName("Test different indexing statuses of DocumentInfo")
    public void testDifferentIndexingStatuses() throws JsonProcessingException {
        // Test different indexing statuses
        String[] statuses = {"pending", "processing", "completed", "error", "failed"};

        for (String status : statuses) {
            // Create document info with different indexing status
            DocumentInfo documentInfo = new DocumentInfo();
            documentInfo.setId("doc-" + status);
            documentInfo.setIndexingStatus(status);

            if ("error".equals(status) || "failed".equals(status)) {
                documentInfo.setError("Indexing failed: Invalid file format");
            }

            // Serialize to JSON
            String json = objectMapper.writeValueAsString(documentInfo);

            // Deserialize from JSON
            DocumentInfo deserializedDocumentInfo = objectMapper.readValue(json, DocumentInfo.class);

            // Verify status correctly preserved
            assertEquals(status, deserializedDocumentInfo.getIndexingStatus());
            assertEquals("doc-" + status, deserializedDocumentInfo.getId());

            if ("error".equals(status) || "failed".equals(status)) {
                assertEquals("Indexing failed: Invalid file format", deserializedDocumentInfo.getError());
            }
        }
    }

    @Test
    @DisplayName("Test toString method of DocumentInfo")
    public void testToString() {
        // Create test object
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc-123");
        documentInfo.setName("Test Document");
        documentInfo.setIndexingStatus("completed");

        // Get toString result
        String toStringResult = documentInfo.toString();

        // Verify toString contains important fields
        assertTrue(toStringResult.contains("id=doc-123"));
        assertTrue(toStringResult.contains("name=Test Document"));
        assertTrue(toStringResult.contains("indexingStatus=completed"));
    }
} 