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
package io.github.guoshiqiufeng.dify.dataset.enums.document;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 14:27
 */
public enum DocFormEnum {

    /**
     * text 文档直接 embedding，经济模式默认为该模式
     */
    text_model,

    /**
     * parent-child 模式
     */
    hierarchical_model,

    /**
     * Q&A 模式：为分片文档生成 Q&A 对，然后对问题进行 embedding
     */
    qa_model
}
