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
package io.github.guoshiqiufeng.dify.server.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Request payload for inviting new members to the current workspace.
 *
 * @author yanghq
 * @version 2.3.0
 * @since 2026/4/22
 */
@Data
public class MembersInviteRequest implements Serializable {
    private static final long serialVersionUID = 4179856317185630053L;

    /**
     * 被邀请成员的邮箱列表
     */
    private List<String> emails;

    /**
     * 成员角色，可选值：normal、editor、admin
     */
    private String role;

    /**
     * 界面语言，例如 "zh-Hans"、"en-US"
     */
    private String language;
}
