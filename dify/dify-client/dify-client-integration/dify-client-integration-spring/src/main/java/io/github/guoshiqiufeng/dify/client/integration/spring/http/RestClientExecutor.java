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
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Executor for Spring RestClient (Spring 6+).
 * Handles request execution using reflection to support RestClient API.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-31
 */
@Slf4j
class RestClientExecutor {

    private final Object restClient;
    private final JsonMapper jsonMapper;
    private final ResponseConverter responseConverter;
    private final Boolean skipNull;

    /**
     * Constructor.
     *
     * @param restClient REST client instance
     * @param jsonMapper JSON mapper
     */
    RestClientExecutor(Object restClient, JsonMapper jsonMapper) {
        this(restClient, jsonMapper, true);
    }

    RestClientExecutor(Object restClient, JsonMapper jsonMapper, Boolean skipNull) {
        this.restClient = restClient;
        this.jsonMapper = jsonMapper;
        this.skipNull = skipNull == null || skipNull;
        this.responseConverter = new ResponseConverter(jsonMapper);
    }

    /**
     * Execute request and return body.
     *
     * @param method       HTTP method
     * @param uri          request URI
     * @param headers      request headers
     * @param cookies      request cookies
     * @param body         request body
     * @param responseType response type
     * @param <T>          response type
     * @return response body
     */
    <T> T execute(String method, String uri, Map<String, String> headers,
                  Map<String, String> cookies, Object body, Class<T> responseType) {
        try {
            Object requestSpec = buildRequest(method, uri, headers, cookies, body);
            String responseBody = retrieveBody(requestSpec);
            return responseConverter.deserialize(responseBody, responseType);
        } catch (Exception e) {
            throw new HttpClientException("RestClient request failed: " + getExceptionMessage(e), unwrapException(e));
        }
    }

    /**
     * Execute request with TypeReference and return body.
     *
     * @param method        HTTP method
     * @param uri           request URI
     * @param headers       request headers
     * @param cookies       request cookies
     * @param body          request body
     * @param typeReference type reference
     * @param <T>           response type
     * @return response body
     */
    <T> T execute(String method, String uri, Map<String, String> headers,
                  Map<String, String> cookies, Object body, TypeReference<T> typeReference) {
        try {
            Object requestSpec = buildRequest(method, uri, headers, cookies, body);
            String responseBody = retrieveBody(requestSpec);
            return responseConverter.deserialize(responseBody, typeReference);
        } catch (Exception e) {
            throw new HttpClientException("RestClient request failed: " + getExceptionMessage(e), unwrapException(e));
        }
    }

    /**
     * Execute request and return full HttpResponse.
     *
     * @param method       HTTP method
     * @param uri          request URI
     * @param headers      request headers
     * @param cookies      request cookies
     * @param body         request body
     * @param responseType response type
     * @param <T>          response type
     * @return HttpResponse with status, headers, and body
     */
    <T> HttpResponse<T> executeForEntity(String method, String uri, Map<String, String> headers,
                                         Map<String, String> cookies, Object body, Class<T> responseType) {
        try {
            Object requestSpec = buildRequest(method, uri, headers, cookies, body);
            ResponseEntity<String> responseEntity = retrieveEntity(requestSpec);

            // For error responses (non-2xx), don't attempt deserialization
            // Return the raw error message as the body (cast to T)
            // Use reflection to get status code to avoid Spring version compatibility issues
            int statusCode = getStatusCodeValue(responseEntity);
            if (statusCode >= 200 && statusCode < 300) {
                // Success response - deserialize normally
                return responseConverter.convert(responseEntity, responseType);
            } else {
                // Error response - return with raw error message as body
                // The error handler will receive this and can process it
                @SuppressWarnings("unchecked")
                T errorBody = (T) responseEntity.getBody();
                return HttpResponse.<T>builder()
                        .statusCode(statusCode)
                        .headers(convertHeaders(responseEntity.getHeaders()))
                        .body(errorBody)
                        .build();
            }
        } catch (Exception e) {
            throw new HttpClientException("RestClient request failed: " + getExceptionMessage(e), unwrapException(e));
        }
    }

