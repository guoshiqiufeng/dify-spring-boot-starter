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
 * Request payload for creating a Dify application (agent).
 *
 * @author yanghq
 * @version 2.3.0
 * @since 2026/4/22
 */
@Data
public class AppsCreateRequest implements Serializable {
    private static final long serialVersionUID = -4179856317185630052L;

    /**
     * 应用名称
     */
    private String name;

    /**
     * 图标类型，例如 "emoji"
     */
    @JsonAlias("iconType")
    @JsonProperty("icon_type")
    private String iconType;

    /**
     * 图标
     */
    private String icon;

    /**
     * 图标背景色，例如 "#FFEAD5"
     */
    @JsonAlias("iconBackground")
    @JsonProperty("icon_background")
    private String iconBackground;

    /**
     * 应用模式，可选值：chat、agent-chat、completion、advanced-chat、workflow
     */
    private String mode;

    /**
     * 应用描述
     */
    private String description;
}
