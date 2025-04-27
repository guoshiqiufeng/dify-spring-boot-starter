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
package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link Enabled}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class EnabledTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        Enabled enabled = new Enabled();
        Boolean isEnabled = true;

        // Act
        enabled.setEnabled(isEnabled);

        // Assert
        assertEquals(isEnabled, enabled.getEnabled());
    }

    @Test
    public void testNoArgsConstructor() {
        // Arrange & Act
        Enabled enabled = new Enabled();

        // Assert
        assertNull(enabled.getEnabled());
    }

    @Test
    public void testAllArgsConstructor() {
        // Arrange & Act
        Boolean isEnabled = true;
        Enabled enabled = new Enabled(isEnabled);

        // Assert
        assertEquals(isEnabled, enabled.getEnabled());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        Enabled enabled1 = new Enabled(true);
        Enabled enabled2 = new Enabled(true);
        Enabled enabled3 = new Enabled(false);

        // Assert
        assertEquals(enabled1, enabled2);
        assertEquals(enabled1.hashCode(), enabled2.hashCode());
        assertNotEquals(enabled1, enabled3);
        assertNotEquals(enabled1.hashCode(), enabled3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        Enabled enabled = new Enabled(true);

        // Act
        String toString = enabled.toString();

        // Assert
        assertTrue(toString.contains("enabled=true"));
    }
}
