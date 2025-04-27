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

import java.io.Serializable;

/**
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27 09:25
 */
@Data
public class AppAnnotationResponse implements Serializable {
    private static final long serialVersionUID = 7358615091264023969L;

    private String id;

    private String question;

    private String answer;

    @JsonAlias("hit_count")
    private Integer hitCount;

    @JsonAlias("created_at")
    private Long createdAt;
}
