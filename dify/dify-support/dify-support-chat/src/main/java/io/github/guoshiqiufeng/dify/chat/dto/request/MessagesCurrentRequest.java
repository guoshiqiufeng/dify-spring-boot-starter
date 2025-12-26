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
package io.github.guoshiqiufeng.dify.chat.dto.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/1/8 15:39
 */
@Data
public class MessagesCurrentRequest implements Serializable {
    private static final long serialVersionUID = 5790356634269119842L;

    private String appId;

    private String conversationId;


    /**
     * 当前页第一条聊天记录的 ID，默认 null
     */
    private String firstId;

    /**
     * 一次请求返回多少条记录，默认 20 条，最大 100 条，最小 1 条。
     */
    private Integer limit = 20;


}
