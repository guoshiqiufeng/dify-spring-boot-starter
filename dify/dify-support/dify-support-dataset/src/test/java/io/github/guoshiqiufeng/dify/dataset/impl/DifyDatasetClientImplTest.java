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
package io.github.guoshiqiufeng.dify.dataset.impl;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.dto.RetrievalModel;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbedding;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.MetaDataActionEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.RerankingModeEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.SearchMethodEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocActionEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyDatasetClientImpl}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/24 10:14
 */
public class DifyDatasetClientImplTest {
    private static DifyDatasetClient difyDatasetClient;
    private static DifyDataset difyDataset;

    @BeforeAll
    public static void setup() {
        difyDatasetClient = Mockito.mock(DifyDatasetClient.class);
        difyDataset = new DifyDatasetClientImpl(difyDatasetClient);
    }

    @Test
    void testCreate() {
        // Arrange
        DatasetCreateRequest request = new DatasetCreateRequest();
        request.setName("Test Dataset");
        request.setDescription("Dataset for testing");
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);

        DatasetResponse expectedResponse = new DatasetResponse();
        expectedResponse.setId("dataset_123");
        expectedResponse.setName("Test Dataset");
        expectedResponse.setDescription("Dataset for testing");

        when(difyDatasetClient.create(any(DatasetCreateRequest.class))).thenReturn(expectedResponse);

        // Act
        DatasetResponse actualResponse = difyDataset.create(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        verify(difyDatasetClient, times(1)).create(any(DatasetCreateRequest.class));
    }

    @Test
    void testPage() {
        // Arrange
        DatasetPageRequest request = new DatasetPageRequest();
        request.setLimit(10);
        request.setPage(1);

        DatasetResponse dataset = new DatasetResponse();
        dataset.setId("dataset_123");
        dataset.setName("Test Dataset");

        DifyPageResult<DatasetResponse> expectedResult = new DifyPageResult<>();
        expectedResult.setData(List.of(dataset));
        expectedResult.setLimit(10);
        expectedResult.setHasMore(false);

        when(difyDatasetClient.page(any(DatasetPageRequest.class))).thenReturn(expectedResult);

        // Act
        DifyPageResult<DatasetResponse> actualResult = difyDataset.page(request);

        // Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult.getLimit(), actualResult.getLimit());
        assertEquals(expectedResult.getHasMore(), actualResult.getHasMore());
        assertEquals(expectedResult.getData().size(), actualResult.getData().size());
        assertEquals(expectedResult.getData().get(0).getId(), actualResult.getData().get(0).getId());
        verify(difyDatasetClient, times(1)).page(any(DatasetPageRequest.class));
    }

    @Test
    void testDelete() {
        // Arrange
        String datasetId = "dataset_123";
        String apiKey = "test_api_key";

        doNothing().when(difyDatasetClient).delete(anyString(), anyString());

        // Act
        difyDataset.delete(datasetId, apiKey);

        // Assert
        verify(difyDatasetClient, times(1)).delete(datasetId, apiKey);
    }

    @Test
    void testDeleteNoApiKey() {
        // Arrange
        String datasetId = "dataset_123";
        String apiKey = "test_api_key";

        doNothing().when(difyDatasetClient).delete(anyString(), any());

        // Act
        difyDataset.delete(datasetId);

        // Assert
        verify(difyDatasetClient, times(1)).delete(datasetId, null);
    }

    @Test
    void testCreateDocumentByText() {
        // Arrange
        DocumentCreateByTextRequest request = new DocumentCreateByTextRequest();
        request.setDatasetId("dataset_123");
        request.setName("Test Document");
        request.setText("This is a test document content");
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);

        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc_456");
        documentInfo.setName("Test Document");

        DocumentCreateResponse expectedResponse = new DocumentCreateResponse();
        expectedResponse.setDocument(documentInfo);
        expectedResponse.setBatch("batch_123");

