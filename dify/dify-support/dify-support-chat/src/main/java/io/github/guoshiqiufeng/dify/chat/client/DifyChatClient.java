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
package io.github.guoshiqiufeng.dify.chat.client;

import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.*;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Dify Chat Client Interface
 * Provides methods to interact with Dify's chat API for sending messages,
 * managing conversations, and handling message-related operations.
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/7 13:20
 */
public interface DifyChatClient {

    /**
     * Sends a chat message and returns the response
     *
     * @param chatRequest The chat message request containing message content and parameters
     * @return The response from the chat service
     */
    ChatMessageSendResponse chat(ChatMessageSendRequest chatRequest);

    /**
     * Sends a chat message and returns the response as a reactive stream
     * Used for streaming responses from the chat service
     *
     * @param chatRequest The chat message request containing message content and parameters
     * @return A Flux stream of chat message completion responses
     */
    Flux<ChatMessageSendCompletionResponse> streamingChat(ChatMessageSendRequest chatRequest);

    /**
     * Stops an ongoing message stream
     *
     * @param apiKey The API key for authentication
     * @param taskId The ID of the streaming task to stop
     * @param userId The ID of the user who initiated the stream
     */
    void stopMessagesStream(String apiKey, String taskId, String userId);

    /**
     * Provides feedback for a specific message
     *
     * @param request The message feedback request containing rating and other feedback details
     * @return The response from the feedback submission
     */
    MessageFeedbackResponse messageFeedback(MessageFeedbackRequest request);

    /**
     * Retrieves a paginated list of conversations
     *
     * @param request The request parameters for fetching conversations
     * @return Paginated result containing conversation data
     */
    DifyPageResult<MessageConversationsResponse> conversations(MessageConversationsRequest request);

    /**
     * Retrieves a paginated list of messages
     *
     * @param request The request parameters for fetching messages
     * @return Paginated result containing message data
     */
    DifyPageResult<MessagesResponseVO> messages(MessagesRequest request);

    /**
     * Retrieves suggested follow-up messages for a given message
     *
     * @param messageId The ID of the message to get suggestions for
     * @param apiKey    The API key for authentication
     * @param userId    The ID of the user requesting suggestions
     * @return A list of suggested message texts
     */
    List<String> messagesSuggested(String messageId, String apiKey, String userId);

    /**
     * Deletes a specific conversation
     *
     * @param conversationId The ID of the conversation to delete
     * @param apiKey         The API key for authentication
     * @param userId         The ID of the user who owns the conversation
     */
    void deleteConversation(String conversationId, String apiKey, String userId);

    /**
     * Converts text to audio format
     *
     * @param request The text to audio conversion request
     * @return Response entity containing the audio data as byte array
     */
    ResponseEntity<byte[]> textToAudio(TextToAudioRequest request);

    /**
     * Converts audio to text format
     *
     * @param request The audio to text conversion request
     * @return Text data extracted from the audio
     */
    DifyTextVO audioToText(AudioToTextRequest request);

    /**
     * Renames an existing conversation
     *
     * @param renameConversationRequest The request containing the new name and conversation details
     * @return Updated conversation data
     */
    MessageConversationsResponse renameConversation(RenameConversationRequest renameConversationRequest);

    /**
     * Retrieves application parameters using the provided API key
     *
     * @param apiKey The API key for authentication and retrieving app parameters
     * @return Application parameters data
     */
    AppParametersResponseVO parameters(String apiKey);

}
