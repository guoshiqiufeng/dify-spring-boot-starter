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
package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:36
 */
@Data
public class TextInput implements Serializable {

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
