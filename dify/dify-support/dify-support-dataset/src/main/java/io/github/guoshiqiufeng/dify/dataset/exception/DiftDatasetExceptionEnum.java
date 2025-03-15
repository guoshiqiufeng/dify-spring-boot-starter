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
package io.github.guoshiqiufeng.dify.dataset.exception;

import io.github.guoshiqiufeng.dify.core.exception.BaseExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 11:39
 */
@Getter
@AllArgsConstructor
public enum DiftDatasetExceptionEnum implements BaseExceptionEnum {

    /**
     * 删除失败
     */
    DELETE_ERROR(38003, "删除失败"),
    /**
     * 数据重复添加
     */
    DATA_EXIST(38004, "数据重复添加"),

    /**
     * App 不存在
     */
    DIFY_APP_NOT_EXIST(39001, "App 不存在"),

    /**
     * Api Key 初始化失败
     */
    DIFY_API_KET_INIT_ERROR(39011, "Api Key 初始化失败"),

    /**
     * 数据解析失败
     */
    DIFY_DATA_PARSING_FAILURE(39910, "数据解析失败"),

    /**
     * 远程调用失败
     */
    DIFY_API_ERROR(39990, "远程调用失败"),
    ;

    private final Integer code;

    private final String msg;

}
