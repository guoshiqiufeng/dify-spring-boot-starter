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
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/1/8 17:02
 */
@Data
public class AppParametersResponseVO implements Serializable {
    private static final long serialVersionUID = 77338007223632127L;

    /**
     * opening_statement (string) 开场白
     */
    @JsonAlias("opening_statement")
    private String openingStatement;
    /**
     * suggested_questions (array[string]) 开场推荐问题列表
     */
    @JsonAlias("suggested_questions")
    private List<String> suggestedQuestions;
    /**
     * suggested_questions_after_answer (object) 启用回答后给出推荐问题。
     */
    @JsonAlias("suggested_questions_after_answer")
    private Enabled suggestedQuestionsAfterAnswer;

    /**
     * speech_to_text (object) 语音转文本
     */
    @JsonAlias("speech_to_text")
    private Enabled speechToText;

    /**
     * retriever_resource (object) 引用和归属
     */
    @JsonAlias("retriever_resource")
    private Enabled retrieverResource;

    /**
     * annotation_reply (object) 标记回复
     */
    @JsonAlias("annotation_reply")
    private Enabled annotationReply;

    /**
     * user_input_form (array[object]) 用户输入表单配置
     */
    @JsonAlias("user_input_form")
    private List<UserInputForm> userInputForm;


    /**
     * file_upload(object) 文件上传配置
     */
    @JsonAlias("file_upload")
    private FileUpload fileUpload;


    @Data
    public static class Enabled implements Serializable {
        /**
         * 是否开启
         */
        private Boolean enabled;
    }

    @Data
    public static class UserInputForm implements Serializable {
        /**
         * text-input (object) 文本输入控件
         */
        @JsonAlias("text-input")
        private TextInput textInput;

        /**
         * paragraph(object) 段落文本输入控件
         */
        @JsonAlias("paragraph")
        private Paragraph paragraph;

        /**
         * select(object) 下拉控件
         */
        @JsonAlias("select")
        private Select select;
    }

    @Data
    public static class TextInput implements Serializable {

        /**
         * label (string) 控件展示标签名
         */
        @JsonAlias("label")
        private String label;
        /**
         * variable (string) 控件 ID
         */
        @JsonAlias("variable")
        private String variable;
        /**
         * required (bool) 是否必填
         */
        @JsonAlias("required")
        private Boolean required;

        @JsonAlias("max_length")
        private Integer maxLength;
        /**
         * default (string) 默认值
         */
        @JsonAlias("default")
        private String defaultValue;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Paragraph extends TextInput implements Serializable {

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Select extends TextInput implements Serializable {

        private List<String> options;
    }

    @Data
    public static class FileUpload implements Serializable {
        private static final long serialVersionUID = -7707822045842729660L;
        /**
         * image(object) 图片设置 当前仅支持图片类型：png,jpg,jpeg,webp,gif
         */
        @JsonAlias("image")
        private FileUploadImage image;

    }

    @Data
    public static class FileUploadImage implements Serializable {

        private static final long serialVersionUID = 2405030232637136714L;

        /**
         * enabled(bool) 是否开启
         */
        @JsonAlias("enabled")
        private Boolean enabled;
        /**
         * number_limits(int) 图片数量限制，默认 3
         */
        @JsonAlias("number_limits")
        private Integer numberLimits;

        /**
         * transfer_methods(array[string]) 传递方式列表，remote_url ,local_file， 必选一个
         */
        @JsonAlias("transfer_methods")
        private List<String> transferMethods;


    }
}
