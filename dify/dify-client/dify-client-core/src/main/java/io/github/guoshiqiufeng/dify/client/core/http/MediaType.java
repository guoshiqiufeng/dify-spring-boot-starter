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

/**
 * Common media type constants for HTTP requests and responses.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-30
 */
public interface MediaType {

    /**
     * JSON media type: application/json
     */
    String APPLICATION_JSON = "application/json";

    /**
     * JSON media type with UTF-8 charset: application/json;charset=UTF-8
     */
    String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";

    /**
     * XML media type: application/xml
     */
    String APPLICATION_XML = "application/xml";

    /**
     * Form URL encoded media type: application/x-www-form-urlencoded
     */
    String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

    /**
     * Multipart form data media type: multipart/form-data
     */
    String MULTIPART_FORM_DATA = "multipart/form-data";

    /**
     * Plain text media type: text/plain
     */
    String TEXT_PLAIN = "text/plain";

    /**
     * HTML media type: text/html
     */
    String TEXT_HTML = "text/html";

    /**
     * Event stream media type for Server-Sent Events: text/event-stream
     */
    String TEXT_EVENT_STREAM = "text/event-stream";

    /**
     * Octet stream media type: application/octet-stream
     */
    String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /**
     * PNG image media type: image/png
     */
    String IMAGE_PNG = "image/png";
}
