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
package io.github.guoshiqiufeng.dify.status.checker;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.dto.response.DatasetResponse;
import io.github.guoshiqiufeng.dify.dataset.dto.response.TagInfoResponse;
import io.github.guoshiqiufeng.dify.dataset.dto.response.TextEmbeddingListResponse;
import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * DifyDatasetStatusChecker test
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2026/01/15
 */
class DifyDatasetStatusCheckerTest {

    @Mock
    private DifyDataset difyDataset;

    private DifyDatasetStatusChecker checker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checker = new DifyDatasetStatusChecker(difyDataset);
    }

    @Test
    void testGetClientName() {
        assertEquals("DifyDataset", checker.getClientName());
    }

    @Test
    void testCheckStatus_Page_Success() {
        DifyPageResult<DatasetResponse> response = new DifyPageResult<DatasetResponse>();
        when(difyDataset.page(any())).thenReturn(response);

        ApiStatusResult result = checker.checkStatus("page", "test-api-key");

        assertEquals("page", result.getMethodName());
        assertEquals("/datasets", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
        assertNotNull(result.getResponseTimeMs());
        assertNotNull(result.getCheckTime());
    }

    @Test
    void testCheckStatus_ListTextEmbedding_Success() {
        TextEmbeddingListResponse response = new TextEmbeddingListResponse();
        response.setData(Collections.emptyList());
        when(difyDataset.listTextEmbedding(anyString())).thenReturn(response);

        ApiStatusResult result = checker.checkStatus("listTextEmbedding", "test-api-key");

        assertEquals("listTextEmbedding", result.getMethodName());
        assertEquals("/text-embeddings", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
    }

    @Test
    void testCheckStatus_ListRerank_Success() {
        TextEmbeddingListResponse response = new TextEmbeddingListResponse();
        response.setData(Collections.emptyList());
        when(difyDataset.listRerank(anyString())).thenReturn(response);

        ApiStatusResult result = checker.checkStatus("listRerank", "test-api-key");

        assertEquals("listRerank", result.getMethodName());
        assertEquals("/rerank", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
    }

    @Test
    void testCheckStatus_ListTag_Success() {
        List<TagInfoResponse> response = new ArrayList<>();
        when(difyDataset.listTag(anyString())).thenReturn(response);

        ApiStatusResult result = checker.checkStatus("listTag", "test-api-key");

        assertEquals("listTag", result.getMethodName());
        assertEquals("/tags", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
    }

    @Test
    void testCheckStatus_NotFound() {
        when(difyDataset.page(any()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("page", "test-api-key");

        assertEquals("page", result.getMethodName());
        assertEquals(ApiStatus.NOT_FOUND_404, result.getStatus());
        assertEquals(404, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_Unauthorized() {
        when(difyDataset.page(any()))
                .thenThrow(HttpClientErrorException.Unauthorized.create(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("page", "test-api-key");

        assertEquals("page", result.getMethodName());
        assertEquals(ApiStatus.UNAUTHORIZED_401, result.getStatus());
        assertEquals(401, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_ServerError() {
        when(difyDataset.page(any()))
                .thenThrow(HttpServerErrorException.InternalServerError.create(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal Server Error",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("page", "test-api-key");

        assertEquals("page", result.getMethodName());
        assertEquals(ApiStatus.SERVER_ERROR, result.getStatus());
        assertEquals(500, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_UnknownMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            checker.checkStatus("unknownMethod", "test-api-key");
        });
    }

    @Test
    void testCheckAllApis_AllSuccess() {
        DifyPageResult<DatasetResponse> pageResponse = new DifyPageResult<DatasetResponse>();
        TextEmbeddingListResponse embeddingResponse = new TextEmbeddingListResponse();
        embeddingResponse.setData(Collections.emptyList());
        TextEmbeddingListResponse rerankResponse = new TextEmbeddingListResponse();
        rerankResponse.setData(Collections.emptyList());
        List<TagInfoResponse> tagResponse = new ArrayList<>();

        when(difyDataset.page(any())).thenReturn(pageResponse);
        when(difyDataset.listTextEmbedding(anyString())).thenReturn(embeddingResponse);
        when(difyDataset.listRerank(anyString())).thenReturn(rerankResponse);
        when(difyDataset.listTag(anyString())).thenReturn(tagResponse);

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertEquals("DifyDataset", report.getClientName());
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(4, report.getTotalApis());
        assertEquals(4, report.getNormalApis());
        assertEquals(0, report.getErrorApis());
        assertNotNull(report.getReportTime());
        assertNotNull(report.getStatusSummary());
    }

    @Test
    void testCheckAllApis_SomeErrors() {
        DifyPageResult<DatasetResponse> pageResponse = new DifyPageResult<DatasetResponse>();
        when(difyDataset.page(any())).thenReturn(pageResponse);
        when(difyDataset.listTextEmbedding(anyString()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));
        TextEmbeddingListResponse rerankResponse = new TextEmbeddingListResponse();
        rerankResponse.setData(Collections.emptyList());
        when(difyDataset.listRerank(anyString())).thenReturn(rerankResponse);
        List<TagInfoResponse> tagResponse = new ArrayList<>();

        when(difyDataset.listTag(anyString())).thenReturn(tagResponse);

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertEquals("DifyDataset", report.getClientName());
        assertEquals(ApiStatus.CLIENT_ERROR, report.getOverallStatus());
        assertEquals(4, report.getTotalApis());
        assertEquals(3, report.getNormalApis());
        assertEquals(1, report.getErrorApis());
    }

    @Test
    void testCheckAllApis_AllErrors() {
        when(difyDataset.page(any()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyDataset.listTextEmbedding(anyString()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyDataset.listRerank(anyString()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyDataset.listTag(anyString()))
                .thenThrow(new RuntimeException("Server Error"));

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertEquals("DifyDataset", report.getClientName());
        assertEquals(ApiStatus.SERVER_ERROR, report.getOverallStatus());
        assertEquals(4, report.getTotalApis());
        assertEquals(0, report.getNormalApis());
        assertEquals(4, report.getErrorApis());
    }

    @Test
    void testCheckAllApis_ResponseTime() {
        DifyPageResult<DatasetResponse> pageResponse = new DifyPageResult<DatasetResponse>();
        when(difyDataset.page(any())).thenReturn(pageResponse);
        TextEmbeddingListResponse embeddingResponse = new TextEmbeddingListResponse();
        embeddingResponse.setData(Collections.emptyList());
        when(difyDataset.listTextEmbedding(anyString())).thenReturn(embeddingResponse);
        TextEmbeddingListResponse rerankResponse = new TextEmbeddingListResponse();
        rerankResponse.setData(Collections.emptyList());
        when(difyDataset.listRerank(anyString())).thenReturn(rerankResponse);
        List<TagInfoResponse> tagResponse = new ArrayList<>();

        when(difyDataset.listTag(anyString())).thenReturn(tagResponse);

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertNotNull(report.getApiStatuses());
        report.getApiStatuses().forEach(apiStatus -> {
            assertNotNull(apiStatus.getResponseTimeMs());
            assertTrue(apiStatus.getResponseTimeMs() >= 0);
        });
    }

    @Test
    void testCheckAllApis_StatusSummary() {
        DifyPageResult<DatasetResponse> pageResponse = new DifyPageResult<DatasetResponse>();
        when(difyDataset.page(any())).thenReturn(pageResponse);
        when(difyDataset.listTextEmbedding(anyString()))
                .thenThrow(HttpClientErrorException.Unauthorized.create(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        null,
                        null,
                        null
                ));
        when(difyDataset.listRerank(anyString()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));
        List<TagInfoResponse> tagResponse = new ArrayList<>();

        when(difyDataset.listTag(anyString())).thenReturn(tagResponse);

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertNotNull(report.getStatusSummary());
        assertTrue(report.getStatusSummary().containsKey(ApiStatus.NORMAL));
        assertTrue(report.getStatusSummary().containsKey(ApiStatus.UNAUTHORIZED_401));
        assertTrue(report.getStatusSummary().containsKey(ApiStatus.NOT_FOUND_404));
        assertEquals(2, report.getStatusSummary().get(ApiStatus.NORMAL));
        assertEquals(1, report.getStatusSummary().get(ApiStatus.UNAUTHORIZED_401));
        assertEquals(1, report.getStatusSummary().get(ApiStatus.NOT_FOUND_404));
    }
}
