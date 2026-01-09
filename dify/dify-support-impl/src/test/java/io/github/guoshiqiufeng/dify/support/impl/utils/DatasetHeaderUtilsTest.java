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
package io.github.guoshiqiufeng.dify.support.impl.utils;

import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.dataset.dto.request.BaseDatasetRequest;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DatasetHeaderUtils
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class DatasetHeaderUtilsTest {

    @Test
    void testGetHttpHeadersConsumerWithRequest() {
        // Arrange
        BaseDatasetRequest request = new BaseDatasetRequest();
        request.setApiKey("test-api-key");
        HttpHeaders headers = new HttpHeaders();

        // Act
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(request);
        consumer.accept(headers);

        // Assert
        assertNotNull(consumer);
        // The header is added with empty key, which is unusual but matches the implementation
        assertTrue(headers.containsKey(AUTHORIZATION));
    }

    @Test
    void testGetHttpHeadersConsumerWithNullApiKey() {
        // Arrange
        BaseDatasetRequest request = new BaseDatasetRequest();
        request.setApiKey(null);
        HttpHeaders headers = new HttpHeaders();

        // Act
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(request);
        consumer.accept(headers);

        // Assert
        assertNotNull(consumer);
        assertFalse(headers.containsKey(""));
    }

    @Test
    void testGetHttpHeadersConsumerWithEmptyApiKey() {
        // Arrange
        BaseDatasetRequest request = new BaseDatasetRequest();
        request.setApiKey("");
        HttpHeaders headers = new HttpHeaders();

        // Act
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(request);
        consumer.accept(headers);

        // Assert
        assertNotNull(consumer);
        assertFalse(headers.containsKey(""));
    }

    @Test
    void testGetHttpHeadersConsumerWithString() {
        // Arrange
        String apiKey = "test-api-key";
        HttpHeaders headers = new HttpHeaders();

        // Act
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(apiKey);
        consumer.accept(headers);

        // Assert
        assertNotNull(consumer);
        assertTrue(headers.containsKey(AUTHORIZATION));
    }

    @Test
    void testGetHttpHeadersConsumerWithNullString() {
        // Arrange
        String apiKey = null;
        HttpHeaders headers = new HttpHeaders();

        // Act
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(apiKey);
        consumer.accept(headers);

        // Assert
        assertNotNull(consumer);
        assertFalse(headers.containsKey(""));
    }

    @Test
    void testGetHttpHeadersConsumerWithEmptyString() {
        // Arrange
        String apiKey = "";
        HttpHeaders headers = new HttpHeaders();

        // Act
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(apiKey);
        consumer.accept(headers);

        // Assert
        assertNotNull(consumer);
        assertFalse(headers.containsKey(""));
    }

    @Test
    void testUtilityClassCannotBeInstantiated() throws Exception {
        // Act & Assert
        java.lang.reflect.Constructor<DatasetHeaderUtils> constructor =
                DatasetHeaderUtils.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void testConsumerCanBeReused() {
        // Arrange
        String apiKey = "test-api-key";
        Consumer<HttpHeaders> consumer = DatasetHeaderUtils.getHttpHeadersConsumer(apiKey);

        // Act
        HttpHeaders headers1 = new HttpHeaders();
        consumer.accept(headers1);

        HttpHeaders headers2 = new HttpHeaders();
        consumer.accept(headers2);

        // Assert
        assertTrue(headers1.containsKey(AUTHORIZATION));
        assertTrue(headers2.containsKey(AUTHORIZATION));
    }

    @Test
    void testConsumerWithDifferentApiKeys() {
        // Arrange
        String apiKey1 = "api-key-1";
        String apiKey2 = "api-key-2";

        // Act
        Consumer<HttpHeaders> consumer1 = DatasetHeaderUtils.getHttpHeadersConsumer(apiKey1);
        Consumer<HttpHeaders> consumer2 = DatasetHeaderUtils.getHttpHeadersConsumer(apiKey2);

        HttpHeaders headers1 = new HttpHeaders();
        consumer1.accept(headers1);

        HttpHeaders headers2 = new HttpHeaders();
        consumer2.accept(headers2);

        // Assert
        assertTrue(headers1.containsKey(AUTHORIZATION));
        assertTrue(headers2.containsKey(AUTHORIZATION));
    }
}
