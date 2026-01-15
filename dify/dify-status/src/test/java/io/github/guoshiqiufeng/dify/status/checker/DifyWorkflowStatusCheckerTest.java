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
import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowLogs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * DifyWorkflowStatusChecker test
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2026/01/15
 */
class DifyWorkflowStatusCheckerTest {

    @Mock
    private DifyWorkflow difyWorkflow;

    private DifyWorkflowStatusChecker checker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checker = new DifyWorkflowStatusChecker(difyWorkflow);
    }

    @Test
    void testGetClientName() {
        assertEquals("DifyWorkflow", checker.getClientName());
    }

    @Test
    void testCheckStatus_Logs_Success() {
        DifyPageResult<WorkflowLogs> response = new DifyPageResult<WorkflowLogs>();
        response.setData(Collections.emptyList());
        when(difyWorkflow.logs(any())).thenReturn(response);

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        assertEquals("logs", result.getMethodName());
        assertEquals("/logs", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
        assertNotNull(result.getResponseTimeMs());
        assertNotNull(result.getCheckTime());
    }

    @Test
    void testCheckStatus_NotFound() {
        when(difyWorkflow.logs(any()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        assertEquals("logs", result.getMethodName());
        assertEquals(ApiStatus.NOT_FOUND_404, result.getStatus());
        assertEquals(404, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_Unauthorized() {
        when(difyWorkflow.logs(any()))
                .thenThrow(HttpClientErrorException.Unauthorized.create(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        assertEquals("logs", result.getMethodName());
        assertEquals(ApiStatus.UNAUTHORIZED_401, result.getStatus());
        assertEquals(401, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_BadRequest() {
        when(difyWorkflow.logs(any()))
                .thenThrow(HttpClientErrorException.BadRequest.create(
                        org.springframework.http.HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        assertEquals("logs", result.getMethodName());
        assertEquals(ApiStatus.CLIENT_ERROR, result.getStatus());
        assertEquals(400, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_Forbidden() {
        when(difyWorkflow.logs(any()))
                .thenThrow(HttpClientErrorException.Forbidden.create(
                        org.springframework.http.HttpStatus.FORBIDDEN,
                        "Forbidden",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        assertEquals("logs", result.getMethodName());
        assertEquals(ApiStatus.CLIENT_ERROR, result.getStatus());
        assertEquals(403, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_ServerError() {
        when(difyWorkflow.logs(any()))
                .thenThrow(HttpServerErrorException.InternalServerError.create(
                        org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal Server Error",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        assertEquals("logs", result.getMethodName());
        assertEquals(ApiStatus.SERVER_ERROR, result.getStatus());
        assertEquals(500, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_ServiceUnavailable() {
        when(difyWorkflow.logs(any()))
                .thenThrow(HttpServerErrorException.ServiceUnavailable.create(
                        org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                        "Service Unavailable",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        assertEquals("logs", result.getMethodName());
        assertEquals(ApiStatus.SERVER_ERROR, result.getStatus());
        assertEquals(503, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_Timeout_ResourceAccessException() {
        when(difyWorkflow.logs(any()))
                .thenThrow(new ResourceAccessException("Connection timeout", new SocketTimeoutException("timeout")));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        assertEquals("logs", result.getMethodName());
        assertEquals(ApiStatus.TIMEOUT, result.getStatus());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("timeout"));
    }

    @Test
    void testCheckStatus_Timeout_TimeoutException() {
        when(difyWorkflow.logs(any()))
                .thenThrow(new RuntimeException("Timeout", new TimeoutException("Request timeout")));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        // TimeoutException wrapped in RuntimeException will be caught as UNKNOWN_ERROR
        assertEquals("logs", result.getMethodName());
        assertEquals(ApiStatus.UNKNOWN_ERROR, result.getStatus());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_Timeout_SocketTimeoutException() {
        when(difyWorkflow.logs(any()))
                .thenThrow(new RuntimeException("Socket timeout", new SocketTimeoutException("Read timed out")));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        // SocketTimeoutException wrapped in RuntimeException will be caught as UNKNOWN_ERROR
        assertEquals("logs", result.getMethodName());
        assertEquals(ApiStatus.UNKNOWN_ERROR, result.getStatus());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_UnknownError() {
        when(difyWorkflow.logs(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        assertEquals("logs", result.getMethodName());
        assertEquals(ApiStatus.UNKNOWN_ERROR, result.getStatus());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Unexpected error"));
    }

    @Test
    void testCheckStatus_UnknownMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            checker.checkStatus("unknownMethod", "test-api-key");
        });
    }

    @Test
    void testCheckAllApis_Success() {
        DifyPageResult<WorkflowLogs> response = new DifyPageResult<WorkflowLogs>();
        response.setData(Collections.emptyList());
        when(difyWorkflow.logs(any())).thenReturn(response);

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertEquals("DifyWorkflow", report.getClientName());
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(1, report.getTotalApis());
        assertEquals(1, report.getNormalApis());
        assertEquals(0, report.getErrorApis());
        assertNotNull(report.getReportTime());
        assertNotNull(report.getStatusSummary());
    }

    @Test
    void testCheckAllApis_Error() {
        when(difyWorkflow.logs(any()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertEquals("DifyWorkflow", report.getClientName());
        assertEquals(ApiStatus.SERVER_ERROR, report.getOverallStatus());
        assertEquals(1, report.getTotalApis());
        assertEquals(0, report.getNormalApis());
        assertEquals(1, report.getErrorApis());
    }

    @Test
    void testCheckAllApis_ResponseTime() {
        DifyPageResult<WorkflowLogs> response = new DifyPageResult<WorkflowLogs>();
        response.setData(Collections.emptyList());
        when(difyWorkflow.logs(any())).thenReturn(response);

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertNotNull(report.getApiStatuses());
        report.getApiStatuses().forEach(apiStatus -> {
            assertNotNull(apiStatus.getResponseTimeMs());
            assertTrue(apiStatus.getResponseTimeMs() >= 0);
        });
    }

    @Test
    void testCheckAllApis_StatusSummary() {
        DifyPageResult<WorkflowLogs> response = new DifyPageResult<WorkflowLogs>();
        response.setData(Collections.emptyList());
        when(difyWorkflow.logs(any())).thenReturn(response);

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertNotNull(report.getStatusSummary());
        assertTrue(report.getStatusSummary().containsKey(ApiStatus.NORMAL));
        assertEquals(1, report.getStatusSummary().get(ApiStatus.NORMAL));
    }

    @Test
    void testCheckAllApis_ExceptionHandling() {
        when(difyWorkflow.logs(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertEquals("DifyWorkflow", report.getClientName());
        assertEquals(1, report.getTotalApis());
        assertEquals(0, report.getNormalApis());
        assertEquals(1, report.getErrorApis());

        // Verify the error result was added
        boolean hasUnknownError = report.getApiStatuses().stream()
                .anyMatch(result -> result.getStatus() == ApiStatus.UNKNOWN_ERROR);
        assertTrue(hasUnknownError);
    }

    @Test
    void testCheckStatus_ResponseTimeTracking() {
        DifyPageResult<WorkflowLogs> response = new DifyPageResult<WorkflowLogs>();
        response.setData(Collections.emptyList());
        when(difyWorkflow.logs(any())).thenReturn(response);

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        assertNotNull(result.getResponseTimeMs());
        assertTrue(result.getResponseTimeMs() >= 0);
        // Response time should be reasonable (less than 10 seconds for a mock call)
        assertTrue(result.getResponseTimeMs() < 10000);
    }

    @Test
    void testCheckStatus_ErrorResponseTimeTracking() {
        when(difyWorkflow.logs(any()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("logs", "test-api-key");

        // Even for errors, response time should be tracked
        assertNotNull(result.getResponseTimeMs());
        assertTrue(result.getResponseTimeMs() >= 0);
    }

    @Test
    void testCheckAllApis_MultipleErrorTypes() {
        // Test with different error types to ensure status summary works correctly
        when(difyWorkflow.logs(any()))
                .thenThrow(HttpClientErrorException.Unauthorized.create(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        null,
                        null,
                        null
                ));

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertNotNull(report.getStatusSummary());
        assertTrue(report.getStatusSummary().containsKey(ApiStatus.UNAUTHORIZED_401));
        assertEquals(1, report.getStatusSummary().get(ApiStatus.UNAUTHORIZED_401));
    }
}
