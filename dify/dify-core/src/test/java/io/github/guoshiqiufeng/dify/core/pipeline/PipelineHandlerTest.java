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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PipelineHandlerTest {

    private PipelineHandler pipelineHandler;
    private TestPipelineModel model;
    private PipelineContext<TestPipelineModel> context;

    @BeforeEach
    void setUp() {
        pipelineHandler = new PipelineHandler();
        model = new TestPipelineModel();
        context = new PipelineContext<>();
        context.setModel(model);
        context.setNeedBreak(false);
    }

    @Test
    void testProcessWithEmptyTemplateConfig() {
        Map<String, PipelineTemplate<? extends PipelineModel>> templateConfig = new HashMap<>();
        pipelineHandler.setTemplateConfig(templateConfig);

        context.setCode("testCode");
        context.getModel().setTestData("initial");

        PipelineContext<TestPipelineModel> result = pipelineHandler.process(context);

        assertEquals("initial", result.getModel().getTestData());
        assertFalse(result.getModel().isProcessed());
    }

    @Test
    void testProcessWithNonMatchingCode() {
        Map<String, PipelineTemplate<? extends PipelineModel>> templateConfig = new HashMap<>();
        PipelineTemplate<TestPipelineModel> template = new PipelineTemplate<>();
        List<PipelineProcess<TestPipelineModel>> processList = new ArrayList<>();
        processList.add(new TestPipelineProcess("initial", "processed", false, 0, true));
        template.setProcessList(processList);
        templateConfig.put("otherCode", template);
        pipelineHandler.setTemplateConfig(templateConfig);

        context.setCode("testCode");
        context.getModel().setTestData("initial");

        PipelineContext<TestPipelineModel> result = pipelineHandler.process(context);

        assertEquals("initial", result.getModel().getTestData());
        assertFalse(result.getModel().isProcessed());
    }

    @Test
    void testProcessWithMatchingCode() {
        Map<String, PipelineTemplate<? extends PipelineModel>> templateConfig = new HashMap<>();
        PipelineTemplate<TestPipelineModel> template = new PipelineTemplate<>();
        List<PipelineProcess<TestPipelineModel>> processList = new ArrayList<>();
        processList.add(new TestPipelineProcess("initial", "processed", false, 0, true));
        template.setProcessList(processList);
        templateConfig.put("testCode", template);
        pipelineHandler.setTemplateConfig(templateConfig);

        context.setCode("testCode");
        context.getModel().setTestData("initial");

        PipelineContext<TestPipelineModel> result = pipelineHandler.process(context);

        assertEquals("processed", result.getModel().getTestData());
        assertTrue(result.getModel().isProcessed());
    }

    @Test
    void testProcessWithMultipleProcessors() {
        Map<String, PipelineTemplate<? extends PipelineModel>> templateConfig = new HashMap<>();
        PipelineTemplate<TestPipelineModel> template = new PipelineTemplate<>();
        List<PipelineProcess<TestPipelineModel>> processList = new ArrayList<>();
        processList.add(new TestPipelineProcess("initial", "step1", false, 1, true));
        processList.add(new TestPipelineProcess("step1", "step2", false, 0, true)); // Order 0, should run first
        template.setProcessList(processList);
        templateConfig.put("testCode", template);
        pipelineHandler.setTemplateConfig(templateConfig);

        context.setCode("testCode");
        context.getModel().setTestData("initial");

        PipelineContext<TestPipelineModel> result = pipelineHandler.process(context);

        assertEquals("step1", result.getModel().getTestData()); // Should be processed by both, in order
        assertTrue(result.getModel().isProcessed());
    }

    @Test
    void testProcessWithBreak() {
        Map<String, PipelineTemplate<? extends PipelineModel>> templateConfig = new HashMap<>();
        PipelineTemplate<TestPipelineModel> template = new PipelineTemplate<>();
        List<PipelineProcess<TestPipelineModel>> processList = new ArrayList<>();
        processList.add(new TestPipelineProcess("initial", "step1", true, 0, true)); // Should break
        processList.add(new TestPipelineProcess("step1", "step2", false, 1, true)); // Should not be executed
        template.setProcessList(processList);
        templateConfig.put("testCode", template);
        pipelineHandler.setTemplateConfig(templateConfig);

        context.setCode("testCode");
        context.getModel().setTestData("initial");

        PipelineContext<TestPipelineModel> result = pipelineHandler.process(context);

        assertEquals("step1", result.getModel().getTestData()); // Should only be processed by first processor
        assertTrue(result.getModel().isProcessed());
        assertTrue(result.getNeedBreak());
    }

    @Test
    void testProcessWithUnsupportedProcessor() {
        Map<String, PipelineTemplate<? extends PipelineModel>> templateConfig = new HashMap<>();
        PipelineTemplate<TestPipelineModel> template = new PipelineTemplate<>();
        List<PipelineProcess<TestPipelineModel>> processList = new ArrayList<>();
        processList.add(new TestPipelineProcess("initial", "step1", false, 0, false)); // Not supported
        processList.add(new TestPipelineProcess("initial", "step2", false, 1, true)); // Supported
        template.setProcessList(processList);
        templateConfig.put("testCode", template);
        pipelineHandler.setTemplateConfig(templateConfig);

        context.setCode("testCode");
        context.getModel().setTestData("initial");

        PipelineContext<TestPipelineModel> result = pipelineHandler.process(context);

        assertEquals("step2", result.getModel().getTestData()); // Should only be processed by supported processor
        assertTrue(result.getModel().isProcessed());
    }

    @Test
    void testGetAndSetTemplateConfig() {
        Map<String, PipelineTemplate<? extends PipelineModel>> templateConfig = new HashMap<>();
        pipelineHandler.setTemplateConfig(templateConfig);
        assertEquals(templateConfig, pipelineHandler.getTemplateConfig());
    }
}
