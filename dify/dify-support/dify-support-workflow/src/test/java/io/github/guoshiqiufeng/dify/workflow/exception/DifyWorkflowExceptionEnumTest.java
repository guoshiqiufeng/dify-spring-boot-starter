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
package io.github.guoshiqiufeng.dify.workflow.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link DifyWorkflowExceptionEnum}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class DifyWorkflowExceptionEnumTest {

    @Test
    public void testDifyDataParsingFailure() {
        // Act
        DifyWorkflowExceptionEnum exceptionEnum = DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE;

        // Assert
        assertEquals(39910, exceptionEnum.getCode());
        assertEquals("数据解析失败", exceptionEnum.getMsg());
    }

    @Test
    public void testDifyApiError() {
        // Act
        DifyWorkflowExceptionEnum exceptionEnum = DifyWorkflowExceptionEnum.DIFY_API_ERROR;

        // Assert
        assertEquals(29990, exceptionEnum.getCode());
        assertEquals("远程调用失败", exceptionEnum.getMsg());
    }

    @Test
    public void testEnumValues() {
        // Act
        DifyWorkflowExceptionEnum[] values = DifyWorkflowExceptionEnum.values();

        // Assert
        assertEquals(2, values.length);
        assertEquals(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE, values[0]);
        assertEquals(DifyWorkflowExceptionEnum.DIFY_API_ERROR, values[1]);
    }

    @Test
    public void testEnumValueOf() {
        // Act
        DifyWorkflowExceptionEnum dataParsingFailure = DifyWorkflowExceptionEnum.valueOf("DIFY_DATA_PARSING_FAILURE");
        DifyWorkflowExceptionEnum apiError = DifyWorkflowExceptionEnum.valueOf("DIFY_API_ERROR");

        // Assert
        assertEquals(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE, dataParsingFailure);
        assertEquals(DifyWorkflowExceptionEnum.DIFY_API_ERROR, apiError);
    }

    @Test
    public void testEnumImplementsBaseExceptionEnum() {
        // Act & Assert
        assertTrue(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE instanceof io.github.guoshiqiufeng.dify.core.exception.BaseExceptionEnum);
    }

    @Test
    public void testAllArgsConstructor() {
        // Assert
        assertNotNull(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        assertNotNull(DifyWorkflowExceptionEnum.DIFY_API_ERROR);
    }
} 