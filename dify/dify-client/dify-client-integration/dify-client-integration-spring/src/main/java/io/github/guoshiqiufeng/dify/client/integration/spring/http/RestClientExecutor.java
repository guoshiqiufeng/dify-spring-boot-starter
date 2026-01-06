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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Executor for Spring RestClient (Spring 6+).
 * Handles request execution using reflection to support RestClient API.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-31
 */
class RestClientExecutor {

    private final Object restClient;
    private final JsonMapper jsonMapper;
    private final ResponseConverter responseConverter;

    /**
     * Constructor.
     *
     * @param restClient REST client instance
     * @param jsonMapper JSON mapper
     */
    RestClientExecutor(Object restClient, JsonMapper jsonMapper) {
        this.restClient = restClient;
        this.jsonMapper = jsonMapper;
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
            return responseConverter.convert(responseEntity, responseType);
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
            return responseConverter.convert(responseEntity, typeReference);
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

        // Set body - serialize to JSON string using JsonMapper
        if (body != null) {
            String jsonBody = jsonMapper.toJson(body);
            java.lang.reflect.Method bodyMethod = findMethod(requestSpec.getClass(), "body", Object.class);
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
}
