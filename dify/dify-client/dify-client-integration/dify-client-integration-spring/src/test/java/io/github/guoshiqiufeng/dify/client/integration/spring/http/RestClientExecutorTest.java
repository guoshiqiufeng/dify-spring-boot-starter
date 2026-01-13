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
package io.github.guoshiqiufeng.dify.client.integration.spring.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RestClientExecutor
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class RestClientExecutorTest {

    @Mock
    private Object restClient;

    @Mock
    private JsonMapper jsonMapper;

    private RestClientExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new RestClientExecutor(restClient, jsonMapper);
    }

    @Test
    void testConstructorWithTwoParameters() {
        // Act
        RestClientExecutor executor = new RestClientExecutor(restClient, jsonMapper);

        // Assert
        assertNotNull(executor);
    }

    @Test
    void testConstructorWithThreeParameters() {
        // Act
        RestClientExecutor executor = new RestClientExecutor(restClient, jsonMapper, false);

        // Assert
        assertNotNull(executor);
    }

    @Test
    void testConstructorWithNullSkipNull() {
        // Act
        RestClientExecutor executor = new RestClientExecutor(restClient, jsonMapper, null);

        // Assert
        assertNotNull(executor);
    }

    @Test
    void testExtractFilenameFromContentDisposition() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("extractFilename", String.class);
        method.setAccessible(true);

        // Act & Assert
        assertEquals("test.txt", method.invoke(executor, "form-data; name=\"file\"; filename=\"test.txt\""));
        assertEquals("document.pdf", method.invoke(executor, "attachment; filename=\"document.pdf\""));
        assertEquals("file", method.invoke(executor, "form-data; name=\"file\""));
        assertEquals("file", method.invoke(executor, (String) null));
        assertEquals("file", method.invoke(executor, ""));
    }

    @Test
    void testExtractFilenameWithSpecialCharacters() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("extractFilename", String.class);
        method.setAccessible(true);

        // Act & Assert
        assertEquals("test file.txt", method.invoke(executor, "form-data; name=\"file\"; filename=\"test file.txt\""));
        assertEquals("test-file_123.txt", method.invoke(executor, "form-data; name=\"file\"; filename=\"test-file_123.txt\""));
    }

    @Test
    void testConvertHeadersWithMultipleValues() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("convertHeaders", org.springframework.http.HttpHeaders.class);
        method.setAccessible(true);

        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("Content-Type", "application/json");
        springHeaders.add("Authorization", "Bearer token");
        springHeaders.add("X-Custom-Header", "value1");
        springHeaders.add("X-Custom-Header", "value2");

        // Act
        var result = method.invoke(executor, springHeaders);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGetStatusCodeValueWithResponseEntity() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("getStatusCodeValue", ResponseEntity.class);
        method.setAccessible(true);

        ResponseEntity<String> responseEntity = ResponseEntity.ok("test");

        // Act
        int statusCode = (int) method.invoke(executor, responseEntity);

        // Assert
        assertEquals(200, statusCode);
    }

    @Test
    void testGetStatusCodeValueWithErrorResponse() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("getStatusCodeValue", ResponseEntity.class);
        method.setAccessible(true);

        ResponseEntity<String> responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");

        // Act
        int statusCode = (int) method.invoke(executor, responseEntity);

        // Assert
        assertEquals(400, statusCode);
    }

    @Test
    void testUnwrapExceptionWithInvocationTargetException() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("unwrapException", Exception.class);
        method.setAccessible(true);

        Exception cause = new RuntimeException("Original cause");
        java.lang.reflect.InvocationTargetException wrappedException =
            new java.lang.reflect.InvocationTargetException(cause);

        // Act
        Throwable result = (Throwable) method.invoke(executor, wrappedException);

        // Assert
        assertSame(cause, result);
    }

    @Test
    void testUnwrapExceptionWithRegularException() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("unwrapException", Exception.class);
        method.setAccessible(true);

        Exception regularException = new RuntimeException("Regular exception");

        // Act
        Throwable result = (Throwable) method.invoke(executor, regularException);

        // Assert
        assertSame(regularException, result);
    }

    @Test
    void testGetExceptionMessageWithRegularException() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("getExceptionMessage", Exception.class);
        method.setAccessible(true);

        Exception exception = new RuntimeException("Test error message");

        // Act
        String message = (String) method.invoke(executor, exception);

        // Assert
        assertEquals("Test error message", message);
    }

    @Test
    void testGetExceptionMessageWithNullMessage() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("getExceptionMessage", Exception.class);
        method.setAccessible(true);

        Exception exception = new RuntimeException((String) null);

        // Act
        String message = (String) method.invoke(executor, exception);

        // Assert
        assertEquals("RuntimeException", message);
    }

    @Test
    void testFindMethodInHierarchyWithExistingMethod() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("findMethodInHierarchy", Class.class, String.class);
        method.setAccessible(true);

        // Act
        Method foundMethod = (Method) method.invoke(executor, String.class, "toString");

        // Assert
        assertNotNull(foundMethod);
        assertEquals("toString", foundMethod.getName());
    }

    @Test
    void testFindMethodInHierarchyWithNonExistingMethod() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("findMethodInHierarchy", Class.class, String.class);
        method.setAccessible(true);

        // Act
        Method foundMethod = (Method) method.invoke(executor, String.class, "nonExistingMethod");

        // Assert
        assertNull(foundMethod);
    }

    @Test
    void testExtractFilenameWithEdgeCases() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("extractFilename", String.class);
        method.setAccessible(true);

        // Act & Assert - edge cases
        assertEquals("", method.invoke(executor, "filename=\"\""));
        assertEquals("file", method.invoke(executor, "form-data"));
        assertEquals("test.txt", method.invoke(executor, "filename=\"test.txt\"; other=\"value\""));
    }

    @Test
    void testConvertHeadersWithEmptyHeaders() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("convertHeaders", org.springframework.http.HttpHeaders.class);
        method.setAccessible(true);

        org.springframework.http.HttpHeaders emptyHeaders = new org.springframework.http.HttpHeaders();

        // Act
        var result = method.invoke(executor, emptyHeaders);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testExtractFilenameWithMissingClosingQuote() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("extractFilename", String.class);
        method.setAccessible(true);

        // Act & Assert - missing closing quote
        assertEquals("file", method.invoke(executor, "filename=\"test.txt"));
    }

    @Test
    void testUnwrapExceptionWithNullCause() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("unwrapException", Exception.class);
        method.setAccessible(true);

        java.lang.reflect.InvocationTargetException exceptionWithNullCause =
            new java.lang.reflect.InvocationTargetException(null);

        // Act
        Throwable result = (Throwable) method.invoke(executor, exceptionWithNullCause);

        // Assert
        assertSame(exceptionWithNullCause, result);
    }

    @Test
    void testFindMethodWithPublicMethod() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("findMethod", Class.class, String.class, Class[].class);
        method.setAccessible(true);

        // Act
        Method foundMethod = (Method) method.invoke(executor, String.class, "toString", new Class[]{});

        // Assert
        assertNotNull(foundMethod);
        assertEquals("toString", foundMethod.getName());
    }

    @Test
    void testFindMethodWithDeclaredMethod() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("findMethod", Class.class, String.class, Class[].class);
        method.setAccessible(true);

        // Act - find a private method
        Method foundMethod = (Method) method.invoke(executor, RestClientExecutor.class, "extractFilename", new Class[]{String.class});

        // Assert
        assertNotNull(foundMethod);
        assertEquals("extractFilename", foundMethod.getName());
    }

    @Test
    void testFindMethodNotFound() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("findMethod", Class.class, String.class, Class[].class);
        method.setAccessible(true);

        // Act & Assert
        try {
            method.invoke(executor, String.class, "nonExistentMethod", new Class[]{});
            fail("Expected InvocationTargetException");
        } catch (java.lang.reflect.InvocationTargetException e) {
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
    }

    @Test
    void testGetStatusCodeValueWithDifferentStatusCodes() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("getStatusCodeValue", ResponseEntity.class);
        method.setAccessible(true);

        // Test various status codes
        ResponseEntity<String> ok = ResponseEntity.ok("test");
        ResponseEntity<String> created = ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body("test");
        ResponseEntity<String> notFound = ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body("test");

        // Act & Assert
        assertEquals(200, (int) method.invoke(executor, ok));
        assertEquals(201, (int) method.invoke(executor, created));
        assertEquals(404, (int) method.invoke(executor, notFound));
    }

    @Test
    void testConvertHeadersWithMultipleHeaderValues() throws Exception {
        // Arrange
        Method method = RestClientExecutor.class.getDeclaredMethod("convertHeaders", org.springframework.http.HttpHeaders.class);
        method.setAccessible(true);

        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("X-Custom", "value1");
        springHeaders.add("X-Custom", "value2");
        springHeaders.add("X-Custom", "value3");

        // Act
        var result = method.invoke(executor, springHeaders);

        // Assert
        assertNotNull(result);
    }
}
