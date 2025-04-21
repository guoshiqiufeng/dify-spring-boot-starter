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
package io.github.guoshiqiufeng.dify.client.spring5.utils;

import io.github.guoshiqiufeng.dify.dataset.dto.request.BaseDatasetRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/21 11:40
 */
public class DatasetHeaderUtilsTest {

    @Test
    @DisplayName("Test getHttpHeadersConsumer with BaseDatasetRequest")
    public void testGetHttpHeadersConsumerWithRequest() {
        // Create a mock request with API key
        BaseDatasetRequest request = new BaseDatasetRequest() {};
        request.setApiKey("test-api-key");

        // Get the consumer
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(request);

        // Create headers and apply consumer
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        // Verify the Authorization header was set correctly
        assertTrue(headers.containsKey(HttpHeaders.AUTHORIZATION), "Headers should contain Authorization");
        assertEquals("Bearer test-api-key", headers.getFirst(HttpHeaders.AUTHORIZATION),
                "Authorization header should be set with Bearer token");
    }

    @Test
    @DisplayName("Test getHttpHeadersConsumer with BaseDatasetRequest - empty API key")
    public void testGetHttpHeadersConsumerWithRequestEmptyApiKey() {
        // Create a mock request with empty API key
        BaseDatasetRequest request = new BaseDatasetRequest() {};
        request.setApiKey("");

        // Get the consumer
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(request);

        // Create headers and apply consumer
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        // Verify the Authorization header was not set
        assertFalse(headers.containsKey(HttpHeaders.AUTHORIZATION), "Headers should not contain Authorization");
    }

    @Test
    @DisplayName("Test getHttpHeadersConsumer with BaseDatasetRequest - null API key")
    public void testGetHttpHeadersConsumerWithRequestNullApiKey() {
        // Create a mock request with null API key
        BaseDatasetRequest request = new BaseDatasetRequest() {};
        request.setApiKey(null);

        // Get the consumer
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(request);

        // Create headers and apply consumer
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        // Verify the Authorization header was not set
        assertFalse(headers.containsKey(HttpHeaders.AUTHORIZATION), "Headers should not contain Authorization");
    }

    @Test
    @DisplayName("Test getHttpHeadersConsumer with API key string")
    public void testGetHttpHeadersConsumerWithApiKey() {
        // Get the consumer with API key
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer("test-api-key");

        // Create headers and apply consumer
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        // Verify the Authorization header was set correctly
        assertTrue(headers.containsKey(HttpHeaders.AUTHORIZATION), "Headers should contain Authorization");
        assertEquals("Bearer test-api-key", headers.getFirst(HttpHeaders.AUTHORIZATION),
                "Authorization header should be set with Bearer token");
    }

    @Test
    @DisplayName("Test getHttpHeadersConsumer with empty API key string")
    public void testGetHttpHeadersConsumerWithEmptyApiKey() {
        // Get the consumer with empty API key
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer("");

        // Create headers and apply consumer
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        // Verify the Authorization header was not set
        assertFalse(headers.containsKey(HttpHeaders.AUTHORIZATION), "Headers should not contain Authorization");
    }
}
