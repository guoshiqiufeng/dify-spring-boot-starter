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
package io.github.guoshiqiufeng.dify.core.logging.masking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MaskingContext
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
class MaskingContextTest {

    @Test
    void testConstructorWithDefaultConfig() {
        // Arrange
        MaskingConfig config = MaskingConfig.createDefault();

        // Act
        MaskingContext context = new MaskingContext(config);

        // Assert
        assertNotNull(context);
        assertNotNull(context.getRegistry());
        assertTrue(context.isEnabled());
        assertEquals(1000, context.getMaxBodyLength());
    }

    @Test
    void testConstructorWithCustomConfig() {
        // Arrange
        MaskingRuleRegistry customRegistry = MaskingRuleRegistry.builder()
                .addDefaultRules()
                .build();

        MaskingConfig config = MaskingConfig.builder()
                .enabled(false)
                .maxBodyLength(500)
                .registry(customRegistry)
                .build();

        // Act
        MaskingContext context = new MaskingContext(config);

        // Assert
        assertNotNull(context);
        assertFalse(context.isEnabled());
        assertEquals(500, context.getMaxBodyLength());
        assertSame(customRegistry, context.getRegistry());
    }

    @Test
    void testGetConfig() {
        // Arrange
        MaskingConfig config = MaskingConfig.createDefault();
        MaskingContext context = new MaskingContext(config);

        // Act
        MaskingConfig retrievedConfig = context.getConfig();

        // Assert
        assertNotNull(retrievedConfig);
        assertSame(config, retrievedConfig);
    }

    @Test
    void testGetRegistry() {
        // Arrange
        MaskingConfig config = MaskingConfig.createDefault();
        MaskingContext context = new MaskingContext(config);

        // Act
        MaskingRuleRegistry registry = context.getRegistry();

        // Assert
        assertNotNull(registry);
        assertSame(config.getRegistry(), registry);
    }

    @Test
    void testGetMaxBodyLength() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .maxBodyLength(2000)
                .build();
        MaskingContext context = new MaskingContext(config);

        // Act
        int maxBodyLength = context.getMaxBodyLength();

        // Assert
        assertEquals(2000, maxBodyLength);
    }

    @Test
    void testIsEnabled() {
        // Arrange - enabled context
        MaskingConfig enabledConfig = MaskingConfig.builder()
                .enabled(true)
                .build();
        MaskingContext enabledContext = new MaskingContext(enabledConfig);

        // Act & Assert
        assertTrue(enabledContext.isEnabled());

        // Arrange - disabled context
        MaskingConfig disabledConfig = MaskingConfig.builder()
                .enabled(false)
                .build();
        MaskingContext disabledContext = new MaskingContext(disabledConfig);

        // Act & Assert
        assertFalse(disabledContext.isEnabled());
    }

    @Test
    void testContextWithMinimalConfig() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(0)
                .build();

        // Act
        MaskingContext context = new MaskingContext(config);

        // Assert
        assertNotNull(context);
        assertTrue(context.isEnabled());
        assertEquals(0, context.getMaxBodyLength());
        assertNotNull(context.getRegistry());
    }

    @Test
    void testContextWithLargeMaxBodyLength() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .maxBodyLength(Integer.MAX_VALUE)
                .build();

        // Act
        MaskingContext context = new MaskingContext(config);

        // Assert
        assertEquals(Integer.MAX_VALUE, context.getMaxBodyLength());
    }

    @Test
    void testMultipleContextsWithSameConfig() {
        // Arrange
        MaskingConfig config = MaskingConfig.createDefault();

        // Act
        MaskingContext context1 = new MaskingContext(config);
        MaskingContext context2 = new MaskingContext(config);

        // Assert
        assertNotSame(context1, context2);
        assertSame(context1.getConfig(), context2.getConfig());
        assertSame(context1.getRegistry(), context2.getRegistry());
    }
}
