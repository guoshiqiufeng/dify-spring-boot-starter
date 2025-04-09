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
package io.github.guoshiqiufeng.dify.core.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseModeEnumTest {

    @Test
    void testEnumValues() {
        // Test that the enum has the expected values
        assertEquals(2, ResponseModeEnum.values().length);

        assertTrue(containsEnumValue(ResponseModeEnum.values(), "streaming"));
        assertTrue(containsEnumValue(ResponseModeEnum.values(), "blocking"));
    }

    @Test
    void testStreamingValue() {
        // Test the streaming value
        assertEquals("streaming", ResponseModeEnum.streaming.name());

        // Access the enum directly
        ResponseModeEnum mode = ResponseModeEnum.streaming;
        assertEquals("streaming", mode.name());
        assertEquals(0, mode.ordinal());
    }

    @Test
    void testBlockingValue() {
        // Test the blocking value
        assertEquals("blocking", ResponseModeEnum.blocking.name());

        // Access the enum directly
        ResponseModeEnum mode = ResponseModeEnum.blocking;
        assertEquals("blocking", mode.name());
        assertEquals(1, mode.ordinal());
    }

    @Test
    void testValueOf() {
        // Test valueOf method
        ResponseModeEnum streamingMode = ResponseModeEnum.valueOf("streaming");
        assertEquals(ResponseModeEnum.streaming, streamingMode);

        ResponseModeEnum blockingMode = ResponseModeEnum.valueOf("blocking");
        assertEquals(ResponseModeEnum.blocking, blockingMode);

        // Verify that invalid enum values throw appropriate exception
        assertThrows(IllegalArgumentException.class, () -> {
            ResponseModeEnum.valueOf("invalid_mode");
        });
    }

    // Helper method to check if an enum array contains a value with the given name
    private boolean containsEnumValue(ResponseModeEnum[] enumValues, String name) {
        for (ResponseModeEnum value : enumValues) {
            if (value.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
