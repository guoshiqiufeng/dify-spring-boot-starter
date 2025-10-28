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
package io.github.guoshiqiufeng.dify.client.spring5.dataset;

import io.github.guoshiqiufeng.dify.client.spring5.BaseClientTest;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbedding;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbeddingIcon;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbeddingLabel;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbeddingModel;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.MetaDataActionEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.SearchMethodEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocActionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyDatasetDefaultClient}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/25 09:21
 */
@SuppressWarnings("unchecked")
public class DifyDatasetDefaultClientTest extends BaseClientTest {

    private DifyDatasetDefaultClient client;

    @BeforeEach
    public void setup() {
        super.setup();
        // Create real client with mocked WebClient
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        client = new DifyDatasetDefaultClient("https://api.dify.ai", clientConfig, webClientBuilderMock);
    }

    @Test
    public void testCreate() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetName = "Test Dataset";
        String datasetDescription = "This is a test dataset";

        // Create request
        DatasetCreateRequest request = new DatasetCreateRequest();
        request.setApiKey(apiKey);
        request.setName(datasetName);
        request.setDescription(datasetDescription);

        // Create expected response
        DatasetResponse expectedResponse = new DatasetResponse();
        expectedResponse.setId("dataset-123456");
        expectedResponse.setName(datasetName);
        expectedResponse.setDescription(datasetDescription);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DatasetResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DatasetResponse actualResponse = client.create(request);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(DatasetUriConstant.V1_DATASETS_URL);
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(DatasetResponse.class);
    }

    @Test
    public void testPage() {
        // Prepare test data
        String apiKey = "test-api-key";
        int page = 1;
        int limit = 10;
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("tag_ids"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("keyword"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("include_all"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create request
        DatasetPageRequest request = new DatasetPageRequest();
        request.setApiKey(apiKey);
        request.setPage(page);
        request.setLimit(limit);
        request.setTagIds(List.of("123456"));
        request.setKeyword("search-keyword");
        request.setIncludeAll(false);

        // Create expected response
        DifyPageResult<DatasetResponse> expectedResponse = new DifyPageResult<>();
        List<DatasetResponse> items = new ArrayList<>();
        DatasetResponse dataset1 = new DatasetResponse();
        dataset1.setId("dataset-123456");
        dataset1.setName("Dataset 1");
        dataset1.setDescription("Description 1");
        items.add(dataset1);

        DatasetResponse dataset2 = new DatasetResponse();
        dataset2.setId("dataset-789012");
        dataset2.setName("Dataset 2");
        dataset2.setDescription("Description 2");
        items.add(dataset2);

        expectedResponse.setData(items);
        expectedResponse.setLimit(limit);
        expectedResponse.setPage(page);
        expectedResponse.setTotal(2);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DifyPageResult<DatasetResponse> actualResponse = client.page(request);

        // Verify the result
        assertEquals(expectedResponse.getTotal(), actualResponse.getTotal());
        assertEquals(expectedResponse.getPage(), actualResponse.getPage());
        assertEquals(expectedResponse.getLimit(), actualResponse.getLimit());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(1).getName(), actualResponse.getData().get(1).getName());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));

        request.setTagIds(List.of());
        request.setKeyword("");
        request.setIncludeAll(true);
        client.page(request);
    }

    @Test
    public void testInfo() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";

        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(anyString())).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create request
        DatasetInfoRequest request = new DatasetInfoRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);

        // Create expected response
        DatasetInfoResponse expectedResponse = new DatasetInfoResponse();
        expectedResponse.setId(datasetId);
        expectedResponse.setName("Test Dataset");
        expectedResponse.setDescription("This is a test dataset");
        expectedResponse.setCreatedAt(1650000000L);
        expectedResponse.setUpdatedAt(1650000100L);
        expectedResponse.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);
        expectedResponse.setDocumentCount(10);
        expectedResponse.setWordCount(5000);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DatasetInfoResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DatasetInfoResponse actualResponse = client.info(request);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
        assertEquals(expectedResponse.getUpdatedAt(), actualResponse.getUpdatedAt());
        assertEquals(expectedResponse.getIndexingTechnique(), actualResponse.getIndexingTechnique());
        assertEquals(expectedResponse.getDocumentCount(), actualResponse.getDocumentCount());
        assertEquals(expectedResponse.getWordCount(), actualResponse.getWordCount());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(responseSpecMock).bodyToMono(DatasetInfoResponse.class);
    }

    @Test
    public void testUpdate() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String updatedName = "Updated Dataset Name";
        String updatedDescription = "This is an updated description";

        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);
        when(requestBodyUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(anyString())).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestBodySpecMock;
        });

        // Create request
        DatasetUpdateRequest request = new DatasetUpdateRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);

        // Create expected response
        DatasetInfoResponse expectedResponse = new DatasetInfoResponse();
        expectedResponse.setId(datasetId);
        expectedResponse.setName(updatedName);
        expectedResponse.setDescription(updatedDescription);
        expectedResponse.setCreatedAt(1650000000L);
        expectedResponse.setUpdatedAt(1650100000L);
        expectedResponse.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);
        expectedResponse.setDocumentCount(10);
        expectedResponse.setWordCount(5000);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DatasetInfoResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DatasetInfoResponse actualResponse = client.update(request);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
        assertEquals(expectedResponse.getUpdatedAt(), actualResponse.getUpdatedAt());
        assertEquals(expectedResponse.getIndexingTechnique(), actualResponse.getIndexingTechnique());
        assertEquals(expectedResponse.getDocumentCount(), actualResponse.getDocumentCount());
        assertEquals(expectedResponse.getWordCount(), actualResponse.getWordCount());

        // Verify WebClient interactions
        verify(webClientMock).patch();
        verify(requestBodyUriSpecMock).uri(any(Function.class));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(DatasetInfoResponse.class);
    }

    @Test
    public void testDelete() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";

        // Set up the response mock
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Execute the method
        client.delete(datasetId, apiKey);

        // Verify WebClient interactions
        verify(webClientMock).delete();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_DATASETS_URL + "/{datasetId}"), eq(datasetId));
        verify(responseSpecMock).bodyToMono(Void.class);
    }

    @Test
    public void testCreateDocumentByText() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String content = "This is a test document content";
        String documentName = "Test Document";

        // Create request
        DocumentCreateByTextRequest request = new DocumentCreateByTextRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setText(content);
        request.setName(documentName);

        // Create expected response
        DocumentCreateResponse expectedResponse = new DocumentCreateResponse();
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("document-123456");
        documentInfo.setName(documentName);
        expectedResponse.setDocument(documentInfo);
        expectedResponse.setBatch("batch-123456");

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DocumentCreateResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DocumentCreateResponse actualResponse = client.createDocumentByText(request);

        // Verify the result
        assertEquals(expectedResponse.getDocument().getId(), actualResponse.getDocument().getId());
        assertEquals(expectedResponse.getDocument().getName(), actualResponse.getDocument().getName());
        assertEquals(expectedResponse.getBatch(), actualResponse.getBatch());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENT_CREATE_BY_TEXT_URL), eq(datasetId));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(DocumentCreateResponse.class);
    }

    @Test
    public void testCreateDocumentByFile() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String fileName = "test-document.pdf";
        String fileContent = "Test file content";

        // Create mock file
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.getContentType()).thenReturn("application/pdf");
        try {
            when(mockFile.getBytes()).thenReturn(fileContent.getBytes());
            when(mockFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(fileContent.getBytes()));
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        // Create request
        DocumentCreateByFileRequest request = new DocumentCreateByFileRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setFile(mockFile);

        // Create expected response
        DocumentCreateResponse expectedResponse = new DocumentCreateResponse();
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("document-123456");
        documentInfo.setName(fileName);
        expectedResponse.setDocument(documentInfo);
        expectedResponse.setBatch("batch-123456");

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DocumentCreateResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DocumentCreateResponse actualResponse = client.createDocumentByFile(request);

        // Verify the result
        assertEquals(expectedResponse.getDocument().getId(), actualResponse.getDocument().getId());
        assertEquals(expectedResponse.getDocument().getName(), actualResponse.getDocument().getName());
        assertEquals(expectedResponse.getBatch(), actualResponse.getBatch());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENT_CREATE_BY_FILE_URL), eq(datasetId));
        verify(requestBodySpecMock).contentType(MediaType.MULTIPART_FORM_DATA);
        verify(requestBodySpecMock).bodyValue(any());
        verify(responseSpecMock).bodyToMono(DocumentCreateResponse.class);
    }

    @Test
    public void testUpdateDocumentByText() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String content = "Updated document content";

        // Create request
        DocumentUpdateByTextRequest request = new DocumentUpdateByTextRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setText(content);

        // Create expected response
        DocumentCreateResponse expectedResponse = new DocumentCreateResponse();
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId(documentId);
        expectedResponse.setDocument(documentInfo);
        expectedResponse.setBatch("batch-update-123456");

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DocumentCreateResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DocumentCreateResponse actualResponse = client.updateDocumentByText(request);

        // Verify the result
        assertEquals(expectedResponse.getDocument().getId(), actualResponse.getDocument().getId());
        assertEquals(expectedResponse.getBatch(), actualResponse.getBatch());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENT_UPDATE_BY_TEXT_URL), eq(datasetId), eq(documentId));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(DocumentCreateResponse.class);
    }

    @Test
    public void testUpdateDocumentByFile() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String fileName = "updated-document.pdf";
        String fileContent = "Updated file content";

        // Create mock file
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.getContentType()).thenReturn("application/pdf");
        try {
            when(mockFile.getBytes()).thenReturn(fileContent.getBytes());
            when(mockFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(fileContent.getBytes()));
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        // Create request
        DocumentUpdateByFileRequest request = new DocumentUpdateByFileRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setFile(mockFile);

        // Create expected response
        DocumentCreateResponse expectedResponse = new DocumentCreateResponse();
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId(documentId);
        documentInfo.setName(fileName);
        expectedResponse.setDocument(documentInfo);
        expectedResponse.setBatch("batch-update-123456");

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DocumentCreateResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DocumentCreateResponse actualResponse = client.updateDocumentByFile(request);

        // Verify the result
        assertEquals(expectedResponse.getDocument().getId(), actualResponse.getDocument().getId());
        assertEquals(expectedResponse.getDocument().getName(), actualResponse.getDocument().getName());
        assertEquals(expectedResponse.getBatch(), actualResponse.getBatch());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENT_UPDATE_BY_FILE_URL), eq(datasetId), eq(documentId));
        verify(requestBodySpecMock).contentType(MediaType.MULTIPART_FORM_DATA);
        verify(requestBodySpecMock).bodyValue(any());
        verify(responseSpecMock).bodyToMono(DocumentCreateResponse.class);
    }

    @Test
    public void testPageDocument() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        int page = 1;
        int limit = 10;
        String keyword = "test";

        // Create request
        DatasetPageDocumentRequest request = new DatasetPageDocumentRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setPage(page);
        request.setLimit(limit);
        request.setKeyword(keyword);

        // Create expected response
        DifyPageResult<DocumentInfo> expectedResponse = new DifyPageResult<>();
        List<DocumentInfo> items = new ArrayList<>();

        DocumentInfo document1 = new DocumentInfo();
        document1.setId("document-123456");
        document1.setName("Document 1");
        items.add(document1);

        DocumentInfo document2 = new DocumentInfo();
        document2.setId("document-789012");
        document2.setName("Document 2");
        items.add(document2);

        expectedResponse.setData(items);
        expectedResponse.setLimit(limit);
        expectedResponse.setPage(page);
        expectedResponse.setTotal(2);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DifyPageResult<DocumentInfo> actualResponse = client.pageDocument(request);

        // Verify the result
        assertEquals(expectedResponse.getTotal(), actualResponse.getTotal());
        assertEquals(expectedResponse.getPage(), actualResponse.getPage());
        assertEquals(expectedResponse.getLimit(), actualResponse.getLimit());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(1).getName(), actualResponse.getData().get(1).getName());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(
                eq(DatasetUriConstant.V1_DOCUMENTS_URL + "?page={page}&limit={limit}&keyword={keyword}"),
                eq(datasetId), eq(page), eq(limit), eq(keyword));
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
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
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(expectedResponse));

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
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(
                eq(DatasetUriConstant.V1_DOCUMENT_INDEXING_STATUS_URL), eq(datasetId), eq(batch));
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testDeleteDocument() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        // Mock the response to return Mono.empty()
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());

        client.deleteDocument(datasetId, documentId, apiKey);

        // Verify WebClient interactions
        verify(webClientMock).delete();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENT_URL), eq(datasetId), eq(documentId));
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
        RetrieveRetrievalModel retrievalModel = new RetrieveRetrievalModel();
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
        when(responseSpecMock.bodyToMono(RetrieveResponse.class)).thenReturn(Mono.just(expectedResponse));

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
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DATASETS_RETRIEVE_URL), eq(datasetId));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(RetrieveResponse.class);
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
        when(responseSpecMock.bodyToMono(TextEmbeddingListResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        TextEmbeddingListResponse actualResponse = client.listTextEmbedding(apiKey);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getProvider(), actualResponse.getData().get(0).getProvider());
        assertEquals(expectedResponse.getData().get(0).getStatus(), actualResponse.getData().get(0).getStatus());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(DatasetUriConstant.V1_TEXT_EMBEDDING_LIST_URL);
        verify(responseSpecMock).bodyToMono(TextEmbeddingListResponse.class);
    }

    @Test
    public void testListRerank() {
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
        when(responseSpecMock.bodyToMono(TextEmbeddingListResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        TextEmbeddingListResponse actualResponse = client.listRerank(apiKey);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getProvider(), actualResponse.getData().get(0).getProvider());
        assertEquals(expectedResponse.getData().get(0).getStatus(), actualResponse.getData().get(0).getStatus());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(DatasetUriConstant.V1_RERANK_LIST_URL);
        verify(responseSpecMock).bodyToMono(TextEmbeddingListResponse.class);
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
        when(responseSpecMock.bodyToMono(SegmentResponse.class)).thenReturn(Mono.just(expectedResponse));

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
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_URL), eq(datasetId), eq(documentId));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(SegmentResponse.class);
    }

    @Test
    public void testPageSegment() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String keyword = "test";
        String status = "active";

        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("status"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("keyword"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create request
        SegmentPageRequest request = new SegmentPageRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setKeyword(keyword);
        request.setStatus(status);
        request.setPage(1);
        request.setLimit(10);

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
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(expectedResponse));

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
        verify(webClientMock).get();
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));

        request.setKeyword("");
        request.setStatus("");
        client.pageSegment(request);
    }

    @Test
    public void testDeleteSegment() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String segmentId = "segment-123456";
        // Mock the response to return Mono.empty()
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());

        client.deleteSegment(datasetId, documentId, segmentId, apiKey);

        // Verify WebClient interactions
        verify(webClientMock).delete();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL),
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
        when(responseSpecMock.bodyToMono(SegmentUpdateResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        SegmentUpdateResponse actualResponse = client.updateSegment(request);

        // Verify the result
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getData().getContent(), actualResponse.getData().getContent());
        assertEquals(expectedResponse.getData().getWordCount(), actualResponse.getData().getWordCount());
        assertEquals(expectedResponse.getDocForm(), actualResponse.getDocForm());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL),
                eq(datasetId), eq(documentId), eq(segmentId));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(SegmentUpdateResponse.class);
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
        when(responseSpecMock.bodyToMono(MetaDataResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        MetaDataResponse actualResponse = client.createMetaData(request);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getType(), actualResponse.getType());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_METADATA_CREATE_URL), eq(datasetId));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(MetaDataResponse.class);
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
        when(responseSpecMock.bodyToMono(MetaDataResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        MetaDataResponse actualResponse = client.updateMetaData(request);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getType(), actualResponse.getType());

        // Verify WebClient interactions
        verify(webClientMock).patch();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_METADATA_UPDATE_URL), eq(datasetId), eq(metadataId));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(MetaDataResponse.class);
    }

    @Test
    public void testDeleteMetaData() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String metadataId = "metadata-123456";

        // Set up the response mock
        when(responseSpecMock.bodyToMono(void.class)).thenReturn(Mono.empty());

        // Execute the method
        client.deleteMetaData(datasetId, metadataId, apiKey);

        // Verify WebClient interactions
        verify(webClientMock).delete();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_METADATA_DELETE_URL), eq(datasetId), eq(metadataId));
        verify(responseSpecMock).bodyToMono(void.class);
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
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Execute the method
        client.actionMetaData(request);

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_METADATA_ACTION_URL), eq(datasetId), eq(MetaDataActionEnum.enable.name()));
        verify(responseSpecMock).bodyToMono(Void.class);
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
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Execute the method
        client.updateDocumentMetaData(request);

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENT_METADATA_UPDATE_URL), eq(datasetId));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(Void.class);
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
        when(responseSpecMock.bodyToMono(MetaDataListResponse.class)).thenReturn(Mono.just(expectedResponse));

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
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_METADATA_LIST_URL), eq(datasetId));
        verify(responseSpecMock).bodyToMono(MetaDataListResponse.class);
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
        when(responseSpecMock.bodyToMono(SegmentChildChunkCreateResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        SegmentChildChunkCreateResponse actualResponse = client.createSegmentChildChunk(request);

        // Verify the result
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getData().getSegmentId(), actualResponse.getData().getSegmentId());
        assertEquals(expectedResponse.getData().getContent(), actualResponse.getData().getContent());
        assertEquals(expectedResponse.getData().getWordCount(), actualResponse.getData().getWordCount());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNKS_URL),
                eq(datasetId), eq(documentId), eq(segmentId));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(SegmentChildChunkCreateResponse.class);
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
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(expectedResponse));

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
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(
                eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNKS_URL + "?keyword={keyword}&page={page}&limit={limit}"),
                eq(datasetId), eq(documentId), eq(segmentId), eq(keyword), eq(page), eq(limit));
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testDeleteSegmentChildChunk() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String segmentId = "segment-123456";
        String childChunkId = "chunk-123456";
        // Mock the response to return Mono.empty()
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());
        // Create request
        SegmentChildChunkDeleteRequest request = new SegmentChildChunkDeleteRequest();
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegmentId(segmentId);
        request.setChildChunkId(childChunkId);

        client.deleteSegmentChildChunk(request);

        // Verify WebClient interactions
        verify(webClientMock).delete();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNK_URL),
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
        when(responseSpecMock.bodyToMono(SegmentChildChunkUpdateResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        SegmentChildChunkUpdateResponse actualResponse = client.updateSegmentChildChunk(request);

        // Verify the result
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getData().getSegmentId(), actualResponse.getData().getSegmentId());
        assertEquals(expectedResponse.getData().getContent(), actualResponse.getData().getContent());
        assertEquals(expectedResponse.getData().getWordCount(), actualResponse.getData().getWordCount());

        // Verify WebClient interactions
        verify(webClientMock).patch();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNK_URL),
                eq(datasetId), eq(documentId), eq(segmentId), eq(childChunkId));
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(SegmentChildChunkUpdateResponse.class);
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
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(expectedResponse));

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
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENTS_UPLOAD_FILE), eq(datasetId), eq(documentId));
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testCreateTag() {
        // Prepare test data
        String apiKey = "test-api-key";
        String tagName = "Test Tag";

        // Create request
        TagCreateRequest request = new TagCreateRequest();
        request.setApiKey(apiKey);
        request.setName(tagName);

        // Create expected response
        TagInfoResponse expectedResponse = new TagInfoResponse();
        expectedResponse.setId("tag-123456");
        expectedResponse.setName(tagName);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(TagInfoResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        TagInfoResponse actualResponse = client.createTag(request);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(DatasetUriConstant.V1_TAGS);
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(TagInfoResponse.class);
    }

    @Test
    public void testListTag() {
        // Prepare test data
        String apiKey = "test-api-key";

        // Create expected response
        List<TagInfoResponse> expectedResponse = new ArrayList<>();
        TagInfoResponse tag1 = new TagInfoResponse();
        tag1.setId("tag-123456");
        tag1.setName("Tag 1");
        expectedResponse.add(tag1);

        TagInfoResponse tag2 = new TagInfoResponse();
        tag2.setId("tag-789012");
        tag2.setName("Tag 2");
        expectedResponse.add(tag2);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        List<TagInfoResponse> actualResponse = client.listTag(apiKey);

        // Verify the result
        assertEquals(expectedResponse.size(), actualResponse.size());
        assertEquals(expectedResponse.get(0).getId(), actualResponse.get(0).getId());
        assertEquals(expectedResponse.get(0).getName(), actualResponse.get(0).getName());
        assertEquals(expectedResponse.get(1).getId(), actualResponse.get(1).getId());
        assertEquals(expectedResponse.get(1).getName(), actualResponse.get(1).getName());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(DatasetUriConstant.V1_TAGS);
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testUpdateTag() {
        // Prepare test data
        String apiKey = "test-api-key";
        String tagId = "tag-123456";
        String updatedTagName = "Updated Tag Name";

        // Create request
        TagUpdateRequest request = new TagUpdateRequest();
        request.setApiKey(apiKey);
        request.setTagId(tagId);
        request.setName(updatedTagName);

        // Create expected response
        TagInfoResponse expectedResponse = new TagInfoResponse();
        expectedResponse.setId(tagId);
        expectedResponse.setName(updatedTagName);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(TagInfoResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        TagInfoResponse actualResponse = client.updateTag(request);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());

        // Verify WebClient interactions
        verify(webClientMock).patch();
        verify(requestBodyUriSpecMock).uri(DatasetUriConstant.V1_TAGS);
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(TagInfoResponse.class);
    }

    @Test
    public void testDeleteTag() {
        // Prepare test data
        String apiKey = "test-api-key";
        String tagId = "tag-123456";

        // Set up the response mock
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Execute the method
        client.deleteTag(tagId, apiKey);

        // Verify WebClient interactions
        verify(webClientMock).method(HttpMethod.DELETE);
        verify(requestBodyUriSpecMock).uri(DatasetUriConstant.V1_TAGS);
        verify(requestBodySpecMock).bodyValue(any(Map.class));
        verify(responseSpecMock).bodyToMono(Void.class);
    }

    @Test
    public void testDeleteTagWithEmptyTagId() {
        // Prepare test data
        String apiKey = "test-api-key";
        String tagId = "";

        // Execute the method and verify that IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            client.deleteTag(tagId, apiKey);
        });

        // Verify the exception message
        assertEquals("Tag ID must not be null or empty", exception.getMessage());

        // Verify that no WebClient interactions occurred
        verifyNoInteractions(webClientMock);
    }

    @Test
    public void testDeleteTagWithNullTagId() {
        // Prepare test data
        String apiKey = "test-api-key";
        String tagId = null;

        // Execute the method and verify that IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            client.deleteTag(tagId, apiKey);
        });

        // Verify the exception message
        assertEquals("Tag ID must not be null or empty", exception.getMessage());

        // Verify that no WebClient interactions occurred
        verifyNoInteractions(webClientMock);
    }

    @Test
    public void testBindingTag() {
        // Prepare test data
        String apiKey = "test-api-key";
        List<String> tagIds = List.of("tag-123456");
        String targetId = "dataset-123456";

        // Create request
        TagBindingRequest request = new TagBindingRequest();
        request.setApiKey(apiKey);
        request.setTagIds(tagIds);
        request.setTargetId(targetId);

        // Set up the response mock
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Execute the method
        client.bindingTag(request);

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(DatasetUriConstant.V1_TAGS_BINDING);
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(Void.class);
    }

    @Test
    public void testUnbindingTag() {
        // Prepare test data
        String apiKey = "test-api-key";
        String tagId = "tag-123456";
        String targetId = "dataset-123456";

        // Create request
        TagUnbindingRequest request = new TagUnbindingRequest();
        request.setApiKey(apiKey);
        request.setTagId(tagId);
        request.setTargetId(targetId);

        // Set up the response mock
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Execute the method
        client.unbindingTag(request);

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(DatasetUriConstant.V1_TAGS_UNBINDING);
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(Void.class);
    }

    @Test
    public void testListDatasetTag() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";

        // Create expected response
        DataSetTagsResponse expectedResponse = new DataSetTagsResponse();
        List<DataSetTagInfo> data = new ArrayList<>();

        // Create tag 1
        DataSetTagInfo tag1 = new DataSetTagInfo();
        tag1.setId("tag-123456");
        tag1.setName("test-tag-1");
        data.add(tag1);

        // Create tag 2
        DataSetTagInfo tag2 = new DataSetTagInfo();
        tag2.setId("tag-789012");
        tag2.setName("test-tag-2");
        data.add(tag2);

        expectedResponse.setData(data);
        expectedResponse.setTotal(2);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DataSetTagsResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DataSetTagsResponse actualResponse = client.listDatasetTag(datasetId, apiKey);

        // Verify the result
        assertEquals(expectedResponse.getTotal(), actualResponse.getTotal());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getName(), actualResponse.getData().get(0).getName());
        assertEquals(expectedResponse.getData().get(1).getId(), actualResponse.getData().get(1).getId());
        assertEquals(expectedResponse.getData().get(1).getName(), actualResponse.getData().get(1).getName());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_DATASET_TAGS), eq(datasetId));
        verify(responseSpecMock).bodyToMono(DataSetTagsResponse.class);
    }

    @Test
    public void testChangeDocumentStatus() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        Set<String> documentIds = Set.of("document-123456", "document-789012");

        // Set up the response mock
        DatasetStatusResponse expectedResponse = new DatasetStatusResponse();
        expectedResponse.setResult("success");

        when(responseSpecMock.bodyToMono(DatasetStatusResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DatasetStatusResponse actualResponse = client.changeDocumentStatus(datasetId, documentIds, DocActionEnum.enable, apiKey);

        // Verify the result
        assertEquals(expectedResponse.getResult(), actualResponse.getResult());

        // Verify WebClient interactions
        verify(webClientMock).patch();
        verify(requestBodyUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENT_STATUS), eq(datasetId), eq(DocActionEnum.enable.name()));
        verify(requestBodySpecMock).bodyValue(argThat(body -> {
            if (body instanceof Map) {
                Map<String, Set<String>> bodyMap = (Map<String, Set<String>>) body;
                return bodyMap.containsKey("document_ids") && bodyMap.get("document_ids").equals(documentIds);
            }
            return false;
        }));
        verify(responseSpecMock).bodyToMono(DatasetStatusResponse.class);
    }

    @Test
    public void testGetDocument() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";

        // Create expected response
        DocumentInfo expectedResponse = new DocumentInfo();
        expectedResponse.setId(documentId);
        expectedResponse.setName("Test Document");
        expectedResponse.setDataSourceType("text");
        expectedResponse.setWordCount("1000");

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DocumentInfo.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DocumentInfo actualResponse = client.getDocument(datasetId, documentId, apiKey);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getDataSourceType(), actualResponse.getDataSourceType());
        assertEquals(expectedResponse.getWordCount(), actualResponse.getWordCount());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENT_URL), eq(datasetId), eq(documentId));
        verify(requestHeadersSpecMock).headers(any(Consumer.class));
        verify(responseSpecMock).bodyToMono(DocumentInfo.class);
    }

    @Test
    public void testGetDocumentWithMetadata() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String metadata = "only";

        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("metadata"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(anyString(), anyString())).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create expected response
        DocumentInfo expectedResponse = new DocumentInfo();
        expectedResponse.setId(documentId);
        expectedResponse.setName("Test Document");
        expectedResponse.setDataSourceType("text");
        expectedResponse.setWordCount("1000");

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DocumentInfo.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DocumentInfo actualResponse = client.getDocument(datasetId, documentId, metadata, apiKey);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getDataSourceType(), actualResponse.getDataSourceType());
        assertEquals(expectedResponse.getWordCount(), actualResponse.getWordCount());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(responseSpecMock).bodyToMono(DocumentInfo.class);
    }

    @Test
    public void testGetSegment() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";
        String documentId = "document-123456";
        String segmentId = "segment-123456";

        // Create expected response
        SegmentData expectedResponse = new SegmentData();
        expectedResponse.setId(segmentId);
        expectedResponse.setDocumentId(documentId);
        expectedResponse.setContent("Segment content");
        expectedResponse.setWordCount(100);
        expectedResponse.setKeywords(List.of("keyword1", "keyword2"));

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(SegmentData.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        SegmentData actualResponse = client.getSegment(datasetId, documentId, segmentId, apiKey);

        // Verify the result
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getDocumentId(), actualResponse.getDocumentId());
        assertEquals(expectedResponse.getContent(), actualResponse.getContent());
        assertEquals(expectedResponse.getWordCount(), actualResponse.getWordCount());
        assertEquals(expectedResponse.getKeywords(), actualResponse.getKeywords());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL), eq(datasetId), eq(documentId), eq(segmentId));
        verify(requestHeadersSpecMock).headers(any(Consumer.class));
        verify(responseSpecMock).bodyToMono(SegmentData.class);
    }
}
