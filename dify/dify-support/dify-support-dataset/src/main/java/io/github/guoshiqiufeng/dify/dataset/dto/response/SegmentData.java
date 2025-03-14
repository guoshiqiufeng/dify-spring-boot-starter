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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 17:20
 */
@Data
public class SegmentData implements Serializable {


    private static final long serialVersionUID = -1501282407892329334L;

    private String id;

    private Integer position;

    private String content;

    private String answer;

    @JsonAlias("word_count")
    private Integer wordCount;

    private Integer tokens;

    @JsonAlias("indexing_status")
    private String indexingStatus;

    private String error;

    private String enabled;

    @JsonAlias("disabled_at")
    private Long disabledAt;

    @JsonAlias("disabled_by")
    private String disabledBy;

    private Boolean archived;

}
