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

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/10 13:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RenameConversationRequest extends BaseChatRequest implements Serializable {

    private static final long serialVersionUID = 3291175701376858324L;

    /**
     * 会话ID，标识需要重命名的会话
     */
    private String conversationId;

    /**
     * 会话名称
     */
    private String name;

    /**
     * 自动生成标题，默认 false
     */
    private Boolean autoGenerate = false;
}
