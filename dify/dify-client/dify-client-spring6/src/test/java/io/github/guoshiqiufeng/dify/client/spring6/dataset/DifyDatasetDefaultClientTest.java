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
package io.github.guoshiqiufeng.dify.client.spring6.dataset;

import io.github.guoshiqiufeng.dify.client.spring6.BaseClientTest;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import io.github.guoshiqiufeng.dify.dataset.dto.RetrievalModel;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbedding;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbeddingIcon;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbeddingLabel;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbeddingModel;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.MetaDataActionEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.SearchMethodEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyDatasetDefaultClient}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/21 14:50
 */
@SuppressWarnings("unchecked")
@DisplayName("DifyDatasetDefaultClient Tests")
public class DifyDatasetDefaultClientTest extends BaseClientTest {

    private static final String BASE_URL = "https://api.dify.ai";

    private DifyDatasetDefaultClient client;

    private RestClient restClient;
    private RestClient.RequestBodySpec requestBodySpec;
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    private RestClient.ResponseSpec responseSpec;
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;

    @BeforeEach
    public void setup() {
        super.setup();
        client = new DifyDatasetDefaultClient(BASE_URL, new DifyProperties.ClientConfig(), restClientMock.getRestClientBuilder(), webClientMock.getWebClientBuilder());
        restClient = restClientMock.getRestClient();
        requestBodySpec = restClientMock.getRequestBodySpec();
        requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        responseSpec = restClientMock.getResponseSpec();
        requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        requestHeadersSpec = restClientMock.getRequestHeadersSpec();
    }

    @Test
    @DisplayName("Test create dataset method")
    public void testCreate() {
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Mock the response
        DatasetResponse mockResponse = new DatasetResponse();
        mockResponse.setId("dataset-123");
        mockResponse.setName("Test Dataset");
        mockResponse.setDescription("This is a test dataset");
        when(responseSpec.body(DatasetResponse.class)).thenReturn(mockResponse);

        // Create a dataset request
        DatasetCreateRequest request = new DatasetCreateRequest();
        request.setApiKey("test-api-key");
        request.setName("Test Dataset");
        request.setDescription("This is a test dataset");
        request.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);

        // Call the method to test
        DatasetResponse response = client.create(request);

        // Verify the response
        assertNotNull(response);
        assertEquals("dataset-123", response.getId());
        assertEquals("Test Dataset", response.getName());
        assertEquals("This is a test dataset", response.getDescription());

        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri(DatasetUriConstant.V1_DATASETS_URL);
        verify(requestBodySpec).headers(any(Consumer.class));

