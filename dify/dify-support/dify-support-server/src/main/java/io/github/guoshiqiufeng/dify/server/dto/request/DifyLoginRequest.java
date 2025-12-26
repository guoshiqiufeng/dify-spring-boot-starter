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
 * @author yanghq
 * @version 1.0
 * @since 2024/12/31 14:17
 */
@Data
public class DifyLoginRequest implements Serializable {
    private static final long serialVersionUID = 7556073605254679649L;

    private String email;

    private String password;

    private String language;

    @JsonAlias("rememberMe")
    @JsonProperty("remember-me")
    private Boolean rememberMe;

    public static DifyLoginRequest build(String email, String password) {
        DifyLoginRequest vo = new DifyLoginRequest();
        vo.setEmail(email);
        vo.setPassword(password);
        vo.setLanguage("zh-Hans");
        vo.setRememberMe(true);
        return vo;
    }
}
