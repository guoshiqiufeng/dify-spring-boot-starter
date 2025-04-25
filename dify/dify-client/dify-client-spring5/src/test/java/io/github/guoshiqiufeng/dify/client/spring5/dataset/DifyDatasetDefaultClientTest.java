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
import io.github.guoshiqiufeng.dify.dataset.dto.RetrievalModel;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbedding;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbeddingIcon;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbeddingLabel;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbeddingModel;
import io.github.guoshiqiufeng.dify.dataset.enums.SearchMethodEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private DifyDatasetDefaultClient difyDatasetDefaultClient;

    @BeforeEach
    public void setup() {
        super.setup();
        // Create real client with mocked WebClient
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        difyDatasetDefaultClient = new DifyDatasetDefaultClient("https://api.dify.ai", clientConfig, webClientBuilderMock);
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
        DatasetResponse actualResponse = difyDatasetDefaultClient.create(request);

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

        // Create request
        DatasetPageRequest request = new DatasetPageRequest();
        request.setApiKey(apiKey);
        request.setPage(page);
        request.setLimit(limit);

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
        DifyPageResult<DatasetResponse> actualResponse = difyDatasetDefaultClient.page(request);

        // Verify the result
        assertEquals(expectedResponse.getTotal(), actualResponse.getTotal());
        assertEquals(expectedResponse.getPage(), actualResponse.getPage());
        assertEquals(expectedResponse.getLimit(), actualResponse.getLimit());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(1).getName(), actualResponse.getData().get(1).getName());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(DatasetUriConstant.V1_DATASETS_URL + "?page={page}&limit={limit}"), eq(page), eq(limit));
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testDelete() {
        // Prepare test data
        String apiKey = "test-api-key";
        String datasetId = "dataset-123456";

        // Set up the response mock
        when(responseSpecMock.bodyToMono(Void.class)).thenReturn(Mono.empty());

        // Execute the method
        difyDatasetDefaultClient.delete(datasetId, apiKey);

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
        DocumentCreateResponse actualResponse = difyDatasetDefaultClient.createDocumentByText(request);

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
        DocumentCreateResponse actualResponse = difyDatasetDefaultClient.createDocumentByFile(request);

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
        DocumentCreateResponse actualResponse = difyDatasetDefaultClient.updateDocumentByText(request);

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
        when(responseSpecMock.bodyToMono(RetrieveResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        RetrieveResponse actualResponse = difyDatasetDefaultClient.retrieve(request);

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
        TextEmbeddingListResponse actualResponse = difyDatasetDefaultClient.listTextEmbedding(apiKey);

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
}