        when(difyDatasetClient.createDocumentByText(any(DocumentCreateByTextRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        DocumentCreateResponse actualResponse = difyDataset.createDocumentByText(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getDocument());
        assertEquals(expectedResponse.getDocument().getId(), actualResponse.getDocument().getId());
        assertEquals(expectedResponse.getBatch(), actualResponse.getBatch());
        verify(difyDatasetClient, times(1)).createDocumentByText(any(DocumentCreateByTextRequest.class));
    }

    @Test
    void testCreateDocumentByFile() {
        // Arrange
        DocumentCreateByFileRequest request = new DocumentCreateByFileRequest();
        request.setDatasetId("dataset_123");
        request.setOriginalDocumentId("original_doc_123");
        request.setFile(mock(MultipartFile.class));
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);

        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc_789");
        documentInfo.setName("Test File Document");

        DocumentCreateResponse expectedResponse = new DocumentCreateResponse();
        expectedResponse.setDocument(documentInfo);
        expectedResponse.setBatch("batch_456");

        when(difyDatasetClient.createDocumentByFile(any(DocumentCreateByFileRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        DocumentCreateResponse actualResponse = difyDataset.createDocumentByFile(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getDocument());
        assertEquals(expectedResponse.getDocument().getId(), actualResponse.getDocument().getId());
        assertEquals(expectedResponse.getBatch(), actualResponse.getBatch());
        verify(difyDatasetClient, times(1)).createDocumentByFile(any(DocumentCreateByFileRequest.class));
    }

    @Test
    void testUpdateDocumentByText() {
        // Arrange
        DocumentUpdateByTextRequest request = new DocumentUpdateByTextRequest();
        request.setDatasetId("dataset_123");
        request.setDocumentId("doc_456");
        request.setText("Updated content for the test document");
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);

        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc_456");
        documentInfo.setName("Test Document");

        DocumentCreateResponse expectedResponse = new DocumentCreateResponse();
        expectedResponse.setDocument(documentInfo);
        expectedResponse.setBatch("batch_789");

        when(difyDatasetClient.updateDocumentByText(any(DocumentUpdateByTextRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        DocumentCreateResponse actualResponse = difyDataset.updateDocumentByText(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getDocument());
        assertEquals(expectedResponse.getDocument().getId(), actualResponse.getDocument().getId());
        assertEquals(expectedResponse.getBatch(), actualResponse.getBatch());
        verify(difyDatasetClient, times(1)).updateDocumentByText(any(DocumentUpdateByTextRequest.class));
    }

    @Test
    void testUpdateDocumentByFile() {
        // Arrange
        DocumentUpdateByFileRequest request = new DocumentUpdateByFileRequest();
        request.setDatasetId("dataset_123");
        request.setDocumentId("doc_789");
        request.setName("Updated File Document");
        request.setFile(mock(MultipartFile.class));
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);

        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc_789");
        documentInfo.setName("Updated File Document");

        DocumentCreateResponse expectedResponse = new DocumentCreateResponse();
        expectedResponse.setDocument(documentInfo);
        expectedResponse.setBatch("batch_101");

        when(difyDatasetClient.updateDocumentByFile(any(DocumentUpdateByFileRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        DocumentCreateResponse actualResponse = difyDataset.updateDocumentByFile(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getDocument());
        assertEquals(expectedResponse.getDocument().getId(), actualResponse.getDocument().getId());
        assertEquals(expectedResponse.getBatch(), actualResponse.getBatch());
        verify(difyDatasetClient, times(1)).updateDocumentByFile(any(DocumentUpdateByFileRequest.class));
    }

    @Test
    void testPageDocument() {
        // Arrange
        DatasetPageDocumentRequest request = new DatasetPageDocumentRequest();
        request.setDatasetId("dataset_123");
        request.setLimit(20);
        request.setPage(1);

        DocumentInfo document = new DocumentInfo();
        document.setId("doc_456");
        document.setName("Test Document");

        DifyPageResult<DocumentInfo> expectedResult = new DifyPageResult<>();
        expectedResult.setData(List.of(document));
        expectedResult.setLimit(20);
        expectedResult.setHasMore(false);

        when(difyDatasetClient.pageDocument(any(DatasetPageDocumentRequest.class)))
                .thenReturn(expectedResult);

        // Act
        DifyPageResult<DocumentInfo> actualResult = difyDataset.pageDocument(request);

        // Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult.getLimit(), actualResult.getLimit());
        assertEquals(expectedResult.getData().size(), actualResult.getData().size());
        assertEquals(expectedResult.getData().get(0).getId(), actualResult.getData().get(0).getId());
        verify(difyDatasetClient, times(1)).pageDocument(any(DatasetPageDocumentRequest.class));
    }

    @Test
    void testIndexingStatus() {
        // Arrange
        DocumentIndexingStatusRequest request = new DocumentIndexingStatusRequest();
        request.setDatasetId("dataset_123");
        request.setBatch("batch_123");

        DocumentIndexingStatusResponse.ProcessingStatus processingStatus = new DocumentIndexingStatusResponse.ProcessingStatus();
        processingStatus.setId("doc_456");
        processingStatus.setIndexingStatus("completed");
        processingStatus.setCompletedSegments(10);
        processingStatus.setTotalSegments(10);

        DocumentIndexingStatusResponse expectedResponse = new DocumentIndexingStatusResponse();
        expectedResponse.setData(List.of(processingStatus));

        when(difyDatasetClient.indexingStatus(any(DocumentIndexingStatusRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        DocumentIndexingStatusResponse actualResponse = difyDataset.indexingStatus(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(1, actualResponse.getData().size());
        assertEquals(processingStatus.getId(), actualResponse.getData().get(0).getId());
        assertEquals(processingStatus.getIndexingStatus(), actualResponse.getData().get(0).getIndexingStatus());
        verify(difyDatasetClient, times(1)).indexingStatus(any(DocumentIndexingStatusRequest.class));
    }

    @Test
    void testDeleteDocument() {
        // Arrange
        String datasetId = "dataset_123";
        String documentId = "doc_456";
        String apiKey = "test_api_key";
        // Act
        difyDataset.deleteDocument(datasetId, documentId, apiKey);

        // Assert
        verify(difyDatasetClient, times(1)).deleteDocument(datasetId, documentId, apiKey);
    }

    @Test
    void testDeleteDocumentNoApiKey() {
        // Arrange
        String datasetId = "dataset_123";
        String documentId = "doc_456";
        // Act
        difyDataset.deleteDocument(datasetId, documentId);

        // Assert
        verify(difyDatasetClient, times(1)).deleteDocument(datasetId, documentId, null);
    }

    @Test
    void testCreateSegment() {
        // Arrange
        SegmentCreateRequest request = new SegmentCreateRequest();
        request.setDatasetId("dataset_123");
        request.setDocumentId("doc_456");
        request.setApiKey("test_api_key");

        SegmentParam segmentParam = new SegmentParam();
        segmentParam.setContent("Segment content");
        segmentParam.setAnswer("Segment answer");
        segmentParam.setKeywords(List.of("keyword1", "keyword2"));
        request.setSegments(List.of(segmentParam));

        SegmentData segmentData = new SegmentData();
        segmentData.setId("seg_789");
        segmentData.setContent("Segment content");

        SegmentResponse expectedResponse = new SegmentResponse();
        expectedResponse.setData(List.of(segmentData));
        expectedResponse.setDocForm("text");

        when(difyDatasetClient.createSegment(any(SegmentCreateRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        SegmentResponse actualResponse = difyDataset.createSegment(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(1, actualResponse.getData().size());
        assertEquals(segmentData.getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getDocForm(), actualResponse.getDocForm());
        verify(difyDatasetClient, times(1)).createSegment(any(SegmentCreateRequest.class));
    }

    @Test
    void testPageSegment() {
        // Arrange
        SegmentPageRequest request = new SegmentPageRequest();
        request.setDatasetId("dataset_123");
        request.setDocumentId("doc_456");
        request.setApiKey("test_api_key");
        request.setKeyword("search term");
        request.setStatus("enabled");

        SegmentData segmentData = new SegmentData();
        segmentData.setId("seg_789");
        segmentData.setContent("Segment content");

        SegmentResponse expectedResponse = new SegmentResponse();
        expectedResponse.setData(List.of(segmentData));
        expectedResponse.setDocForm("text");

        when(difyDatasetClient.pageSegment(any(SegmentPageRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        SegmentResponse actualResponse = difyDataset.pageSegment(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(1, actualResponse.getData().size());
        assertEquals(segmentData.getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getDocForm(), actualResponse.getDocForm());
        verify(difyDatasetClient, times(1)).pageSegment(any(SegmentPageRequest.class));
    }

    @Test
    void testDeleteSegment() {
        // Arrange
        String datasetId = "dataset_123";
        String documentId = "doc_456";
        String segmentId = "seg_789";
        String apiKey = "test_api_key";

        // Act
        difyDataset.deleteSegment(datasetId, documentId, segmentId, apiKey);

        // Assert
        verify(difyDatasetClient, times(1)).deleteSegment(datasetId, documentId, segmentId, apiKey);
    }

    @Test
    void testDeleteSegmentNoApiKey() {
        // Arrange
        String datasetId = "dataset_123";
        String documentId = "doc_456";
        String segmentId = "seg_789";

        // Act
        difyDataset.deleteSegment(datasetId, documentId, segmentId);

        // Assert
        verify(difyDatasetClient, times(1)).deleteSegment(datasetId, documentId, segmentId, null);
    }

    @Test
    void testUpdateSegment() {
        // Arrange
        SegmentUpdateRequest request = new SegmentUpdateRequest();
        request.setDatasetId("dataset_123");
        request.setDocumentId("doc_456");
        request.setSegmentId("seg_789");
        request.setApiKey("test_api_key");

        SegmentUpdateParam segmentParam = new SegmentUpdateParam();
        segmentParam.setContent("Updated segment content");
        segmentParam.setAnswer("Updated segment answer");
        segmentParam.setKeywords(List.of("updated_keyword1", "updated_keyword2"));
        segmentParam.setEnabled(true);
        segmentParam.setRegenerateChildChunks(true);
        request.setSegment(segmentParam);

        SegmentData segmentData = new SegmentData();
        segmentData.setId("seg_789");
        segmentData.setContent("Updated segment content");

        SegmentUpdateResponse expectedResponse = new SegmentUpdateResponse();
        expectedResponse.setData(segmentData);
        expectedResponse.setDocForm("text");

        when(difyDatasetClient.updateSegment(any(SegmentUpdateRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        SegmentUpdateResponse actualResponse = difyDataset.updateSegment(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(segmentData.getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getDocForm(), actualResponse.getDocForm());
        verify(difyDatasetClient, times(1)).updateSegment(any(SegmentUpdateRequest.class));
    }

    @Test
    void testCreateSegmentChildChunk() {
        // Arrange
        SegmentChildChunkCreateRequest request = new SegmentChildChunkCreateRequest();
        request.setDatasetId("dataset_123");
        request.setDocumentId("doc_456");
        request.setSegmentId("seg_789");
        request.setContent("Child chunk content");
        request.setApiKey("test_api_key");

        SegmentChildChunkResponse childChunk = new SegmentChildChunkResponse();
        childChunk.setId("chunk_123");
        childChunk.setContent("Child chunk content");

        SegmentChildChunkCreateResponse expectedResponse = new SegmentChildChunkCreateResponse();
        expectedResponse.setData(childChunk);

        when(difyDatasetClient.createSegmentChildChunk(any(SegmentChildChunkCreateRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        SegmentChildChunkCreateResponse actualResponse = difyDataset.createSegmentChildChunk(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getData().getContent(), actualResponse.getData().getContent());
        verify(difyDatasetClient, times(1)).createSegmentChildChunk(any(SegmentChildChunkCreateRequest.class));
    }

    @Test
    void testPageSegmentChildChunk() {
        // Arrange
        SegmentChildChunkPageRequest request = new SegmentChildChunkPageRequest();
        request.setDatasetId("dataset_123");
        request.setDocumentId("doc_456");
        request.setSegmentId("seg_789");
        request.setLimit(10);
        request.setPage(1);
        request.setApiKey("test_api_key");

        SegmentChildChunkResponse childChunk = new SegmentChildChunkResponse();
        childChunk.setId("chunk_123");
        childChunk.setContent("Child chunk content");

        DifyPageResult<SegmentChildChunkResponse> expectedResult = new DifyPageResult<>();
        expectedResult.setData(List.of(childChunk));
        expectedResult.setLimit(10);
        expectedResult.setHasMore(false);

        when(difyDatasetClient.pageSegmentChildChunk(any(SegmentChildChunkPageRequest.class)))
                .thenReturn(expectedResult);

        // Act
        DifyPageResult<SegmentChildChunkResponse> actualResult = difyDataset.pageSegmentChildChunk(request);

        // Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult.getLimit(), actualResult.getLimit());
        assertEquals(expectedResult.getData().size(), actualResult.getData().size());
        assertEquals(expectedResult.getData().get(0).getId(), actualResult.getData().get(0).getId());
        verify(difyDatasetClient, times(1)).pageSegmentChildChunk(any(SegmentChildChunkPageRequest.class));
    }

    @Test
    void testDeleteSegmentChildChunk() {
        // Arrange
        SegmentChildChunkDeleteRequest request = new SegmentChildChunkDeleteRequest();
        request.setDatasetId("dataset_123");
        request.setDocumentId("doc_456");
        request.setSegmentId("seg_789");
        request.setChildChunkId("chunk_123");
        request.setApiKey("test_api_key");

        // Act
        difyDataset.deleteSegmentChildChunk(request);

        // Assert
        verify(difyDatasetClient, times(1)).deleteSegmentChildChunk(any(SegmentChildChunkDeleteRequest.class));
    }

    @Test
    void testUpdateSegmentChildChunk() {
        // Arrange
        SegmentChildChunkUpdateRequest request = new SegmentChildChunkUpdateRequest();
        request.setDatasetId("dataset_123");
        request.setDocumentId("doc_456");
        request.setSegmentId("seg_789");
        request.setChildChunkId("chunk_123");
        request.setContent("Updated child chunk content");
        request.setApiKey("test_api_key");

        SegmentChildChunkResponse childChunk = new SegmentChildChunkResponse();
        childChunk.setId("chunk_123");
        childChunk.setContent("Updated child chunk content");

        SegmentChildChunkUpdateResponse expectedResponse = new SegmentChildChunkUpdateResponse();
        expectedResponse.setData(childChunk);

        when(difyDatasetClient.updateSegmentChildChunk(any(SegmentChildChunkUpdateRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        SegmentChildChunkUpdateResponse actualResponse = difyDataset.updateSegmentChildChunk(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getData().getContent(), actualResponse.getData().getContent());
        verify(difyDatasetClient, times(1)).updateSegmentChildChunk(any(SegmentChildChunkUpdateRequest.class));
    }

    @Test
    void testUploadFileInfo() {
        // Arrange
        String datasetId = "dataset_123";
        String documentId = "doc_456";
        String apiKey = "test_api_key";

        UploadFileInfoResponse expectedResponse = new UploadFileInfoResponse();
        expectedResponse.setId("file_789");
        expectedResponse.setName("test-file.pdf");
        expectedResponse.setSize(1024);
        expectedResponse.setExtension("pdf");
        expectedResponse.setUrl("https://example.com/files/file_789");
        expectedResponse.setDownloadUrl("https://example.com/files/download/file_789");
        expectedResponse.setMimeType("application/pdf");
        expectedResponse.setCreatedBy("user123");
        expectedResponse.setCreatedAt(System.currentTimeMillis());

        when(difyDatasetClient.uploadFileInfo(anyString(), anyString(), anyString()))
                .thenReturn(expectedResponse);

        // Act
        UploadFileInfoResponse actualResponse = difyDataset.uploadFileInfo(datasetId, documentId, apiKey);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getSize(), actualResponse.getSize());
        assertEquals(expectedResponse.getExtension(), actualResponse.getExtension());
        assertEquals(expectedResponse.getUrl(), actualResponse.getUrl());
        assertEquals(expectedResponse.getDownloadUrl(), actualResponse.getDownloadUrl());
        assertEquals(expectedResponse.getMimeType(), actualResponse.getMimeType());
        assertEquals(expectedResponse.getCreatedBy(), actualResponse.getCreatedBy());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
        verify(difyDatasetClient, times(1)).uploadFileInfo(datasetId, documentId, apiKey);
    }

    @Test
    void testUploadFileInfoNoApiKey() {
        // Arrange
        String datasetId = "dataset_123";
        String documentId = "doc_456";
        String apiKey = "test_api_key";

        UploadFileInfoResponse expectedResponse = new UploadFileInfoResponse();
        expectedResponse.setId("file_789");
        expectedResponse.setName("test-file.pdf");
        expectedResponse.setSize(1024);
        expectedResponse.setExtension("pdf");
        expectedResponse.setUrl("https://example.com/files/file_789");
        expectedResponse.setDownloadUrl("https://example.com/files/download/file_789");
        expectedResponse.setMimeType("application/pdf");
        expectedResponse.setCreatedBy("user123");
        expectedResponse.setCreatedAt(System.currentTimeMillis());

        when(difyDatasetClient.uploadFileInfo(anyString(), anyString(), any()))
                .thenReturn(expectedResponse);

        // Act
        UploadFileInfoResponse actualResponse = difyDataset.uploadFileInfo(datasetId, documentId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getSize(), actualResponse.getSize());
        assertEquals(expectedResponse.getExtension(), actualResponse.getExtension());
        assertEquals(expectedResponse.getUrl(), actualResponse.getUrl());
        assertEquals(expectedResponse.getDownloadUrl(), actualResponse.getDownloadUrl());
        assertEquals(expectedResponse.getMimeType(), actualResponse.getMimeType());
        assertEquals(expectedResponse.getCreatedBy(), actualResponse.getCreatedBy());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
        verify(difyDatasetClient, times(1)).uploadFileInfo(datasetId, documentId, null);
    }

    @Test
    void testRetrieve() {
        // Arrange
        RetrieveRequest request = new RetrieveRequest();
        request.setQuery("Test query");

        // 创建检索模型设置
        RetrievalModel retrievalModel = new RetrievalModel();
        retrievalModel.setSearchMethod(SearchMethodEnum.hybrid_search);
        retrievalModel.setTopK(5);
        retrievalModel.setRerankingEnable(true);
        retrievalModel.setRerankingMode(RerankingModeEnum.weighted_score);
        request.setRetrievalModel(retrievalModel);

        request.setDatasetId("dataset_123");
        request.setApiKey("test_api_key");

        // 创建响应内容
        RetrieveResponse expectedResponse = new RetrieveResponse();

        // 设置查询信息
        RetrieveResponse.RetrieveQuery query = new RetrieveResponse.RetrieveQuery();
        query.setContent("Test query");
        expectedResponse.setQuery(query);

        // 设置检索记录
        List<RetrieveResponse.RetrieveRecord> records = new ArrayList<>();

        // 创建检索片段
        RetrieveResponse.Segment segment = new RetrieveResponse.Segment();
        segment.setId("seg_123");
        segment.setPosition(1);
        segment.setDocumentId("doc_456");
        segment.setContent("Relevant content");
        segment.setKeywords(List.of("test", "query", "relevant"));
        segment.setWordCount(3);
        segment.setTokens(15);
        segment.setEnabled("true");
        segment.setStatus("available");

        // 创建文档信息
        RetrieveResponse.Document document = new RetrieveResponse.Document();
        document.setId("doc_456");
        document.setDataSourceType("text");
        document.setName("Test Document");
        document.setDocType("pdf");
        segment.setDocument(document);

        // 创建检索记录
        RetrieveResponse.RetrieveRecord record = new RetrieveResponse.RetrieveRecord();
        record.setSegment(segment);
        record.setScore(0.95f);

        // 创建TSNE位置
        RetrieveResponse.TsnePosition tsnePosition = new RetrieveResponse.TsnePosition();
        tsnePosition.setX(0.5f);
        tsnePosition.setY(0.7f);
        record.setTsnePosition(tsnePosition);

        records.add(record);
        expectedResponse.setRecords(records);

        when(difyDatasetClient.retrieve(any(RetrieveRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        RetrieveResponse actualResponse = difyDataset.retrieve(request);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getQuery());
        assertEquals("Test query", actualResponse.getQuery().getContent());

        assertNotNull(actualResponse.getRecords());
        assertEquals(1, actualResponse.getRecords().size());

        RetrieveResponse.RetrieveRecord actualRecord = actualResponse.getRecords().get(0);
        assertEquals(0.95f, actualRecord.getScore());
        assertNotNull(actualRecord.getTsnePosition());
        assertEquals(0.5f, actualRecord.getTsnePosition().getX());
        assertEquals(0.7f, actualRecord.getTsnePosition().getY());

        RetrieveResponse.Segment actualSegment = actualRecord.getSegment();
        assertNotNull(actualSegment);
        assertEquals("seg_123", actualSegment.getId());
        assertEquals("Relevant content", actualSegment.getContent());
        assertEquals("doc_456", actualSegment.getDocumentId());

        assertNotNull(actualSegment.getDocument());
        assertEquals("doc_456", actualSegment.getDocument().getId());
        assertEquals("Test Document", actualSegment.getDocument().getName());

        verify(difyDatasetClient, times(1)).retrieve(any(RetrieveRequest.class));
    }

    @Test
    void testCreateMetaData() {
        // Arrange
        MetaDataCreateRequest request = new MetaDataCreateRequest();
        request.setDatasetId("dataset_123");
        request.setName("category");
        request.setType("string");
        request.setApiKey("test_api_key");

        // 注意：根据MetaDataCreateRequest.java的定义，它不包含setMetaData方法
        // 类中只有datasetId, type和name字段

        MetaDataResponse expectedResponse = new MetaDataResponse();
        expectedResponse.setId("meta_123");
        expectedResponse.setName("category");

        when(difyDatasetClient.createMetaData(any(MetaDataCreateRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        MetaDataResponse actualResponse = difyDataset.createMetaData(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        verify(difyDatasetClient, times(1)).createMetaData(any(MetaDataCreateRequest.class));
    }

    @Test
    void testUpdateMetaData() {
        // Arrange
        MetaDataUpdateRequest request = new MetaDataUpdateRequest();
        request.setDatasetId("dataset_123");
        request.setMetaDataId("meta_123");
        request.setName("Updated Metadata");
        request.setApiKey("test_api_key");

        MetaDataResponse expectedResponse = new MetaDataResponse();
        expectedResponse.setId("meta_123");
        expectedResponse.setName("Updated Metadata");

        when(difyDatasetClient.updateMetaData(any(MetaDataUpdateRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        MetaDataResponse actualResponse = difyDataset.updateMetaData(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        verify(difyDatasetClient, times(1)).updateMetaData(any(MetaDataUpdateRequest.class));
    }

    @Test
    void testDeleteMetaData() {
        // Arrange
        String datasetId = "dataset_123";
        String metadataId = "meta_123";
        String apiKey = "test_api_key";

        doNothing().when(difyDatasetClient).deleteMetaData(anyString(), anyString(), anyString());

        // Act
        difyDataset.deleteMetaData(datasetId, metadataId, apiKey);

        // Assert
        verify(difyDatasetClient, times(1)).deleteMetaData(datasetId, metadataId, apiKey);
    }

    @Test
    void testDeleteMetaDataNoApiKey() {
        // Arrange
        String datasetId = "dataset_123";
        String metadataId = "meta_123";
        String apiKey = "test_api_key";

        doNothing().when(difyDatasetClient).deleteMetaData(anyString(), anyString(), any());

        // Act
        difyDataset.deleteMetaData(datasetId, metadataId);

        // Assert
        verify(difyDatasetClient, times(1)).deleteMetaData(datasetId, metadataId, null);
    }

    @Test
    void testActionMetaData() {
        // Arrange
        MetaDataActionRequest request = new MetaDataActionRequest();
        request.setDatasetId("dataset_123");
        request.setAction(MetaDataActionEnum.enable);
        request.setApiKey("test_api_key");

        doNothing().when(difyDatasetClient).actionMetaData(any(MetaDataActionRequest.class));

        // Act
        difyDataset.actionMetaData(request);

        // Assert
        verify(difyDatasetClient, times(1)).actionMetaData(any(MetaDataActionRequest.class));
    }

    @Test
    void testUpdateDocumentMetaData() {
        // Arrange
        DocumentMetaDataUpdateRequest request = new DocumentMetaDataUpdateRequest();
        request.setDatasetId("dataset_123");
        request.setApiKey("test_api_key");

        // 创建操作数据
        DocumentMetaDataUpdateRequest.OperationData operationData = new DocumentMetaDataUpdateRequest.OperationData();
        operationData.setDocumentId("doc_456");

        // 添加元数据列表
        List<MetaData> metadataList = new ArrayList<>();
        MetaData metaData = new MetaData();
        metaData.setId("meta_123");
        metaData.setType("string");
        metaData.setName("category");
        metaData.setValue("Technical");
        metadataList.add(metaData);

        operationData.setMetadataList(metadataList);

        // 设置操作数据
        request.setOperationData(List.of(operationData));

        doNothing().when(difyDatasetClient).updateDocumentMetaData(any(DocumentMetaDataUpdateRequest.class));

        // Act
        difyDataset.updateDocumentMetaData(request);

        // Assert
        verify(difyDatasetClient, times(1)).updateDocumentMetaData(any(DocumentMetaDataUpdateRequest.class));
    }

    @Test
    void testListMetaData() {
        // Arrange
        String datasetId = "dataset_123";
        String apiKey = "test_api_key";

        MetaDataListResponse expectedResponse = new MetaDataListResponse();
        expectedResponse.setBuiltInFieldEnabled(true);

        // 创建文档元数据列表
        List<MetaDataListResponse.DocMetadata> docMetadataList = new ArrayList<>();

        MetaDataListResponse.DocMetadata docMetadata = new MetaDataListResponse.DocMetadata();
        docMetadata.setId("meta_123");
        docMetadata.setType("string");
        docMetadata.setName("category");
        docMetadata.setUserCount(5);

        docMetadataList.add(docMetadata);
        expectedResponse.setDocMetadata(docMetadataList);

        when(difyDatasetClient.listMetaData(anyString(), anyString()))
                .thenReturn(expectedResponse);

        // Act
        MetaDataListResponse actualResponse = difyDataset.listMetaData(datasetId, apiKey);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getBuiltInFieldEnabled(), actualResponse.getBuiltInFieldEnabled());
        assertNotNull(actualResponse.getDocMetadata());
        assertEquals(expectedResponse.getDocMetadata().size(), actualResponse.getDocMetadata().size());
        assertEquals(expectedResponse.getDocMetadata().get(0).getId(), actualResponse.getDocMetadata().get(0).getId());
        assertEquals(expectedResponse.getDocMetadata().get(0).getName(), actualResponse.getDocMetadata().get(0).getName());
        assertEquals(expectedResponse.getDocMetadata().get(0).getType(), actualResponse.getDocMetadata().get(0).getType());
        assertEquals(expectedResponse.getDocMetadata().get(0).getUserCount(), actualResponse.getDocMetadata().get(0).getUserCount());
        verify(difyDatasetClient, times(1)).listMetaData(datasetId, apiKey);
    }

    @Test
    void testListMetaDataNoApiKey() {
        // Arrange
        String datasetId = "dataset_123";
        String apiKey = "test_api_key";

        MetaDataListResponse expectedResponse = new MetaDataListResponse();
        expectedResponse.setBuiltInFieldEnabled(true);

        // 创建文档元数据列表
        List<MetaDataListResponse.DocMetadata> docMetadataList = new ArrayList<>();

        MetaDataListResponse.DocMetadata docMetadata = new MetaDataListResponse.DocMetadata();
        docMetadata.setId("meta_123");
        docMetadata.setType("string");
        docMetadata.setName("category");
        docMetadata.setUserCount(5);

        docMetadataList.add(docMetadata);
        expectedResponse.setDocMetadata(docMetadataList);

        when(difyDatasetClient.listMetaData(anyString(), any()))
                .thenReturn(expectedResponse);

        // Act
        MetaDataListResponse actualResponse = difyDataset.listMetaData(datasetId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getBuiltInFieldEnabled(), actualResponse.getBuiltInFieldEnabled());
        assertNotNull(actualResponse.getDocMetadata());
        assertEquals(expectedResponse.getDocMetadata().size(), actualResponse.getDocMetadata().size());
        assertEquals(expectedResponse.getDocMetadata().get(0).getId(), actualResponse.getDocMetadata().get(0).getId());
        assertEquals(expectedResponse.getDocMetadata().get(0).getName(), actualResponse.getDocMetadata().get(0).getName());
        assertEquals(expectedResponse.getDocMetadata().get(0).getType(), actualResponse.getDocMetadata().get(0).getType());
        assertEquals(expectedResponse.getDocMetadata().get(0).getUserCount(), actualResponse.getDocMetadata().get(0).getUserCount());
        verify(difyDatasetClient, times(1)).listMetaData(datasetId, null);
    }

    @Test
    void testListTextEmbedding() {
        // Arrange
        String apiKey = "test_api_key";

        List<TextEmbedding> embeddingList = new ArrayList<>();

        TextEmbedding embedding1 = new TextEmbedding();
        embedding1.setProvider("openai");
        embedding1.setStatus("active");

        TextEmbedding embedding2 = new TextEmbedding();
        embedding2.setProvider("anthropic");
        embedding2.setStatus("active");

        embeddingList.add(embedding1);
        embeddingList.add(embedding2);

        TextEmbeddingListResponse expectedResponse = new TextEmbeddingListResponse();
        expectedResponse.setData(embeddingList);

        when(difyDatasetClient.listTextEmbedding(anyString()))
                .thenReturn(expectedResponse);

        // Act
        TextEmbeddingListResponse actualResponse = difyDataset.listTextEmbedding(apiKey);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals("openai", actualResponse.getData().get(0).getProvider());
        assertEquals("anthropic", actualResponse.getData().get(1).getProvider());
        assertEquals("active", actualResponse.getData().get(0).getStatus());
        verify(difyDatasetClient, times(1)).listTextEmbedding(apiKey);
    }

    @Test
    void testListRerank() {
        // Arrange
        String apiKey = "test_api_key";

        List<TextEmbedding> embeddingList = new ArrayList<>();

        TextEmbedding embedding1 = new TextEmbedding();
        embedding1.setProvider("openai");
        embedding1.setStatus("active");

        TextEmbedding embedding2 = new TextEmbedding();
        embedding2.setProvider("anthropic");
        embedding2.setStatus("active");

        embeddingList.add(embedding1);
        embeddingList.add(embedding2);

        TextEmbeddingListResponse expectedResponse = new TextEmbeddingListResponse();
        expectedResponse.setData(embeddingList);

        when(difyDatasetClient.listRerank(anyString()))
                .thenReturn(expectedResponse);

        // Act
        TextEmbeddingListResponse actualResponse = difyDataset.listRerank(apiKey);

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals("openai", actualResponse.getData().get(0).getProvider());
        assertEquals("anthropic", actualResponse.getData().get(1).getProvider());
        assertEquals("active", actualResponse.getData().get(0).getStatus());
        verify(difyDatasetClient, times(1)).listRerank(apiKey);
    }

    @Test
    void testListRerankForNoArgs() {
        // Arrange
        String apiKey = "test_api_key";

        List<TextEmbedding> embeddingList = new ArrayList<>();

        TextEmbedding embedding1 = new TextEmbedding();
        embedding1.setProvider("openai");
        embedding1.setStatus("active");

        TextEmbedding embedding2 = new TextEmbedding();
        embedding2.setProvider("anthropic");
        embedding2.setStatus("active");

        embeddingList.add(embedding1);
        embeddingList.add(embedding2);

        TextEmbeddingListResponse expectedResponse = new TextEmbeddingListResponse();
        expectedResponse.setData(embeddingList);

        when(difyDatasetClient.listRerank(null))
                .thenReturn(expectedResponse);

        // Act
        TextEmbeddingListResponse actualResponse = difyDataset.listRerank();

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals("openai", actualResponse.getData().get(0).getProvider());
        assertEquals("anthropic", actualResponse.getData().get(1).getProvider());
        assertEquals("active", actualResponse.getData().get(0).getStatus());
        verify(difyDatasetClient, times(1)).listRerank(null);
    }

    @Test
    void testListTextEmbeddingNoApiKey() {
        // Arrange
        String apiKey = "test_api_key";

        List<TextEmbedding> embeddingList = new ArrayList<>();

        TextEmbedding embedding1 = new TextEmbedding();
        embedding1.setProvider("openai");
        embedding1.setStatus("active");

        TextEmbedding embedding2 = new TextEmbedding();
        embedding2.setProvider("anthropic");
        embedding2.setStatus("active");

        embeddingList.add(embedding1);
        embeddingList.add(embedding2);

        TextEmbeddingListResponse expectedResponse = new TextEmbeddingListResponse();
        expectedResponse.setData(embeddingList);

        when(difyDatasetClient.listTextEmbedding(any()))
                .thenReturn(expectedResponse);

        // Act
        TextEmbeddingListResponse actualResponse = difyDataset.listTextEmbedding();

        // Assert
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals("openai", actualResponse.getData().get(0).getProvider());
        assertEquals("anthropic", actualResponse.getData().get(1).getProvider());
        assertEquals("active", actualResponse.getData().get(0).getStatus());
        verify(difyDatasetClient, times(1)).listTextEmbedding(null);
    }

    @Test
    void testInfo() {
        // Arrange
        DatasetInfoRequest request = new DatasetInfoRequest();
        request.setDatasetId("dataset_123");
        request.setApiKey("test_api_key");

        DatasetInfoResponse expectedResponse = new DatasetInfoResponse();
        expectedResponse.setId("dataset_123");
        expectedResponse.setName("Test Dataset");
        expectedResponse.setDescription("Dataset for testing");
        expectedResponse.setCreatedAt(System.currentTimeMillis());
        expectedResponse.setDocumentCount(10);

        when(difyDatasetClient.info(any(DatasetInfoRequest.class))).thenReturn(expectedResponse);

        // Act
        DatasetInfoResponse actualResponse = difyDataset.info(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
        assertEquals(expectedResponse.getDocumentCount(), actualResponse.getDocumentCount());
        verify(difyDatasetClient, times(1)).info(any(DatasetInfoRequest.class));
    }

    @Test
    void testUpdate() {
        // Arrange
        DatasetUpdateRequest request = new DatasetUpdateRequest();
        request.setDatasetId("dataset_123");
        request.setApiKey("test_api_key");

        DatasetInfoResponse expectedResponse = new DatasetInfoResponse();
        expectedResponse.setId("dataset_123");
        expectedResponse.setName("Updated Dataset");
        expectedResponse.setDescription("Updated description for testing");
        expectedResponse.setCreatedAt(System.currentTimeMillis());
        expectedResponse.setDocumentCount(10);

        when(difyDatasetClient.update(any(DatasetUpdateRequest.class))).thenReturn(expectedResponse);

        // Act
        DatasetInfoResponse actualResponse = difyDataset.update(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
        assertEquals(expectedResponse.getDocumentCount(), actualResponse.getDocumentCount());
        verify(difyDatasetClient, times(1)).update(any(DatasetUpdateRequest.class));
    }

    @Test
    void testCreateTag() {
        // Arrange
        TagCreateRequest request = new TagCreateRequest();
        request.setName("Test Tag");
        request.setApiKey("test_api_key");

        TagInfoResponse expectedResponse = new TagInfoResponse();
        expectedResponse.setId("tag_123");
        expectedResponse.setName("Test Tag");
        expectedResponse.setType("custom");
        expectedResponse.setBindingCount(0);

        when(difyDatasetClient.createTag(any(TagCreateRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        TagInfoResponse actualResponse = difyDataset.createTag(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getType(), actualResponse.getType());
        assertEquals(expectedResponse.getBindingCount(), actualResponse.getBindingCount());
        verify(difyDatasetClient, times(1)).createTag(any(TagCreateRequest.class));
    }

    @Test
    void testListTag() {
        // Arrange
        String apiKey = "test_api_key";

        TagInfoResponse tag1 = new TagInfoResponse();
        tag1.setId("tag_123");
        tag1.setName("Test Tag 1");
        tag1.setType("custom");
        tag1.setBindingCount(2);

        TagInfoResponse tag2 = new TagInfoResponse();
        tag2.setId("tag_456");
        tag2.setName("Test Tag 2");
        tag2.setType("system");
        tag2.setBindingCount(5);

        List<TagInfoResponse> expectedResponse = List.of(tag1, tag2);

        when(difyDatasetClient.listTag(anyString()))
                .thenReturn(expectedResponse);

        // Act
        List<TagInfoResponse> actualResponse = difyDataset.listTag(apiKey);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.size(), actualResponse.size());
        assertEquals(expectedResponse.get(0).getId(), actualResponse.get(0).getId());
        assertEquals(expectedResponse.get(0).getName(), actualResponse.get(0).getName());
        assertEquals(expectedResponse.get(0).getType(), actualResponse.get(0).getType());
        assertEquals(expectedResponse.get(0).getBindingCount(), actualResponse.get(0).getBindingCount());
        verify(difyDatasetClient, times(1)).listTag(apiKey);
    }

    @Test
    void testListTagNoApiKey() {
        // Arrange
        String apiKey = "test_api_key";

        TagInfoResponse tag1 = new TagInfoResponse();
        tag1.setId("tag_123");
        tag1.setName("Test Tag 1");
        tag1.setType("custom");
        tag1.setBindingCount(2);

        TagInfoResponse tag2 = new TagInfoResponse();
        tag2.setId("tag_456");
        tag2.setName("Test Tag 2");
        tag2.setType("system");
        tag2.setBindingCount(5);

        List<TagInfoResponse> expectedResponse = List.of(tag1, tag2);

        when(difyDatasetClient.listTag(any()))
                .thenReturn(expectedResponse);

        // Act
        List<TagInfoResponse> actualResponse = difyDataset.listTag();

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.size(), actualResponse.size());
        assertEquals(expectedResponse.get(0).getId(), actualResponse.get(0).getId());
        assertEquals(expectedResponse.get(0).getName(), actualResponse.get(0).getName());
        assertEquals(expectedResponse.get(0).getType(), actualResponse.get(0).getType());
        assertEquals(expectedResponse.get(0).getBindingCount(), actualResponse.get(0).getBindingCount());
        verify(difyDatasetClient, times(1)).listTag(null);
    }

    @Test
    void testUpdateTag() {
        // Arrange
        TagUpdateRequest request = new TagUpdateRequest();
        request.setTagId("tag_123");
        request.setName("Updated Test Tag");
        request.setApiKey("test_api_key");

        TagInfoResponse expectedResponse = new TagInfoResponse();
        expectedResponse.setId("tag_123");
        expectedResponse.setName("Updated Test Tag");
        expectedResponse.setType("custom");
        expectedResponse.setBindingCount(3);

        when(difyDatasetClient.updateTag(any(TagUpdateRequest.class)))
                .thenReturn(expectedResponse);

        // Act
        TagInfoResponse actualResponse = difyDataset.updateTag(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getType(), actualResponse.getType());
        assertEquals(expectedResponse.getBindingCount(), actualResponse.getBindingCount());
        verify(difyDatasetClient, times(1)).updateTag(any(TagUpdateRequest.class));
    }

    @Test
    void testDeleteTag() {
        // Arrange
        String tagId = "tag_123";
        String apiKey = "test_api_key";

        doNothing().when(difyDatasetClient).deleteTag(anyString(), anyString());

        // Act
        difyDataset.deleteTag(tagId, apiKey);

        // Assert
        verify(difyDatasetClient, times(1)).deleteTag(tagId, apiKey);
    }

    @Test
    void testDeleteTagNoApiKey() {
        // Arrange
        String tagId = "tag_123";
        String apiKey = "test_api_key";

        doNothing().when(difyDatasetClient).deleteTag(anyString(), any());

        // Act
        difyDataset.deleteTag(tagId);

        // Assert
        verify(difyDatasetClient, times(1)).deleteTag(tagId, null);
    }

    @Test
    void testBindingTag() {
        // Arrange
        TagBindingRequest request = new TagBindingRequest();
        request.setTagIds(List.of("tag_123", "tag_456"));
        request.setTargetId("dataset_789");
        request.setApiKey("test_api_key");

        doNothing().when(difyDatasetClient).bindingTag(any(TagBindingRequest.class));

        // Act
        difyDataset.bindingTag(request);

        // Assert
        verify(difyDatasetClient, times(1)).bindingTag(any(TagBindingRequest.class));
    }

    @Test
    void testUnbindingTag() {
        // Arrange
        TagUnbindingRequest request = new TagUnbindingRequest();
        request.setTagId("tag_123");
        request.setTargetId("dataset_789");
        request.setApiKey("test_api_key");

        doNothing().when(difyDatasetClient).unbindingTag(any(TagUnbindingRequest.class));

        // Act
        difyDataset.unbindingTag(request);

        // Assert
        verify(difyDatasetClient, times(1)).unbindingTag(any(TagUnbindingRequest.class));
    }

    @Test
    void testListDatasetTag() {
        // Arrange
        String datasetId = "dataset_123";
        String apiKey = "test_api_key";

        DataSetTagInfo tagInfo1 = new DataSetTagInfo();
        tagInfo1.setId("tag_123");
        tagInfo1.setName("Test Tag 1");

        DataSetTagInfo tagInfo2 = new DataSetTagInfo();
        tagInfo2.setId("tag_456");
        tagInfo2.setName("Test Tag 2");

        List<DataSetTagInfo> tagInfoList = List.of(tagInfo1, tagInfo2);

        DataSetTagsResponse expectedResponse = new DataSetTagsResponse();
        expectedResponse.setData(tagInfoList);
        expectedResponse.setTotal(2);

        when(difyDatasetClient.listDatasetTag(anyString(), anyString()))
                .thenReturn(expectedResponse);

        // Act
        DataSetTagsResponse actualResponse = difyDataset.listDatasetTag(datasetId, apiKey);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getTotal(), actualResponse.getTotal());
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getName(), actualResponse.getData().get(0).getName());
        verify(difyDatasetClient, times(1)).listDatasetTag(datasetId, apiKey);
    }

    @Test
    void testListDatasetTagNoApiKey() {
        // Arrange
        String datasetId = "dataset_123";
        String apiKey = "test_api_key";

        DataSetTagInfo tagInfo1 = new DataSetTagInfo();
        tagInfo1.setId("tag_123");
        tagInfo1.setName("Test Tag 1");

        DataSetTagInfo tagInfo2 = new DataSetTagInfo();
        tagInfo2.setId("tag_456");
        tagInfo2.setName("Test Tag 2");

        List<DataSetTagInfo> tagInfoList = List.of(tagInfo1, tagInfo2);

        DataSetTagsResponse expectedResponse = new DataSetTagsResponse();
        expectedResponse.setData(tagInfoList);
        expectedResponse.setTotal(2);

        when(difyDatasetClient.listDatasetTag(anyString(), any()))
                .thenReturn(expectedResponse);

        // Act
        DataSetTagsResponse actualResponse = difyDataset.listDatasetTag(datasetId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getTotal(), actualResponse.getTotal());
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getName(), actualResponse.getData().get(0).getName());
        verify(difyDatasetClient, times(1)).listDatasetTag(datasetId, null);
    }

    @Test
    void testChangeDocumentStatus() {
        // Arrange
        String datasetId = "dataset_123";
        String apiKey = "test_api_key";
        String documentId = "doc_123";

        DatasetStatusResponse expectedResponse = new DatasetStatusResponse();
        expectedResponse.setResult("success");
        when(difyDatasetClient.changeDocumentStatus(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(expectedResponse);

        // Act
        DatasetStatusResponse statusResponse = difyDataset.changeDocumentStatus(datasetId, documentId, DocActionEnum.enable.name(), apiKey);

        // Assert
        assertNotNull(statusResponse);
        assertNotNull(statusResponse.getResult());
        verify(difyDatasetClient, times(1)).listDatasetTag(datasetId, apiKey);
    }
}
