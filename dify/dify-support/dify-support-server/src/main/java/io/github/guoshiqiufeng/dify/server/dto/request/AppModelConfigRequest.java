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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for updating (publishing) the model configuration of a Dify application.
 * <p>Matches the payload expected by
 * {@code POST /console/api/apps/{appId}/model-config}. All fields are optional; only
 * the fields provided by the caller will be serialized.
 *
 * @author yanghq
 * @version 2.3.0
 * @since 2026/4/23
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppModelConfigRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Pre-prompt text used for simple prompt mode.
     */
    @JsonProperty("pre_prompt")
    @JsonAlias({"prePrompt"})
    private String prePrompt;

    /**
     * Prompt type, e.g. {@code simple} or {@code advanced}.
     */
    @JsonProperty("prompt_type")
    @JsonAlias({"promptType"})
    private String promptType;

    /**
     * Advanced chat prompt configuration (free-form structure defined by Dify).
     */
    @JsonProperty("chat_prompt_config")
    @JsonAlias({"chatPromptConfig"})
    private Map<String, Object> chatPromptConfig;

    /**
     * Advanced completion prompt configuration (free-form structure defined by Dify).
     */
    @JsonProperty("completion_prompt_config")
    @JsonAlias({"completionPromptConfig"})
    private Map<String, Object> completionPromptConfig;

    /**
     * User input form definition. Each entry typically contains a single key such as
     * {@code text-input}, {@code paragraph}, {@code select} mapped to its field config.
     */
    @JsonProperty("user_input_form")
    @JsonAlias({"userInputForm"})
    private List<Map<String, Object>> userInputForm;

    /**
     * Variable name used when querying datasets from within a prompt.
     */
    @JsonProperty("dataset_query_variable")
    @JsonAlias({"datasetQueryVariable"})
    private String datasetQueryVariable;

    /**
     * "More like this" feature configuration.
     */
    @JsonProperty("more_like_this")
    @JsonAlias({"moreLikeThis"})
    private EnabledConfig moreLikeThis;

    /**
     * Greeting shown when the conversation starts.
     */
    @JsonProperty("opening_statement")
    @JsonAlias({"openingStatement"})
    private String openingStatement;

    /**
     * Suggested questions shown next to the opening statement.
     */
    @JsonProperty("suggested_questions")
    @JsonAlias({"suggestedQuestions"})
    private List<String> suggestedQuestions;

    /**
     * Sensitive word avoidance (moderation) configuration.
     */
    @JsonProperty("sensitive_word_avoidance")
    @JsonAlias({"sensitiveWordAvoidance"})
    private SensitiveWordAvoidance sensitiveWordAvoidance;

    /**
     * Speech-to-text configuration.
     */
    @JsonProperty("speech_to_text")
    @JsonAlias({"speechToText"})
    private EnabledConfig speechToText;

    /**
     * Text-to-speech configuration.
     */
    @JsonProperty("text_to_speech")
    @JsonAlias({"textToSpeech"})
    private TextToSpeech textToSpeech;

    /**
     * File upload configuration.
     */
    @JsonProperty("file_upload")
    @JsonAlias({"fileUpload"})
    private FileUpload fileUpload;

    /**
     * Whether to show suggested follow-up questions after each answer.
     */
    @JsonProperty("suggested_questions_after_answer")
    @JsonAlias({"suggestedQuestionsAfterAnswer"})
    private EnabledConfig suggestedQuestionsAfterAnswer;

    /**
     * Whether to expose the retrieval sources (citations) in the answer.
     */
    @JsonProperty("retriever_resource")
    @JsonAlias({"retrieverResource"})
    private EnabledConfig retrieverResource;

    /**
     * Agent mode configuration (tools, strategy, iteration limit, ...).
     */
    @JsonProperty("agent_mode")
    @JsonAlias({"agentMode"})
    private AgentMode agentMode;

    /**
     * Model provider / name / completion parameters.
     */
    private Model model;

    /**
     * Dataset retrieval configuration.
     */
    @JsonProperty("dataset_configs")
    @JsonAlias({"datasetConfigs"})
    private DatasetConfigs datasetConfigs;

    /**
     * Simple {@code { "enabled": boolean }} container used by several toggle options.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EnabledConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        private Boolean enabled;
    }

    /**
     * Sensitive word avoidance configuration.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SensitiveWordAvoidance implements Serializable {
        private static final long serialVersionUID = 1L;

        private Boolean enabled;
        private String type;
        private List<Map<String, Object>> configs;
    }

    /**
     * Text-to-speech configuration.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TextToSpeech implements Serializable {
        private static final long serialVersionUID = 1L;

        private Boolean enabled;
        private String voice;
        private String language;
    }

    /**
     * File upload configuration.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FileUpload implements Serializable {
        private static final long serialVersionUID = 1L;

        private ImageFileUpload image;

        private Boolean enabled;

        @JsonProperty("allowed_file_types")
        @JsonAlias({"allowedFileTypes"})
        private List<String> allowedFileTypes;

        @JsonProperty("allowed_file_extensions")
        @JsonAlias({"allowedFileExtensions"})
        private List<String> allowedFileExtensions;

        @JsonProperty("allowed_file_upload_methods")
        @JsonAlias({"allowedFileUploadMethods"})
        private List<String> allowedFileUploadMethods;

        @JsonProperty("number_limits")
        @JsonAlias({"numberLimits"})
        private Integer numberLimits;
    }

    /**
     * Image-specific file upload configuration.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ImageFileUpload implements Serializable {
        private static final long serialVersionUID = 1L;

        private String detail;
        private Boolean enabled;

        @JsonProperty("number_limits")
        @JsonAlias({"numberLimits"})
        private Integer numberLimits;

        @JsonProperty("transfer_methods")
        @JsonAlias({"transferMethods"})
        private List<String> transferMethods;
    }

    /**
     * Agent mode configuration.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AgentMode implements Serializable {
        private static final long serialVersionUID = 1L;

        @JsonProperty("max_iteration")
        @JsonAlias({"maxIteration"})
        private Integer maxIteration;

        private Boolean enabled;

        private String strategy;

        private List<AgentTool> tools;

        private String prompt;
    }

    /**
     * Configuration of a tool enabled in agent mode.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AgentTool implements Serializable {
        private static final long serialVersionUID = 1L;

        @JsonProperty("provider_id")
        @JsonAlias({"providerId"})
        private String providerId;

        @JsonProperty("provider_type")
        @JsonAlias({"providerType"})
        private String providerType;

        @JsonProperty("provider_name")
        @JsonAlias({"providerName"})
        private String providerName;

        @JsonProperty("tool_name")
        @JsonAlias({"toolName"})
        private String toolName;

        @JsonProperty("tool_label")
        @JsonAlias({"toolLabel"})
        private String toolLabel;

        @JsonProperty("tool_parameters")
        @JsonAlias({"toolParameters"})
        private Map<String, Object> toolParameters;

        private Boolean enabled;

        @JsonProperty("isDeleted")
        @JsonAlias({"is_deleted"})
        private Boolean isDeleted;

        @JsonProperty("notAuthor")
        @JsonAlias({"not_author"})
        private Boolean notAuthor;
    }

    /**
     * Model configuration: provider, name, mode and completion parameters.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Model implements Serializable {
        private static final long serialVersionUID = 1L;

        private String provider;
        private String name;
        private String mode;

        @JsonProperty("completion_params")
        @JsonAlias({"completionParams"})
        private Map<String, Object> completionParams;
    }

    /**
     * Dataset retrieval configuration.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DatasetConfigs implements Serializable {
        private static final long serialVersionUID = 1L;

        @JsonProperty("retrieval_model")
        @JsonAlias({"retrievalModel"})
        private String retrievalModel;

        @JsonProperty("top_k")
        @JsonAlias({"topK"})
        private Integer topK;

        @JsonProperty("reranking_enable")
        @JsonAlias({"rerankingEnable"})
        private Boolean rerankingEnable;

        private Datasets datasets;
    }

    /**
     * Wrapper for the list of datasets referenced by an app.
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Datasets implements Serializable {
        private static final long serialVersionUID = 1L;

        private List<Map<String, Object>> datasets;
    }
}
