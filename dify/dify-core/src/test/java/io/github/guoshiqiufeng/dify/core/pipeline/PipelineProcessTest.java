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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineProcessTest {

    @Test
    void testDefaultSupportMethod() {
        PipelineProcess<TestPipelineModel> process = new PipelineProcess<TestPipelineModel>() {
            @Override
            public void process(PipelineContext<TestPipelineModel> context) {
                // Do nothing for this test
            }
        };

        PipelineContext<TestPipelineModel> context = new PipelineContext<>();
        assertTrue(process.support(context));
    }

    @Test
    void testDefaultOrderMethod() {
        PipelineProcess<TestPipelineModel> process = new PipelineProcess<TestPipelineModel>() {
            @Override
            public void process(PipelineContext<TestPipelineModel> context) {
                // Do nothing for this test
            }
        };

        assertEquals(0L, process.order());
    }

    @Test
    void testProcessMethod() {
        TestPipelineModel model = new TestPipelineModel();
        model.setTestData("initial");

        PipelineContext<TestPipelineModel> context = new PipelineContext<>();
        context.setModel(model);

        PipelineProcess<TestPipelineModel> process = new PipelineProcess<TestPipelineModel>() {
            @Override
            public void process(PipelineContext<TestPipelineModel> context) {
                context.getModel().setTestData("processed");
            }
        };

        process.process(context);
        assertEquals("processed", context.getModel().getTestData());
    }
}
