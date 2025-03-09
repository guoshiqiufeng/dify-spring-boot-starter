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
package io.github.guoshiqiufeng.dify.chat;

import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.AppParametersResponseVO;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.DifyTextVO;
import io.github.guoshiqiufeng.dify.chat.dto.response.MessageConversationsResponse;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 聊天相关接口定义
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/3/4 14:24
 */
public interface DifyChat {

    /**
     * 发送消息
     *
     * @param sendRequest 消息发送请求对象，包含消息内容等信息
     * @return ChatMessageSendResponse 消息发送响应对象，包含发送结果等信息
     */
    ChatMessageSendResponse send(ChatMessageSendRequest sendRequest);

    /**
     * 发送消息并获取消息流
     *
     * @param sendRequest 消息发送请求对象，包含消息内容等信息
     * @return Flux<ChatMessageSendResponse> 消息流，实时返回消息发送的结果
     */
    Flux<ChatMessageSendResponse> sendChatMessageStream(ChatMessageSendRequest sendRequest);

    /**
     * 停止消息流
     *
     * @param apiKey API密钥，用于身份验证
     * @param taskId 任务ID，标识需要停止的消息流任务
     * @param userId 用户ID，标识操作者
     */
    void stopMessagesStream(String apiKey, String taskId, String userId);

    /**
     * 获取会话列表
     *
     * @param request 会话查询请求对象，包含分页、过滤条件等信息
     * @return DifyPageResult<MessageConversationsResponse> 分页结果，包含会话列表信息
     */
    DifyPageResult<MessageConversationsResponse> conversations(MessageConversationsRequest request);

    /**
     * 获取消息列表
     *
     * @param request 消息查询请求对象，包含分页、过滤条件等信息
     * @return DifyPageResult<MessagesResponseVO> 分页结果，包含消息列表信息
     */
    DifyPageResult<MessagesResponseVO> messages(MessagesRequest request);

    /**
     * 获取建议消息列表
     *
     * @param messageId 消息ID，标识需要获取建议消息的目标消息
     * @param apiKey    API密钥，用于身份验证
     * @param userId    用户ID，标识查询者
     * @return List<String> 建议消息列表，包含建议的消息内容
     */
    List<String> messagesSuggested(String messageId, String apiKey, String userId);

    /**
     * 删除会话
     *
     * @param conversationId 会话ID，标识需要删除的会话
     * @param apiKey         API密钥，用于身份验证
     * @param userId         用户ID，标识操作者
     */
    void deleteConversation(String conversationId, String apiKey, String userId);

    /**
     * 获取应用参数
     *
     * @param apiKey API密钥，用于身份验证
     * @return DifyAppParametersResponseVO 应用参数响应对象，包含应用配置信息
     */
    AppParametersResponseVO parameters(String apiKey);

    /**
     * 文本转语音
     *
     * @param request  文本转语音请求对象，包含文本内容等信息
     * @param response HTTP响应对象，用于返回生成的语音文件
     */
    void textToAudio(TextToAudioRequest request, HttpServletResponse response);

    /**
     * 语音转文本
     *
     * @param request 语音转文本请求对象，包含语音文件等信息
     * @return DifyTextVO 文本响应对象，包含转换后的文本内容
     */
    DifyTextVO audioToText(AudioToTextRequest request);
}
