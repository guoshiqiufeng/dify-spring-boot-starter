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
package io.github.guoshiqiufeng.dify.server.exception;

import io.github.guoshiqiufeng.dify.core.exception.BaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link DifyServerException}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class DifyServerExceptionTest {

    @Test
    public void testConstructorWithDifyDataParsingFailure() {
        // Arrange
        DifyServerExceptionEnum exceptionEnum = DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE;

        // Act
        DifyServerException exception = new DifyServerException(exceptionEnum);

        // Assert
        assertNotNull(exception);
        assertInstanceOf(BaseException.class, exception);
        assertEquals(exceptionEnum.getMsg(), exception.getMessage());
    }

    @Test
    public void testConstructorWithDifyApiError() {
        // Arrange
        DifyServerExceptionEnum exceptionEnum = DifyServerExceptionEnum.DIFY_API_ERROR;

        // Act
        DifyServerException exception = new DifyServerException(exceptionEnum);

        // Assert
        assertNotNull(exception);
        assertInstanceOf(BaseException.class, exception);
        assertEquals(exceptionEnum.getMsg(), exception.getMessage());
    }

    @Test
    public void testExtendsBaseException() {
        // Act
        DifyServerException exception = new DifyServerException(DifyServerExceptionEnum.DIFY_API_ERROR);

        // Assert
        assertInstanceOf(BaseException.class, exception);
    }

    @Test
    public void testExceptionCanBeThrown() {
        // Arrange
        DifyServerException exception = new DifyServerException(DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE);

        // Act & Assert
        DifyServerException thrownException = ExceptionTestUtil.assertThrowsException(
                DifyServerException.class,
                () -> ExceptionTestUtil.throwException(exception)
        );

        // Assert further
        assertSame(exception, thrownException);
    }

    @Test
    public void testThrowingExceptionInCode() {
        // Act & Assert
        DifyServerException thrownException = ExceptionTestUtil.assertThrowsException(
                DifyServerException.class,
                () -> {
                    throw new DifyServerException(DifyServerExceptionEnum.DIFY_API_ERROR);
                }
        );

        // Assert further
        assertNotNull(thrownException);
        assertEquals(DifyServerExceptionEnum.DIFY_API_ERROR.getMsg(), thrownException.getMessage());
    }

    @Test
    public void testAllExceptionEnumValuesCanCreateException() {
        // Act & Assert
        for (DifyServerExceptionEnum exceptionEnum : DifyServerExceptionEnum.values()) {
            DifyServerException exception = new DifyServerException(exceptionEnum);
            assertNotNull(exception);
            assertInstanceOf(BaseException.class, exception);
            assertEquals(exceptionEnum.getMsg(), exception.getMessage());
        }
    }

    @Test
    public void testCatchingAndHandlingException() {
        // Act & Assert
        try {
            throw new DifyServerException(DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        } catch (DifyServerException e) {
            assertEquals(DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE.getMsg(), e.getMessage());
        }
    }
}
