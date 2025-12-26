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
package io.github.guoshiqiufeng.dify.workflow.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link BaseWorkflowRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class BaseWorkflowRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        BaseWorkflowRequest request = new BaseWorkflowRequest();
        String apiKey = "test-api-key";
        String userId = "test-user-id";

        // Act
        request.setApiKey(apiKey);
        request.setUserId(userId);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(userId, request.getUserId());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        BaseWorkflowRequest request = new BaseWorkflowRequest();

        // Assert
        assertNull(request.getApiKey());
        assertNull(request.getUserId());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        BaseWorkflowRequest request1 = new BaseWorkflowRequest();
        request1.setApiKey("api-key-1");
        request1.setUserId("user-id-1");

        BaseWorkflowRequest request2 = new BaseWorkflowRequest();
        request2.setApiKey("api-key-1");
        request2.setUserId("user-id-1");

        BaseWorkflowRequest request3 = new BaseWorkflowRequest();
        request3.setApiKey("api-key-2");
        request3.setUserId("user-id-2");

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        BaseWorkflowRequest request = new BaseWorkflowRequest();
        request.setApiKey("test-api-key");
        request.setUserId("test-user-id");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("apiKey=test-api-key"));
        assertTrue(toString.contains("userId=test-user-id"));
    }
} 