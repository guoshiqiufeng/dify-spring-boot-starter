/*
 * Copyright (c) 2025-2026, fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.http.util;

import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import lombok.experimental.UtilityClass;
import okhttp3.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for processing OkHttp responses.
 * Provides methods for extracting headers and building HttpResponse objects.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@UtilityClass
public class OkHttpResponseProcessor {

    /**
     * Build HttpResponse wrapper from OkHttp Response.
     * Extracts status code, headers, and body into a unified HttpResponse object.
     *
     * @param response     OkHttp response
     * @param responseBody deserialized response body
     * @param <T>          response body type
     * @return HttpResponse with status, headers, and body
     */
    public static <T> HttpResponse<T> buildHttpResponse(Response response, T responseBody) {
        return HttpResponse.<T>builder()
                .statusCode(response.code())
                .headers(extractHeaders(response))
                .body(responseBody)
                .build();
    }

    /**
     * Extract headers from OkHttp Response.
     * Converts OkHttp Headers to a Map<String, List<String>> format.
     *
     * @param response OkHttp response
     * @return map of header names to list of values
     */
    public static Map<String, List<String>> extractHeaders(Response response) {
        Map<String, List<String>> headers = new HashMap<>(response.headers().names().size());
        for (String name : response.headers().names()) {
            headers.put(name, response.headers().values(name));
        }
        return headers;
    }
}
