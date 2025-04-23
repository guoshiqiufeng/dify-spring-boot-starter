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
package io.github.guoshiqiufeng.dify.client.spring6.utils;

import io.github.guoshiqiufeng.dify.dataset.dto.request.BaseDatasetRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/21 14:25
 */
public class DatasetHeaderUtilsTest {

    @Test
    @DisplayName("Test getHttpHeadersConsumer with BaseDatasetRequest containing API key")
    public void testGetHttpHeadersConsumerWithBaseDatasetRequest() {
        // Create a BaseDatasetRequest with API key
        BaseDatasetRequest request = new BaseDatasetRequest();
        request.setApiKey("test-api-key");

        // Get the HttpHeaders consumer
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(request);

        // Create HttpHeaders and apply the consumer
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        // Verify that the Authorization header is set correctly
        assertEquals("Bearer test-api-key", headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @DisplayName("Test getHttpHeadersConsumer with BaseDatasetRequest containing empty API key")
    public void testGetHttpHeadersConsumerWithEmptyBaseDatasetRequest() {
        // Create a BaseDatasetRequest with empty API key
        BaseDatasetRequest request = new BaseDatasetRequest();
        request.setApiKey("");

        // Get the HttpHeaders consumer
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(request);

        // Create HttpHeaders and apply the consumer
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        // Verify that the Authorization header is not set
        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @DisplayName("Test getHttpHeadersConsumer with API key string")
    public void testGetHttpHeadersConsumerWithApiKey() {
        // Get the HttpHeaders consumer with API key
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer("test-api-key");

        // Create HttpHeaders and apply the consumer
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        // Verify that the Authorization header is set correctly
        assertEquals("Bearer test-api-key", headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @DisplayName("Test getHttpHeadersConsumer with empty API key string")
    public void testGetHttpHeadersConsumerWithEmptyApiKey() {
        // Get the HttpHeaders consumer with empty API key
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer("");

        // Create HttpHeaders and apply the consumer
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        // Verify that the Authorization header is not set
        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @DisplayName("Test getHttpHeadersConsumer with null API key string")
    public void testGetHttpHeadersConsumerWithNullApiKey() {
        // Get the HttpHeaders consumer with null API key
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer((String) null);

        // Create HttpHeaders and apply the consumer
        HttpHeaders headers = new HttpHeaders();
        consumer.accept(headers);

        // Verify that the Authorization header is not set
        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
    }
}
