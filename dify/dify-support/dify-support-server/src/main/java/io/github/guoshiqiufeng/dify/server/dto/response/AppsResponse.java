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
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2024/12/31 15:46
 */
@Data
public class AppsResponse implements Serializable {
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

    @JsonAlias("enable_site")
    private Boolean enableSite;

    @JsonAlias("enable_api")
    private Boolean enableApi;

    @JsonAlias("model_config")
    private Map<String, Object> modelConfig;

    private Map<String, Object> workflow;

    private Map<String, Object> site;

    @JsonAlias("api_base_url")
    private String apiBaseUrl;

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

    private List<AppsTag> tags;

    @JsonAlias("deleted_tools")
    private List<String> deletedTools;

    @Data
    public static class ModelConfig implements Serializable {
        private static final long serialVersionUID = 5952505802589220765L;
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
    public static class Model implements Serializable {
        private static final long serialVersionUID = 3375062828857004922L;
        private String provider;
        private String name;
        private String mode;
        @JsonAlias("completion_params")
        private CompletionParams completionParams;
    }

    @Data
    public static class CompletionParams implements Serializable {
        private static final long serialVersionUID = -8990638022329588332L;
        private List<String> stop;

        private String format;
    }

    @Data
    public static class AppsTag implements Serializable {
        private static final long serialVersionUID = 6681884820860339692L;

        private String id;

        private String name;

        private String type;
    }
}
