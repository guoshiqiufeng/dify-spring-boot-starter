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
package io.github.guoshiqiufeng.dify.dataset.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 13:31
 */
@AllArgsConstructor
@Getter
public enum PermissionEnum {

    /**
     * 仅自己
     */
    ONLY_ME("only_me"),

    /**
     * 所有团队成员
     */
    ALL_TEAM_MEMBERS("all_team_members"),

    /**
     * 部分团队成员
     */
    PARTIAL_MEMBERS("partial_members");

    @JsonValue
    private final String code;

    @JsonCreator
    public static PermissionEnum codeOf(String code) {
        for (PermissionEnum value : PermissionEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
