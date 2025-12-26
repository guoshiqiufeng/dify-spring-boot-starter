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
 * Test for {@link WorkflowLogsRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class WorkflowLogsRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        WorkflowLogsRequest request = new WorkflowLogsRequest();
        String keyword = "test-keyword";
        String status = "succeeded";
        Integer page = 2;
        Integer limit = 50;

        // Act
        request.setKeyword(keyword);
        request.setStatus(status);
        request.setPage(page);
        request.setLimit(limit);

        // Assert
        assertEquals(keyword, request.getKeyword());
        assertEquals(status, request.getStatus());
        assertEquals(page, request.getPage());
        assertEquals(limit, request.getLimit());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        WorkflowLogsRequest request = new WorkflowLogsRequest();

        // Assert
        assertNull(request.getKeyword());
        assertNull(request.getStatus());
        assertEquals(1, request.getPage());
        assertEquals(20, request.getLimit());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        WorkflowLogsRequest request = new WorkflowLogsRequest();
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
    public void testEqualsAndHashCode() {
        // Arrange
        WorkflowLogsRequest request1 = new WorkflowLogsRequest();
        request1.setKeyword("keyword1");
        request1.setStatus("succeeded");
        request1.setPage(2);
        request1.setLimit(30);

        WorkflowLogsRequest request2 = new WorkflowLogsRequest();
        request2.setKeyword("keyword1");
        request2.setStatus("succeeded");
        request2.setPage(2);
        request2.setLimit(30);

        WorkflowLogsRequest request3 = new WorkflowLogsRequest();
        request3.setKeyword("keyword2");
        request3.setStatus("failed");
        request3.setPage(3);
        request3.setLimit(40);

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        WorkflowLogsRequest request = new WorkflowLogsRequest();
        request.setKeyword("test-keyword");
        request.setStatus("succeeded");
        request.setPage(2);
        request.setLimit(30);

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("keyword=test-keyword"));
        assertTrue(toString.contains("status=succeeded"));
        assertTrue(toString.contains("page=2"));
        assertTrue(toString.contains("limit=30"));
    }
} 