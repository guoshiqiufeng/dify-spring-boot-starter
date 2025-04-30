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
package io.github.guoshiqiufeng.dify.client.spring6.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyRestLoggingInterceptor}.
 *
 * @author yanghq
 * @version 0.11.0
 * @since 2025/4/29 17:50
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DifyRestLoggingInterceptorTest {

    private DifyRestLoggingInterceptor interceptor;
    private HttpRequest mockRequest;
    private ClientHttpRequestExecution mockExecution;
    private ClientHttpResponse mockResponse;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        interceptor = new DifyRestLoggingInterceptor();
        mockRequest = mock(HttpRequest.class);
        mockExecution = mock(ClientHttpRequestExecution.class);
        mockResponse = mock(ClientHttpResponse.class);

        // 设置日志记录器捕获
        logger = (Logger) LoggerFactory.getLogger(DifyRestLoggingInterceptor.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        logger.setLevel(Level.DEBUG);
    }

    @AfterEach
    void tearDown() {
        // 移除日志捕获器
        logger.detachAppender(listAppender);
    }

    @Test
    @Order(2)
    @DisplayName("testIntercept")
    void testIntercept() throws IOException {
        // 设置请求模拟
        URI uri = URI.create("http://example.com/api/test");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        byte[] requestBody = "{\"key\":\"value\"}".getBytes(StandardCharsets.UTF_8);

        when(mockRequest.getURI()).thenReturn(uri);
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);
        when(mockRequest.getHeaders()).thenReturn(headers);

        // 设置响应模拟
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");
        when(mockResponse.getStatusCode()).thenReturn(HttpStatusCode.valueOf(200));
        when(mockResponse.getHeaders()).thenReturn(responseHeaders);

        // 模拟响应体
        String responseBody = "{\"result\":\"success\"}";
        InputStream responseBodyStream = new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8));
        when(mockResponse.getBody()).thenReturn(responseBodyStream);

        // 执行拦截器
        when(mockExecution.execute(mockRequest, requestBody)).thenReturn(mockResponse);
        ClientHttpResponse result = interceptor.intercept(mockRequest, requestBody, mockExecution);

        // 验证执行
        verify(mockExecution, times(1)).execute(mockRequest, requestBody);

        // 验证日志
        assertTrue(listAppender.list.size() >= 2, "应该记录请求和响应日志");

        // 确保返回的响应是BufferedClientHttpResponse
        assertNotNull(result);
        assertInstanceOf(DifyRestLoggingInterceptor.BufferedClientHttpResponse.class, result);

        // 验证请求日志
        boolean hasRequestLog = listAppender.list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains("logRequest") &&
                        event.getFormattedMessage().contains(uri.toString()));
        assertTrue(hasRequestLog, "请求日志应包含URL和方法信息");

        // 验证响应日志
        boolean hasResponseLog = listAppender.list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains("logResponse") &&
                        event.getFormattedMessage().contains("200"));
        assertTrue(hasResponseLog, "响应日志应包含状态码信息");
    }

    @Test
    @Order(3)
    @DisplayName("testBufferedResponse")
    void testBufferedResponse() throws IOException {
        // 创建模拟原始响应
        ClientHttpResponse originalResponse = mock(ClientHttpResponse.class);
        when(originalResponse.getStatusCode()).thenReturn(HttpStatusCode.valueOf(200));
        when(originalResponse.getStatusText()).thenReturn("OK");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        when(originalResponse.getHeaders()).thenReturn(headers);

        // 创建缓冲响应
        byte[] cachedBody = "{\"result\":\"success\"}".getBytes(StandardCharsets.UTF_8);
        DifyRestLoggingInterceptor.BufferedClientHttpResponse bufferedResponse =
                new DifyRestLoggingInterceptor.BufferedClientHttpResponse(originalResponse, cachedBody);

        // 验证方法
        assertEquals(HttpStatusCode.valueOf(200), bufferedResponse.getStatusCode());
        assertEquals("OK", bufferedResponse.getStatusText());
        assertEquals(headers, bufferedResponse.getHeaders());

        // 验证读取多次响应体
        InputStream firstRead = bufferedResponse.getBody();
        byte[] firstReadBytes = firstRead.readAllBytes();
        assertArrayEquals(cachedBody, firstReadBytes);

        // 确认可以多次读取
        InputStream secondRead = bufferedResponse.getBody();
        byte[] secondReadBytes = secondRead.readAllBytes();
        assertArrayEquals(cachedBody, secondReadBytes);

        // 确认关闭方法被调用
        bufferedResponse.close();
        verify(originalResponse, times(1)).close();
    }
}
