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
package io.github.guoshiqiufeng.dify.client.core.web.util;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of UriBuilder.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-29
 */
public class DefaultUriBuilder implements UriBuilder {

    private String path;
    private final Map<String, String> queryParams = new LinkedHashMap<>();
    private Object[] uriVariables;

    @Override
    public UriBuilder path(String path) {
        this.path = path;
        return this;
    }

    @Override
    public UriBuilder queryParam(String name, Object value) {
        if (value != null) {
            queryParams.put(name, String.valueOf(value));
        }
        return this;
    }

    @Override
    public UriBuilder queryParamIfPresent(String name, Optional<?> value) {
        value.ifPresent(v -> queryParam(name, v));
        return this;
    }

    @Override
    public URI build() {
        return build(uriVariables != null ? uriVariables : new Object[0]);
    }

    @Override
    public URI build(Object... uriVariables) {
        // Store the variables for later use if build() is called without parameters
        if (uriVariables != null && uriVariables.length > 0) {
            this.uriVariables = uriVariables;
        }

        String processedPath = path;
        if (processedPath == null) {
            processedPath = "";
        }

        // Replace path variables if any using UriUtils
        Object[] varsToUse = (uriVariables != null && uriVariables.length > 0) ? uriVariables : this.uriVariables;
        processedPath = UriUtils.replacePlaceholders(processedPath, varsToUse);


        if (queryParams.isEmpty()) {
            return URI.create(processedPath);
        }

        StringBuilder uri = new StringBuilder(processedPath);
        uri.append("?");

        boolean first = true;
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            if (!first) {
                uri.append("&");
            }
            uri.append(UriUtils.encodeQueryParam(entry.getKey()))
                    .append("=")
                    .append(UriUtils.encodeQueryParam(entry.getValue()));
            first = false;
        }

        return URI.create(uri.toString());
    }
}
