/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.client.integration.spring.compat;

import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Backward compatibility adapters for Spring users.
 * Provides utilities to convert between framework-agnostic types and Spring types.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class SpringCompatibilityAdapters {

    /**
     * Convert HttpResponse to Spring ResponseEntity.
     *
     * @param httpResponse the HttpResponse to convert
     * @param <T>          the response body type
     * @return a ResponseEntity
     */
    public static <T> ResponseEntity<T> toResponseEntity(HttpResponse<T> httpResponse) {
        if (httpResponse == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, List<String>> entry : httpResponse.getHeaders().entrySet()) {
            headers.addAll(entry.getKey(), entry.getValue());
        }

        return ResponseEntity
                .status(HttpStatus.valueOf(httpResponse.getStatusCode()))
                .headers(headers)
                .body(httpResponse.getBody());
    }

    /**
     * Convert Spring ResponseEntity to HttpResponse.
     *
     * @param responseEntity the ResponseEntity to convert
     * @param <T>            the response body type
     * @return an HttpResponse
     */
    public static <T> HttpResponse<T> fromResponseEntity(ResponseEntity<T> responseEntity) {
        if (responseEntity == null) {
            return HttpResponse.<T>builder()
                    .statusCode(404)
                    .build();
        }

        HttpResponse.Builder<T> builder = HttpResponse.<T>builder()
                .statusCode(getStatusCodeValue(responseEntity))
                .body(responseEntity.getBody());

        // Convert headers
        HttpHeaders springHeaders = responseEntity.getHeaders();
        for (Map.Entry<String, List<String>> entry : springHeaders.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    /**
     * Get status code value from ResponseEntity in a Spring version-agnostic way.
     *
     * @param responseEntity the response entity
     * @return status code value
     */
    private static int getStatusCodeValue(ResponseEntity<?> responseEntity) {
        try {
            // Use reflection to avoid compile-time method signature binding
            java.lang.reflect.Method getStatusCodeMethod = ResponseEntity.class.getMethod("getStatusCode");
            Object statusCode = getStatusCodeMethod.invoke(responseEntity);
            java.lang.reflect.Method valueMethod = statusCode.getClass().getMethod("value");
            return (int) valueMethod.invoke(statusCode);
        } catch (Exception e) {
            // Fallback
            return responseEntity.getStatusCode().value();
        }
    }

    /**
     * Wrap a DifyChatClient to provide Spring-compatible return types.
     * This is useful for gradual migration.
     *
     * @param client the client to wrap
     * @return a Spring-compatible wrapper
     */
    public static SpringCompatibleChatClient wrapForSpring(io.github.guoshiqiufeng.dify.chat.client.DifyChatClient client) {
        return new SpringCompatibleChatClient(client);
    }

    /**
     * Spring-compatible wrapper for DifyChatClient.
     * Converts Publisher to Flux and HttpResponse to ResponseEntity.
     */
    public static class SpringCompatibleChatClient {
        private final io.github.guoshiqiufeng.dify.chat.client.DifyChatClient delegate;

        public SpringCompatibleChatClient(io.github.guoshiqiufeng.dify.chat.client.DifyChatClient delegate) {
            this.delegate = delegate;
        }

        /**
         * Get text to audio as ResponseEntity (Spring-compatible).
         *
         * @param request the request
         * @return ResponseEntity with audio data
         */
        public ResponseEntity<byte[]> textToAudioAsResponseEntity(
                io.github.guoshiqiufeng.dify.chat.dto.request.TextToAudioRequest request) {
            return toResponseEntity(delegate.textToAudio(request));
        }

        /**
         * Get file preview as ResponseEntity (Spring-compatible).
         *
         * @param request the request
         * @return ResponseEntity with file data
         */
        public ResponseEntity<byte[]> filePreviewAsResponseEntity(
                io.github.guoshiqiufeng.dify.chat.dto.request.FilePreviewRequest request) {
            return toResponseEntity(delegate.filePreview(request));
        }

        /**
         * Get the underlying delegate client.
         *
         * @return the delegate client
         */
        public io.github.guoshiqiufeng.dify.chat.client.DifyChatClient getDelegate() {
            return delegate;
        }
    }
}
