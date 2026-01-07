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
package io.github.guoshiqiufeng.dify.client.integration.spring.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring version detection utility.
 * Detects which version of Spring is available at runtime.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class SpringVersionDetector {

    private static final Logger log = LoggerFactory.getLogger(SpringVersionDetector.class);

    private static final SpringVersion DETECTED_VERSION;

    static {
        DETECTED_VERSION = detectVersion();
        // log.debug("【Dify】Detected Spring version: {}", DETECTED_VERSION);
    }

    /**
     * Get the detected Spring version.
     *
     * @return the Spring version
     */
    public static SpringVersion getVersion() {
        return DETECTED_VERSION;
    }

    /**
     * Detect Spring version by checking for RestClient class.
     * RestClient was introduced in Spring 6.1.
     *
     * @return the detected Spring version
     */
    private static SpringVersion detectVersion() {
        try {
            // Try to load RestClient class (available in Spring 6.1+)
            Class.forName("org.springframework.web.client.RestClient");
            return SpringVersion.SPRING_6_OR_LATER;
        } catch (ClassNotFoundException e) {
            // RestClient not found, must be Spring 5
            return SpringVersion.SPRING_5;
        }
    }

    /**
     * Check if RestClient is available.
     *
     * @return true if RestClient is available
     */
    public static boolean hasRestClient() {
        return DETECTED_VERSION == SpringVersion.SPRING_6_OR_LATER;
    }

    /**
     * Check if this is Spring 5.
     *
     * @return true if Spring 5
     */
    public static boolean isSpring5() {
        return DETECTED_VERSION == SpringVersion.SPRING_5;
    }
}
