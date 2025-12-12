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
package io.github.guoshiqiufeng.dify.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.guoshiqiufeng.dify.chat.dto.response.jackson.ChatMessageSendCompletionResponseDeserializer;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.CompletionData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/4 10:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
//@JsonDeserialize(using = ChatMessageSendCompletionResponseDeserializer.class)
public class ChatMessageSendCompletionResponse extends ChatMessageSendResponse implements Serializable {

    private static final long serialVersionUID = 3819274658903174523L;

    @JsonAlias("workflow_run_id")
    private String workflowRunId;

    // @JsonDeserialize(using = CompletionDataDeserializer.class)
    private CompletionData data;

    private Integer position;

    private String thought;

    private String observation;

    private String tool;

    @JsonAlias("tool_labels")
    private Map<String, Object> toolLabels;

    @JsonAlias("tool_input")
    private String toolInput;

    /**
     * message_files (array[string]) 当前agent_thought 关联的文件ID
     */
    @JsonAlias("message_files")
    private List<String> messageFiles;

    /**
     * HTTP 状态码
     *
     * @return
     */
    private Integer status;

    /**
     * 错误码
     *
     * @return
     */
    private String code;

    /**
     * 错误消息
     *
     * @return
     */
    private String message;
}