    /**
     * Execute request with TypeReference and return full HttpResponse.
     *
     * @param method        HTTP method
     * @param uri           request URI
     * @param headers       request headers
     * @param cookies       request cookies
     * @param body          request body
     * @param typeReference type reference
     * @param <T>           response type
     * @return HttpResponse with status, headers, and body
     */
    <T> HttpResponse<T> executeForEntity(String method, String uri, Map<String, String> headers,
                                         Map<String, String> cookies, Object body, TypeReference<T> typeReference) {
        try {
            Object requestSpec = buildRequest(method, uri, headers, cookies, body);
            ResponseEntity<String> responseEntity = retrieveEntity(requestSpec);

            // For error responses (non-2xx), don't attempt deserialization
            // Return the raw error message as the body (cast to T)
            // Use reflection to get status code to avoid Spring version compatibility issues
            int statusCode = getStatusCodeValue(responseEntity);
            if (statusCode >= 200 && statusCode < 300) {
                // Success response - deserialize normally
                return responseConverter.convert(responseEntity, typeReference);
            } else {
                // Error response - return with raw error message as body
                // The error handler will receive this and can process it
                @SuppressWarnings("unchecked")
                T errorBody = (T) responseEntity.getBody();
                return HttpResponse.<T>builder()
                        .statusCode(statusCode)
                        .headers(convertHeaders(responseEntity.getHeaders()))
                        .body(errorBody)
                        .build();
            }
        } catch (Exception e) {
            throw new HttpClientException("RestClient request failed: " + getExceptionMessage(e), unwrapException(e));
        }
    }

    /**
     * Convert Spring HttpHeaders to our HttpHeaders format.
     */
    private io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders convertHeaders(org.springframework.http.HttpHeaders springHeaders) {
        io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders headers = new io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders();
        springHeaders.forEach((key, values) -> {
            for (String value : values) {
                headers.add(key, value);
            }
        });
        return headers;
    }

    /**
     * Build RestClient request spec using reflection.
     *
     * @param method  HTTP method
     * @param uri     request URI
     * @param headers request headers
     * @param cookies request cookies
     * @param body    request body
     * @return request spec object
     * @throws Exception if reflection fails
     */
    private Object buildRequest(String method, String uri, Map<String, String> headers,
                                Map<String, String> cookies, Object body) throws Exception {
        // Use interface methods instead of concrete class to avoid module access issues
        java.lang.reflect.Method methodMethod = findMethod(restClient.getClass(), "method", HttpMethod.class);
        methodMethod.setAccessible(true);
        Object requestSpec = methodMethod.invoke(restClient, HttpMethod.valueOf(method));

        // Set URI
        if (uri != null) {
            java.lang.reflect.Method uriMethod = findMethod(requestSpec.getClass(), "uri", String.class, Object[].class);
            uriMethod.setAccessible(true);
            requestSpec = uriMethod.invoke(requestSpec, uri, new Object[0]);
        }

        // Set headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            java.lang.reflect.Method headerMethod = findMethod(requestSpec.getClass(), "header", String.class, String[].class);
            headerMethod.setAccessible(true);
            requestSpec = headerMethod.invoke(requestSpec, entry.getKey(), new String[]{entry.getValue()});
        }

