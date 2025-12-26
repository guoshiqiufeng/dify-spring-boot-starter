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
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for converting Spring ResponseEntity to HttpResponse.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-31
 */
class ResponseConverter {

    private final JsonMapper jsonMapper;

    /**
     * Constructor.
     *
     * @param jsonMapper JSON mapper for deserialization
     */
    ResponseConverter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    /**
     * Convert Spring ResponseEntity to HttpResponse with Class type.
     *
     * @param responseEntity Spring ResponseEntity with String body
     * @param responseType   target response type
     * @param <T>            response type
     * @return HttpResponse with deserialized body
     */
    <T> HttpResponse<T> convert(ResponseEntity<String> responseEntity, Class<T> responseType) {
        T responseBody = deserialize(responseEntity.getBody(), responseType);
        return buildHttpResponse(responseEntity, responseBody);
    }

    /**
     * Convert Spring ResponseEntity to HttpResponse with TypeReference.
     *
     * @param responseEntity Spring ResponseEntity with String body
     * @param typeReference  type reference for complex types
     * @param <T>            response type
     * @return HttpResponse with deserialized body
     */
    <T> HttpResponse<T> convert(ResponseEntity<String> responseEntity, TypeReference<T> typeReference) {
        T responseBody = deserialize(responseEntity.getBody(), typeReference);
        return buildHttpResponse(responseEntity, responseBody);
    }

    /**
     * Deserialize response body string to target type.
     *
     * @param bodyString   response body as string
     * @param responseType target type
     * @param <T>          response type
     * @return deserialized object
     */
    <T> T deserialize(String bodyString, Class<T> responseType) {
        if (bodyString == null || bodyString.isEmpty()) {
            return null;
        }

        if (responseType == String.class) {
            return responseType.cast(bodyString);
        }

        try {
            return jsonMapper.fromJson(bodyString, responseType);
        } catch (Exception e) {
            throw new HttpClientException("Failed to deserialize response", e);
        }
    }

    /**
     * Deserialize response body string with TypeReference.
     *
     * @param bodyString    response body as string
     * @param typeReference type reference
     * @param <T>           response type
     * @return deserialized object
     */
    <T> T deserialize(String bodyString, TypeReference<T> typeReference) {
        if (bodyString == null || bodyString.isEmpty()) {
            return null;
        }

        try {
            return jsonMapper.fromJson(bodyString, typeReference);
        } catch (Exception e) {
            throw new HttpClientException("Failed to deserialize response", e);
        }
    }

    /**
     * Build HttpResponse from Spring ResponseEntity.
     *
     * @param responseEntity Spring ResponseEntity
     * @param responseBody   deserialized response body
     * @param <T>            response type
     * @return HttpResponse
     */
    private <T> HttpResponse<T> buildHttpResponse(ResponseEntity<String> responseEntity, T responseBody) {
        Map<String, List<String>> headers = new HashMap<>();
        responseEntity.getHeaders().forEach((key, values) -> {
            headers.put(key, new ArrayList<>(values));
        });

        return new HttpResponse<>(
                getStatusCodeValue(responseEntity),
                headers,
                responseBody
        );
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
}
