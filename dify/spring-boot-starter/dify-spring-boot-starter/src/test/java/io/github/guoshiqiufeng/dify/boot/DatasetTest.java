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
package io.github.guoshiqiufeng.dify.boot;

import cn.hutool.json.JSONUtil;
import io.github.guoshiqiufeng.dify.boot.base.BaseDatasetContainerTest;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.dto.RetrievalModel;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.request.document.ProcessRule;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.MetaDataActionEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.SearchMethodEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocFormEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocTypeEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.ModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Dataset API integration tests
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/1 14:22
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatasetTest extends BaseDatasetContainerTest {

    private static String datasetId;
    private static String documentTextId;
    private static String batch;
    private static String documentFileId;
    private static String segmentId;
    private static String metaDataId;
    private static MultipartFile testFile;

    @BeforeAll
    public static void setup() throws IOException {
        // Initialize test file that will be used in multiple tests
        testFile = createTestFile("test.txt", MediaType.TEXT_PLAIN_VALUE);
    }

    @Test
    @Order(1)
    @DisplayName("Test dataset creation and listing")
    public void testDatasetOperations() {
        // Test dataset listing
        DatasetPageRequest pageRequest = new DatasetPageRequest();
        DifyPageResult<DatasetResponse> pageResult = difyDataset.page(pageRequest);
        assertNotNull(pageResult);
        log.info("Dataset page: {}", JSONUtil.toJsonStr(pageResult));

        // Test dataset creation
        DatasetCreateRequest createRequest = new DatasetCreateRequest();
        createRequest.setName("api-test-dataset");
        createRequest.setDescription("Test dataset for API testing");

        DatasetResponse response = difyDataset.create(createRequest);
        assertNotNull(response);
        assertNotNull(response.getId());
        log.info("Created dataset: {}", JSONUtil.toJsonStr(response));

        // Store dataset ID for subsequent tests
        datasetId = response.getId();
    }

    @Test
    @Order(2)
    @DisplayName("Test document creation by text")
    public void testDocumentCreateByText() {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");

        DocumentCreateByTextRequest request = new DocumentCreateByTextRequest();
        request.setDatasetId(datasetId);
        request.setName("Text Document Test");
        request.setText("This is a test document content for API testing.");
        request.setDocType(DocTypeEnum.others);

        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);
        request.setDocForm(DocFormEnum.hierarchical_model);
        request.setDocLanguage("English");

        // Configure process rule
        ProcessRule processRule = new ProcessRule();
        processRule.setMode(ModeEnum.automatic);
        request.setProcessRule(processRule);

        // Configure retrieval model
        request.setRetrievalModel(createRetrievalModel());
        request.setEmbeddingModel("bge-m3:latest");
        request.setEmbeddingModelProvider("langgenius/ollama/ollama");

        // Create document
        DocumentCreateResponse response = difyDataset.createDocumentByText(request);
        assertNotNull(response);
        assertNotNull(response.getDocument());
        assertNotNull(response.getDocument().getId());

        // Store document ID for subsequent tests
        documentTextId = response.getDocument().getId();
        batch = response.getBatch();
        log.info("Created text document: {}", JSONUtil.toJsonStr(response));
    }

    @Test
    @Order(3)
    @DisplayName("Test document creation by file")
    public void testDocumentCreateByFile() {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");
        assertNotNull(testFile, "Test file should be available");

        DocumentCreateByFileRequest request = new DocumentCreateByFileRequest();
        request.setDatasetId(datasetId);
        request.setFile(testFile);
        request.setDocType(DocTypeEnum.others);
        // request.setDocMetadata(Map.of("source", "file", "type", "text-file"));
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);
        request.setDocForm(DocFormEnum.hierarchical_model);
        request.setDocLanguage("English");

        // Configure process rule
        ProcessRule processRule = new ProcessRule();
        processRule.setMode(ModeEnum.automatic);
        request.setProcessRule(processRule);

        // Configure retrieval model
        RetrievalModel retrievalModel = new RetrievalModel();
        retrievalModel.setSearchMethod(SearchMethodEnum.hybrid_search);
        retrievalModel.setRerankingEnable(false);
        retrievalModel.setTopK(2);
        retrievalModel.setScoreThresholdEnabled(true);
        retrievalModel.setScoreThreshold(0.3f);

        request.setRetrievalModel(retrievalModel);
        request.setEmbeddingModel("bge-m3:latest");
        request.setEmbeddingModelProvider("langgenius/ollama/ollama");

        // Create document
        DocumentCreateResponse response = difyDataset.createDocumentByFile(request);
        assertNotNull(response);
        assertNotNull(response.getDocument());
        assertNotNull(response.getDocument().getId());

        // Store document ID for subsequent tests
        documentFileId = response.getDocument().getId();
        log.info("Created file document: {}", JSONUtil.toJsonStr(response));
    }

    @Test
    @Order(4)
    @DisplayName("Test document listing")
    public void testDocumentListing() {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");

        DatasetPageDocumentRequest request = new DatasetPageDocumentRequest();
        request.setDatasetId(datasetId);

        DifyPageResult<DocumentInfo> result = difyDataset.pageDocument(request);
        assertNotNull(result);
        assertFalse(result.getData().isEmpty(), "Document list should not be empty");
        assertEquals(2, result.getData().size(), "Should have 2 documents");

        log.info("Document page: {}", JSONUtil.toJsonStr(result));
    }

    @Test
    @Order(5)
    @DisplayName("Test document update by text")
    public void testDocumentUpdateByText() throws InterruptedException {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");
        assertNotNull(documentTextId, "Document ID should be available from previous test");

        // Test creating metadata
        MetaDataCreateRequest createRequest = new MetaDataCreateRequest();
        createRequest.setDatasetId(datasetId);
        createRequest.setType("string");
        createRequest.setName("test-create-metadata");

        MetaDataResponse createResponse = difyDataset.createMetaData(createRequest);
        assertNotNull(createResponse);
        assertNotNull(createResponse.getId());

        DocumentUpdateByTextRequest request = new DocumentUpdateByTextRequest();
        request.setDatasetId(datasetId);
        request.setDocumentId(documentTextId);
        request.setName("Updated Text Document");
        request.setDocForm(DocFormEnum.hierarchical_model);
        request.setDocLanguage("English");

        request.setDocMetadata(List.of(MetaData.builder()
                .id(createResponse.getId())
                .name(createRequest.getName())
                .type(createRequest.getType())
                .value("testDocumentUpdateByText")
                .build()));
        ProcessRule processRule = new ProcessRule();
        processRule.setMode(ModeEnum.automatic);
        request.setProcessRule(processRule);

        // Add a slight delay to ensure previous operations have completed
        Thread.sleep(500);

        DocumentCreateResponse response = difyDataset.updateDocumentByText(request);
        assertNotNull(response);
        log.info("Updated text document: {}", JSONUtil.toJsonStr(response));
    }

    @Test
    @Order(6)
    @DisplayName("Test document update by file")
    public void testDocumentUpdateByFile() {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");
        assertNotNull(documentFileId, "Document File ID should be available from previous test");
        assertNotNull(testFile, "Test file should be available");

        DocumentUpdateByFileRequest request = new DocumentUpdateByFileRequest();
        request.setDatasetId(datasetId);
        request.setDocumentId(documentFileId);
        request.setFile(testFile);

        DocumentCreateResponse response = difyDataset.updateDocumentByFile(request);
        assertNotNull(response);
        log.info("Updated file document: {}", JSONUtil.toJsonStr(response));
    }

    @Test
    @Order(7)
    @DisplayName("Test document indexing status")
    public void testDocumentIndexingStatus() throws InterruptedException {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");

        DocumentIndexingStatusRequest request = new DocumentIndexingStatusRequest();
        request.setDatasetId(datasetId);
        request.setBatch(batch);

        DocumentIndexingStatusResponse response = difyDataset.indexingStatus(request);
        assertNotNull(response);
        log.info("Indexing status: {}", JSONUtil.toJsonStr(response));
        int attempts = 0;
        while (response.getData().getFirst().getIndexingStatus().equals("indexing") && attempts < 5) {
            attempts++;
            Thread.sleep(500);
            response = difyDataset.indexingStatus(request);
        }
        log.info("Indexing status: {}", JSONUtil.toJsonStr(response));
    }

    @Test
    @Order(8)
    @DisplayName("Test segment operations")
    public void testSegmentOperations() throws InterruptedException {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");
        assertNotNull(documentTextId, "Document ID should be available from previous test");

        // Wait for indexing to complete
        Thread.sleep(1500);

        // Test listing segments
        SegmentPageRequest pageRequest = new SegmentPageRequest();
        pageRequest.setDatasetId(datasetId);
        pageRequest.setDocumentId(documentTextId);

        SegmentResponse pageResponse = difyDataset.pageSegment(pageRequest);
        assertNotNull(pageResponse);
        log.info("Segment listing: {}", JSONUtil.toJsonStr(pageResponse));

        // Test creating segment
        SegmentCreateRequest createRequest = new SegmentCreateRequest();
        createRequest.setDatasetId(datasetId);
        createRequest.setDocumentId(documentTextId);

        List<SegmentParam> segments = new ArrayList<>();
        SegmentParam segmentParam = new SegmentParam();
        segmentParam.setContent("This is a test segment content.");
        segments.add(segmentParam);
        createRequest.setSegments(segments);

        SegmentResponse createResponse = difyDataset.createSegment(createRequest);
        assertNotNull(createResponse);
        assertNotNull(createResponse.getData());
        assertFalse(createResponse.getData().isEmpty());

        segmentId = createResponse.getData().get(0).getId();
        log.info("Created segment: {}", JSONUtil.toJsonStr(createResponse));

        // Test updating segment
        SegmentUpdateRequest updateRequest = new SegmentUpdateRequest();
        updateRequest.setDatasetId(datasetId);
        updateRequest.setDocumentId(documentTextId);
        updateRequest.setSegmentId(segmentId);
        updateRequest.setSegment(new SegmentParam().setContent("Updated test segment content."));

        SegmentUpdateResponse updateResponse = difyDataset.updateSegment(updateRequest);
        assertNotNull(updateResponse);
        log.info("Updated segment: {}", JSONUtil.toJsonStr(updateResponse));

        // Test deleting segment
        SegmentDeleteResponse deleteResponse = difyDataset.deleteSegment(datasetId, documentTextId, segmentId);
        assertNotNull(deleteResponse);
        log.info("Deleted segment: {}", JSONUtil.toJsonStr(deleteResponse));
    }

    @Test
    @Order(9)
    @DisplayName("Test file upload info")
    public void testFileUploadInfo() {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");
        assertNotNull(documentTextId, "Document ID should be available from previous test");

        UploadFileInfoResponse response = difyDataset.uploadFileInfo(datasetId, documentTextId);
        assertNotNull(response);
        log.info("Upload file info: {}", JSONUtil.toJsonStr(response));
    }

    @Test
    @Order(10)
    @DisplayName("Test retrieval")
    public void testRetrieval() {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");

        RetrieveRequest request = new RetrieveRequest();
        request.setDatasetId(datasetId);
        request.setQuery("test");

        RetrieveResponse response = difyDataset.retrieve(request);
        assertNotNull(response);
        log.info("Retrieval results: {}", JSONUtil.toJsonStr(response));
    }

    @Test
    @Order(11)
    @DisplayName("Test metadata operations")
    public void testMetadataOperations() {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");

        // Test creating metadata
        MetaDataCreateRequest createRequest = new MetaDataCreateRequest();
        createRequest.setDatasetId(datasetId);
        createRequest.setType("string");
        createRequest.setName("test-metadata");

        MetaDataResponse createResponse = difyDataset.createMetaData(createRequest);
        assertNotNull(createResponse);
        assertNotNull(createResponse.getId());

        metaDataId = createResponse.getId();
        log.info("Created metadata: {}", JSONUtil.toJsonStr(createResponse));

        // Test updating metadata
        MetaDataUpdateRequest updateRequest = new MetaDataUpdateRequest();
        updateRequest.setDatasetId(datasetId);
        updateRequest.setMetaDataId(metaDataId);
        updateRequest.setName("updated-test-metadata");

        MetaDataResponse updateResponse = difyDataset.updateMetaData(updateRequest);
        assertNotNull(updateResponse);
        log.info("Updated metadata: {}", JSONUtil.toJsonStr(updateResponse));

        // Test metadata action
        MetaDataActionRequest actionRequest = new MetaDataActionRequest();
        actionRequest.setDatasetId(datasetId);
        actionRequest.setAction(MetaDataActionEnum.disable);

        difyDataset.actionMetaData(actionRequest);
        log.info("Executed metadata action: disable");

        // Test updating document metadata
        DocumentMetaDataUpdateRequest docMetadataRequest = new DocumentMetaDataUpdateRequest();
        docMetadataRequest.setDatasetId(datasetId);

        List<DocumentMetaDataUpdateRequest.OperationData> operationDataList = new ArrayList<>();
        DocumentMetaDataUpdateRequest.OperationData operationData = new DocumentMetaDataUpdateRequest.OperationData();
        operationData.setDocumentId(documentTextId);

        List<MetaData> dataList = new ArrayList<>();
        MetaData metaData = new MetaData();
        metaData.setId(metaDataId);
        metaData.setType("string");
        metaData.setName("updated-test-metadata");
        metaData.setValue("test-value");
        dataList.add(metaData);

        operationData.setMetadataList(dataList);
        operationDataList.add(operationData);
        docMetadataRequest.setOperationData(operationDataList);

        difyDataset.updateDocumentMetaData(docMetadataRequest);
        log.info("Updated document metadata");

        // Test listing metadata
        MetaDataListResponse listResponse = difyDataset.listMetaData(datasetId);
        assertNotNull(listResponse);
        log.info("Metadata list: {}", JSONUtil.toJsonStr(listResponse));

        // Test deleting metadata
        difyDataset.deleteMetaData(datasetId, metaDataId);
        log.info("Deleted metadata");
    }

    @Test
    @Order(12)
    @DisplayName("Test document deletion")
    public void testDocumentDeletion() {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");
        assertNotNull(documentFileId, "Document File ID should be available from previous test");

        DocumentDeleteResponse response = difyDataset.deleteDocument(datasetId, documentFileId);
        assertNotNull(response);
        log.info("Deleted document: {}", JSONUtil.toJsonStr(response));
    }

    @Test
    @Order(13)
    @DisplayName("Test dataset deletion")
    public void testDatasetDeletion() {
        assertNotNull(datasetId, "Dataset ID should be available from previous test");

        difyDataset.delete(datasetId);
        log.info("Deleted dataset");
    }

    /**
     * Create a retrieval model for document creation/update requests
     */
    private RetrievalModel createRetrievalModel() {
        RetrievalModel model = new RetrievalModel();
        model.setSearchMethod(SearchMethodEnum.hybrid_search);
        model.setRerankingEnable(false);

        RetrievalModel.RerankingModelWeight weights = new RetrievalModel.RerankingModelWeight();

        RetrievalModel.VectorSetting vectorSetting = new RetrievalModel.VectorSetting();
        vectorSetting.setVectorWeight(0.7f);
        vectorSetting.setEmbeddingModelName("bge-m3:latest");
        vectorSetting.setEmbeddingProviderName("langgenius/ollama/ollama");
        weights.setVectorSetting(vectorSetting);

        RetrievalModel.KeywordSetting keywordSetting = new RetrievalModel.KeywordSetting();
        keywordSetting.setKeywordWeight(0.3f);
        weights.setKeywordSetting(keywordSetting);

        model.setWeights(weights);
        model.setTopK(2);
        model.setScoreThresholdEnabled(true);
        model.setScoreThreshold(0.3f);

        return model;
    }

    /**
     * Create a test file for document upload tests
     */
    private static MultipartFile createTestFile(String fileName, String contentType) throws IOException {
        InputStream inputStream = DatasetTest.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            // Create a simple file if the test.txt resource doesn't exist
            String content = "This is a test file content for API testing.";
            return new MockMultipartFile("file", fileName, contentType, content.getBytes());
        }

        byte[] bytes = IOUtils.toByteArray(inputStream);
        return new MockMultipartFile("file", fileName, contentType, bytes);
    }
}
