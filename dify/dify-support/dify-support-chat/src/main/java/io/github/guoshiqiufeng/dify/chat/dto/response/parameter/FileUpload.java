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
package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:31
 */
@Data
public class FileUpload implements Serializable {
    private static final long serialVersionUID = -7707822045842729660L;
    /**
     * image(object) 图片设置 当前仅支持图片类型：png,jpg,jpeg,webp,gif
     */
    @JsonAlias("image")
    private FileUploadImage image;

    private Boolean enabled;

    @JsonAlias("allowed_file_types")
    private List<String> allowedFileTypes;

    @JsonAlias("allowed_file_extensions")
    private List<String> allowedFileExtensions;

    @JsonAlias("allowed_file_upload_methods")
    private List<String> allowedFileUploadMethods;

    @JsonAlias("number_limits")
    private Integer numberLimits;

    private FileUploadConfig fileUploadConfig;

}
