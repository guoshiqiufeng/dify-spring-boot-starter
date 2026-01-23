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
import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.SpringStatusCodeExtractor;

import java.nio.charset.StandardCharsets;
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
    <T> ResponseEntity<T> convert(org.springframework.http.ResponseEntity<String> responseEntity, Class<T> responseType) {
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
    <T> ResponseEntity<T> convert(org.springframework.http.ResponseEntity<String> responseEntity, TypeReference<T> typeReference) {
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

        if (responseType == Void.class || responseType == void.class) {
            return null;
        }

        if (responseType == byte[].class) {
            return responseType.cast(bodyString.getBytes(StandardCharsets.UTF_8));
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

        if (typeReference == null) {
            throw new HttpClientException("Failed to deserialize response: type reference is null");
        }

        if (typeReference.getType() instanceof Class<?>) {
            Class<?> targetType = (Class<?>) typeReference.getType();
            if (targetType == Void.class || targetType == void.class) {
                return null;
            }
            if (targetType == byte[].class) {
                @SuppressWarnings("unchecked")
                T result = (T) bodyString.getBytes(StandardCharsets.UTF_8);
                return result;
            }
            if (targetType == String.class) {
                @SuppressWarnings("unchecked")
                T result = (T) bodyString;
                return result;
            }
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
    private <T> ResponseEntity<T> buildHttpResponse(org.springframework.http.ResponseEntity<String> responseEntity, T responseBody) {
        Map<String, List<String>> headers = new HashMap<>();
        responseEntity.getHeaders().forEach((key, values) -> {
            headers.put(key, new ArrayList<>(values));
        });

        return new ResponseEntity<>(
                SpringStatusCodeExtractor.getStatusCodeValue(responseEntity),
                headers,
                responseBody
        );
    }
}
