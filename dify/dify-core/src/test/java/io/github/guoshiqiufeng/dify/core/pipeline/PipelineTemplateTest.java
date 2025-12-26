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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTemplateTest {

    @Test
    void testPipelineTemplateSetAndGetProcessList() {
        PipelineTemplate<TestPipelineModel> template = new PipelineTemplate<>();
        List<PipelineProcess<TestPipelineModel>> processList = new ArrayList<>();

        TestPipelineProcess process = new TestPipelineProcess("test", "processed", false, 0, true);
        processList.add(process);

        template.setProcessList(processList);

        assertEquals(processList, template.getProcessList());
        assertEquals(1, template.getProcessList().size());
    }

    @Test
    void testPipelineTemplateDefaultConstructor() {
        PipelineTemplate<TestPipelineModel> template = new PipelineTemplate<>();
        assertNull(template.getProcessList());
    }

    @Test
    void testPipelineTemplateWithData() {
        List<PipelineProcess<TestPipelineModel>> processList = new ArrayList<>();
        TestPipelineProcess process1 = new TestPipelineProcess("test1", "processed1", false, 1, true);
        TestPipelineProcess process2 = new TestPipelineProcess("test2", "processed2", false, 2, true);
        processList.add(process1);
        processList.add(process2);

        PipelineTemplate<TestPipelineModel> template = new PipelineTemplate<>();
        template.setProcessList(processList);

        assertNotNull(template.getProcessList());
        assertEquals(2, template.getProcessList().size());
    }
}
