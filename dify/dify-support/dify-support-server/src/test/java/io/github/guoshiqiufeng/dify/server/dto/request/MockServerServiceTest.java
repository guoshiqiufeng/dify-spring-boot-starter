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
package io.github.guoshiqiufeng.dify.server.dto.request;

import io.github.guoshiqiufeng.dify.server.exception.DifyServerException;
import io.github.guoshiqiufeng.dify.server.exception.DifyServerExceptionEnum;
import io.github.guoshiqiufeng.dify.server.exception.ExceptionTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Mock service class that uses DifyLoginRequestVO and throws DifyServerException
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class MockServerServiceTest {

    private MockServerService mockServerService;

    @BeforeEach
    public void setUp() {
        mockServerService = new MockServerService();
    }

    /**
     * A simple mock server service that uses DifyLoginRequestVO
     */
    private static class MockServerService {
        public String login(DifyLoginRequest loginRequest) throws DifyServerException {
            if (loginRequest == null) {
                throw new DifyServerException(DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE);
            }

            if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
                throw new DifyServerException(DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE);
            }

            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                throw new DifyServerException(DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE);
            }

            // Simulate a remote API call that might fail
            if ("error@example.com".equals(loginRequest.getEmail())) {
                throw new DifyServerException(DifyServerExceptionEnum.DIFY_API_ERROR);
            }

            return "Login successful for: " + loginRequest.getEmail();
        }
    }

    @Test
    public void testLoginSuccess() throws DifyServerException {
        // Arrange
        DifyLoginRequest request = DifyLoginRequest.build("test@example.com", "password123");

        // Act
        String result = mockServerService.login(request);

        // Assert
        assertEquals("Login successful for: test@example.com", result);
    }

    @Test
    public void testLoginWithNullRequest() {
        // Act & Assert
        DifyServerException exception = ExceptionTestUtil.assertThrowsException(
                DifyServerException.class,
                () -> mockServerService.login(null)
        );

        assertEquals(DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE.getMsg(), exception.getMessage());
    }

    @Test
    public void testLoginWithEmptyEmail() {
        // Arrange
        DifyLoginRequest request = DifyLoginRequest.build("", "password123");

        // Act & Assert
        DifyServerException exception = ExceptionTestUtil.assertThrowsException(
                DifyServerException.class,
                () -> mockServerService.login(request)
        );

        assertEquals(DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE.getMsg(), exception.getMessage());
    }

    @Test
    public void testLoginWithEmptyPassword() {
        // Arrange
        DifyLoginRequest request = DifyLoginRequest.build("test@example.com", "");

        // Act & Assert
        DifyServerException exception = ExceptionTestUtil.assertThrowsException(
                DifyServerException.class,
                () -> mockServerService.login(request)
        );

        assertEquals(DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE.getMsg(), exception.getMessage());
    }

    @Test
    public void testLoginWithApiError() {
        // Arrange
        DifyLoginRequest request = DifyLoginRequest.build("error@example.com", "password123");

        // Act & Assert
        DifyServerException exception = ExceptionTestUtil.assertThrowsException(
                DifyServerException.class,
                () -> mockServerService.login(request)
        );

        assertEquals(DifyServerExceptionEnum.DIFY_API_ERROR.getMsg(), exception.getMessage());
    }

    @Test
    public void testLoginWithCatchingAndHandlingException() {
        // Arrange
        DifyLoginRequest request = DifyLoginRequest.build("error@example.com", "password123");

        // Act & Assert
        try {
            mockServerService.login(request);
            fail("Expected exception was not thrown");
        } catch (DifyServerException e) {
            assertEquals(DifyServerExceptionEnum.DIFY_API_ERROR.getMsg(), e.getMessage());
        }
    }
}
