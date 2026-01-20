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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for extracting error response information from Spring HTTP exceptions.
 * Uses reflection exclusively to support different Spring versions (5, 6, 7).
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@Slf4j
@UtilityClass
public class SpringErrorResponseExtractor {

    /**
     * Extract error response from Spring's HTTP exceptions.
     * Handles RestClientResponseException, HttpClientErrorException, HttpServerErrorException, etc.
     *
     * @param throwable the exception thrown by Spring HTTP client
     * @return ResponseEntity with error details, or null if extraction fails
     */
    public static ResponseEntity<String> extractErrorResponse(Throwable throwable) {
        try {
            Class<?> exceptionClass = throwable.getClass();
            String className = exceptionClass.getName();

            log.debug("Extracting error response from exception: {}", className);

            // Handle Spring HTTP exceptions (works for Spring 5, 6, and 7)
            if (className.contains("RestClientResponseException") ||
                    className.contains("HttpClientErrorException") ||
                    className.contains("HttpServerErrorException") ||
                    className.contains("HttpStatusCodeException") ||
                    className.contains("WebClientResponseException")) {

                // Extract status code using reflection only
                int statusCode = extractStatusCode(throwable, exceptionClass);
                log.debug("Extracted status code: {}", statusCode);

                // Extract response body using reflection only
                String responseBody = extractResponseBody(throwable, exceptionClass);
                log.debug("Extracted response body length: {}", responseBody != null ? responseBody.length() : 0);

                // Extract headers using reflection only
                HttpHeaders headers = extractHeaders(throwable, exceptionClass);

                // Build ResponseEntity
                return ResponseEntity.status(statusCode)
                        .headers(headers)
                        .body(responseBody);
            }
        } catch (Exception e) {
            // If extraction fails, return null to let the original exception propagate
            log.warn("Failed to extract error response from {}: {}", throwable.getClass().getName(), e.getMessage(), e);
        }
        return null;
    }

    /**
     * Extract status code from exception using reflection.
     * Tries multiple methods to support different Spring versions.
     *
     * @param throwable      the exception
     * @param exceptionClass the exception class
     * @return status code
     * @throws Exception if extraction fails
     */
    public static int extractStatusCode(Throwable throwable, Class<?> exceptionClass) throws Exception {
        log.debug("Attempting to extract status code from: {}", exceptionClass.getName());

        // Try getStatusCode().value() (works for both Spring 5 and 6)
        try {
            java.lang.reflect.Method getStatusCodeMethod = findMethodInHierarchy(exceptionClass, "getStatusCode");
            if (getStatusCodeMethod != null) {
                log.debug("Found getStatusCode() method");
                getStatusCodeMethod.setAccessible(true);
                Object statusCode = getStatusCodeMethod.invoke(throwable);
                log.debug("getStatusCode() returned: {} (type: {})", statusCode, statusCode != null ? statusCode.getClass().getName() : "null");

                if (statusCode instanceof Integer) {
                    return (Integer) statusCode;
                }
                // Try calling value() on the result
                try {
                    java.lang.reflect.Method valueMethod = statusCode.getClass().getMethod("value");
                    valueMethod.setAccessible(true);
                    int value = (int) valueMethod.invoke(statusCode);
                    log.debug("Extracted status code via value(): {}", value);
                    return value;
                } catch (NoSuchMethodException e) {
                    log.debug("No value() method found on status code object");
                }
            } else {
                log.debug("getStatusCode() method not found");
            }
        } catch (Exception e) {
            log.debug("Failed to extract via getStatusCode(): {}", e.getMessage());
        }

        // Try getRawStatusCode() (Spring 6+)
        try {
            java.lang.reflect.Method getRawStatusCodeMethod = findMethodInHierarchy(exceptionClass, "getRawStatusCode");
            if (getRawStatusCodeMethod != null) {
                log.debug("Found getRawStatusCode() method");
                getRawStatusCodeMethod.setAccessible(true);
                int value = (int) getRawStatusCodeMethod.invoke(throwable);
                log.debug("Extracted status code via getRawStatusCode(): {}", value);
                return value;
            } else {
                log.debug("getRawStatusCode() method not found");
            }
        } catch (Exception e) {
            log.debug("Failed to extract via getRawStatusCode(): {}", e.getMessage());
        }

        // Try getStatusCodeValue() (older Spring versions)
        try {
            java.lang.reflect.Method getStatusCodeValueMethod = findMethodInHierarchy(exceptionClass, "getStatusCodeValue");
            if (getStatusCodeValueMethod != null) {
                log.debug("Found getStatusCodeValue() method");
                getStatusCodeValueMethod.setAccessible(true);
                int value = (int) getStatusCodeValueMethod.invoke(throwable);
                log.debug("Extracted status code via getStatusCodeValue(): {}", value);
                return value;
            } else {
                log.debug("getStatusCodeValue() method not found");
            }
        } catch (Exception e) {
            log.debug("Failed to extract via getStatusCodeValue(): {}", e.getMessage());
        }

        throw new Exception("Unable to extract status code from exception: " + exceptionClass.getName());
    }

