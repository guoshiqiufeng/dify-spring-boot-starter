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
package io.github.guoshiqiufeng.dify.chat.pipeline;

import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendCompletionResponse;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineModel;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/8/26 10:30
 */
class ChatMessagePipelineModelTest {

    @Test
    void testInheritance() {
        ChatMessagePipelineModel model = new ChatMessagePipelineModel();

        // Should extend ChatMessageSendCompletionResponse
        assertTrue(model instanceof ChatMessageSendCompletionResponse);

        // Should implement PipelineModel
        assertTrue(model instanceof PipelineModel);

        // Should be serializable
        assertTrue(model instanceof Serializable);
    }

    @Test
    void testEqualsAndHashCode() {
        ChatMessagePipelineModel model1 = new ChatMessagePipelineModel();
        model1.setEvent("test_event");
        model1.setAnswer("test_answer");

        ChatMessagePipelineModel model2 = new ChatMessagePipelineModel();
        model2.setEvent("test_event");
        model2.setAnswer("test_answer");

        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());
    }

    @Test
    void testGetterAndSetter() {
        ChatMessagePipelineModel model = new ChatMessagePipelineModel();

        // Test basic properties from parent class
        model.setEvent("test_event");
        model.setConversationId("test_conversation_id");
        model.setMessageId("test_message_id");
        model.setAnswer("test_answer");

        assertEquals("test_event", model.getEvent());
        assertEquals("test_conversation_id", model.getConversationId());
        assertEquals("test_message_id", model.getMessageId());
        assertEquals("test_answer", model.getAnswer());

        // Test properties from ChatMessageSendCompletionResponse
        model.setWorkflowRunId("test_workflow_run_id");
        model.setPosition(1);
        model.setThought("test_thought");
        model.setObservation("test_observation");
        model.setTool("test_tool");
        model.setStatus(200);
        model.setCode("test_code");
        model.setMessage("test_message");

        assertEquals("test_workflow_run_id", model.getWorkflowRunId());
        assertEquals(Integer.valueOf(1), model.getPosition());
        assertEquals("test_thought", model.getThought());
        assertEquals("test_observation", model.getObservation());
        assertEquals("test_tool", model.getTool());
        assertEquals(Integer.valueOf(200), model.getStatus());
        assertEquals("test_code", model.getCode());
        assertEquals("test_message", model.getMessage());
    }
}
