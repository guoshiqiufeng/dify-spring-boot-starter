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

import io.github.guoshiqiufeng.dify.chat.enums.AnnotationReplyActionEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AppAnnotationReplyRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class AppAnnotationReplyRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        AppAnnotationReplyRequest request = new AppAnnotationReplyRequest();
        AnnotationReplyActionEnum action = AnnotationReplyActionEnum.enable;
        String embeddingProviderName = "test-provider";
        String embeddingModelName = "test-model";
        Float scoreThreshold = 0.85f;

        // Act
        request.setAction(action);
        request.setEmbeddingProviderName(embeddingProviderName);
        request.setEmbeddingModelName(embeddingModelName);
        request.setScoreThreshold(scoreThreshold);

        // Assert
        assertEquals(action, request.getAction());
        assertEquals(embeddingProviderName, request.getEmbeddingProviderName());
        assertEquals(embeddingModelName, request.getEmbeddingModelName());
        assertEquals(scoreThreshold, request.getScoreThreshold());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        AppAnnotationReplyRequest request = new AppAnnotationReplyRequest();
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
        AppAnnotationReplyRequest request1 = new AppAnnotationReplyRequest();
        request1.setAction(AnnotationReplyActionEnum.enable);
        request1.setEmbeddingProviderName("provider1");
        request1.setEmbeddingModelName("model1");
        request1.setScoreThreshold(0.75f);

        AppAnnotationReplyRequest request2 = new AppAnnotationReplyRequest();
        request2.setAction(AnnotationReplyActionEnum.enable);
        request2.setEmbeddingProviderName("provider1");
        request2.setEmbeddingModelName("model1");
        request2.setScoreThreshold(0.75f);

        AppAnnotationReplyRequest request3 = new AppAnnotationReplyRequest();
        request3.setAction(AnnotationReplyActionEnum.disable);
        request3.setEmbeddingProviderName("provider2");
        request3.setEmbeddingModelName("model2");
        request3.setScoreThreshold(0.85f);

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        AppAnnotationReplyRequest request = new AppAnnotationReplyRequest();
        request.setAction(AnnotationReplyActionEnum.enable);
        request.setEmbeddingProviderName("test-provider");
        request.setEmbeddingModelName("test-model");
        request.setScoreThreshold(0.8f);

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("action=" + AnnotationReplyActionEnum.enable));
        assertTrue(toString.contains("embeddingProviderName=test-provider"));
        assertTrue(toString.contains("embeddingModelName=test-model"));
        assertTrue(toString.contains("scoreThreshold=0.8"));
    }
}
