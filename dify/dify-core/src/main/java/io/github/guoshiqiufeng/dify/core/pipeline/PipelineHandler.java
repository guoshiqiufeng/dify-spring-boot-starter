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
package io.github.guoshiqiufeng.dify.core.pipeline;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程处理
 *
 * @author yanghq
 * @version 1.0
 * @since 2023/2/9 14:33
 */
@Slf4j
@Data
public class PipelineHandler {

    /**
     * 模板映射
     */
    private Map<String, PipelineTemplate<? extends PipelineModel>> templateConfig = null;

    public <T extends PipelineModel> PipelineContext<T> process(PipelineContext<T> context) {
        String code = context.getCode();
        PipelineTemplate<? extends PipelineModel> pipelineTemplate = templateConfig.get(code);

        if (pipelineTemplate == null || pipelineTemplate.getProcessList() == null ||
                pipelineTemplate.getProcessList().isEmpty()) {
            return context;
        }

        @SuppressWarnings("unchecked")
        PipelineTemplate<T> castedPipelineTemplate = (PipelineTemplate<T>) pipelineTemplate;

        List<PipelineProcess<T>> processList = new ArrayList<>(castedPipelineTemplate.getProcessList());
        processList = processList.stream().filter(process -> process.support(context))
                .sorted(Comparator.comparing(PipelineProcess::order))
                .collect(Collectors.toList());

        for (PipelineProcess<T> pipelineProcess : processList) {
            pipelineProcess.process(context);
            if (context.getNeedBreak()) {
                break;
            }
        }

        return context;
    }

}