        // Set cookies
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            java.lang.reflect.Method cookieMethod = findMethod(requestSpec.getClass(), "cookie", String.class, String.class);
            cookieMethod.setAccessible(true);
            requestSpec = cookieMethod.invoke(requestSpec, entry.getKey(), entry.getValue());
        }

        // Set body - handle multipart or JSON based on Content-Type header
        if (body != null) {
            // Check if Content-Type is multipart/form-data
            String contentType = headers.get("Content-Type");
            boolean isMultipart = contentType != null && contentType.toLowerCase().contains("multipart/form-data");

            if (isMultipart && body instanceof Map) {
                Map<?, ?> bodyMap = (Map<?, ?>) body;
                // Check if it's a multipart body by inspecting the first value
                if (!bodyMap.isEmpty()) {
                    Object firstValue = bodyMap.values().iterator().next();
                    if (firstValue instanceof io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part) {
                        // Handle multipart data
                        org.springframework.util.LinkedMultiValueMap<String, Object> multipartData =
                            new org.springframework.util.LinkedMultiValueMap<>();

                        @SuppressWarnings("unchecked")
                        Map<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part> parts =
                            (Map<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part>) bodyMap;

                        for (Map.Entry<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part> entry : parts.entrySet()) {
                            io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part part = entry.getValue();
                            Object partValue = part.getValue();

                            // Convert byte[] to Spring's ByteArrayResource for file uploads
                            if (partValue instanceof byte[]) {
                                byte[] bytes = (byte[]) partValue;
                                String filename = extractFilename(part.getHeader("Content-Disposition"));

                                org.springframework.core.io.ByteArrayResource resource =
                                    new org.springframework.core.io.ByteArrayResource(bytes) {
                                        @Override
                                        public String getFilename() {
                                            return filename;
                                        }
                                    };

                                multipartData.add(entry.getKey(), resource);
                            } else if (partValue instanceof String || partValue instanceof Number || partValue instanceof Boolean) {
                                // For simple types, add directly
                                multipartData.add(entry.getKey(), partValue);
                            } else {
                                // For complex objects, serialize to JSON
                                String jsonValue = skipNull ? jsonMapper.toJsonIgnoreNull(partValue) : jsonMapper.toJson(partValue);
                                multipartData.add(entry.getKey(), jsonValue);
                            }
                        }

                        // Set multipart body using reflection
                        Method bodyMethod = findMethod(requestSpec.getClass(), "body", Object.class);
                        bodyMethod.setAccessible(true);
                        requestSpec = bodyMethod.invoke(requestSpec, multipartData);
                        return requestSpec;
                    }
                }
            }

            // Default: serialize to JSON
            String jsonBody = skipNull ? jsonMapper.toJsonIgnoreNull(body) : jsonMapper.toJson(body);

            Method bodyMethod = findMethod(requestSpec.getClass(), "body", Object.class);
            bodyMethod.setAccessible(true);
            requestSpec = bodyMethod.invoke(requestSpec, jsonBody);
        }

        return requestSpec;
    }

    /**
     * Extract filename from Content-Disposition header.
     *
     * @param contentDisposition Content-Disposition header value
     * @return filename, or "file" if not found
     */
    private String extractFilename(String contentDisposition) {
        if (contentDisposition == null) {
            return "file";
        }

        // Parse: form-data; name="file"; filename="test.txt"
        int filenameIndex = contentDisposition.indexOf("filename=\"");
        if (filenameIndex != -1) {
            // length of "filename=\""
            int start = filenameIndex + 10;
            int end = contentDisposition.indexOf("\"", start);
            if (end != -1) {
                return contentDisposition.substring(start, end);
            }
        }

        return "file";
    }

    /**
     * Find method by name and parameter types, searching through class hierarchy.
     *
     * @param clazz          class to search
     * @param methodName     method name
     * @param parameterTypes parameter types
     * @return method
     * @throws NoSuchMethodException if method not found
     */
    private java.lang.reflect.Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            // Try declared methods
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ex) {
                // Search in superclass and interfaces
                if (clazz.getSuperclass() != null) {
                    try {
                        return findMethod(clazz.getSuperclass(), methodName, parameterTypes);
                    } catch (NoSuchMethodException ignored) {
                    }
                }
                for (Class<?> iface : clazz.getInterfaces()) {
                    try {
                        return findMethod(iface, methodName, parameterTypes);
                    } catch (NoSuchMethodException ignored) {
                    }
                }
                throw e;
            }
        }
    }

    /**
     * Retrieve response body as String.
     *
     * @param requestSpec request spec object
     * @return response body string
     * @throws Exception if reflection fails
     */
    private String retrieveBody(Object requestSpec) throws Exception {
        // Execute and retrieve response: requestSpec.retrieve().body(String.class)
        java.lang.reflect.Method retrieveMethod = findMethod(requestSpec.getClass(), "retrieve");
        retrieveMethod.setAccessible(true);
        Object responseSpec = retrieveMethod.invoke(requestSpec);

        java.lang.reflect.Method bodyMethod = findMethod(responseSpec.getClass(), "body", Class.class);
        bodyMethod.setAccessible(true);
        return (String) bodyMethod.invoke(responseSpec, String.class);
    }

    /**
     * Retrieve full ResponseEntity.
     * Handles HTTP error responses gracefully by catching exceptions and extracting response data.
     *
     * @param requestSpec request spec object
     * @return ResponseEntity with String body
     * @throws Exception if reflection fails
     */
    private ResponseEntity<String> retrieveEntity(Object requestSpec) throws Exception {
        // Execute and retrieve response entity: requestSpec.retrieve().toEntity(String.class)
        java.lang.reflect.Method retrieveMethod = findMethod(requestSpec.getClass(), "retrieve");
        retrieveMethod.setAccessible(true);
        Object responseSpec = retrieveMethod.invoke(requestSpec);

        java.lang.reflect.Method toEntityMethod = findMethod(responseSpec.getClass(), "toEntity", Class.class);
        toEntityMethod.setAccessible(true);

        try {
            Object result = toEntityMethod.invoke(responseSpec, String.class);

            if (!(result instanceof ResponseEntity)) {
                throw new HttpClientException("Unexpected return type: " + (result != null ? result.getClass() : "null"));
            }

            // Since we called toEntity(String.class), we can safely cast to ResponseEntity<String>
            ResponseEntity<?> rawEntity = (ResponseEntity<?>) result;
            // Use int status code to avoid Spring version compatibility issues
            return ResponseEntity.status(getStatusCodeValue(rawEntity))
                    .headers(rawEntity.getHeaders())
                    .body((String) rawEntity.getBody());
        } catch (java.lang.reflect.InvocationTargetException e) {
            // Handle HTTP error responses (4xx, 5xx) thrown by Spring RestClient
            Throwable cause = e.getCause();
            if (cause != null) {
                // Try to extract response information from Spring's RestClientResponseException
                ResponseEntity<String> errorResponse = extractErrorResponse(cause);
                if (errorResponse != null) {
                    return errorResponse;
                }
            }
            throw e;
        }
    }

    /**
     * Get status code value from ResponseEntity in a Spring version-agnostic way.
     * Uses reflection to avoid method signature binding at compile time.
     *
     * @param responseEntity the response entity
     * @return status code value
     */
    private int getStatusCodeValue(ResponseEntity<?> responseEntity) {
        try {
            // Call getStatusCode() using reflection to avoid compile-time method signature binding
            java.lang.reflect.Method getStatusCodeMethod = ResponseEntity.class.getMethod("getStatusCode");
            Object statusCode = getStatusCodeMethod.invoke(responseEntity);

            // Call value() on the result (works for both HttpStatus and HttpStatusCode)
            java.lang.reflect.Method valueMethod = statusCode.getClass().getMethod("value");
            return (int) valueMethod.invoke(statusCode);
        } catch (Exception e) {
            // Fallback: try direct call (this works if compiled with Spring 6+)
            return responseEntity.getStatusCode().value();
        }
    }

    /**
     * Unwrap InvocationTargetException to get the actual cause.
     *
     * @param e exception to unwrap
     * @return unwrapped exception
     */
    private Throwable unwrapException(Exception e) {
        if (e instanceof java.lang.reflect.InvocationTargetException) {
            Throwable cause = e.getCause();
            return cause != null ? cause : e;
        }
        return e;
    }

    /**
     * Get meaningful exception message, unwrapping InvocationTargetException if needed.
     *
     * @param e exception
     * @return exception message
     */
    private String getExceptionMessage(Exception e) {
        Throwable unwrapped = unwrapException(e);
        String message = unwrapped.getMessage();
        return message != null ? message : unwrapped.getClass().getSimpleName();
    }

    /**
     * Extract error response from Spring's RestClientResponseException.
     * Uses reflection exclusively to support different Spring versions (5, 6, 7).
     *
     * @param throwable the exception thrown by RestClient
     * @return ResponseEntity with error details, or null if extraction fails
     */
    private ResponseEntity<String> extractErrorResponse(Throwable throwable) {
        try {
            // Check if it's a Spring HTTP exception
            Class<?> exceptionClass = throwable.getClass();
            String className = exceptionClass.getName();

            log.debug("Extracting error response from exception: {}", className);

            // Handle Spring HTTP exceptions (works for Spring 5, 6, and 7)
            if (className.contains("RestClientResponseException") ||
                    className.contains("HttpClientErrorException") ||
                    className.contains("HttpServerErrorException") ||
                    className.contains("HttpStatusCodeException")) {

                // Extract status code using reflection only (to avoid Spring version compatibility issues)
                int statusCode = extractStatusCodeViaReflection(throwable, exceptionClass);
                log.debug("Extracted status code: {}", statusCode);

                // Extract response body using reflection only
                String responseBody = extractResponseBodyViaReflection(throwable, exceptionClass);
                log.debug("Extracted response body length: {}", responseBody != null ? responseBody.length() : 0);

                // Extract headers using reflection only
                org.springframework.http.HttpHeaders headers = extractHeadersViaReflection(throwable, exceptionClass);

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
     * Extract status code via reflection (fallback method).
     */
    private int extractStatusCodeViaReflection(Throwable throwable, Class<?> exceptionClass) throws Exception {
        log.debug("Attempting to extract status code via reflection from: {}", exceptionClass.getName());

        // Try getStatusCode().value()
        try {
            java.lang.reflect.Method getStatusCodeMethod = exceptionClass.getMethod("getStatusCode");
            getStatusCodeMethod.setAccessible(true);
            Object statusCode = getStatusCodeMethod.invoke(throwable);
            log.debug("getStatusCode() returned: {} (type: {})", statusCode, statusCode != null ? statusCode.getClass().getName() : "null");

            if (statusCode instanceof Integer) {
                return (Integer) statusCode;
            }
            // Try calling value() on the result
            java.lang.reflect.Method valueMethod = statusCode.getClass().getMethod("value");
            valueMethod.setAccessible(true);
            int value = (int) valueMethod.invoke(statusCode);
            log.debug("Extracted status code via value(): {}", value);
            return value;
        } catch (Exception e) {
            log.debug("Failed to extract via getStatusCode(): {}", e.getMessage());
        }

        // Try getRawStatusCode() (Spring 6+)
        try {
            java.lang.reflect.Method getRawStatusCodeMethod = exceptionClass.getMethod("getRawStatusCode");
            getRawStatusCodeMethod.setAccessible(true);
            int value = (int) getRawStatusCodeMethod.invoke(throwable);
            log.debug("Extracted status code via getRawStatusCode(): {}", value);
            return value;
        } catch (Exception e) {
            log.debug("Failed to extract via getRawStatusCode(): {}", e.getMessage());
        }

        throw new Exception("Unable to extract status code from exception: " + exceptionClass.getName());
    }

    /**
     * Extract response body via reflection (fallback method).
     */
    private String extractResponseBodyViaReflection(Throwable throwable, Class<?> exceptionClass) throws Exception {
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
        // This provides useful information like "401 Unauthorized: [no body]"
        String message = throwable.getMessage();
        if (message != null && !message.isEmpty()) {
            log.debug("Using exception message as response body: ", message);
            return message;
        }

        // Return extracted body even if empty (to distinguish from null)
        log.debug("No response body could be extracted, returning: '{}'", extractedBody != null ? extractedBody : "");
        return extractedBody != null ? extractedBody : "";
    }

    /**
     * Extract headers via reflection (fallback method).
     */
    private org.springframework.http.HttpHeaders extractHeadersViaReflection(Throwable throwable, Class<?> exceptionClass) throws Exception {
        try {
            java.lang.reflect.Method getHeadersMethod = exceptionClass.getMethod("getResponseHeaders");
            getHeadersMethod.setAccessible(true);
            Object headers = getHeadersMethod.invoke(throwable);
            if (headers instanceof org.springframework.http.HttpHeaders) {
                return (org.springframework.http.HttpHeaders) headers;
            }
        } catch (Exception e) {
            log.debug("Failed to extract headers: {}", e.getMessage());
        }
        return new org.springframework.http.HttpHeaders();
    }

    /**
     * Extract status code from exception using reflection.
     *
     * @param throwable      the exception
     * @param exceptionClass the exception class
     * @return status code
     * @throws Exception if extraction fails
     */
    private int extractStatusCode(Throwable throwable, Class<?> exceptionClass) throws Exception {
        log.debug("Attempting to extract status code from: {}", exceptionClass.getName());

        // Try multiple methods in order of preference
        // 1. Try getStatusCode().value() (works for both Spring 5 and 6)
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
                    // Continue to next method
                }
            } else {
                log.debug("getStatusCode() method not found");
            }
        } catch (Exception e) {
            log.debug("Failed to extract via getStatusCode(): {}", e.getMessage());
            // Continue to next method
        }

        // 2. Try getRawStatusCode() (Spring 6+)
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
            // Continue to next method
        }

        // 3. Try getStatusCodeValue() (older Spring versions)
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
            // All methods failed
        }

        throw new Exception("Unable to extract status code from exception: " + exceptionClass.getName());
    }

    /**
     * Find method in class hierarchy (including superclasses).
     *
     * @param clazz      the class to search
     * @param methodName the method name
     * @return the method, or null if not found
     */
    private java.lang.reflect.Method findMethodInHierarchy(Class<?> clazz, String methodName) {
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

    /**
     * Extract response body from exception using reflection.
     *
     * @param throwable      the exception
     * @param exceptionClass the exception class
     * @return response body as String
     * @throws Exception if extraction fails
     */
    private String extractResponseBody(Throwable throwable, Class<?> exceptionClass) throws Exception {
        // This method is now deprecated - use extractResponseBodyViaReflection instead
        return extractResponseBodyViaReflection(throwable, exceptionClass);
    }

    /**
     * Extract headers from exception using reflection.
     *
     * @param throwable      the exception
     * @param exceptionClass the exception class
     * @return HTTP headers
     * @throws Exception if extraction fails
     */
    private org.springframework.http.HttpHeaders extractHeaders(Throwable throwable, Class<?> exceptionClass) throws Exception {
        // This method is now deprecated - use extractHeadersViaReflection instead
        return extractHeadersViaReflection(throwable, exceptionClass);
    }
}
