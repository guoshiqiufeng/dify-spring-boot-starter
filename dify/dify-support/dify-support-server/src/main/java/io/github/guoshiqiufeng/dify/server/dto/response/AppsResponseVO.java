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
package io.github.guoshiqiufeng.dify.server.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/12/31 15:46
 */
@Data
public class AppsResponseVO implements Serializable {
    private static final long serialVersionUID = -8281293887457701741L;

    private String id;
    private String name;

    @JsonAlias("max_active_requests")
    private Integer maxActiveRequests;

    private String description;
    private String mode;

    @JsonAlias("icon_type")
    private String iconType;

    private String icon;

    @JsonAlias("icon_background")
    private String iconBackground;

    @JsonAlias("icon_url")
    private String iconUrl;
    @JsonAlias("model_config")
    private ModelConfig modelConfig;
    private Object workflow;
    @JsonAlias("use_icon_as_answer_icon")
    private Boolean useIconAsAnswerIcon;
    @JsonAlias("created_by")
    private String createdBy;
    @JsonAlias("created_at")
    private Long createdAt;
    @JsonAlias("updated_by")
    private String updatedBy;
    @JsonAlias("updated_at")
    private Long updatedAt;
    private List<String> tags;

    @Data
    public static class ModelConfig {
        private Model model;
        @JsonAlias("pre_prompt")
        private String prePrompt;
        @JsonAlias("created_by")
        private String createdBy;
        @JsonAlias("created_at")
        private Long createdAt;
        @JsonAlias("updated_by")
        private String updatedBy;
        @JsonAlias("updated_at")
        private Long updatedAt;
    }

    @Data
    public static class Model {
        private String provider;
        private String name;
        private String mode;
        @JsonAlias("completion_params")
        private CompletionParams completionParams;
    }

    @Data
    public static class CompletionParams {
        private List<String> stop;
    }
}
