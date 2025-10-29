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
package io.github.guoshiqiufeng.dify.server.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.5.0
 * @since 2025/10/29 16:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppsRequest extends ServerPageRequest implements Serializable {
    private static final long serialVersionUID = 5565437185630052876L;

    /**
     * 不传为所有，可选值： workflow、advanced-chat、chat、agent-chat、completion
     */
    private String mode;

    private String name;

    @JsonAlias("isCreatedByMe")
    @JsonProperty("is_created_by_me")
    private Boolean isCreatedByMe;



}
