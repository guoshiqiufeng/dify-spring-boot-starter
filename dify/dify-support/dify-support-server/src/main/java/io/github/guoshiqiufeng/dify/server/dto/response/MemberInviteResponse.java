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
 * Response DTO for workspace member invitation.
 *
 * @author yanghq
 * @version 2.3.0
 * @since 2026/4/22
 */
@Data
public class MemberInviteResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总体调用结果，通常为 "success"
     */
    private String result;

    /**
     * 各邀请邮箱的处理结果
     */
    @JsonAlias("invitation_results")
    private List<InvitationResult> invitationResults;

    /**
     * 单个邀请邮箱的结果详情
     */
    @Data
    public static class InvitationResult implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 处理状态：success 或 failed
         */
        private String status;

        /**
         * 邀请邮箱
         */
        private String email;

        /**
         * 激活 URL（status=success 时返回）
         */
        private String url;

        /**
         * 失败信息（status=failed 时返回）
         */
        private String message;
    }
}
