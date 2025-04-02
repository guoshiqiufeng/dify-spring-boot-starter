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

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:44
 */
@Data
public class FileUploadConfig implements Serializable {

    @JsonAlias("file_size_limit")
    private Integer fileSizeLimit;
    @JsonAlias("batch_count_limit")
    private Integer batchCountLimit;
    @JsonAlias("image_file_size_limit")
    private Integer imageFileSizeLimit;
    @JsonAlias("video_file_size_limit")
    private Integer videoFileSizeLimit;
    @JsonAlias("audio_file_size_limit")
    private Integer audioFileSizeLimit;
    @JsonAlias("workflow_file_upload_limit")
    private Integer workflowFileUploadLimit;
}
