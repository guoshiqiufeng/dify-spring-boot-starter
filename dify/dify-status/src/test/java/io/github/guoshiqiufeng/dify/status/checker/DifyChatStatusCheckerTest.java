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
package io.github.guoshiqiufeng.dify.status.checker;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.dto.response.AppInfoResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.AppMetaResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.AppParametersResponseVO;
import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * DifyChatStatusChecker test
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 15:00
 */
class DifyChatStatusCheckerTest {

    @Mock
    private DifyChat difyChat;

    private DifyChatStatusChecker checker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        checker = new DifyChatStatusChecker(difyChat);
    }

    @Test
    void testGetClientName() {
        assertEquals("DifyChat", checker.getClientName());
    }

    @Test
    void testCheckStatus_Parameters_Success() {
        when(difyChat.parameters(anyString())).thenReturn(new AppParametersResponseVO());

        ApiStatusResult result = checker.checkStatus("parameters", "test-api-key");

        assertEquals("parameters", result.getMethodName());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
        assertNotNull(result.getResponseTimeMs());
        assertNotNull(result.getCheckTime());
    }

    @Test
    void testCheckStatus_Info_Success() {
        when(difyChat.info(anyString())).thenReturn(new AppInfoResponse());

        ApiStatusResult result = checker.checkStatus("info", "test-api-key");

        assertEquals("info", result.getMethodName());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
    }

    @Test
    void testCheckStatus_Meta_Success() {
        when(difyChat.meta(anyString())).thenReturn(new AppMetaResponse());

        ApiStatusResult result = checker.checkStatus("meta", "test-api-key");

        assertEquals("meta", result.getMethodName());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
    }

    @Test
    void testCheckStatus_NotFound() {
        when(difyChat.parameters(anyString()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("parameters", "test-api-key");

        assertEquals("parameters", result.getMethodName());
        assertEquals(ApiStatus.NOT_FOUND_404, result.getStatus());
        assertEquals(404, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testCheckStatus_Unauthorized() {
        when(difyChat.parameters(anyString()))
                .thenThrow(HttpClientErrorException.Unauthorized.create(
                        org.springframework.http.HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        null,
                        null,
                        null
                ));

        ApiStatusResult result = checker.checkStatus("parameters", "test-api-key");

        assertEquals("parameters", result.getMethodName());
        assertEquals(ApiStatus.UNAUTHORIZED_401, result.getStatus());
        assertEquals(401, result.getHttpStatusCode());
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
        when(difyChat.parameters(anyString())).thenReturn(new AppParametersResponseVO());
        when(difyChat.info(anyString())).thenReturn(new AppInfoResponse());
        when(difyChat.meta(anyString())).thenReturn(new AppMetaResponse());

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertEquals("DifyChat", report.getClientName());
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertTrue(report.getTotalApis() > 0);
        assertEquals(report.getTotalApis(), report.getNormalApis());
        assertEquals(0, report.getErrorApis());
        assertNotNull(report.getReportTime());
    }

    @Test
    void testCheckAllApis_SomeErrors() {
        when(difyChat.parameters(anyString())).thenReturn(new AppParametersResponseVO());
        when(difyChat.info(anyString()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null
                ));
        when(difyChat.meta(anyString())).thenReturn(new AppMetaResponse());

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertEquals("DifyChat", report.getClientName());
        assertNotEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertTrue(report.getErrorApis() > 0);
        assertTrue(report.getNormalApis() > 0);
    }

    @Test
    void testCheckAllApis_AllErrors() {
        when(difyChat.parameters(anyString()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyChat.info(anyString()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyChat.meta(anyString()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyChat.site(anyString()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyChat.conversations(any()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyChat.messages(any()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyChat.feedbacks(any()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyChat.pageAppAnnotation(any()))
                .thenThrow(new RuntimeException("Server Error"));
        when(difyChat.conversationVariables(any()))
                .thenThrow(new RuntimeException("Server Error"));

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertEquals("DifyChat", report.getClientName());
        assertNotEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(0, report.getNormalApis());
        assertTrue(report.getErrorApis() > 0);
    }

    @Test
    void testCheckAllApis_ResponseTime() {
        when(difyChat.parameters(anyString())).thenReturn(new AppParametersResponseVO());

        ClientStatusReport report = checker.checkAllApis("test-api-key");

        assertNotNull(report.getApiStatuses());
        report.getApiStatuses().forEach(apiStatus -> {
            assertNotNull(apiStatus.getResponseTimeMs());
            assertTrue(apiStatus.getResponseTimeMs() >= 0);
        });
    }
}
