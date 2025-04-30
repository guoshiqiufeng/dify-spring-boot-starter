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
package io.github.guoshiqiufeng.dify.client.spring6.base;

import io.github.guoshiqiufeng.dify.client.spring6.BaseClientTest;
import io.github.guoshiqiufeng.dify.client.spring6.logging.DifyLoggingControl;
import io.github.guoshiqiufeng.dify.client.spring6.logging.DifyLoggingFilter;
import io.github.guoshiqiufeng.dify.client.spring6.logging.DifyRestLoggingInterceptor;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link BaseDifyDefaultClient}.
 *
 * @author yanghq
 * @version 0.11.0
 * @since 2025/4/29 18:40
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseDifyDefaultClientLoggingTest extends BaseClientTest {

    /**
     * 自定义测试客户端实现，扩展BaseDifyDefaultClient
     */
    static class TestDifyClient extends BaseDifyDefaultClient {
        public TestDifyClient(String baseUrl, DifyProperties.ClientConfig clientConfig,
                              RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
            super(baseUrl, clientConfig, restClientBuilder, webClientBuilder);
        }
    }

    @BeforeEach
    void setUp() {
        super.setup(); // 设置模拟对象
        DifyLoggingControl.getInstance().reset(); // 重置日志控制状态
    }

    @Test
    @Order(1)
    @DisplayName("测试启用日志时添加拦截器")
    void testAddLoggingInterceptors() {
        // 创建配置，启用日志
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setLogging(true);

        // 创建测试客户端
        TestDifyClient client = new TestDifyClient(
                "http://example.com",
                clientConfig,
                restClientMock.getRestClientBuilder(),
                webClientMock.getWebClientBuilder()
        );

        // 验证客户端不为空
        assertNotNull(client);

        // 验证是否为RestClient.Builder添加了拦截器
        verify(restClientMock.getRestClientBuilder(), times(1)).requestInterceptor(any(DifyRestLoggingInterceptor.class));

        // 验证是否为WebClient.Builder添加了过滤器
        verify(webClientMock.getWebClientBuilder(), times(1)).filter(any(DifyLoggingFilter.class));
    }

    @Test
    @Order(2)
    @DisplayName("测试禁用日志时不添加拦截器")
    void testNoLoggingInterceptorsWhenDisabled() {
        // 创建配置，禁用日志
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setLogging(false);

        // 创建测试客户端
        TestDifyClient client = new TestDifyClient(
                "http://example.com",
                clientConfig,
                restClientMock.getRestClientBuilder(),
                webClientMock.getWebClientBuilder()
        );

        // 验证客户端不为空
        assertNotNull(client);

        // 验证没有为RestClient.Builder添加拦截器
        verify(restClientMock.getRestClientBuilder(), never()).requestInterceptor(any(DifyRestLoggingInterceptor.class));

        // 验证没有为WebClient.Builder添加过滤器
        verify(webClientMock.getWebClientBuilder(), never()).filter(any(DifyLoggingFilter.class));
    }

    @Test
    @Order(3)
    @DisplayName("测试重复创建客户端时只添加一次拦截器")
    void testAddInterceptorsOnlyOnce() {
        // 创建配置，启用日志
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setLogging(true);

        // 创建第一个测试客户端
        TestDifyClient client1 = new TestDifyClient(
                "http://example.com",
                clientConfig,
                restClientMock.getRestClientBuilder(),
                webClientMock.getWebClientBuilder()
        );
        assertNotNull(client1);

        // 创建第二个测试客户端
        TestDifyClient client2 = new TestDifyClient(
                "http://example.com",
                clientConfig,
                restClientMock.getRestClientBuilder(),
                webClientMock.getWebClientBuilder()
        );
        assertNotNull(client2);

        // 验证RestClient.Builder的拦截器只添加了一次
        verify(restClientMock.getRestClientBuilder(), times(1)).requestInterceptor(any(DifyRestLoggingInterceptor.class));

        // 验证WebClient.Builder的过滤器只添加了一次
        verify(webClientMock.getWebClientBuilder(), times(1)).filter(any(DifyLoggingFilter.class));
    }

    @Test
    @Order(4)
    @DisplayName("测试重置后再次添加拦截器")
    void testAddInterceptorsAfterReset() {
        // 创建配置，启用日志
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setLogging(true);

        // 创建第一个测试客户端
        TestDifyClient client1 = new TestDifyClient(
                "http://example.com",
                clientConfig,
                restClientMock.getRestClientBuilder(),
                webClientMock.getWebClientBuilder()
        );
        assertNotNull(client1);

        // 重置日志控制状态
        DifyLoggingControl.getInstance().reset();

        // 创建第二个测试客户端
        TestDifyClient client2 = new TestDifyClient(
                "http://example.com",
                clientConfig,
                restClientMock.getRestClientBuilder(),
                webClientMock.getWebClientBuilder()
        );
        assertNotNull(client2);

        // 验证RestClient.Builder的拦截器添加了两次
        verify(restClientMock.getRestClientBuilder(), times(2)).requestInterceptor(any(DifyRestLoggingInterceptor.class));

        // 验证WebClient.Builder的过滤器添加了两次
        verify(webClientMock.getWebClientBuilder(), times(2)).filter(any(DifyLoggingFilter.class));
    }
}
