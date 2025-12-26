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
package io.github.guoshiqiufeng.dify.dataset.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link BaseDatasetRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class BaseDatasetRequestTest {

    @Test
    public void testDefaultConstructor() {
        // Act
        BaseDatasetRequest request = new BaseDatasetRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getApiKey());
    }

    @Test
    public void testGetterAndSetter() {
        // Arrange
        BaseDatasetRequest request = new BaseDatasetRequest();
        String apiKey = "test-api-key";

        // Act
        request.setApiKey(apiKey);

        // Assert
        assertEquals(apiKey, request.getApiKey());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        BaseDatasetRequest request1 = new BaseDatasetRequest();
        request1.setApiKey("test-api-key-1");

        BaseDatasetRequest request2 = new BaseDatasetRequest();
        request2.setApiKey("test-api-key-1");

        BaseDatasetRequest request3 = new BaseDatasetRequest();
        request3.setApiKey("test-api-key-2");

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        BaseDatasetRequest request = new BaseDatasetRequest();
        request.setApiKey("test-api-key");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("apiKey=test-api-key"));
    }
} 