        // Capture and verify the request body
        ArgumentCaptor<DatasetCreateRequest> bodyCaptor = ArgumentCaptor.forClass(DatasetCreateRequest.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        DatasetCreateRequest capturedBody = bodyCaptor.getValue();
        assertEquals("Test Dataset", capturedBody.getName());
        assertEquals("This is a test dataset", capturedBody.getDescription());
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, capturedBody.getIndexingTechnique());
    }

    @Test
    @DisplayName("Test page datasets method")
    public void testPage() {
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Mock the response
        DifyPageResult<DatasetResponse> mockResponse = new DifyPageResult<>();
        List<DatasetResponse> data = new ArrayList<>();

        DatasetResponse dataset1 = new DatasetResponse();
        dataset1.setId("dataset-1");
        dataset1.setName("Dataset 1");
        data.add(dataset1);

        DatasetResponse dataset2 = new DatasetResponse();
        dataset2.setId("dataset-2");
        dataset2.setName("Dataset 2");
        data.add(dataset2);

        mockResponse.setData(data);
        mockResponse.setLimit(10);
        mockResponse.setPage(1);
        mockResponse.setTotal(2);

        doReturn(mockResponse).when(responseSpec).body(any(ParameterizedTypeReference.class));

        // Create a page request
        DatasetPageRequest request = new DatasetPageRequest();
        request.setApiKey("test-api-key");
        request.setPage(1);
        request.setLimit(10);

        // Call the method to test
        DifyPageResult<DatasetResponse> response = client.page(request);

        // Verify the response
        assertNotNull(response);
        assertEquals(1, response.getPage());
        assertEquals(10, response.getLimit());
        assertEquals(2, response.getTotal());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals("dataset-1", response.getData().get(0).getId());
        assertEquals("Dataset 1", response.getData().get(0).getName());
        assertEquals("dataset-2", response.getData().get(1).getId());
        assertEquals("Dataset 2", response.getData().get(1).getName());

        // Verify interactions with mocks
        verify(requestHeadersSpec).headers(any(Consumer.class));
    }

    @Test
    @DisplayName("Test delete dataset method")
    public void testDelete() {
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Call the method to test
        String datasetId = "dataset-123";
        String apiKey = "test-api-key";
        client.delete(datasetId, apiKey);

        // Verify interactions with mocks
        verify(requestHeadersUriSpec).uri("/v1/datasets/{datasetId}", datasetId);
        verify(requestHeadersSpec).headers(any(Consumer.class));
        verify(responseSpec).body(Void.class);
    }

    @Test
    @DisplayName("Test create document by text method")
    public void testCreateDocumentByText() {
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Mock the response
        DocumentCreateResponse mockResponse = new DocumentCreateResponse();
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc-123");
        documentInfo.setName("Test Document");
        mockResponse.setDocument(documentInfo);
        mockResponse.setBatch("batch-1");

        when(responseSpec.body(DocumentCreateResponse.class)).thenReturn(mockResponse);

        // Create a document request
        DocumentCreateByTextRequest request = new DocumentCreateByTextRequest();
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");
        request.setName("Test Document");
        request.setText("This is a test document content");

        // Call the method to test
        DocumentCreateResponse response = client.createDocumentByText(request);

        // Verify the response
        assertNotNull(response);
        assertNotNull(response.getDocument());
        assertEquals("doc-123", response.getDocument().getId());
        assertEquals("Test Document", response.getDocument().getName());
        assertEquals("batch-1", response.getBatch());

        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri("/v1/datasets/{datasetId}/document/create-by-text", "dataset-123");
        verify(requestBodySpec).headers(any(Consumer.class));

        // Capture and verify the request body
        ArgumentCaptor<DocumentCreateByTextRequest> bodyCaptor = ArgumentCaptor.forClass(DocumentCreateByTextRequest.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        DocumentCreateByTextRequest capturedBody = bodyCaptor.getValue();
        assertEquals("Test Document", capturedBody.getName());
        assertEquals("This is a test document content", capturedBody.getText());
    }

    @Test
    @DisplayName("Test page documents method")
    public void testPageDocument() {
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Mock the response
        DifyPageResult<DocumentInfo> mockResponse = new DifyPageResult<>();
        List<DocumentInfo> data = new ArrayList<>();

        DocumentInfo doc1 = new DocumentInfo();
        doc1.setId("doc-1");
        doc1.setName("Document 1");
        data.add(doc1);

        DocumentInfo doc2 = new DocumentInfo();
        doc2.setId("doc-2");
        doc2.setName("Document 2");
        data.add(doc2);

        mockResponse.setData(data);
        mockResponse.setLimit(10);
        mockResponse.setPage(1);
        mockResponse.setTotal(2);

        doReturn(mockResponse).when(responseSpec).body(any(ParameterizedTypeReference.class));

        // Create a page document request
        DatasetPageDocumentRequest request = new DatasetPageDocumentRequest();
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");
        request.setPage(1);
        request.setLimit(10);
        request.setKeyword("test");

        // Call the method to test
        DifyPageResult<DocumentInfo> response = client.pageDocument(request);

        // Verify the response
        assertNotNull(response);
        assertEquals(1, response.getPage());
        assertEquals(10, response.getLimit());
        assertEquals(2, response.getTotal());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals("doc-1", response.getData().get(0).getId());
        assertEquals("Document 1", response.getData().get(0).getName());
        assertEquals("doc-2", response.getData().get(1).getId());
        assertEquals("Document 2", response.getData().get(1).getName());

        // Verify interactions with mocks
        verify(requestHeadersUriSpec).uri("/v1/datasets/{datasetId}/documents?page={page}&limit={limit}&keyword={keyword}",
                "dataset-123", 1, 10, "test");
        verify(requestHeadersSpec).headers(any(Consumer.class));
    }

    @Test
    public void testIndexingStatus() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String batch = "batch-123456";

        // Create request
        DocumentIndexingStatusRequest request = new DocumentIndexingStatusRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setBatch(batch);

        // Create expected response
        DocumentIndexingStatusResponse expectedResponse = new DocumentIndexingStatusResponse();
        List<DocumentIndexingStatusResponse.ProcessingStatus> dataList = new ArrayList<>();

        // Create a processing status object
        DocumentIndexingStatusResponse.ProcessingStatus status = new DocumentIndexingStatusResponse.ProcessingStatus();
        status.setId("document-123456");
        status.setIndexingStatus("completed");
        status.setProcessingStartedAt(1650000000L);
        status.setParsingCompletedAt(1650000100L);
        status.setCleaningCompletedAt(1650000200L);
        status.setSplittingCompletedAt(1650000300L);
        status.setCompletedAt(1650000400L);
        status.setCompletedSegments(10);
        status.setTotalSegments(10);

        dataList.add(status);
        expectedResponse.setData(dataList);

        // Set up the response mock to return our expected response
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedResponse);

        // Execute the method
        DocumentIndexingStatusResponse actualResponse = client.indexingStatus(request);

        // Verify the result
        assertNotNull(actualResponse.getData());
        assertEquals(1, actualResponse.getData().size());
        assertEquals("document-123456", actualResponse.getData().get(0).getId());
        assertEquals("completed", actualResponse.getData().get(0).getIndexingStatus());
        assertEquals(Long.valueOf(1650000000L), actualResponse.getData().get(0).getProcessingStartedAt());
        assertEquals(Integer.valueOf(10), actualResponse.getData().get(0).getCompletedSegments());
        assertEquals(Integer.valueOf(10), actualResponse.getData().get(0).getTotalSegments());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(
                eq(DatasetUriConstant.V1_DOCUMENT_INDEXING_STATUS_URL), eq(datasetId), eq(batch));
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testDeleteDocument() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";

        client.deleteDocument(datasetId, documentId, apiKey);

        // Verify WebClient interactions
        verify(restClient).delete();
        verify(requestHeadersUriSpec).uri(eq(DatasetUriConstant.V1_DOCUMENT_URL), eq(datasetId), eq(documentId));
    }

    @Test
    public void testRetrieve() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String query = "test query";
        Integer topK = 5;

        // Create request
        RetrieveRequest request = new RetrieveRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setQuery(query);

        // Configure retrieval model
        RetrievalModel retrievalModel = new RetrievalModel();
        retrievalModel.setTopK(topK);
        retrievalModel.setSearchMethod(SearchMethodEnum.hybrid_search);
        request.setRetrievalModel(retrievalModel);

        // Create expected response
        RetrieveResponse expectedResponse = new RetrieveResponse();

        // Set query
        RetrieveResponse.RetrieveQuery retrieveQuery = new RetrieveResponse.RetrieveQuery();
        retrieveQuery.setContent(query);
        expectedResponse.setQuery(retrieveQuery);

        // Create records
        List<RetrieveResponse.RetrieveRecord> records = new ArrayList<>();
        RetrieveResponse.RetrieveRecord record = new RetrieveResponse.RetrieveRecord();
        record.setScore(0.95f);

        // Set segment
        RetrieveResponse.Segment segment = new RetrieveResponse.Segment();
        segment.setId("segment-123456");
        segment.setDocumentId("document-123456");
        segment.setContent("Document content");

        // Set document within segment
        RetrieveResponse.Document document = new RetrieveResponse.Document();
        document.setId("document-123456");
        document.setName("Test Document");
        segment.setDocument(document);

        record.setSegment(segment);
        records.add(record);
        expectedResponse.setRecords(records);

        // Set up the response mock to return our expected response
        when(responseSpec.body(RetrieveResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        RetrieveResponse actualResponse = client.retrieve(request);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getQuery().getContent(), actualResponse.getQuery().getContent());
        assertEquals(expectedResponse.getRecords().size(), actualResponse.getRecords().size());
        assertEquals(expectedResponse.getRecords().get(0).getSegment().getId(), actualResponse.getRecords().get(0).getSegment().getId());
        assertEquals(expectedResponse.getRecords().get(0).getSegment().getContent(), actualResponse.getRecords().get(0).getSegment().getContent());
        assertEquals(expectedResponse.getRecords().get(0).getScore(), actualResponse.getRecords().get(0).getScore());
        assertEquals(expectedResponse.getRecords().get(0).getSegment().getDocument().getId(),
                actualResponse.getRecords().get(0).getSegment().getDocument().getId());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(eq(DatasetUriConstant.V1_DATASETS_RETRIEVE_URL), eq(datasetId));
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(RetrieveResponse.class);
    }

    @Test
    public void testListTextEmbedding() {
        // Prepare test data
        String apiKey = "test-api-key";

        // Create expected response
        TextEmbeddingListResponse expectedResponse = new TextEmbeddingListResponse();
        List<TextEmbedding> textEmbeddings = new ArrayList<>();

        // Create a TextEmbedding with proper structure
        TextEmbedding embedding = new TextEmbedding();
        embedding.setProvider("openai");
        embedding.setStatus("active");

        // Create and set TextEmbeddingLabel
        TextEmbeddingLabel label = new TextEmbeddingLabel();
        // Assuming TextEmbeddingLabel has name field
        embedding.setLabel(label);

        // Create and set icons
        TextEmbeddingIcon smallIcon = new TextEmbeddingIcon();
        // Assuming TextEmbeddingIcon has url field
        embedding.setIconSmall(smallIcon);

        TextEmbeddingIcon largeIcon = new TextEmbeddingIcon();
        embedding.setIconLarge(largeIcon);

        // Create and set models
        List<TextEmbeddingModel> models = new ArrayList<>();
        TextEmbeddingModel model = new TextEmbeddingModel();
        // Assuming TextEmbeddingModel has id and name fields
        models.add(model);
        embedding.setModels(models);

        textEmbeddings.add(embedding);
        expectedResponse.setData(textEmbeddings);

        // Set up the response mock to return our expected response
        when(responseSpec.body(TextEmbeddingListResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        TextEmbeddingListResponse actualResponse = client.listTextEmbedding(apiKey);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getProvider(), actualResponse.getData().get(0).getProvider());
        assertEquals(expectedResponse.getData().get(0).getStatus(), actualResponse.getData().get(0).getStatus());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(DatasetUriConstant.V1_TEXT_EMBEDDING_LIST_URL);
        verify(responseSpec).body(TextEmbeddingListResponse.class);
    }

    @Test
    public void testCreateSegment() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String content = "This is segment content";

        // Create request
        SegmentCreateRequest request = new SegmentCreateRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);

        // Create a segment parameter
        List<SegmentParam> segments = new ArrayList<>();
        SegmentParam segmentParam = new SegmentParam();
        segmentParam.setContent(content);
        segments.add(segmentParam);

        request.setSegments(segments);

        // Create expected response
        SegmentResponse expectedResponse = new SegmentResponse();
        List<SegmentData> segmentDataList = new ArrayList<>();

        // Create segment data
        SegmentData segmentData = new SegmentData();
        segmentData.setId("segment-123456");
        segmentData.setContent(content);
        segmentData.setWordCount(5); // "This is segment content" has 5 words
        segmentDataList.add(segmentData);

        expectedResponse.setData(segmentDataList);
        expectedResponse.setDocForm("text");

        // Set up the response mock to return our expected response
        when(responseSpec.body(SegmentResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        SegmentResponse actualResponse = client.createSegment(request);

        // Verify the result
        assertNotNull(actualResponse.getData());
        assertEquals(1, actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getContent(), actualResponse.getData().get(0).getContent());
        assertEquals(expectedResponse.getData().get(0).getWordCount(), actualResponse.getData().get(0).getWordCount());
        assertEquals(expectedResponse.getDocForm(), actualResponse.getDocForm());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_URL), eq(datasetId), eq(documentId));
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(SegmentResponse.class);
    }

    @Test
    public void testPageSegment() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String keyword = "test";
        String status = "active";

        // Create request
        SegmentPageRequest request = new SegmentPageRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setKeyword(keyword);
        request.setStatus(status);

        // Create expected response with segments
        SegmentResponse expectedResponse = new SegmentResponse();
        List<SegmentData> segmentDataList = new ArrayList<>();

        // Create segment 1
        SegmentData segmentData1 = new SegmentData();
        segmentData1.setId("segment-123456");
        segmentData1.setContent("Test segment 1");
        segmentData1.setPosition(1);
        segmentData1.setWordCount(3);
        segmentDataList.add(segmentData1);

        // Create segment 2
        SegmentData segmentData2 = new SegmentData();
        segmentData2.setId("segment-789012");
        segmentData2.setContent("Test segment 2");
        segmentData2.setPosition(2);
        segmentData2.setWordCount(3);
        segmentDataList.add(segmentData2);

        expectedResponse.setData(segmentDataList);
        expectedResponse.setDocForm("text");

        // Set up the response mock to return our expected response
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedResponse);

        // Execute the method
        SegmentResponse actualResponse = client.pageSegment(request);

        // Verify the result
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getContent(), actualResponse.getData().get(0).getContent());
        assertEquals(expectedResponse.getData().get(1).getId(), actualResponse.getData().get(1).getId());
        assertEquals(expectedResponse.getData().get(1).getContent(), actualResponse.getData().get(1).getContent());
        assertEquals(expectedResponse.getDocForm(), actualResponse.getDocForm());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testDeleteSegment() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String segmentId = "segment-123456";

        client.deleteSegment(datasetId, documentId, segmentId, apiKey);

        // Verify WebClient interactions
        verify(restClient).delete();
        verify(requestHeadersUriSpec).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL),
                eq(datasetId), eq(documentId), eq(segmentId));
    }

    @Test
    public void testUpdateSegment() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String segmentId = "segment-123456";
        String content = "Updated segment content";

        // Create request
        SegmentUpdateRequest request = new SegmentUpdateRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegmentId(segmentId);

        // Create a segment parameter
        SegmentParam segmentParam = new SegmentParam();
        segmentParam.setContent(content);
        request.setSegment(segmentParam);

        // Create expected response
        SegmentUpdateResponse expectedResponse = new SegmentUpdateResponse();

        // Create segment data
        SegmentData segmentData = new SegmentData();
        segmentData.setId(segmentId);
        segmentData.setContent(content);
        segmentData.setWordCount(3); // Count of words in "Updated segment content"

        expectedResponse.setData(segmentData);
        expectedResponse.setDocForm("text");

        // Set up the response mock to return our expected response
        when(responseSpec.body(SegmentUpdateResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        SegmentUpdateResponse actualResponse = client.updateSegment(request);

        // Verify the result
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getData().getContent(), actualResponse.getData().getContent());
        assertEquals(expectedResponse.getData().getWordCount(), actualResponse.getData().getWordCount());
        assertEquals(expectedResponse.getDocForm(), actualResponse.getDocForm());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL),
                eq(datasetId), eq(documentId), eq(segmentId));
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(SegmentUpdateResponse.class);
    }

    @Test
    public void testCreateMetaData() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String name = "category";
        String type = "string";

        // Create request
        MetaDataCreateRequest request = new MetaDataCreateRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setName(name);
        request.setType(type);

        // Create expected response
        MetaDataResponse expectedResponse = new MetaDataResponse();
        expectedResponse.setId("metadata-123456");
        expectedResponse.setName(name);
        expectedResponse.setType(type);

        // Set up the response mock to return our expected response
        when(responseSpec.body(MetaDataResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        MetaDataResponse actualResponse = client.createMetaData(request);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getType(), actualResponse.getType());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(eq(DatasetUriConstant.V1_METADATA_CREATE_URL), eq(datasetId));
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(MetaDataResponse.class);
    }

    @Test
    public void testUpdateMetaData() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String metadataId = "metadata-123456";

        // Create request
        MetaDataUpdateRequest request = new MetaDataUpdateRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setMetaDataId(metadataId);

        // Create expected response
        MetaDataResponse expectedResponse = new MetaDataResponse();
        expectedResponse.setId(metadataId);
        expectedResponse.setName("category");
        expectedResponse.setType("string");

        // Set up the response mock to return our expected response
        when(responseSpec.body(MetaDataResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        MetaDataResponse actualResponse = client.updateMetaData(request);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getType(), actualResponse.getType());

        // Verify WebClient interactions
        verify(restClient).patch();
        verify(requestBodyUriSpec).uri(eq(DatasetUriConstant.V1_METADATA_UPDATE_URL), eq(datasetId), eq(metadataId));
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(MetaDataResponse.class);
    }

    @Test
    public void testDeleteMetaData() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String metadataId = "metadata-123456";

        // Set up the response mock
        when(responseSpec.body(void.class)).thenReturn(null);

        // Execute the method
        client.deleteMetaData(datasetId, metadataId, apiKey);

        // Verify WebClient interactions
        verify(restClient).delete();
        verify(requestHeadersUriSpec).uri(eq(DatasetUriConstant.V1_METADATA_DELETE_URL), eq(datasetId), eq(metadataId));
        verify(responseSpec).body(void.class);
    }

    @Test
    public void testActionMetaData() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";

        // Create request
        MetaDataActionRequest request = new MetaDataActionRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setAction(MetaDataActionEnum.enable);

        // Set up the response mock
        when(responseSpec.body(Void.class)).thenReturn(null);

        // Execute the method
        client.actionMetaData(request);

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(eq(DatasetUriConstant.V1_METADATA_ACTION_URL), eq(datasetId), eq(MetaDataActionEnum.enable.name()));
        verify(responseSpec).body(Void.class);
    }

    @Test
    public void testUpdateDocumentMetaData() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";

        // Create request
        DocumentMetaDataUpdateRequest request = new DocumentMetaDataUpdateRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);

        // Create operation data
        List<DocumentMetaDataUpdateRequest.OperationData> operationDataList = new ArrayList<>();
        DocumentMetaDataUpdateRequest.OperationData operationData = new DocumentMetaDataUpdateRequest.OperationData();
        operationData.setDocumentId(documentId);

        // Create metadata list
        List<MetaData> metadataList = new ArrayList<>();

        // Create category metadata
        MetaData categoryMetadata = new MetaData();
        categoryMetadata.setName("category");
        categoryMetadata.setType("string");
        categoryMetadata.setValue("test");
        metadataList.add(categoryMetadata);

        // Create priority metadata
        MetaData priorityMetadata = new MetaData();
        priorityMetadata.setName("priority");
        priorityMetadata.setType("number");
        priorityMetadata.setValue("1");
        metadataList.add(priorityMetadata);

        operationData.setMetadataList(metadataList);
        operationDataList.add(operationData);
        request.setOperationData(operationDataList);

        // Set up the response mock
        when(responseSpec.body(Void.class)).thenReturn(null);

        // Execute the method
        client.updateDocumentMetaData(request);

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(eq(DatasetUriConstant.V1_DOCUMENT_METADATA_UPDATE_URL), eq(datasetId));
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(Void.class);
    }

    @Test
    public void testListMetaData() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";

        // Create expected response
        MetaDataListResponse expectedResponse = new MetaDataListResponse();
        List<MetaDataListResponse.DocMetadata> docMetadataList = new ArrayList<>();

        // Create metadata 1
        MetaDataListResponse.DocMetadata metadata1 = new MetaDataListResponse.DocMetadata();
        metadata1.setId("metadata-123456");
        metadata1.setName("category");
        metadata1.setType("string");
        metadata1.setUserCount(10);
        docMetadataList.add(metadata1);

        // Create metadata 2
        MetaDataListResponse.DocMetadata metadata2 = new MetaDataListResponse.DocMetadata();
        metadata2.setId("metadata-789012");
        metadata2.setName("priority");
        metadata2.setType("number");
        metadata2.setUserCount(5);
        docMetadataList.add(metadata2);

        expectedResponse.setDocMetadata(docMetadataList);
        expectedResponse.setBuiltInFieldEnabled(true);

        // Set up the response mock
        when(responseSpec.body(MetaDataListResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        MetaDataListResponse actualResponse = client.listMetaData(datasetId, apiKey);

        // Verify the result
        assertEquals(expectedResponse.getBuiltInFieldEnabled(), actualResponse.getBuiltInFieldEnabled());
        assertEquals(expectedResponse.getDocMetadata().size(), actualResponse.getDocMetadata().size());
        assertEquals(expectedResponse.getDocMetadata().get(0).getId(), actualResponse.getDocMetadata().get(0).getId());
        assertEquals(expectedResponse.getDocMetadata().get(0).getName(), actualResponse.getDocMetadata().get(0).getName());
        assertEquals(expectedResponse.getDocMetadata().get(0).getType(), actualResponse.getDocMetadata().get(0).getType());
        assertEquals(expectedResponse.getDocMetadata().get(0).getUserCount(), actualResponse.getDocMetadata().get(0).getUserCount());
        assertEquals(expectedResponse.getDocMetadata().get(1).getId(), actualResponse.getDocMetadata().get(1).getId());
        assertEquals(expectedResponse.getDocMetadata().get(1).getName(), actualResponse.getDocMetadata().get(1).getName());
        assertEquals(expectedResponse.getDocMetadata().get(1).getType(), actualResponse.getDocMetadata().get(1).getType());
        assertEquals(expectedResponse.getDocMetadata().get(1).getUserCount(), actualResponse.getDocMetadata().get(1).getUserCount());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(eq(DatasetUriConstant.V1_METADATA_LIST_URL), eq(datasetId));
        verify(responseSpec).body(MetaDataListResponse.class);
    }

    @Test
    public void testCreateSegmentChildChunk() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String segmentId = "segment-123456";
        String content = "This is a child chunk content";

        // Create request
        SegmentChildChunkCreateRequest request = new SegmentChildChunkCreateRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegmentId(segmentId);
        request.setContent(content);

        // Create expected response
        SegmentChildChunkCreateResponse expectedResponse = new SegmentChildChunkCreateResponse();
        SegmentChildChunkResponse responseData = new SegmentChildChunkResponse();
        responseData.setId("chunk-123456");
        responseData.setSegmentId(segmentId);
        responseData.setContent(content);
        responseData.setWordCount(6); // Count of words in "This is a child chunk content"
        expectedResponse.setData(responseData);

        // Set up the response mock
        when(responseSpec.body(SegmentChildChunkCreateResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        SegmentChildChunkCreateResponse actualResponse = client.createSegmentChildChunk(request);

        // Verify the result
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getData().getSegmentId(), actualResponse.getData().getSegmentId());
        assertEquals(expectedResponse.getData().getContent(), actualResponse.getData().getContent());
        assertEquals(expectedResponse.getData().getWordCount(), actualResponse.getData().getWordCount());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNKS_URL),
                eq(datasetId), eq(documentId), eq(segmentId));
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(SegmentChildChunkCreateResponse.class);
    }

    @Test
    public void testPageSegmentChildChunk() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String segmentId = "segment-123456";
        String keyword = "test";
        int page = 1;
        int limit = 10;

        // Create request
        SegmentChildChunkPageRequest request = new SegmentChildChunkPageRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegmentId(segmentId);
        request.setKeyword(keyword);
        request.setPage(page);
        request.setLimit(limit);

        // Create expected response
        DifyPageResult<SegmentChildChunkResponse> expectedResponse = new DifyPageResult<>();
        List<SegmentChildChunkResponse> items = new ArrayList<>();

        // Create child chunk 1
        SegmentChildChunkResponse chunk1 = new SegmentChildChunkResponse();
        chunk1.setId("chunk-123456");
        chunk1.setSegmentId(segmentId);
        chunk1.setContent("Test child chunk 1");
        chunk1.setWordCount(4);
        items.add(chunk1);

        // Create child chunk 2
        SegmentChildChunkResponse chunk2 = new SegmentChildChunkResponse();
        chunk2.setId("chunk-789012");
        chunk2.setSegmentId(segmentId);
        chunk2.setContent("Test child chunk 2");
        chunk2.setWordCount(4);
        items.add(chunk2);

        expectedResponse.setData(items);
        expectedResponse.setPage(page);
        expectedResponse.setLimit(limit);
        expectedResponse.setTotal(2);

        // Set up the response mock
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedResponse);

        // Execute the method
        DifyPageResult<SegmentChildChunkResponse> actualResponse = client.pageSegmentChildChunk(request);

        // Verify the result
        assertEquals(expectedResponse.getTotal(), actualResponse.getTotal());
        assertEquals(expectedResponse.getPage(), actualResponse.getPage());
        assertEquals(expectedResponse.getLimit(), actualResponse.getLimit());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getContent(), actualResponse.getData().get(0).getContent());
        assertEquals(expectedResponse.getData().get(1).getId(), actualResponse.getData().get(1).getId());
        assertEquals(expectedResponse.getData().get(1).getContent(), actualResponse.getData().get(1).getContent());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(
                eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNKS_URL + "?keyword={keyword}&page={page}&limit={limit}"),
                eq(datasetId), eq(documentId), eq(segmentId), eq(keyword), eq(page), eq(limit));
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testDeleteSegmentChildChunk() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String segmentId = "segment-123456";
        String childChunkId = "chunk-123456";

        // Create request
        SegmentChildChunkDeleteRequest request = new SegmentChildChunkDeleteRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegmentId(segmentId);
        request.setChildChunkId(childChunkId);

        client.deleteSegmentChildChunk(request);

        // Verify WebClient interactions
        verify(restClient).delete();
        verify(requestHeadersUriSpec).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNK_URL),
                eq(datasetId), eq(documentId), eq(segmentId), eq(childChunkId));
    }

    @Test
    public void testUpdateSegmentChildChunk() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String segmentId = "segment-123456";
        String childChunkId = "chunk-123456";
        String content = "Updated child chunk content";

        // Create request
        SegmentChildChunkUpdateRequest request = new SegmentChildChunkUpdateRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegmentId(segmentId);
        request.setChildChunkId(childChunkId);
        request.setContent(content);

        // Create expected response
        SegmentChildChunkUpdateResponse expectedResponse = new SegmentChildChunkUpdateResponse();
        SegmentChildChunkResponse responseData = new SegmentChildChunkResponse();
        responseData.setId(childChunkId);
        responseData.setSegmentId(segmentId);
        responseData.setContent(content);
        responseData.setWordCount(4); // Count of words in "Updated child chunk content"
        expectedResponse.setData(responseData);

        // Set up the response mock
        when(responseSpec.body(SegmentChildChunkUpdateResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        SegmentChildChunkUpdateResponse actualResponse = client.updateSegmentChildChunk(request);

        // Verify the result
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getData().getSegmentId(), actualResponse.getData().getSegmentId());
        assertEquals(expectedResponse.getData().getContent(), actualResponse.getData().getContent());
        assertEquals(expectedResponse.getData().getWordCount(), actualResponse.getData().getWordCount());

        // Verify WebClient interactions
        verify(restClient).patch();
        verify(requestBodyUriSpec).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNK_URL),
                eq(datasetId), eq(documentId), eq(segmentId), eq(childChunkId));
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(SegmentChildChunkUpdateResponse.class);
    }

    @Test
    public void testUploadFileInfo() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";

        // Create expected response
        UploadFileInfoResponse expectedResponse = new UploadFileInfoResponse();
        expectedResponse.setId(documentId);
        expectedResponse.setName("test-document.pdf");
        expectedResponse.setSize(1024);
        expectedResponse.setExtension("pdf");
        expectedResponse.setUrl("https://example.com/test-document.pdf");
        expectedResponse.setDownloadUrl("https://example.com/download/test-document.pdf");
        expectedResponse.setMimeType("application/pdf");
        expectedResponse.setCreatedBy("user-123456");
        expectedResponse.setCreatedAt(1650000000L);

        // Set up the response mock
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedResponse);

        // Execute the method
        UploadFileInfoResponse actualResponse = client.uploadFileInfo(datasetId, documentId, apiKey);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getSize(), actualResponse.getSize());
        assertEquals(expectedResponse.getExtension(), actualResponse.getExtension());
        assertEquals(expectedResponse.getUrl(), actualResponse.getUrl());
        assertEquals(expectedResponse.getDownloadUrl(), actualResponse.getDownloadUrl());
        assertEquals(expectedResponse.getMimeType(), actualResponse.getMimeType());
        assertEquals(expectedResponse.getCreatedBy(), actualResponse.getCreatedBy());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(eq(DatasetUriConstant.V1_DOCUMENTS_UPLOAD_FILE), eq(datasetId), eq(documentId));
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }
}
