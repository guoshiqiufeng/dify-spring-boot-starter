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
 * Unit tests for SpringVersion enum
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class SpringVersionTest {

    @Test
    void testEnumValues() {
        SpringVersion[] versions = SpringVersion.values();
        assertEquals(2, versions.length);
        assertEquals(SpringVersion.SPRING_5, versions[0]);
        assertEquals(SpringVersion.SPRING_6_OR_LATER, versions[1]);
    }

    @Test
    void testValueOf() {
        assertEquals(SpringVersion.SPRING_5, SpringVersion.valueOf("SPRING_5"));
        assertEquals(SpringVersion.SPRING_6_OR_LATER, SpringVersion.valueOf("SPRING_6_OR_LATER"));
    }

    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> SpringVersion.valueOf("SPRING_4"));
        assertThrows(IllegalArgumentException.class, () -> SpringVersion.valueOf("spring_5"));
        assertThrows(NullPointerException.class, () -> SpringVersion.valueOf(null));
    }

    @Test
    void testEnumName() {
        assertEquals("SPRING_5", SpringVersion.SPRING_5.name());
        assertEquals("SPRING_6_OR_LATER", SpringVersion.SPRING_6_OR_LATER.name());
    }

    @Test
    void testEnumEquality() {
        SpringVersion version1 = SpringVersion.SPRING_5;
        SpringVersion version2 = SpringVersion.SPRING_5;
        assertSame(version1, version2);

        SpringVersion version3 = SpringVersion.SPRING_6_OR_LATER;
        assertNotSame(version1, version3);
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, SpringVersion.SPRING_5.ordinal());
        assertEquals(1, SpringVersion.SPRING_6_OR_LATER.ordinal());
    }
}
