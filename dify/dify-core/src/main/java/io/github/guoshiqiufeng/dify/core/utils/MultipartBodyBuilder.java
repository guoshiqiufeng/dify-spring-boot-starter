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
package io.github.guoshiqiufeng.dify.core.utils;

import lombok.Getter;

import java.util.*;

/**
 * Builder for multipart request bodies.
 * Inspired by Spring's MultipartBodyBuilder but independent of Spring Framework.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/31
 */
public class MultipartBodyBuilder {

    private final Map<String, Part> parts = new LinkedHashMap<>();

    /**
     * Adds a part to the multipart body.
     *
     * @param name  the name of the part
     * @param value the value of the part (String, byte[], or other Object)
     * @return a PartBuilder for further configuration
     */
    public PartBuilder part(String name, Object value) {
        Part part = new Part(name, value);
        parts.put(name, part);
        return new PartBuilder(part);
    }

    /**
     * Builds and returns the map of parts.
     *
     * @return an unmodifiable map of all parts
     */
    public Map<String, Part> build() {
        return Collections.unmodifiableMap(parts);
    }

    /**
     * Returns all parts in this builder.
     *
     * @return map of parts
     */
    public Map<String, Part> getParts() {
        return Collections.unmodifiableMap(parts);
    }

    /**
     * Builder for configuring individual parts.
     */
    public static class PartBuilder {
        private final Part part;

        private PartBuilder(Part part) {
            this.part = part;
        }

        /**
         * Adds a header to this part.
         *
         * @param headerName  the header name
         * @param headerValue the header value
         * @return this PartBuilder for method chaining
         */
        public PartBuilder header(String headerName, String headerValue) {
            part.addHeader(headerName, headerValue);
            return this;
        }

        /**
         * Adds multiple header values to this part.
         *
         * @param headerName   the header name
         * @param headerValues the header values
         * @return this PartBuilder for method chaining
         */
        public PartBuilder headers(String headerName, String... headerValues) {
            for (String value : headerValues) {
                part.addHeader(headerName, value);
            }
            return this;
        }

        /**
         * Gets the part being built.
         *
         * @return the Part instance
         */
        public Part build() {
            return part;
        }
    }

    /**
     * Represents a single part in a multipart request.
     */
    public static class Part {
        /**
         * -- GETTER --
         * Gets the name of this part.
         *
         * @return the part name
         */
        @Getter
        private final String name;
        /**
         * -- GETTER --
         * Gets the value of this part.
         *
         * @return the part value
         */
        @Getter
        private final Object value;
        private final Map<String, List<String>> headers = new LinkedHashMap<>();

        private Part(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        /**
         * Adds a header to this part.
         *
         * @param headerName  the header name
         * @param headerValue the header value
         */
        public void addHeader(String headerName, String headerValue) {
            headers.computeIfAbsent(headerName, k -> new ArrayList<>()).add(headerValue);
        }

        /**
         * Gets all headers for this part.
         *
         * @return unmodifiable map of headers
         */
        public Map<String, List<String>> getHeaders() {
            return Collections.unmodifiableMap(headers);
        }

        /**
         * Gets the first value for a specific header.
         *
         * @param headerName the header name
         * @return the first header value, or null if not found
         */
        public String getHeader(String headerName) {
            List<String> values = headers.get(headerName);
            return (values != null && !values.isEmpty()) ? values.get(0) : null;
        }

        /**
         * Gets all values for a specific header.
         *
         * @param headerName the header name
         * @return list of header values, or empty list if not found
         */
        public List<String> getHeaderValues(String headerName) {
            return headers.getOrDefault(headerName, Collections.emptyList());
        }
    }
}
