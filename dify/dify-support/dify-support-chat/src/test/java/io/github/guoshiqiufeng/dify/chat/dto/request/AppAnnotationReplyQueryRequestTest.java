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

import io.github.guoshiqiufeng.dify.chat.enums.AnnotationReplyActionEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AppAnnotationReplyQueryRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class AppAnnotationReplyQueryRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        AppAnnotationReplyQueryRequest request = new AppAnnotationReplyQueryRequest();
        AnnotationReplyActionEnum action = AnnotationReplyActionEnum.enable;
        String jobId = "test-job-id";

        // Act
        request.setAction(action);
        request.setJobId(jobId);

        // Assert
        assertEquals(action, request.getAction());
        assertEquals(jobId, request.getJobId());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        AppAnnotationReplyQueryRequest request = new AppAnnotationReplyQueryRequest();
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
        AppAnnotationReplyQueryRequest request1 = new AppAnnotationReplyQueryRequest();
        request1.setAction(AnnotationReplyActionEnum.enable);
        request1.setJobId("job1");

        AppAnnotationReplyQueryRequest request2 = new AppAnnotationReplyQueryRequest();
        request2.setAction(AnnotationReplyActionEnum.enable);
        request2.setJobId("job1");

        AppAnnotationReplyQueryRequest request3 = new AppAnnotationReplyQueryRequest();
        request3.setAction(AnnotationReplyActionEnum.disable);
        request3.setJobId("job2");

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        AppAnnotationReplyQueryRequest request = new AppAnnotationReplyQueryRequest();
        request.setAction(AnnotationReplyActionEnum.enable);
        request.setJobId("test-job-id");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("action=" + AnnotationReplyActionEnum.enable));
        assertTrue(toString.contains("jobId=test-job-id"));
    }
}
