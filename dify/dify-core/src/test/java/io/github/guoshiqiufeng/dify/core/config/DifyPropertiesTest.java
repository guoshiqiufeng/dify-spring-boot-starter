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
package io.github.guoshiqiufeng.dify.core.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DifyPropertiesTest {

    @Test
    void testProperties() {
        DifyProperties properties = new DifyProperties();
        properties.setUrl("https://test-dify.example.com");

        assertEquals("https://test-dify.example.com", properties.getUrl());

        DifyProperties.Dataset dataset = new DifyProperties.Dataset();
        dataset.setApiKey("test-api-key");
        properties.setDataset(dataset);

        assertEquals("test-api-key", properties.getDataset().getApiKey());

        DifyProperties.Server server = new DifyProperties.Server();
        server.setEmail("test@example.com");
        server.setPassword("test-password");
        properties.setServer(server);

        assertEquals("test@example.com", properties.getServer().getEmail());
        assertEquals("test-password", properties.getServer().getPassword());
    }

    @Test
    void testDatasetConstructor() {
        DifyProperties.Dataset dataset = new DifyProperties.Dataset("test-api-key");
        assertEquals("test-api-key", dataset.getApiKey());

        // Test no-args constructor
        DifyProperties.Dataset emptyDataset = new DifyProperties.Dataset();
        assertNull(emptyDataset.getApiKey());
    }

    @Test
    void testServerConstructor() {
        DifyProperties.Server server = new DifyProperties.Server("test@example.com", "test-password");
        assertEquals("test@example.com", server.getEmail());
        assertEquals("test-password", server.getPassword());

        // Test no-args constructor
        DifyProperties.Server emptyServer = new DifyProperties.Server();
        assertNull(emptyServer.getEmail());
        assertNull(emptyServer.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        DifyProperties properties1 = new DifyProperties();
        properties1.setUrl("https://test-dify.example.com");
        properties1.setDataset(new DifyProperties.Dataset("api-key-1"));
        properties1.setServer(new DifyProperties.Server("user1@example.com", "password1"));

        DifyProperties properties2 = new DifyProperties();
        properties2.setUrl("https://test-dify.example.com");
        properties2.setDataset(new DifyProperties.Dataset("api-key-1"));
        properties2.setServer(new DifyProperties.Server("user1@example.com", "password1"));

        DifyProperties properties3 = new DifyProperties();
        properties3.setUrl("https://different-url.example.com");
        properties3.setDataset(new DifyProperties.Dataset("api-key-2"));
        properties3.setServer(new DifyProperties.Server("user2@example.com", "password2"));

        // Test equals
        assertEquals(properties1, properties2);
        assertNotEquals(properties1, properties3);

        // Test hashCode
        assertEquals(properties1.hashCode(), properties2.hashCode());
        assertNotEquals(properties1.hashCode(), properties3.hashCode());
    }

    @Test
    void testClientConfig() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        // 默认值
        assertTrue(config.getSkipNull());
        assertTrue(config.getLogging());

        // setter/getter
        config.setSkipNull(false);
        config.setLogging(false);
        assertFalse(config.getSkipNull());
        assertFalse(config.getLogging());

        // 有参构造
        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig(false, false);
        assertFalse(config2.getSkipNull());
        assertFalse(config2.getLogging());

        // equals/hashCode
        DifyProperties.ClientConfig config3 = new DifyProperties.ClientConfig(false, false);
        assertEquals(config2, config3);
        assertEquals(config2.hashCode(), config3.hashCode());
        assertEquals(config, config2);
    }

    @Test
    void testClientConfigPerformanceSettings() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();

        // 测试连接池配置默认值
        assertEquals(5, config.getMaxIdleConnections());
        assertEquals(300, config.getKeepAliveSeconds());
        assertEquals(64, config.getMaxRequests());
        assertEquals(5, config.getMaxRequestsPerHost());
        assertEquals(0, config.getCallTimeout());

        // 测试 SSE 配置默认值
        assertEquals(0, config.getSseReadTimeout());

        // 测试日志配置默认值
        assertTrue(config.getLoggingMaskEnabled());
        assertEquals(4096, config.getLogBodyMaxBytes());
        assertFalse(config.getLogBinaryBody());

        // 测试 setter/getter - 连接池配置
        config.setMaxIdleConnections(10);
        config.setKeepAliveSeconds(600);
        config.setMaxRequests(128);
        config.setMaxRequestsPerHost(10);
        config.setCallTimeout(60);

        assertEquals(10, config.getMaxIdleConnections());
        assertEquals(600, config.getKeepAliveSeconds());
        assertEquals(128, config.getMaxRequests());
        assertEquals(10, config.getMaxRequestsPerHost());
        assertEquals(60, config.getCallTimeout());

        // 测试 setter/getter - SSE 配置
        config.setSseReadTimeout(120);
        assertEquals(120, config.getSseReadTimeout());

        // 测试 setter/getter - 日志配置
        config.setLoggingMaskEnabled(false);
        config.setLogBodyMaxBytes(8192);
        config.setLogBinaryBody(true);

        assertFalse(config.getLoggingMaskEnabled());
        assertEquals(8192, config.getLogBodyMaxBytes());
        assertTrue(config.getLogBinaryBody());
    }

    @Test
    void testClientConfigNullValues() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();

        // 测试设置 null 值
        config.setMaxIdleConnections(null);
        config.setKeepAliveSeconds(null);
        config.setMaxRequests(null);
        config.setMaxRequestsPerHost(null);
        config.setCallTimeout(null);
        config.setSseReadTimeout(null);
        config.setLoggingMaskEnabled(null);
        config.setLogBodyMaxBytes(null);
        config.setLogBinaryBody(null);

        assertNull(config.getMaxIdleConnections());
        assertNull(config.getKeepAliveSeconds());
        assertNull(config.getMaxRequests());
        assertNull(config.getMaxRequestsPerHost());
        assertNull(config.getCallTimeout());
        assertNull(config.getSseReadTimeout());
        assertNull(config.getLoggingMaskEnabled());
        assertNull(config.getLogBodyMaxBytes());
        assertNull(config.getLogBinaryBody());
    }

    @Test
    void testClientConfigEdgeCases() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();

        // 测试边界值 - 最小值
        config.setMaxIdleConnections(0);
        config.setKeepAliveSeconds(0);
        config.setMaxRequests(0);
        config.setMaxRequestsPerHost(0);
        config.setCallTimeout(0);
        config.setSseReadTimeout(0);
        config.setLogBodyMaxBytes(0);

        assertEquals(0, config.getMaxIdleConnections());
        assertEquals(0, config.getKeepAliveSeconds());
        assertEquals(0, config.getMaxRequests());
        assertEquals(0, config.getMaxRequestsPerHost());
        assertEquals(0, config.getCallTimeout());
        assertEquals(0, config.getSseReadTimeout());
        assertEquals(0, config.getLogBodyMaxBytes());

        // 测试边界值 - 大值
        config.setMaxIdleConnections(1000);
        config.setKeepAliveSeconds(3600);
        config.setMaxRequests(10000);
        config.setMaxRequestsPerHost(1000);
        config.setCallTimeout(300);
        config.setSseReadTimeout(600);
        config.setLogBodyMaxBytes(1048576); // 1MB

        assertEquals(1000, config.getMaxIdleConnections());
        assertEquals(3600, config.getKeepAliveSeconds());
        assertEquals(10000, config.getMaxRequests());
        assertEquals(1000, config.getMaxRequestsPerHost());
        assertEquals(300, config.getCallTimeout());
        assertEquals(600, config.getSseReadTimeout());
        assertEquals(1048576, config.getLogBodyMaxBytes());
    }

    @Test
    void testSetAndGetClientConfig() {
        DifyProperties properties = new DifyProperties();
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig(false, true);
        properties.setClientConfig(config);
        assertEquals(config, properties.getClientConfig());
    }

    @Test
    void testToString() {
        // 测试DifyProperties的toString
        DifyProperties properties = new DifyProperties();
        properties.setUrl("https://test-dify.example.com");
        assertNotNull(properties.toString());
        assertTrue(properties.toString().contains("url=https://test-dify.example.com"));

        // 测试Dataset的toString - apiKey 应该被隐藏
        DifyProperties.Dataset dataset = new DifyProperties.Dataset("test-api-key");
        assertNotNull(dataset.toString());

        // 测试Server的toString - password 应该被隐藏
        DifyProperties.Server server = new DifyProperties.Server("test@example.com", "test-password");
        assertNotNull(server.toString());
        assertTrue(server.toString().contains("email=test@example.com"));

        // 测试ClientConfig的toString
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig(false, false);
        assertNotNull(config.toString());
        assertTrue(config.toString().contains("skipNull=false"));
        assertTrue(config.toString().contains("logging=false"));
    }
}
