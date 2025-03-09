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
package io.github.guoshiqiufeng.dify.core.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回结果类，用于封装接口返回的数据。 <br>
 * 泛型 T 表示返回数据的具体类型。
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/12/31 14:27
 */
@Data
public class DifyResult<T> implements Serializable {

    private static final long serialVersionUID = 4454314914521369005L;

    public static final String SUCCESS = "success";

    /**
     * 返回的具体数据，类型由泛型 T 决定
     */
    private T data;

    /**
     * 返回的结果状态，通常用于描述操作的成功或失败信息
     */
    private String result;
}
