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

import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponseResult;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponse;
import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * DifyServerStatusChecker test
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2026/01/15
 */
class DifyServerStatusCheckerTest {

    @Mock
    private DifyServer difyServer;

    private DifyServerStatusChecker checker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checker = new DifyServerStatusChecker(difyServer);
    }

    @Test
    void testGetClientName() {
        assertEquals("DifyServer", checker.getClientName());
    }

    @Test
    void testGetDifyServer() {
        assertNotNull(checker.getDifyServer());
        assertEquals(difyServer, checker.getDifyServer());
    }

    @Test
    void testCheckStatus_Apps_Success() {
        AppsResponseResult response = new AppsResponseResult();
        response.setData(Collections.emptyList());
        when(difyServer.apps(any())).thenReturn(response);

        ApiStatusResult result = checker.checkStatus("apps", null);

        assertEquals("apps", result.getMethodName());
        assertEquals("/apps", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
        assertNotNull(result.getResponseTimeMs());
        assertNotNull(result.getCheckTime());
    }

    @Test
    void testCheckStatus_GetDatasetApiKey_Success() {
        List<DatasetApiKeyResponse> response = new ArrayList<>();
        when(difyServer.getDatasetApiKey()).thenReturn(response);

        ApiStatusResult result = checker.checkStatus("getDatasetApiKey", null);

        assertEquals("getDatasetApiKey", result.getMethodName());
        assertEquals("/datasets/api-keys", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
    }

    @Test
    void testCheckStatus_NotFound() {
        when(difyServer.apps(any()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("apps", null);

        assertEquals("apps", result.getMethodName());
        assertEquals(ApiStatus.NOT_FOUND_404, result.getStatus());
        assertEquals(404, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_Unauthorized() {
        when(difyServer.apps(any()))
                .thenThrow(HttpClientErrorException.Unauthorized.create(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("apps", null);

        assertEquals("apps", result.getMethodName());
        assertEquals(ApiStatus.UNAUTHORIZED_401, result.getStatus());
        assertEquals(401, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_BadRequest() {
        when(difyServer.apps(any()))
                .thenThrow(HttpClientErrorException.BadRequest.create(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("apps", null);

        assertEquals("apps", result.getMethodName());
        assertEquals(ApiStatus.CLIENT_ERROR, result.getStatus());
        assertEquals(400, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_ServerError() {
        when(difyServer.apps(any()))
                .thenThrow(HttpServerErrorException.InternalServerError.create(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal Server Error",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("apps", null);

        assertEquals("apps", result.getMethodName());
        assertEquals(ApiStatus.SERVER_ERROR, result.getStatus());
        assertEquals(500, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_Timeout() {
        when(difyServer.apps(any()))
                .thenThrow(new ResourceAccessException("Connection timeout", new SocketTimeoutException("timeout")));

        ApiStatusResult result = checker.checkStatus("apps", null);

        assertEquals("apps", result.getMethodName());
        assertEquals(ApiStatus.TIMEOUT, result.getStatus());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("timeout"));
    }

    @Test
    void testCheckStatus_UnknownError() {
        when(difyServer.apps(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        ApiStatusResult result = checker.checkStatus("apps", null);

        assertEquals("apps", result.getMethodName());
        assertEquals(ApiStatus.UNKNOWN_ERROR, result.getStatus());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Unexpected error"));
    }

    @Test
    void testCheckStatus_UnknownMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            checker.checkStatus("unknownMethod", null);
        });
    }

    @Test
    void testCheckAllApis_AllSuccess() {
        AppsResponseResult appsResponse = new AppsResponseResult();
        appsResponse.setData(Collections.emptyList());
        List<DatasetApiKeyResponse> apiKeyResponse = new ArrayList<>();

        when(difyServer.apps(any())).thenReturn(appsResponse);
        when(difyServer.getDatasetApiKey()).thenReturn(apiKeyResponse);

        ClientStatusReport report = checker.checkAllApis();

        assertEquals("DifyServer", report.getClientName());
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(2, report.getTotalApis());
        assertEquals(2, report.getNormalApis());
        assertEquals(0, report.getErrorApis());
        assertNotNull(report.getReportTime());
        assertNotNull(report.getStatusSummary());
    }

    @Test
    void testCheckAllApis_SomeErrors() {
        AppsResponseResult appsResponse = new AppsResponseResult();
        appsResponse.setData(Collections.emptyList());
        when(difyServer.apps(any())).thenReturn(appsResponse);
        when(difyServer.getDatasetApiKey())
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));

        ClientStatusReport report = checker.checkAllApis();

        assertEquals("DifyServer", report.getClientName());
        assertEquals(ApiStatus.CLIENT_ERROR, report.getOverallStatus());
        assertEquals(2, report.getTotalApis());
        assertEquals(1, report.getNormalApis());
        assertEquals(1, report.getErrorApis());
    }

    @Test
    void testCheckAllApis_AllErrors() {
        when(difyServer.apps(any()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyServer.getDatasetApiKey())
                .thenThrow(new RuntimeException("Server Error"));

        ClientStatusReport report = checker.checkAllApis();

        assertEquals("DifyServer", report.getClientName());
        assertEquals(ApiStatus.SERVER_ERROR, report.getOverallStatus());
        assertEquals(2, report.getTotalApis());
        assertEquals(0, report.getNormalApis());
        assertEquals(2, report.getErrorApis());
    }

    @Test
    void testCheckAllApis_ResponseTime() {
        AppsResponseResult appsResponse = new AppsResponseResult();
        appsResponse.setData(Collections.emptyList());
        List<DatasetApiKeyResponse> apiKeyResponse = new ArrayList<>();

        when(difyServer.apps(any())).thenReturn(appsResponse);
        when(difyServer.getDatasetApiKey()).thenReturn(apiKeyResponse);

        ClientStatusReport report = checker.checkAllApis();

        assertNotNull(report.getApiStatuses());
        report.getApiStatuses().forEach(apiStatus -> {
            assertNotNull(apiStatus.getResponseTimeMs());
            assertTrue(apiStatus.getResponseTimeMs() >= 0);
        });
    }

    @Test
    void testCheckAllApis_StatusSummary() {
        AppsResponseResult appsResponse = new AppsResponseResult();
        appsResponse.setData(Collections.emptyList());
        when(difyServer.apps(any())).thenReturn(appsResponse);
        when(difyServer.getDatasetApiKey())
                .thenThrow(HttpClientErrorException.Unauthorized.create(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        null,
                        null,
                        null
                ));

        ClientStatusReport report = checker.checkAllApis();

        assertNotNull(report.getStatusSummary());
        assertTrue(report.getStatusSummary().containsKey(ApiStatus.NORMAL));
        assertTrue(report.getStatusSummary().containsKey(ApiStatus.UNAUTHORIZED_401));
        assertEquals(1, report.getStatusSummary().get(ApiStatus.NORMAL));
        assertEquals(1, report.getStatusSummary().get(ApiStatus.UNAUTHORIZED_401));
    }

    @Test
    void testCheckAllApis_ExceptionHandling() {
        // First call succeeds, second throws exception during checkStatus
        AppsResponseResult appsResponse = new AppsResponseResult();
        appsResponse.setData(Collections.emptyList());
        when(difyServer.apps(any())).thenReturn(appsResponse);
        when(difyServer.getDatasetApiKey())
                .thenThrow(new RuntimeException("Unexpected error"));

        ClientStatusReport report = checker.checkAllApis();

        assertEquals("DifyServer", report.getClientName());
        assertEquals(2, report.getTotalApis());
        assertEquals(1, report.getNormalApis());
        assertEquals(1, report.getErrorApis());

        // Verify the error result was added
        boolean hasUnknownError = report.getApiStatuses().stream()
                .anyMatch(result -> result.getStatus() == ApiStatus.UNKNOWN_ERROR);
        assertTrue(hasUnknownError);
    }

    @Test
    void testCheckStatus_ServiceUnavailable() {
        when(difyServer.apps(any()))
                .thenThrow(HttpServerErrorException.ServiceUnavailable.create(
                        org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                        "Service Unavailable",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("apps", null);

        assertEquals("apps", result.getMethodName());
        assertEquals(ApiStatus.SERVER_ERROR, result.getStatus());
        assertEquals(503, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_Forbidden() {
        when(difyServer.apps(any()))
                .thenThrow(HttpClientErrorException.Forbidden.create(
                        org.springframework.http.HttpStatus.FORBIDDEN,
                        "Forbidden",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("apps", null);

        assertEquals("apps", result.getMethodName());
        assertEquals(ApiStatus.CLIENT_ERROR, result.getStatus());
        assertEquals(403, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }
}
