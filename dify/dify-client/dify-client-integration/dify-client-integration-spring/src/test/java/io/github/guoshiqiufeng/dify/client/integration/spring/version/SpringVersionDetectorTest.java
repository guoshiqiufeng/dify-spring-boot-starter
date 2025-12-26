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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SpringVersionDetector
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class SpringVersionDetectorTest {

    @Test
    void testGetVersion() {
        SpringVersion version = SpringVersionDetector.getVersion();
        assertNotNull(version);
        // Version should be either SPRING_5 or SPRING_6_OR_LATER
        assertTrue(version == SpringVersion.SPRING_5 || version == SpringVersion.SPRING_6_OR_LATER);
    }

    @Test
    void testHasRestClient() {
        boolean hasRestClient = SpringVersionDetector.hasRestClient();
        SpringVersion version = SpringVersionDetector.getVersion();

        if (version == SpringVersion.SPRING_6_OR_LATER) {
            assertTrue(hasRestClient);
        } else {
            assertFalse(hasRestClient);
        }
    }

    @Test
    void testIsSpring5() {
        boolean isSpring5 = SpringVersionDetector.isSpring5();
        SpringVersion version = SpringVersionDetector.getVersion();

        if (version == SpringVersion.SPRING_5) {
            assertTrue(isSpring5);
        } else {
            assertFalse(isSpring5);
        }
    }

    @Test
    void testVersionConsistency() {
        // Multiple calls should return the same version
        SpringVersion version1 = SpringVersionDetector.getVersion();
        SpringVersion version2 = SpringVersionDetector.getVersion();
        assertSame(version1, version2);
    }

    @Test
    void testHasRestClientConsistency() {
        // Multiple calls should return the same result
        boolean result1 = SpringVersionDetector.hasRestClient();
        boolean result2 = SpringVersionDetector.hasRestClient();
        assertEquals(result1, result2);
    }

    @Test
    void testIsSpring5Consistency() {
        // Multiple calls should return the same result
        boolean result1 = SpringVersionDetector.isSpring5();
        boolean result2 = SpringVersionDetector.isSpring5();
        assertEquals(result1, result2);
    }

    @Test
    void testVersionAndHelperMethodsAlignment() {
        SpringVersion version = SpringVersionDetector.getVersion();
        boolean hasRestClient = SpringVersionDetector.hasRestClient();
        boolean isSpring5 = SpringVersionDetector.isSpring5();

        // Verify alignment between version and helper methods
        if (version == SpringVersion.SPRING_5) {
            assertTrue(isSpring5);
            assertFalse(hasRestClient);
        } else if (version == SpringVersion.SPRING_6_OR_LATER) {
            assertFalse(isSpring5);
            assertTrue(hasRestClient);
        }
    }

    @Test
    void testMutualExclusivity() {
        // isSpring5() and hasRestClient() should be mutually exclusive
        boolean isSpring5 = SpringVersionDetector.isSpring5();
        boolean hasRestClient = SpringVersionDetector.hasRestClient();
        assertNotEquals(isSpring5, hasRestClient);
    }
}
