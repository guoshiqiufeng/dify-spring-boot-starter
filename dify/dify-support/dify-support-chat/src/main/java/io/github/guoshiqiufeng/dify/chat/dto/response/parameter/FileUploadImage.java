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
package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:32
 */
@Data
public class FileUploadImage implements Serializable {

    private static final long serialVersionUID = 2405030232637136714L;

    /**
     * enabled(bool) 是否开启
     */
    @JsonAlias("enabled")
    private Boolean enabled;
    /**
     * number_limits(int) 图片数量限制，默认 3
     */
    @JsonAlias("number_limits")
    private Integer numberLimits;

    /**
     * transfer_methods(array[string]) 传递方式列表，remote_url ,local_file， 必选一个
     */
    @JsonAlias("transfer_methods")
    private List<String> transferMethods;


}
