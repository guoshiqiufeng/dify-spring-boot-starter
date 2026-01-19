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
package io.github.guoshiqiufeng.dify.chat.pipeline;

import io.github.guoshiqiufeng.dify.core.exception.UtilException;
import io.github.guoshiqiufeng.dify.core.extra.spring.SpringUtil;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendCompletionResponse;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineContext;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineHandler;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineProcess;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/8/26 10:30
 */
class DifyChatPipelineUtilsTest {

    private MockedStatic<SpringUtil> springUtilMock;
    private PipelineHandler pipelineHandler;

    @BeforeEach
    void setUp() {
        springUtilMock = mockStatic(SpringUtil.class);
        pipelineHandler = mock(PipelineHandler.class);
    }

    @AfterEach
    void tearDown() {
        springUtilMock.close();
    }

    @Test
    void testProcessChatWithNullPipelineHandler() {
        // Given
        springUtilMock.when(SpringUtil::isSpringEnvironment).thenReturn(true);
        springUtilMock.when(() -> SpringUtil.getBean(PipelineHandler.class)).thenReturn(null);
        ChatMessageSendCompletionResponse response = new ChatMessageSendCompletionResponse();
        response.setAnswer("test_answer");

        // When
        ChatMessageSendCompletionResponse result = DifyChatPipelineUtils.processChat(response);

        // Then
        assertSame(response, result);
        assertEquals("test_answer", result.getAnswer());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testProcessChatWithValidPipelineHandler() {
        // Given
        springUtilMock.when(SpringUtil::isSpringEnvironment).thenReturn(true);
        springUtilMock.when(() -> SpringUtil.getBean(PipelineHandler.class)).thenReturn(pipelineHandler);

        ChatMessageSendCompletionResponse response = new ChatMessageSendCompletionResponse();
        response.setAnswer("original_answer");
        response.setEvent("test_event");

        // Create a mock template with a processor that modifies the answer
        PipelineTemplate<ChatMessagePipelineModel> template = new PipelineTemplate<>();
        List<PipelineProcess<ChatMessagePipelineModel>> processList = new ArrayList<>();

        // Create a simple processor that modifies the answer
        PipelineProcess<ChatMessagePipelineModel> processor = mock(PipelineProcess.class);
        doAnswer(invocation -> {
            PipelineContext<ChatMessagePipelineModel> context = invocation.getArgument(0);
            context.getModel().setAnswer("modified_answer");
            return null;
        }).when(processor).process(any());

        when(processor.support(any())).thenReturn(true);
        when(processor.order()).thenReturn(0L);

        processList.add(processor);
        template.setProcessList(processList);

        Map<String, PipelineTemplate<? extends io.github.guoshiqiufeng.dify.core.pipeline.PipelineModel>> templateConfig = new HashMap<>();
        templateConfig.put("CHAT", template);

        when(pipelineHandler.getTemplateConfig()).thenReturn(templateConfig);

        // Mock the process method to simulate actual processing
        when(pipelineHandler.process(any(PipelineContext.class))).thenAnswer(invocation -> {
            PipelineContext<ChatMessagePipelineModel> context = invocation.getArgument(0);

            // Apply the processors
            List<PipelineProcess<ChatMessagePipelineModel>> processors = template.getProcessList();
            for (PipelineProcess<ChatMessagePipelineModel> proc : processors) {
                if (proc.support(context)) {
                    proc.process(context);
                }
            }

            return context;
        });

        // When
        ChatMessageSendCompletionResponse result = DifyChatPipelineUtils.processChat(response);

        // Then
        assertNotNull(result);
        assertEquals("modified_answer", result.getAnswer());
        assertEquals("test_event", result.getEvent());
    }

    @Test
    void testProcessChatWithNoSpringEnvironment() {
        // Given
        springUtilMock.when(SpringUtil::isSpringEnvironment).thenReturn(false);
        ChatMessageSendCompletionResponse response = new ChatMessageSendCompletionResponse();
        response.setAnswer("test_answer");

        // When
        ChatMessageSendCompletionResponse result = DifyChatPipelineUtils.processChat(response);

        // Then
        assertSame(response, result);
        assertEquals("test_answer", result.getAnswer());
    }

    @Test
    void testProcessChatWithUtilException() {
        // Given
        springUtilMock.when(SpringUtil::isSpringEnvironment).thenReturn(true);
        springUtilMock.when(() -> SpringUtil.getBean(PipelineHandler.class))
                .thenThrow(new UtilException("Failed to get bean"));

        ChatMessageSendCompletionResponse response = new ChatMessageSendCompletionResponse();
        response.setAnswer("test_answer");

        // When
        ChatMessageSendCompletionResponse result = DifyChatPipelineUtils.processChat(response);

        // Then
        assertSame(response, result);
        assertEquals("test_answer", result.getAnswer());
    }
}
