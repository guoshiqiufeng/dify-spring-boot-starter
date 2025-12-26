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
package io.github.guoshiqiufeng.dify.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BaseException
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class BaseExceptionTest {

    // Test enum implementing BaseExceptionEnum interface
    private enum TestExceptionEnum implements BaseExceptionEnum {
        TEST_ERROR(10001, "Test error message"),
        ANOTHER_ERROR(10002, "Another error message");

        private final Integer code;
        private final String msg;

        TestExceptionEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        @Override
        public Integer getCode() {
            return code;
        }

        @Override
        public String getMsg() {
            return msg;
        }
    }

    /**
     * Test BaseException with enum constructor
     */
    @Test
    public void testBaseExceptionWithEnum() {
        // Create exception instance
        BaseException exception = new BaseException(TestExceptionEnum.TEST_ERROR);

        // Verify the error message is correctly set
        assertEquals("Test error message", exception.getMessage());

        // Verify exception is a subclass of RuntimeException
        assertInstanceOf(RuntimeException.class, exception);
    }

    /**
     * Test BaseException with direct parameters constructor
     */
    @Test
    public void testBaseExceptionWithParams() {
        // Create exception instance
        Integer errorCode = 500;
        String errorMessage = "Internal server error";
        BaseException exception = new BaseException(errorCode, errorMessage);

        // Verify exception message
        assertNotNull(exception.getMessage());
    }

    /**
     * Test throwing and catching exception
     */
    @Test
    public void testThrowAndCatchException() {
        // Verify exception can be correctly thrown and caught
        assertThrows(BaseException.class, () -> {
            throw new BaseException(TestExceptionEnum.ANOTHER_ERROR);
        });

        // Verify the message after catching exception
        try {
            throw new BaseException(TestExceptionEnum.ANOTHER_ERROR);
        } catch (BaseException e) {
            assertEquals("Another error message", e.getMessage());
        }
    }

    /**
     * Test different constructors
     */
    @Test
    public void testDifferentConstructors() {
        // Test enum constructor
        BaseException exception1 = new BaseException(TestExceptionEnum.TEST_ERROR);
        assertEquals("Test error message", exception1.getMessage());

        // Test direct parameters constructor
        BaseException exception2 = new BaseException(404, "Resource not found");
        assertNotNull(exception2.getMessage());
    }
}
