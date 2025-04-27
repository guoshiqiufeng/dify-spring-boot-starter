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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.guoshiqiufeng.dify.chat.enums.AnnotationReplyActionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27 11:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppAnnotationReplyRequest extends BaseChatRequest implements Serializable {

    private static final long serialVersionUID = 5501616085947094901L;

    private AnnotationReplyActionEnum action;

    @JsonAlias("embeddingModelProvider")
    @JsonProperty("embedding_model_provider")
    private String embeddingModelProvider;

    @JsonAlias("embeddingModel")
    @JsonProperty("embedding_model")
    private String embeddingModel;

    @JsonAlias("scoreThreshold")
    @JsonProperty("score_threshold")
    private Float scoreThreshold;
}
