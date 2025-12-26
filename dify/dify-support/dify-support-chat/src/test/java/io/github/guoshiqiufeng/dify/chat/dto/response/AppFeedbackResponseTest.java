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
package io.github.guoshiqiufeng.dify.chat.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link AppFeedbackResponse}.
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/9/8 17:30
 */
class AppFeedbackResponseTest {

    @Test
    void testGettersAndSetters() {
        AppFeedbackResponse response = new AppFeedbackResponse();

        // Test all fields
        response.setId("test-id");
        response.setAppId("test-app-id");
        response.setConversationId("test-conversation-id");
        response.setMessageId("test-message-id");
        response.setRating("like");
        response.setContent("test content");
        response.setFromSource("user");
        response.setFromEndUserId("test-user-id");
        response.setFromAccountId("test-account-id");
        response.setCreatedAt(LocalDateTime.of(2025, 4, 24, 9, 24, 38));
        response.setUpdatedAt(LocalDateTime.of(2025, 4, 24, 9, 24, 38));

        assertEquals("test-id", response.getId());
        assertEquals("test-app-id", response.getAppId());
        assertEquals("test-conversation-id", response.getConversationId());
        assertEquals("test-message-id", response.getMessageId());
        assertEquals("like", response.getRating());
        assertEquals("test content", response.getContent());
        assertEquals("user", response.getFromSource());
        assertEquals("test-user-id", response.getFromEndUserId());
        assertEquals("test-account-id", response.getFromAccountId());
        assertEquals(LocalDateTime.of(2025, 4, 24, 9, 24, 38), response.getCreatedAt());
        assertEquals(LocalDateTime.of(2025, 4, 24, 9, 24, 38), response.getUpdatedAt());
    }

    @Test
    void testJsonSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        AppFeedbackResponse response = new AppFeedbackResponse();
        response.setId("8c0fbed8-e2f9-49ff-9f0e-15a35bdd0e25");
        response.setAppId("f252d396-fe48-450e-94ec-e184218e7346");
        response.setConversationId("2397604b-9deb-430e-b285-4726e51fd62d");
        response.setMessageId("709c0b0f-0a96-4a4e-91a4-ec0889937b11");
        response.setRating("like");
        response.setContent("message feedback information-3");
        response.setFromSource("user");
        response.setFromEndUserId("74286412-9a1a-42c1-929c-01edb1d381d5");
        response.setFromAccountId(null);

        String json = mapper.writeValueAsString(response);
        AppFeedbackResponse deserialized = mapper.readValue(json, AppFeedbackResponse.class);

        assertEquals(response.getId(), deserialized.getId());
        assertEquals(response.getAppId(), deserialized.getAppId());
        assertEquals(response.getConversationId(), deserialized.getConversationId());
        assertEquals(response.getMessageId(), deserialized.getMessageId());
        assertEquals(response.getRating(), deserialized.getRating());
        assertEquals(response.getContent(), deserialized.getContent());
        assertEquals(response.getFromSource(), deserialized.getFromSource());
        assertEquals(response.getFromEndUserId(), deserialized.getFromEndUserId());
        assertEquals(response.getFromAccountId(), deserialized.getFromAccountId());
    }
}
