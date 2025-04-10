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
}
