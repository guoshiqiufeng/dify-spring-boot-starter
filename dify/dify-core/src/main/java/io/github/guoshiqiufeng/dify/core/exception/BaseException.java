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
 * @since 2025/2/27 15:31
 */
public class BaseException extends RuntimeException {

    /**
     * 错误码
     */
    protected Integer code;

    /**
     * 错误消息
     */
    protected String msg;

    public BaseException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BaseException(BaseExceptionEnum abstractExceptionEnum) {
        super(abstractExceptionEnum.getMsg());
        this.code = abstractExceptionEnum.getCode();
        this.msg = abstractExceptionEnum.getMsg();
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
