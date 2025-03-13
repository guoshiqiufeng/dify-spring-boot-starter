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
package io.github.guoshiqiufeng.dify.workflow.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/11 15:25
 */
@Data
public class WorkflowLogs implements Serializable {
    private static final long serialVersionUID = 1106169784583268181L;

    private String id;

    @JsonAlias("workflow_run")
    private WorkflowRunResponse workflowRun;

    @JsonAlias("created_from")
    private String createdFrom;

    @JsonAlias("created_by_role")
    private String createdByRole;

    @JsonAlias("created_by_account")
    private String createdByAccount;

    @JsonAlias("created_by_end_user")
    private CreatedByEndUser createdByEndUser;

    @JsonAlias("created_at")
    private String createdAt;

    @Data
    public static class CreatedByEndUser implements Serializable {

        private static final long serialVersionUID = -6456523726805302133L;


        private String id;

        /**
         * 类型
         */
        private String type;

        /**
         * 是否匿名
         */
        @JsonAlias("is_anonymous")
        private Boolean isAnonymous;

        /**
         * 会话标识
         */
        @JsonAlias("session_id")
        private String sessionId;
    }
}
