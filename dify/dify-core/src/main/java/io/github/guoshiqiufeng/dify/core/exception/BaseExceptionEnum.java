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
package io.github.guoshiqiufeng.dify.core.exception;

/**
 * @author yanghq
 * @version 1.0
 * @since 2021-07-21 14:19
 */
public interface BaseExceptionEnum {

    /**
     * 获取异常的状态码
     *
     * @return 状态码
     */
    Integer getCode();

    /**
     * 获取异常的消息
     *
     * @return 异常消息
     */
    String getMsg();

}
