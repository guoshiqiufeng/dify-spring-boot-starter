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
package io.github.guoshiqiufeng.dify.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/24 13:38
 */
@Data
public class FileUploadResponse implements Serializable {
    private static final long serialVersionUID = 135249636075831293L;

    private String id;

    private String name;

    private Integer size;

    /**
     * 文件后缀
     */
    private String extension;

    /**
     * 文件 mime-type
     */
    @JsonAlias("mime_type")
    private String mimeType;

    /**
     * 创建人
     */
    @JsonAlias("created_by")
    private String createdBy;

    /**
     * 创建时间
     */
    @JsonAlias("created_at")
    private Long createdAt;

    /**
     * 预览 URL
     */
    @JsonAlias("preview_url")
    private String previewUrl;

    /**
     * 源 URL
     */
    @JsonAlias("source_url")
    private String sourceUrl;

    /**
     * 原始 URL
     */
    @JsonAlias("original_url")
    private String originalUrl;

    /**
     * 用户 ID
     */
    @JsonAlias("user_id")
    private String userId;

    /**
     * 租户 ID
     */
    @JsonAlias("tenant_id")
    private String tenantId;

    /**
     * 会话 ID
     */
    @JsonAlias("conversation_id")
    private String conversationId;

    /**
     * 文件存储键
     */
    @JsonAlias("file_key")
    private String fileKey;
}
