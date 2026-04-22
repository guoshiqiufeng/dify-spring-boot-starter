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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Request DTO for creating a Dify application.
 *
 * @author yanghq
 * @version 2.3.0
 * @since 2026/4/22
 */
@Data
public class AppCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用名称，必填
     */
    private String name;

    /**
     * 应用描述，可选
     */
    private String description;

    /**
     * 应用模式，必填
     * 可选值：chat、agent-chat、advanced-chat、workflow、completion
     */
    private String mode;

    /**
     * 图标类型，可选
     */
    @JsonProperty("icon_type")
    @JsonAlias({"iconType"})
    private String iconType;

    /**
     * 图标内容，可选
     */
    private String icon;

    /**
     * 图标背景色，可选
     */
    @JsonProperty("icon_background")
    @JsonAlias({"iconBackground"})
    private String iconBackground;
}
