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

import cn.hutool.extra.spring.SpringUtil;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendCompletionResponse;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineContext;
import io.github.guoshiqiufeng.dify.core.pipeline.PipelineHandler;
import org.springframework.beans.BeanUtils;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/8/26 09:37
 */
public class DifyChatPipelineUtils {

    private static final String CHAT_CODE = "CHAT";

    /**
     * 触发发送拦截
     *
     * @param completionResponse 消息
     * @return 拦截处理后消息
     */
    public static ChatMessageSendCompletionResponse processChat(ChatMessageSendCompletionResponse completionResponse) {
        PipelineHandler pipelineHandler = SpringUtil.getBean(PipelineHandler.class);
        if (pipelineHandler == null) {
            return completionResponse;
        }
        ChatMessagePipelineModel chatMessagePipelineModel = new ChatMessagePipelineModel();
        BeanUtils.copyProperties(completionResponse, chatMessagePipelineModel);
        PipelineContext<ChatMessagePipelineModel> context = PipelineContext.<ChatMessagePipelineModel>builder()
                .code(CHAT_CODE)
                .model(chatMessagePipelineModel)
                .needBreak(false)
                .build();
        PipelineContext<ChatMessagePipelineModel> process = pipelineHandler.process(context);
        return process.getModel();
    }
}
