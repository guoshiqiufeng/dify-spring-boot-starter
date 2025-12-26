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
package io.github.guoshiqiufeng.dify.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 文件预览请求
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/1/6 10:00
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class FilePreviewRequest extends BaseChatRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID，要预览的文件的唯一标识符
     */
    private String fileId;

    /**
     * 是否强制将文件作为附件下载。默认为 false（在浏览器中预览）
     */
    private Boolean asAttachment;

    /**
     * 构造器，只需要必要的fileId参数
     *
     * @param fileId 文件ID
     */
    public FilePreviewRequest(String fileId) {
        this.fileId = fileId;
        this.asAttachment = false;
    }

    /**
     * 构造器，包含所有参数
     *
     * @param fileId       文件ID
     * @param asAttachment 是否作为附件下载
     * @param apiKey       API密钥
     * @param userId       用户ID
     */
    public FilePreviewRequest(String fileId, Boolean asAttachment, String apiKey, String userId) {
        this.fileId = fileId;
        this.asAttachment = asAttachment;
        this.setApiKey(apiKey);
        this.setUserId(userId);
    }
}
