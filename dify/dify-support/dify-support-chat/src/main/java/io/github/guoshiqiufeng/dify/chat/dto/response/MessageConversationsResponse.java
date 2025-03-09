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
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/1/8 14:42
 */
@Data
public class MessageConversationsResponse implements Serializable {


    private static final long serialVersionUID = -1226883134574203925L;

    /**
     * id (string) 会话 ID
     */
    private String id;
    /**
     * name (string) 会话名称，默认为会话中用户最开始问题的截取。
     */
    private String name;
    /**
     * inputs (object) 用户输入参数。
     */
    private Map<String, Object> inputs;
    /**
     * status (string) 会话状态
     */
    private String status;
    /**
     * introduction (string) 开场白
     */
    private String introduction;
    /**
     * created_at (timestamp) 创建时间
     */
    @JsonAlias("created_at")
    private Long createdAt;
    /**
     * updated_at (timestamp) 更新时间
     */
    @JsonAlias("updated_at")
    private Long updatedAt;
}
