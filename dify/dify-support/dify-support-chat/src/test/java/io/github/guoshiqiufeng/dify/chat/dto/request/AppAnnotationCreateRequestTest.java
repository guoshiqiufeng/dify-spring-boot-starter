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
package io.github.guoshiqiufeng.dify.chat.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AppAnnotationCreateRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class AppAnnotationCreateRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        AppAnnotationCreateRequest request = new AppAnnotationCreateRequest();
        String question = "test question";
        String answer = "test answer";

        // Act
        request.setQuestion(question);
        request.setAnswer(answer);

        // Assert
        assertEquals(question, request.getQuestion());
        assertEquals(answer, request.getAnswer());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        AppAnnotationCreateRequest request = new AppAnnotationCreateRequest();
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
        AppAnnotationCreateRequest request1 = new AppAnnotationCreateRequest();
        request1.setQuestion("question1");
        request1.setAnswer("answer1");

        AppAnnotationCreateRequest request2 = new AppAnnotationCreateRequest();
        request2.setQuestion("question1");
        request2.setAnswer("answer1");

        AppAnnotationCreateRequest request3 = new AppAnnotationCreateRequest();
        request3.setQuestion("question2");
        request3.setAnswer("answer2");

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        AppAnnotationCreateRequest request = new AppAnnotationCreateRequest();
        request.setQuestion("test question");
        request.setAnswer("test answer");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("question=test question"));
        assertTrue(toString.contains("answer=test answer"));
    }
}
