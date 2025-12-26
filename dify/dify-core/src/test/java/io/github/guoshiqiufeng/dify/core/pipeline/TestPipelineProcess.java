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

public class TestPipelineProcess implements PipelineProcess<TestPipelineModel> {

    private final String expectedData;
    private final String processedData;
    private final boolean shouldBreak;
    private final long order;
    private final boolean supported;

    public TestPipelineProcess(String expectedData, String processedData, boolean shouldBreak, long order, boolean supported) {
        this.expectedData = expectedData;
        this.processedData = processedData;
        this.shouldBreak = shouldBreak;
        this.order = order;
        this.supported = supported;
    }

    @Override
    public boolean support(PipelineContext<TestPipelineModel> context) {
        return supported;
    }

    @Override
    public Long order() {
        return order;
    }

    @Override
    public void process(PipelineContext<TestPipelineModel> context) {
        TestPipelineModel model = context.getModel();
        if (expectedData.equals(model.getTestData())) {
            model.setTestData(processedData);
            model.setProcessed(true);
        }
        if (shouldBreak) {
            context.setNeedBreak(true);
        }
    }
}
