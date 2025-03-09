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
 * @since 2025/1/8 14:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageConversationsRequest extends BaseChatRequest implements Serializable {
    private static final long serialVersionUID = 4267154262382364533L;

    /**
     * 当前页最后面一条记录的 ID，默认 null
     */
    private String lastId;

    /**
     * 一次请求返回多少条记录，默认 20 条，最大 100 条，最小 1 条。
     */
    private Integer limit = 20;

    /**
     * 排序字段，默认 -updated_at(按更新时间倒序排列)
     * <p>
     * 可选值：created_at, -created_at, updated_at, -updated_at
     * 字段前面的符号代表顺序或倒序，-代表倒序
     */
    private String sortBy;

}
