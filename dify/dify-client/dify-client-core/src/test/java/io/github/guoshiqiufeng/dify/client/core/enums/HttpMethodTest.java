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
package io.github.guoshiqiufeng.dify.client.core.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HttpMethod enum
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class HttpMethodTest {

    @Test
    void testEnumValues() {
        HttpMethod[] methods = HttpMethod.values();
        assertEquals(7, methods.length);

        assertEquals(HttpMethod.GET, methods[0]);
        assertEquals(HttpMethod.POST, methods[1]);
        assertEquals(HttpMethod.PUT, methods[2]);
        assertEquals(HttpMethod.DELETE, methods[3]);
        assertEquals(HttpMethod.PATCH, methods[4]);
        assertEquals(HttpMethod.HEAD, methods[5]);
        assertEquals(HttpMethod.OPTIONS, methods[6]);
    }

    @Test
    void testValueOf() {
        assertEquals(HttpMethod.GET, HttpMethod.valueOf("GET"));
        assertEquals(HttpMethod.POST, HttpMethod.valueOf("POST"));
        assertEquals(HttpMethod.PUT, HttpMethod.valueOf("PUT"));
        assertEquals(HttpMethod.DELETE, HttpMethod.valueOf("DELETE"));
        assertEquals(HttpMethod.PATCH, HttpMethod.valueOf("PATCH"));
        assertEquals(HttpMethod.HEAD, HttpMethod.valueOf("HEAD"));
        assertEquals(HttpMethod.OPTIONS, HttpMethod.valueOf("OPTIONS"));
    }

    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> HttpMethod.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> HttpMethod.valueOf("get"));
        assertThrows(NullPointerException.class, () -> HttpMethod.valueOf(null));
    }

    @Test
    void testEnumName() {
        assertEquals("GET", HttpMethod.GET.name());
        assertEquals("POST", HttpMethod.POST.name());
        assertEquals("PUT", HttpMethod.PUT.name());
        assertEquals("DELETE", HttpMethod.DELETE.name());
        assertEquals("PATCH", HttpMethod.PATCH.name());
        assertEquals("HEAD", HttpMethod.HEAD.name());
        assertEquals("OPTIONS", HttpMethod.OPTIONS.name());
    }
}
