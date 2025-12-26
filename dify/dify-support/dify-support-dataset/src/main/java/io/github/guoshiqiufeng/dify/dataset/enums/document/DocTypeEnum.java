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
package io.github.guoshiqiufeng.dify.dataset.enums.document;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 14:19
 */
public enum DocTypeEnum {

    /**
     * 图书 Book
     */
    book,

    /**
     * 网页
     */
    web_page,

    /**
     * 学术论文/文章
     */
    paper,

    /**
     * 社交媒体帖子
     */
    social_media_post,

    /**
     * 维基百科条目
     */
    wikipedia_entry,

    /**
     * 个人文档
     */
    personal_document,

    /**
     * 商业文档
     */
    business_document,

    /**
     * 即时通讯记录
     */
    im_chat_log,

    /**
     * Notion同步文档
     */
    synced_from_notion,

    /**
     * GitHub同步文档
     */
    synced_from_github,

    /**
     * 其他文档类型
     */
    others
}
