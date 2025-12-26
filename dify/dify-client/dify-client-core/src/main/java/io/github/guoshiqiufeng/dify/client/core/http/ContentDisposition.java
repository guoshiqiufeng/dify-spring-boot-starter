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
package io.github.guoshiqiufeng.dify.client.core.http;

import java.io.Serializable;

/**
 * Represents the Content-Disposition HTTP header value.
 * This class provides a builder pattern for constructing Content-Disposition headers.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-06
 */
public class ContentDisposition implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String type;
    private final String filename;
    private final String name;

    private ContentDisposition(String type, String filename, String name) {
        this.type = type;
        this.filename = filename;
        this.name = name;
    }

    /**
     * Create a builder for an attachment Content-Disposition.
     *
     * @return a new Builder instance with type set to "attachment"
     */
    public static Builder attachment() {
        return new Builder("attachment");
    }

    /**
     * Create a builder for an inline Content-Disposition.
     *
     * @return a new Builder instance with type set to "inline"
     */
    public static Builder inline() {
        return new Builder("inline");
    }

    /**
     * Create a builder for a form-data Content-Disposition.
     *
     * @return a new Builder instance with type set to "form-data"
     */
    public static Builder formData() {
        return new Builder("form-data");
    }

    /**
     * Get the disposition type (e.g., "attachment", "inline", "form-data").
     *
     * @return the disposition type
     */
    public String getType() {
        return type;
    }

    /**
     * Get the filename parameter.
     *
     * @return the filename, or null if not set
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get the name parameter.
     *
     * @return the name, or null if not set
     */
    public String getName() {
        return name;
    }

    /**
     * Convert this ContentDisposition to its string representation for use in HTTP headers.
     *
     * @return the Content-Disposition header value
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(type);

        if (name != null) {
            sb.append("; name=\"").append(escapeQuotes(name)).append("\"");
        }

        if (filename != null) {
            sb.append("; filename=\"").append(escapeQuotes(filename)).append("\"");
        }

        return sb.toString();
    }

    /**
     * Escape quotes in a string for use in quoted-string values.
     *
     * @param value the value to escape
     * @return the escaped value
     */
    private String escapeQuotes(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Builder for constructing ContentDisposition instances.
     */
    public static class Builder {
        private final String type;
        private String filename;
        private String name;

        private Builder(String type) {
            this.type = type;
        }

        /**
         * Set the filename parameter.
         *
         * @param filename the filename
         * @return this builder
         */
        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        /**
         * Set the name parameter.
         *
         * @param name the name
         * @return this builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Build the ContentDisposition instance.
         *
         * @return a new ContentDisposition instance
         */
        public ContentDisposition build() {
            return new ContentDisposition(type, filename, name);
        }
    }
}
