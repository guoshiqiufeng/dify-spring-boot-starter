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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.publisher;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.FluxSink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * OkHttp-based SSE streaming handler for Server-Sent Events (SSE).
 * This class handles SSE format: "data: {json}\n\n"
 *
 * @param <T> the type of items in the stream
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class OkHttpStreamPublisher<T> {

    private static final Logger log = LoggerFactory.getLogger(OkHttpStreamPublisher.class);

    private final OkHttpClient client;
    private final Request request;
    private final JsonMapper jsonMapper;
    private final Class<T> responseType;

    /**
     * Constructor.
     *
     * @param client       OkHttp client
     * @param request      HTTP request
     * @param jsonMapper   JSON mapper
     * @param responseType response item type
     */
    public OkHttpStreamPublisher(OkHttpClient client, Request request, JsonMapper jsonMapper, Class<T> responseType) {
        this.client = client;
        this.request = request;
        this.jsonMapper = jsonMapper;
        this.responseType = responseType;
    }

    /**
     * Start streaming and emit items to the FluxSink.
     *
     * @param sink the FluxSink to emit items to
     */
    public void stream(FluxSink<T> sink) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    handleError(response, sink);
                    return;
                }

                ResponseBody body = response.body();
                if (body == null) {
                    sink.error(new HttpClientException("Response body is null"));
                    return;
                }

                try {
                    processStream(body, sink);
                } catch (Exception e) {
                    sink.error(e);
                } finally {
                    response.close();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                log.error("【Dify】Stream request failed", e);
                sink.error(new HttpClientException("Stream request failed: " + e.getMessage(), e));
            }
        });
    }

    /**
     * Process SSE stream from response body.
     *
     * @param body response body
     * @param sink FluxSink to emit events
     * @throws IOException if reading stream fails
     */
    private void processStream(ResponseBody body, FluxSink<T> sink) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(body.byteStream(), StandardCharsets.UTF_8))) {

            String line;
            StringBuilder eventData = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                // SSE format: "data: {json}"
                if (line.startsWith("data: ")) {
                    String data = line.substring(6); // Remove "data: " prefix
                    eventData.append(data);

                    // Check if this is a complete event (some SSE events may span multiple lines)
                    if (isCompleteJson(data)) {
                        try {
                            T item = jsonMapper.fromJson(data, responseType);
                            sink.next(item);
                            eventData.setLength(0);
                        } catch (Exception e) {
                            log.warn("【Dify】Failed to parse SSE event: {}", data, e);
                            // Continue processing other events
                        }
                    }
                } else if (line.isEmpty() && eventData.length() > 0) {
                    // Empty line indicates end of event
                    try {
                        String data = eventData.toString();
                        T item = jsonMapper.fromJson(data, responseType);
                        sink.next(item);
                    } catch (Exception e) {
                        log.warn("【Dify】Failed to parse SSE event: {}", eventData, e);
                    }
                    eventData.setLength(0);
                } else if (line.startsWith(":")) {
                    // Comment line, ignore
                    continue;
                } else if (line.startsWith("event:") || line.startsWith("id:") || line.startsWith("retry:")) {
                    // SSE metadata, ignore for now
                    continue;
                }
            }

            // Stream completed successfully
            sink.complete();
        }
    }

    /**
     * Check if a string is complete JSON (simple heuristic).
     *
     * @param data JSON string
     * @return true if appears to be complete JSON
     */
    private boolean isCompleteJson(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        data = data.trim();

        // Check for complete JSON object
        if (data.startsWith("{") && data.endsWith("}")) {
            return true;
        }

        // Check for complete JSON array
        if (data.startsWith("[") && data.endsWith("]")) {
            return true;
        }

        // Check for JSON primitives
        if (data.startsWith("\"") && data.endsWith("\"")) {
            return true;
        }

        if ("true".equals(data) || "false".equals(data) || "null".equals(data)) {
            return true;
        }

        // Check for numbers
        try {
            Double.parseDouble(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Handle HTTP error response.
     *
     * @param response HTTP response
     * @param sink     FluxSink to emit error
     */
    private void handleError(Response response, FluxSink<T> sink) {
        try {
            int statusCode = response.code();
            String responseBody = response.body() != null ? response.body().string() : "";

            log.error("【Dify】Stream request failed: Status: {}, Body: {}", statusCode, responseBody);

            sink.error(new HttpClientException(statusCode, responseBody));
        } catch (IOException e) {
            sink.error(new HttpClientException("Failed to read error response", e));
        } finally {
            response.close();
        }
    }
}
