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
package io.github.guoshiqiufeng.dify.core.pipeline;

/**
 * 业务执行器
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/2/9 14:26
 */
public interface PipelineProcess<T extends PipelineModel> {

    /**
     * 是否支持
     *
     * @param context 内容
     * @return 是否支持 true 支持 false 不支持
     */
    default boolean support(PipelineContext<T> context) {
        return true;
    }

    /**
     * 获取排序，越小越靠前
     *
     * @return 排序
     */
    default Long order() {
        return 0L;
    }

    /**
     * 处理
     *
     * @param context 内容
     */
    void process(PipelineContext<T> context);
}
