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
package io.github.guoshiqiufeng.dify.chat.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AppAnnotationPageRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class AppAnnotationPageRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        AppAnnotationPageRequest request = new AppAnnotationPageRequest();
        Integer page = 5;
        Integer limit = 50;

        // Act
        request.setPage(page);
        request.setLimit(limit);

        // Assert
        assertEquals(page, request.getPage());
        assertEquals(limit, request.getLimit());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        AppAnnotationPageRequest request = new AppAnnotationPageRequest();

        // Assert
        assertEquals(1, request.getPage());
        assertEquals(20, request.getLimit());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        AppAnnotationPageRequest request = new AppAnnotationPageRequest();
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
        AppAnnotationPageRequest request1 = new AppAnnotationPageRequest();
        request1.setPage(2);
        request1.setLimit(30);

        AppAnnotationPageRequest request2 = new AppAnnotationPageRequest();
        request2.setPage(2);
        request2.setLimit(30);

        AppAnnotationPageRequest request3 = new AppAnnotationPageRequest();
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
        AppAnnotationPageRequest request = new AppAnnotationPageRequest();
        request.setPage(3);
        request.setLimit(25);

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("page=3"));
        assertTrue(toString.contains("limit=25"));
    }
}
