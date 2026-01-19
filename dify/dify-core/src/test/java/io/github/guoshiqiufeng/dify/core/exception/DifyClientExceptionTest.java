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
 * @author yanghq
 * @version 1.0
 * @since 2025/5/19 15:35
 */
public class DifyClientExceptionTest {

    /**
     * Test BaseException with enum constructor
     */
    @Test
    public void testBaseExceptionWithEnum() {
        // Create exception instance
        DifyClientException exception = new DifyClientException(DiftClientExceptionEnum.UNAUTHORIZED);

        // Verify the error message is correctly set
        assertEquals("Access token is invalid", exception.getMessage());

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
            throw new DifyClientException(DiftClientExceptionEnum.UNAUTHORIZED);
        });

        // Verify the message after catching exception
        try {
            throw new DifyClientException(DiftClientExceptionEnum.UNAUTHORIZED);
        } catch (BaseException e) {
            assertEquals("Access token is invalid", e.getMessage());
        }
    }

    /**
     * Test different constructors
     */
    @Test
    public void testDifferentConstructors() {
        // Test enum constructor
        DifyClientException exception1 = new DifyClientException(DiftClientExceptionEnum.NOT_FOUND);
        assertEquals("Not Found", exception1.getMessage());

        // Test constructor with Integer and String parameters
        DifyClientException exception2 = new DifyClientException(500, "Internal server error");
        assertEquals("Internal server error", exception2.getMessage());
        assertEquals(500, exception2.getCode());
    }

    /**
     * Test noLogin() method with 401 code
     */
    @Test
    public void testNoLoginWithCode401() {
        DifyClientException exception = new DifyClientException(DiftClientExceptionEnum.UNAUTHORIZED);
        Boolean result = exception.noLogin();
        assertTrue(result);
    }

    /**
     * Test noLogin() method with [401] in message
     */
    @Test
    public void testNoLoginWithMessageContaining401() {
        // Create a custom exception with [401] in message
        DifyClientException exception = new DifyClientException(500, "Error [401] occurred");

        // Test the noLogin method
        Boolean result = exception.noLogin();
        assertTrue(result);
    }

    /**
     * Test noLogin() method returns false when not 401
     */
    @Test
    public void testNoLoginReturnsFalse() {
        DifyClientException exception = new DifyClientException(DiftClientExceptionEnum.NOT_FOUND);
        Boolean result = exception.noLogin();
        assertFalse(result);
    }

}
