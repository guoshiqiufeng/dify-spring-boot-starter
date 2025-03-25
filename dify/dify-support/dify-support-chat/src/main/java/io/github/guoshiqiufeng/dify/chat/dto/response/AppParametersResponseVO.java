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
import io.github.guoshiqiufeng.dify.chat.dto.response.parameter.*;
import lombok.Data;

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

    @JsonAlias("text_to_speech")
    private TextToSpeech textToSpeech;

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

    @JsonAlias("more_like_this")
    private Enabled moreLikeThis;

    /**
     * user_input_form (array[object]) 用户输入表单配置
     */
    @JsonAlias("user_input_form")
    private List<UserInputForm> userInputForm;

    @JsonAlias("sensitive_word_avoidance")
    private Enabled sensitiveWordAvoidance;

    /**
     * file_upload(object) 文件上传配置
     */
    @JsonAlias("file_upload")
    private FileUpload fileUpload;

    @JsonAlias("system_parameters")
    private FileUploadConfig systemParameters;
}
