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
import io.github.guoshiqiufeng.dify.client.core.codec.util.JsonSerializationHelper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.http.util.HttpStatusValidator;
import io.github.guoshiqiufeng.dify.client.core.http.util.MultipartBodyProcessor;
import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.HttpHeaderConverter;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.SpringErrorResponseExtractor;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.SpringMultipartBodyBuilder;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.SpringRequestParameterApplier;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.SpringStatusCodeExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Method;
import java.net.URI;
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
    <T> T execute(String method, URI uri, Map<String, String> headers,
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
    <T> T execute(String method, URI uri, Map<String, String> headers,
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
    <T> ResponseEntity<T> executeForEntity(String method, URI uri, Map<String, String> headers,
                                           Map<String, String> cookies, Object body, Class<T> responseType) {
        try {
            Object requestSpec = buildRequest(method, uri, headers, cookies, body);
            org.springframework.http.ResponseEntity<String> responseEntity = retrieveEntity(requestSpec);

            // For error responses (non-2xx), don't attempt deserialization
            // Return the raw error message as the body (cast to T)
            // Use reflection to get status code to avoid Spring version compatibility issues
            int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(responseEntity);
            if (HttpStatusValidator.isSuccessful(statusCode)) {
                // Success response - deserialize normally
                return responseConverter.convert(responseEntity, responseType);
            } else {
                // Error response - return with raw error message as body
                // The error handler will receive this and can process it
                @SuppressWarnings("unchecked")
                T errorBody = (T) responseEntity.getBody();
                return ResponseEntity.<T>builder()
                        .statusCode(statusCode)
                        .headers(HttpHeaderConverter.fromSpringHeaders(responseEntity.getHeaders()))
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
    <T> ResponseEntity<T> executeForEntity(String method, URI uri, Map<String, String> headers,
                                           Map<String, String> cookies, Object body, TypeReference<T> typeReference) {
        try {
            Object requestSpec = buildRequest(method, uri, headers, cookies, body);
            org.springframework.http.ResponseEntity<String> responseEntity = retrieveEntity(requestSpec);

            // For error responses (non-2xx), don't attempt deserialization
            // Return the raw error message as the body (cast to T)
            // Use reflection to get status code to avoid Spring version compatibility issues
            int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(responseEntity);
            if (HttpStatusValidator.isSuccessful(statusCode)) {
                // Success response - deserialize normally
                return responseConverter.convert(responseEntity, typeReference);
            } else {
                // Error response - return with raw error message as body
                // The error handler will receive this and can process it
                @SuppressWarnings("unchecked")
                T errorBody = (T) responseEntity.getBody();
                return ResponseEntity.<T>builder()
                        .statusCode(statusCode)
                        .headers(HttpHeaderConverter.fromSpringHeaders(responseEntity.getHeaders()))
                        .body(errorBody)
                        .build();
            }
        } catch (Exception e) {
            throw new HttpClientException("RestClient request failed: " + getExceptionMessage(e), unwrapException(e));
        }
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
    private Object buildRequest(String method, URI uri, Map<String, String> headers,
                                Map<String, String> cookies, Object body) throws Exception {
        // Use interface methods instead of concrete class to avoid module access issues
        java.lang.reflect.Method methodMethod = findMethod(restClient.getClass(), "method", HttpMethod.class);
        methodMethod.setAccessible(true);
        Object requestSpec = methodMethod.invoke(restClient, HttpMethod.valueOf(method));

        // Set URI
        if (uri != null) {
            java.lang.reflect.Method uriMethod = findMethod(requestSpec.getClass(), "uri", URI.class);
            uriMethod.setAccessible(true);
            requestSpec = uriMethod.invoke(requestSpec, uri);
        }

        // Set headers and cookies
        requestSpec = SpringRequestParameterApplier.applyHeadersReflection(requestSpec, headers);
        requestSpec = SpringRequestParameterApplier.applyCookiesReflection(requestSpec, cookies);

        // Set body - handle multipart or JSON based on Content-Type header
        if (body != null) {
            // Check if Content-Type is multipart/form-data
            String contentType = headers.get("Content-Type");
            boolean isMultipart = MultipartBodyProcessor.isMultipartRequest(contentType);

            if (isMultipart && body instanceof Map) {
                Map<?, ?> bodyMap = (Map<?, ?>) body;
                // Check if it's a multipart body by inspecting the first value
                if (MultipartBodyProcessor.isMultipartBodyMap(bodyMap)) {
                    // Handle multipart data
                    @SuppressWarnings("unchecked")
                    Map<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part> parts =
                        (Map<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part>) bodyMap;

                    org.springframework.util.LinkedMultiValueMap<String, Object> multipartData =
                        SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, skipNull);

                    // Set multipart body using reflection
                    Method bodyMethod = findMethod(requestSpec.getClass(), "body", Object.class);
                    bodyMethod.setAccessible(true);
                    requestSpec = bodyMethod.invoke(requestSpec, multipartData);
                    return requestSpec;
                }
            }

            // Default: serialize to JSON
            String jsonBody = JsonSerializationHelper.serialize(body, jsonMapper, skipNull);

            Method bodyMethod = findMethod(requestSpec.getClass(), "body", Object.class);
            bodyMethod.setAccessible(true);
            requestSpec = bodyMethod.invoke(requestSpec, jsonBody);
        }

        return requestSpec;
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
    private org.springframework.http.ResponseEntity<String> retrieveEntity(Object requestSpec) throws Exception {
        // Execute and retrieve response entity: requestSpec.retrieve().toEntity(String.class)
        java.lang.reflect.Method retrieveMethod = findMethod(requestSpec.getClass(), "retrieve");
        retrieveMethod.setAccessible(true);
        Object responseSpec = retrieveMethod.invoke(requestSpec);

        java.lang.reflect.Method toEntityMethod = findMethod(responseSpec.getClass(), "toEntity", Class.class);
        toEntityMethod.setAccessible(true);

        try {
            Object result = toEntityMethod.invoke(responseSpec, String.class);

            if (!(result instanceof org.springframework.http.ResponseEntity)) {
                throw new HttpClientException("Unexpected return type: " + (result != null ? result.getClass() : "null"));
            }

            // Since we called toEntity(String.class), we can safely cast to ResponseEntity<String>
            org.springframework.http.ResponseEntity<?> rawEntity = (org.springframework.http.ResponseEntity<?>) result;
            // Use int status code to avoid Spring version compatibility issues
            return org.springframework.http.ResponseEntity.status(SpringStatusCodeExtractor.getStatusCodeValue(rawEntity))
                    .headers(rawEntity.getHeaders())
                    .body((String) rawEntity.getBody());
        } catch (java.lang.reflect.InvocationTargetException e) {
            // Handle HTTP error responses (4xx, 5xx) thrown by Spring RestClient
            Throwable cause = e.getCause();
            if (cause != null) {
                // Try to extract response information from Spring's RestClientResponseException
                org.springframework.http.ResponseEntity<String> errorResponse = extractErrorResponse(cause);
                if (errorResponse != null) {
                    return errorResponse;
                }
            }
            throw e;
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
    private org.springframework.http.ResponseEntity<String> extractErrorResponse(Throwable throwable) {
        return SpringErrorResponseExtractor.extractErrorResponse(throwable);
    }

}
