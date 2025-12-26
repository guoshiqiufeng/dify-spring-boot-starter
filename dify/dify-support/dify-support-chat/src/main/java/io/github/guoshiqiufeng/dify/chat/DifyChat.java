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
package io.github.guoshiqiufeng.dify.chat;

import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.*;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
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
    Flux<ChatMessageSendCompletionResponse> sendChatMessageStream(ChatMessageSendRequest sendRequest);

    /**
     * 停止消息流
     *
     * @param apiKey API密钥，用于身份验证
     * @param taskId 任务ID，标识需要停止的消息流任务
     * @param userId 用户ID，标识操作者
     */
    void stopMessagesStream(String apiKey, String taskId, String userId);

    /**
     * 消息反馈
     *
     * @param messageFeedbackRequest 消息反馈请求对象，包含消息ID、反馈内容等信息
     * @return MessageFeedbackResponse 消息反馈响应
     */
    MessageFeedbackResponse messageFeedback(MessageFeedbackRequest messageFeedbackRequest);

    /**
     * 获取应用反馈列表
     *
     * @param request 反馈列表请求对象，包含分页信息
     * @return DifyPageResult<AppFeedbackResponse> 分页结果，包含反馈列表信息
     */
    DifyPageResult<AppFeedbackResponse> feedbacks(AppFeedbackPageRequest request);


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
     * 会话重命名
     *
     * @param renameConversationRequest 会话重命名请求对象，包含会话ID、新名称等信息
     * @return MessageConversationsResponse 重命名后的会话信息
     */
    MessageConversationsResponse renameConversation(RenameConversationRequest renameConversationRequest);

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
     * @param request 文本转语音请求对象，包含文本内容等信息
     * @return 语音文件流
     */
    HttpResponse<byte[]> textToAudio(TextToAudioRequest request);

    /**
     * 语音转文本
     *
     * @param request 语音转文本请求对象，包含语音文件等信息
     * @return DifyTextVO 文本响应对象，包含转换后的文本内容
     */
    DifyTextVO audioToText(AudioToTextRequest request);

    /**
     * 上传文件
     *
     * @param request 上传文件请求对象
     * @return 上传文件结果
     * @since 0.10.0
     */
    FileUploadResponse fileUpload(FileUploadRequest request);

    /**
     * 文件预览
     * 预览或下载已上传的文件。此端点允许您访问先前通过文件上传 API 上传的文件。
     * 文件只能在属于请求应用程序的消息范围内访问。
     *
     * @param request 文件预览请求对象，包含文件ID、下载方式等信息
     * @return ResponseEntity<byte[]> 文件内容响应，包含适当的浏览器显示或下载标头
     * @since 1.0.0
     */
    HttpResponse<byte[]> filePreview(FilePreviewRequest request);

    /**
     * app info
     *
     * @param apiKey The API key for authentication and retrieving app parameters
     * @return Application info
     * @since 0.10.0
     */
    AppInfoResponse info(String apiKey);

    /**
     * app meta
     *
     * @param apiKey The API key for authentication and retrieving app parameters
     * @return Application meta
     * @since 0.10.0
     */
    AppMetaResponse meta(String apiKey);

    /**
     * app site
     *
     * @param apikey The API key for authentication and retrieving app parameters
     * @return Application site
     * @since 1.1.0
     * @since Dify version 1.4.0 or higher
     */
    AppSiteResponse site(String apikey);

    /**
     * Paginate application annotations
     *
     * @param request Annotation pagination request containing page parameters and filters
     * @return DifyPageResult<AppAnnotationResponse> Paginated result with annotation list
     * @since 0.10.0
     * @since Dify version 1.2.0 or higher
     */
    DifyPageResult<AppAnnotationResponse> pageAppAnnotation(AppAnnotationPageRequest request);

    /**
     * Create application annotation
     *
     * @param request Annotation creation request containing annotation content and metadata
     * @return AppAnnotationResponse The created annotation information
     * @since 0.10.0
     * @since Dify version 1.2.0 or higher
     */
    AppAnnotationResponse createAppAnnotation(AppAnnotationCreateRequest request);

    /**
     * Update application annotation
     *
     * @param request Annotation update request containing modified annotation content and metadata
     * @return AppAnnotationResponse The updated annotation information
     * @since 0.10.0
     * @since Dify version 1.3.1 or higher
     */
    AppAnnotationResponse updateAppAnnotation(AppAnnotationUpdateRequest request);

    /**
     * Delete application annotation
     *
     * @param annotationId Annotation ID to identify the annotation to delete
     * @param apiKey       API key for authentication
     * @since 0.10.0
     * @since Dify version 1.2.0 or higher
     */
    void deleteAppAnnotation(String annotationId, String apiKey);

    /**
     * Reply to an annotation
     *
     * @param request Annotation reply request containing the reply content and metadata
     * @return AppAnnotationReplyResponse Response containing the created reply information
     * @since 0.10.0
     * @since Dify version 1.2.0 or higher
     */
    AppAnnotationReplyResponse annotationReply(AppAnnotationReplyRequest request);

    /**
     * Query annotation reply information
     *
     * @param request Query parameters for retrieving annotation reply data
     * @return AppAnnotationReplyResponse Response containing the requested reply information
     * @since 0.10.0
     * @since Dify version 1.2.0 or higher
     */
    AppAnnotationReplyResponse queryAnnotationReply(AppAnnotationReplyQueryRequest request);

    /**
     * 获取会话变量列表
     *
     * @param request 会话变量请求对象，包含会话ID和可选的变量名称
     * @return DifyPageResult<ConversationVariableResponse> 分页结果，包含会话变量列表信息
     * @since 1.4.1
     */
    DifyPageResult<ConversationVariableResponse> conversationVariables(ConversationVariableRequest request);

    /**
     * 更新会话变量
     *
     * @param request 更新会话变量请求对象，包含会话ID、变量ID和新值
     * @return ConversationVariableResponse 更新后的会话变量信息
     * @since 1.4.1
     */
    ConversationVariableResponse updateConversationVariable(UpdateConversationVariableRequest request);
}