    /**
     * Extract response body from exception using reflection.
     * Tries multiple methods to support different Spring versions.
     *
     * @param throwable      the exception
     * @param exceptionClass the exception class
     * @return response body as String
     * @throws Exception if extraction fails
     */
    public static String extractResponseBody(Throwable throwable, Class<?> exceptionClass) throws Exception {
        String extractedBody = null;

        // Try getResponseBodyAsString()
        try {
            java.lang.reflect.Method getResponseBodyMethod = exceptionClass.getMethod("getResponseBodyAsString");
            getResponseBodyMethod.setAccessible(true);
            Object result = getResponseBodyMethod.invoke(throwable);
            if (result != null) {
                extractedBody = (String) result;
                log.debug("Extracted response body via getResponseBodyAsString(): {} chars", extractedBody.length());
                if (!extractedBody.isEmpty()) {
                    return extractedBody;
                }
            } else {
                log.debug("getResponseBodyAsString() returned null");
            }
        } catch (NoSuchMethodException e) {
            log.debug("Method getResponseBodyAsString() not found");
        } catch (Exception e) {
            log.debug("Failed to extract via getResponseBodyAsString(): {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
        }

        // Try getResponseBodyAsByteArray()
        try {
            java.lang.reflect.Method getResponseBodyMethod = exceptionClass.getMethod("getResponseBodyAsByteArray");
            getResponseBodyMethod.setAccessible(true);
            Object result = getResponseBodyMethod.invoke(throwable);
            if (result != null) {
                byte[] bodyBytes = (byte[]) result;
                if (bodyBytes.length > 0) {
                    extractedBody = new String(bodyBytes, java.nio.charset.StandardCharsets.UTF_8);
                    log.debug("Extracted response body via getResponseBodyAsByteArray(): {} chars", extractedBody.length());
                    return extractedBody;
                } else {
                    log.debug("getResponseBodyAsByteArray() returned empty byte array");
                }
            } else {
                log.debug("getResponseBodyAsByteArray() returned null");
            }
        } catch (NoSuchMethodException e) {
            log.debug("Method getResponseBodyAsByteArray() not found");
        } catch (Exception e) {
            log.debug("Failed to extract via getResponseBodyAsByteArray(): {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
        }

        // If body is empty or null, use exception message as fallback
        String message = throwable.getMessage();
        if (message != null && !message.isEmpty()) {
            log.debug("Using exception message as response body: {}", message);
            return message;
        }

        // Return extracted body even if empty (to distinguish from null)
        log.debug("No response body could be extracted, returning: '{}'", extractedBody != null ? extractedBody : "");
        return extractedBody != null ? extractedBody : "";
    }

    /**
     * Extract headers from exception using reflection.
     * Tries multiple methods to support different Spring versions.
     *
     * @param throwable      the exception
     * @param exceptionClass the exception class
     * @return HTTP headers
     * @throws Exception if extraction fails
     */
    public static HttpHeaders extractHeaders(Throwable throwable, Class<?> exceptionClass) throws Exception {
        try {
            java.lang.reflect.Method getHeadersMethod = exceptionClass.getMethod("getResponseHeaders");
            getHeadersMethod.setAccessible(true);
            Object headers = getHeadersMethod.invoke(throwable);
            if (headers instanceof HttpHeaders) {
                return (HttpHeaders) headers;
            }
        } catch (Exception e) {
            log.debug("Failed to extract headers: {}", e.getMessage());
        }

        // Try getHeaders() as fallback (WebClientResponseException)
        try {
            java.lang.reflect.Method getHeadersMethod = exceptionClass.getMethod("getHeaders");
            getHeadersMethod.setAccessible(true);
            Object headers = getHeadersMethod.invoke(throwable);
            if (headers instanceof HttpHeaders) {
                return (HttpHeaders) headers;
            }
        } catch (Exception e) {
            log.debug("Failed to extract headers via getHeaders(): {}", e.getMessage());
        }

        return new HttpHeaders();
    }

    /**
     * Find method in class hierarchy (including superclasses).
     * Searches through the class hierarchy to find a method by name.
     *
     * @param clazz      the class to search
     * @param methodName the method name
     * @return the method, or null if not found
     */
    private static java.lang.reflect.Method findMethodInHierarchy(Class<?> clazz, String methodName) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            // Try public methods first
            try {
                java.lang.reflect.Method method = currentClass.getMethod(methodName);
                log.debug("Found method {} in class {}", methodName, currentClass.getName());
                return method;
            } catch (NoSuchMethodException e) {
                // Method not found in public methods, try declared methods
            }

            // Try declared methods (including private/protected)
            try {
                java.lang.reflect.Method method = currentClass.getDeclaredMethod(methodName);
                log.debug("Found declared method {} in class {}", methodName, currentClass.getName());
                return method;
            } catch (NoSuchMethodException e) {
                // Method not found in this class, continue to superclass
            }

            // Move to superclass
            currentClass = currentClass.getSuperclass();
        }

        log.debug("Method {} not found in class hierarchy of {}", methodName, clazz.getName());
        return null;
    }
}
