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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PipelineContextTest {

    @Test
    void testPipelineContextBuilder() {
        TestPipelineModel model = new TestPipelineModel();
        model.setTestData("test");

        PipelineContext<TestPipelineModel> context = PipelineContext.<TestPipelineModel>builder()
                .code("testCode")
                .model(model)
                .needBreak(false)
                .build();

        assertEquals("testCode", context.getCode());
        assertEquals("test", context.getModel().getTestData());
        assertFalse(context.getNeedBreak());
    }

    @Test
    void testPipelineContextSettersAndGetters() {
        TestPipelineModel model = new TestPipelineModel();
        model.setTestData("test");

        PipelineContext<TestPipelineModel> context = new PipelineContext<>();
        context.setCode("testCode");
        context.setModel(model);
        context.setNeedBreak(true);

        assertEquals("testCode", context.getCode());
        assertEquals("test", context.getModel().getTestData());
        assertTrue(context.getNeedBreak());
    }

    @Test
    void testPipelineContextNoArgsConstructor() {
        PipelineContext<TestPipelineModel> context = new PipelineContext<>();
        assertNull(context.getCode());
        assertNull(context.getModel());
        assertNull(context.getNeedBreak());
    }

    @Test
    void testPipelineContextAllArgsConstructor() {
        TestPipelineModel model = new TestPipelineModel();
        model.setTestData("test");

        PipelineContext<TestPipelineModel> context = new PipelineContext<>("testCode", model, false);

        assertEquals("testCode", context.getCode());
        assertEquals("test", context.getModel().getTestData());
        assertFalse(context.getNeedBreak());
    }

    @Test
    void testPipelineContextBuilderChain() {
        TestPipelineModel model = new TestPipelineModel();
        model.setTestData("test");

        PipelineContext<TestPipelineModel> context = new PipelineContext<TestPipelineModel>()
                .setCode("testCode")
                .setModel(model)
                .setNeedBreak(false);

        assertEquals("testCode", context.getCode());
        assertEquals("test", context.getModel().getTestData());
        assertFalse(context.getNeedBreak());
    }

    @Test
    void testPipelineContextEqualsAndHashCode() {
        TestPipelineModel model1 = new TestPipelineModel();
        model1.setTestData("test");

        TestPipelineModel model2 = new TestPipelineModel();
        model2.setTestData("test");

        PipelineContext<TestPipelineModel> context1 = new PipelineContext<>("testCode", model1, false);
        PipelineContext<TestPipelineModel> context2 = new PipelineContext<>("testCode", model2, false);

        assertEquals(context1, context2);
        assertEquals(context1.hashCode(), context2.hashCode());
    }
}
