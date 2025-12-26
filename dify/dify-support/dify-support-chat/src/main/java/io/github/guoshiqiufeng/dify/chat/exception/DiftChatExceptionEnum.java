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
package io.github.guoshiqiufeng.dify.chat.exception;

import io.github.guoshiqiufeng.dify.core.exception.BaseExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/4 11:00
 */
@Getter
@AllArgsConstructor
public enum DiftChatExceptionEnum implements BaseExceptionEnum {

    /**
     * 删除失败
     */
    DELETE_ERROR(18003, "删除失败"),
    /**
     * 数据重复添加
     */
    DATA_EXIST(18004, "数据重复添加"),

    /**
     * App 不存在
     */
    DIFY_APP_NOT_EXIST(19001, "App 不存在"),

    /**
     * Api Key 初始化失败
     */
    DIFY_API_KET_INIT_ERROR(19011, "Api Key 初始化失败"),

    /**
     * 数据解析失败
     */
    DIFY_DATA_PARSING_FAILURE(19910, "数据解析失败"),

    /**
     * 文字转语音未启用
     */
    DIFY_TTS_IS_NOT_ENABLED(19911, "文字转语音未启用"),

    /**
     * 语音转文字未启用
     */
    DIFY_SPEECH_TO_TEXT_IS_NOT_ENABLED(19912, "语音转文字未启用"),

    /**
     * 远程调用失败
     */
    DIFY_API_ERROR(19990, "远程调用失败"),
    ;

    private final Integer code;

    private final String msg;
}
