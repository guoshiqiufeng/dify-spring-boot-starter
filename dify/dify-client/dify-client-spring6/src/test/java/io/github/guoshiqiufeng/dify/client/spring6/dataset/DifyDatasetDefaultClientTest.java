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

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.dto.request.DatasetCreateRequest;
import io.github.guoshiqiufeng.dify.dataset.dto.request.DatasetPageDocumentRequest;
import io.github.guoshiqiufeng.dify.dataset.dto.request.DatasetPageRequest;
import io.github.guoshiqiufeng.dify.dataset.dto.request.DocumentCreateByTextRequest;
import io.github.guoshiqiufeng.dify.dataset.dto.response.DatasetResponse;
import io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentCreateResponse;
import io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentInfo;
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/21 14:50
 */
@DisplayName("DifyDatasetDefaultClient Tests")
public class DifyDatasetDefaultClientTest {

    @Test
    @DisplayName("Test create dataset method")
    public void testCreate() {
        // Create mock objects
        RestClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = Mockito.mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // Setup mock behavior for RequestBodyUriSpec
        doReturn(requestBodyUriSpec).when(mockRestClient).post();
        when(requestBodyUriSpec.uri(eq("/v1/datasets"))).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).body(any(DatasetCreateRequest.class));
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

        // Mock the response
        DatasetResponse mockResponse = new DatasetResponse();
        mockResponse.setId("dataset-123");
        mockResponse.setName("Test Dataset");
        mockResponse.setDescription("This is a test dataset");
        when(responseSpec.body(DatasetResponse.class)).thenReturn(mockResponse);

        // Create the client with mocked dependencies
        String baseUrl = "https://api.dify.ai";
        DifyDatasetDefaultClient client = new DifyDatasetDefaultClient(baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

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
        verify(requestBodyUriSpec).uri("/v1/datasets");
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
        // Create mock objects
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // Setup mock behavior for RequestHeadersUriSpec
        doReturn(requestHeadersUriSpec).when(mockRestClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(eq("/v1/datasets?page={page}&limit={limit}"), eq(1), eq(10));
        when(requestHeadersSpec.headers(any(Consumer.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

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

        // Create the client with mocked dependencies
        String baseUrl = "https://api.dify.ai";
        DifyDatasetDefaultClient client = new DifyDatasetDefaultClient(baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

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
        verify(requestHeadersUriSpec).uri("/v1/datasets?page={page}&limit={limit}", 1, 10);
        verify(requestHeadersSpec).headers(any(Consumer.class));
    }

    @Test
    @DisplayName("Test delete dataset method")
    public void testDelete() {
        // Create mock objects
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // Setup mock behavior for RequestHeadersUriSpec
        doReturn(requestHeadersUriSpec).when(mockRestClient).delete();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(eq("/v1/datasets/{datasetId}"), eq("dataset-123"));
        when(requestHeadersSpec.headers(any(Consumer.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);
        when(responseSpec.body(Void.class)).thenReturn(null);

        // Create the client with mocked dependencies
        String baseUrl = "https://api.dify.ai";
        DifyDatasetDefaultClient client = new DifyDatasetDefaultClient(baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

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
        // Create mock objects
        RestClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = Mockito.mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // Setup mock behavior for RequestBodyUriSpec
        doReturn(requestBodyUriSpec).when(mockRestClient).post();
        doReturn(requestBodySpec).when(requestBodyUriSpec).uri(eq("/v1/datasets/{datasetId}/document/create-by-text"), eq("dataset-123"));
        when(requestBodySpec.headers(any(Consumer.class))).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).body(any(DocumentCreateByTextRequest.class));
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

        // Mock the response
        DocumentCreateResponse mockResponse = new DocumentCreateResponse();
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc-123");
        documentInfo.setName("Test Document");
        mockResponse.setDocument(documentInfo);
        mockResponse.setBatch("batch-1");

        when(responseSpec.body(DocumentCreateResponse.class)).thenReturn(mockResponse);

        // Create the client with mocked dependencies
        String baseUrl = "https://api.dify.ai";
        DifyDatasetDefaultClient client = new DifyDatasetDefaultClient(baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

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
        // Create mock objects
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // Setup mock behavior for RequestHeadersUriSpec
        doReturn(requestHeadersUriSpec).when(mockRestClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(
                eq("/v1/datasets/{datasetId}/documents?page={page}&limit={limit}&keyword={keyword}"),
                eq("dataset-123"), eq(1), eq(10), eq("test"));
        when(requestHeadersSpec.headers(any(Consumer.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

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

        // Create the client with mocked dependencies
        String baseUrl = "https://api.dify.ai";
        DifyDatasetDefaultClient client = new DifyDatasetDefaultClient(baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

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
}
