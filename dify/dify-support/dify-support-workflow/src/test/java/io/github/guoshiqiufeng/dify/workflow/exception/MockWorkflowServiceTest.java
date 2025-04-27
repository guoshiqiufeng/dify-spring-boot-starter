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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for {@link MockWorkflowService}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class MockWorkflowServiceTest {

    private MockWorkflowService mockWorkflowService;

    @BeforeEach
    public void setUp() {
        mockWorkflowService = new MockWorkflowService();
    }

    @Test
    public void testProcessDataSuccess() throws DifyWorkflowException {
        // Arrange
        String data = "Valid data";

        // Act
        String result = mockWorkflowService.processData(data);

        // Assert
        assertEquals("Processed: Valid data", result);
    }

    @Test
    public void testProcessDataWithNullThrowsException() {
        // Act & Assert
        DifyWorkflowException exception = ExceptionTestUtil.assertThrowsException(
                DifyWorkflowException.class,
                () -> mockWorkflowService.processData(null)
        );

        // If we had access to BaseException's getCode() and getMsg(), we could assert:
        // assertEquals(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE.getCode(), exception.getCode());
        // assertEquals(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE.getMsg(), exception.getMsg());
    }

    @Test
    public void testProcessDataWithEmptyStringThrowsException() {
        // Act & Assert
        DifyWorkflowException exception = ExceptionTestUtil.assertThrowsException(
                DifyWorkflowException.class,
                () -> mockWorkflowService.processData("")
        );

        // If we had access to BaseException's getCode() and getMsg(), we could assert:
        // assertEquals(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE.getCode(), exception.getCode());
        // assertEquals(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE.getMsg(), exception.getMsg());
    }

    @Test
    public void testCallApiSuccess() throws DifyWorkflowException {
        // Arrange
        String apiUrl = "https://api.example.com";

        // Act
        String result = mockWorkflowService.callApi(apiUrl);

        // Assert
        assertEquals("API Response", result);
    }

    @Test
    public void testCallApiWithNullThrowsException() {
        // Act & Assert
        DifyWorkflowException exception = ExceptionTestUtil.assertThrowsException(
                DifyWorkflowException.class,
                () -> mockWorkflowService.callApi(null)
        );

        // If we had access to BaseException's getCode() and getMsg(), we could assert:
        // assertEquals(DifyWorkflowExceptionEnum.DIFY_API_ERROR.getCode(), exception.getCode());
        // assertEquals(DifyWorkflowExceptionEnum.DIFY_API_ERROR.getMsg(), exception.getMsg());
    }

    @Test
    public void testCallApiWithInvalidUrlThrowsException() {
        // Act & Assert
        DifyWorkflowException exception = ExceptionTestUtil.assertThrowsException(
                DifyWorkflowException.class,
                () -> mockWorkflowService.callApi("http://example.com")
        );

        // If we had access to BaseException's getCode() and getMsg(), we could assert:
        // assertEquals(DifyWorkflowExceptionEnum.DIFY_API_ERROR.getCode(), exception.getCode());
        // assertEquals(DifyWorkflowExceptionEnum.DIFY_API_ERROR.getMsg(), exception.getMsg());
    }

    @Test
    public void testCatchingAndRethrowingException() {
        // Act & Assert
        DifyWorkflowException exception = ExceptionTestUtil.assertThrowsException(
                DifyWorkflowException.class,
                () -> {
                    try {
                        mockWorkflowService.processData(null);
                    } catch (DifyWorkflowException e) {
                        // Log error or do something else
                        throw e; // rethrow the exception
                    }
                }
        );

        assertNotNull(exception);
    }
}
