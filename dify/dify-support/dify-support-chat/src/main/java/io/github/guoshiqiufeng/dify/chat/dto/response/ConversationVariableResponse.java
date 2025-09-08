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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 会话变量响应对象
 *
 * @author yanghq
 * @version 1.4.1
 * @since 2025/9/8 17:30
 */
@Data
public class ConversationVariableResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 变量ID
     */
    private String id;

    /**
     * 变量名称
     */
    private String name;

    /**
     * 值类型 (string/json)
     */
    @JsonProperty("value_type")
    private String valueType;

    /**
     * 变量值
     */
    private String value;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间 (时间戳)
     */
    @JsonProperty("created_at")
    private Long createdAt;

    /**
     * 更新时间 (时间戳)
     */
    @JsonProperty("updated_at")
    private Long updatedAt;
}
