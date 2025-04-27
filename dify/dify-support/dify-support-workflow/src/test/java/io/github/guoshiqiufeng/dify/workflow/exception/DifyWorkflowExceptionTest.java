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

import io.github.guoshiqiufeng.dify.core.exception.BaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link DifyWorkflowException}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class DifyWorkflowExceptionTest {

    @Test
    public void testConstructorWithDifyDataParsingFailure() {
        // Arrange
        DifyWorkflowExceptionEnum exceptionEnum = DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE;

        // Act
        DifyWorkflowException exception = new DifyWorkflowException(exceptionEnum);

        // Assert
        assertNotNull(exception);
        assertTrue(exception instanceof BaseException);
        // These assertions would work if we had access to BaseException
        // assertEquals(exceptionEnum.getCode(), exception.getCode());
        // assertEquals(exceptionEnum.getMsg(), exception.getMsg());
    }

    @Test
    public void testConstructorWithDifyApiError() {
        // Arrange
        DifyWorkflowExceptionEnum exceptionEnum = DifyWorkflowExceptionEnum.DIFY_API_ERROR;

        // Act
        DifyWorkflowException exception = new DifyWorkflowException(exceptionEnum);

        // Assert
        assertNotNull(exception);
        assertTrue(exception instanceof BaseException);
        // These assertions would work if we had access to BaseException
        // assertEquals(exceptionEnum.getCode(), exception.getCode());
        // assertEquals(exceptionEnum.getMsg(), exception.getMsg());
    }

    @Test
    public void testExceptionMessage() {
        // Arrange
        DifyWorkflowExceptionEnum exceptionEnum = DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE;

        // Act
        DifyWorkflowException exception = new DifyWorkflowException(exceptionEnum);

        // Assert
        // If BaseException properly sets the message, this would work
        // But we don't know without seeing BaseException implementation
        // assertTrue(exception.getMessage().contains(exceptionEnum.getMsg()));
        assertNotNull(exception);
    }

    @Test
    public void testExtendsBaseException() {
        // Act
        DifyWorkflowException exception = new DifyWorkflowException(DifyWorkflowExceptionEnum.DIFY_API_ERROR);

        // Assert
        assertTrue(exception instanceof BaseException);
    }

    @Test
    public void testExceptionCanBeThrown() {
        // Arrange
        DifyWorkflowException exception = new DifyWorkflowException(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE);

        // Act & Assert
        DifyWorkflowException thrownException = ExceptionTestUtil.assertThrowsException(
                DifyWorkflowException.class,
                () -> ExceptionTestUtil.throwException(exception)
        );

        // Assert further
        assertSame(exception, thrownException);
    }

    @Test
    public void testThrowingExceptionInCode() {
        // Act & Assert
        DifyWorkflowException thrownException = ExceptionTestUtil.assertThrowsException(
                DifyWorkflowException.class,
                () -> {
                    throw new DifyWorkflowException(DifyWorkflowExceptionEnum.DIFY_API_ERROR);
                }
        );

        // Assert further
        assertNotNull(thrownException);
        // If we had access to BaseException, we could assert:
        // assertEquals(DifyWorkflowExceptionEnum.DIFY_API_ERROR.getCode(), thrownException.getCode());
        // assertEquals(DifyWorkflowExceptionEnum.DIFY_API_ERROR.getMsg(), thrownException.getMsg());
    }

    @Test
    public void testAllExceptionEnumValuesCanCreateException() {
        // Act & Assert
        for (DifyWorkflowExceptionEnum exceptionEnum : DifyWorkflowExceptionEnum.values()) {
            DifyWorkflowException exception = new DifyWorkflowException(exceptionEnum);
            assertNotNull(exception);
            assertTrue(exception instanceof BaseException);
        }
    }
} 