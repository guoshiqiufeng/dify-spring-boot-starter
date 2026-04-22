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
package io.github.guoshiqiufeng.dify.server.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Response payload for inviting members to the current workspace.
 *
 * @author yanghq
 * @version 2.3.0
 * @since 2026/4/22
 */
@Data
public class MembersInviteResponse implements Serializable {
    private static final long serialVersionUID = 4179856317185630054L;

    /**
     * 接口执行结果，成功时为 "success"
     */
    private String result;

    /**
     * 每个邮箱的邀请结果
     */
    @JsonAlias("invitation_results")
    private List<InvitationResult> invitationResults;

    @Data
    public static class InvitationResult implements Serializable {
        private static final long serialVersionUID = 4179856317185630055L;

        /**
         * 邀请状态，可能值：success、failed
         */
        private String status;

        /**
         * 被邀请的邮箱
         */
        private String email;

        /**
         * 激活邀请的 URL（邀请成功时返回）
         */
        private String url;

        /**
         * 失败原因（邀请失败时返回）
         */
        private String message;
    }
}
