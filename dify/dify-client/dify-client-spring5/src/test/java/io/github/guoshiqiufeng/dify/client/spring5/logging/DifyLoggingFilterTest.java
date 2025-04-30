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
package io.github.guoshiqiufeng.dify.client.spring5.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * DifyLoggingFilter单元测试
 *
 * @author yanghq
 * @version 0.11.0
 * @since 2025/4/29 18:15
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DifyLoggingFilterTest {

    private DifyLoggingFilter filter;
    private ClientRequest mockRequest;
    private ExchangeFunction mockExchangeFunction;
    private ClientResponse mockResponse;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        filter = new DifyLoggingFilter();
        mockRequest = mock(ClientRequest.class);
        mockExchangeFunction = mock(ExchangeFunction.class);
        mockResponse = mock(ClientResponse.class);

        // 设置日志记录器捕获
        logger = (Logger) LoggerFactory.getLogger(DifyLoggingFilter.class);
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
    @Order(1)
    @DisplayName("test Filter")
    void testFilter() {
        // 设置请求模拟
        URI uri = URI.create("http://example.com/api/test");
        HttpMethod method = HttpMethod.POST;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        when(mockRequest.url()).thenReturn(uri);
        when(mockRequest.method()).thenReturn(method);
        when(mockRequest.headers()).thenReturn(headers);

        // 设置响应模拟
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");

        ClientResponse.Headers mockResponseHeaders = mock(ClientResponse.Headers.class);
        when(mockResponseHeaders.asHttpHeaders()).thenReturn(responseHeaders);
        when(mockResponse.headers()).thenReturn(mockResponseHeaders);
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK);

        // 模拟交换函数
        when(mockExchangeFunction.exchange(mockRequest)).thenReturn(Mono.just(mockResponse));

        // 执行过滤器
        filter.filter(mockRequest, mockExchangeFunction).block(); // 阻塞等待Mono完成

        // 验证交换函数被调用
        verify(mockExchangeFunction, times(1)).exchange(mockRequest);

        // 验证日志记录
        assertFalse(listAppender.list.isEmpty(), "应该记录至少一条请求日志");

        // 验证请求日志
        boolean hasRequestLog = listAppender.list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains("logRequest") &&
                        event.getFormattedMessage().contains(uri.toString()));
        assertTrue(hasRequestLog, "请求日志应包含URL和方法信息");
    }

    @Test
    @Order(2)
    @DisplayName("test Filter With Exception")
    void testFilterWithException() {
        // 设置请求模拟
        URI uri = URI.create("http://example.com/api/test");
        HttpMethod method = HttpMethod.POST;
        HttpHeaders headers = new HttpHeaders();

        when(mockRequest.url()).thenReturn(uri);
        when(mockRequest.method()).thenReturn(method);
        when(mockRequest.headers()).thenReturn(headers);

        // 模拟交换函数返回异常
        IOException exception = new IOException("Test exception");
        when(mockExchangeFunction.exchange(mockRequest)).thenReturn(Mono.error(exception));

        // 执行过滤器并断言异常传播
        assertThrows(Exception.class, () -> filter.filter(mockRequest, mockExchangeFunction).block());

        // 验证交换函数被调用
        verify(mockExchangeFunction, times(1)).exchange(mockRequest);

        // 验证请求日志
        boolean hasRequestLog = listAppender.list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains("logRequest"));
        assertTrue(hasRequestLog, "即使出现异常也应记录请求日志");
    }
}
