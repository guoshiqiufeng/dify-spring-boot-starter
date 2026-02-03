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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MaskingRuleRegistry
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
class MaskingRuleRegistryTest {

    @Test
    void testCreateDefault() {
        // Act
        MaskingRuleRegistry registry = MaskingRuleRegistry.createDefault();

        // Assert
        assertNotNull(registry);
        assertFalse(registry.getRules().isEmpty());

        // Verify default rules exist
        assertTrue(registry.isSensitive("password"));
        assertTrue(registry.isSensitive("token"));
        assertTrue(registry.isSensitive("phone"));
        assertTrue(registry.isSensitive("email"));
        assertTrue(registry.isSensitive("idcard"));
    }

    @Test
    void testFindRule() {
        // Arrange
        MaskingRuleRegistry registry = MaskingRuleRegistry.createDefault();

        // Act & Assert
        assertNotNull(registry.findRule("password"));
        assertNotNull(registry.findRule("PASSWORD"));
        assertNotNull(registry.findRule("Password"));
        assertNotNull(registry.findRule("token"));
        assertNotNull(registry.findRule("phone"));
        assertNotNull(registry.findRule("email"));
    }

    @Test
    void testFindRuleNotFound() {
        // Arrange
        MaskingRuleRegistry registry = MaskingRuleRegistry.createDefault();

        // Act & Assert
        assertNull(registry.findRule("nonexistent"));
        assertNull(registry.findRule("username"));
    }

    @Test
    void testFindRuleWithNull() {
        // Arrange
        MaskingRuleRegistry registry = MaskingRuleRegistry.createDefault();

        // Act & Assert
        assertNull(registry.findRule(null));
    }

    @Test
    void testIsSensitive() {
        // Arrange
        MaskingRuleRegistry registry = MaskingRuleRegistry.createDefault();

        // Act & Assert
        assertTrue(registry.isSensitive("password"));
        assertTrue(registry.isSensitive("apikey"));
        assertTrue(registry.isSensitive("api_key"));
        assertTrue(registry.isSensitive("authorization"));
        assertFalse(registry.isSensitive("username"));
        assertFalse(registry.isSensitive("name"));
    }

    @Test
    void testIsSensitiveCaseInsensitive() {
        // Arrange
        MaskingRuleRegistry registry = MaskingRuleRegistry.createDefault();

        // Act & Assert
        assertTrue(registry.isSensitive("password"));
        assertTrue(registry.isSensitive("PASSWORD"));
        assertTrue(registry.isSensitive("Password"));
        assertTrue(registry.isSensitive("PaSsWoRd"));
    }

    @Test
    void testGetRules() {
        // Arrange
        MaskingRuleRegistry registry = MaskingRuleRegistry.createDefault();

        // Act
        List<MaskingRule> rules = registry.getRules();

        // Assert
        assertNotNull(rules);
        assertFalse(rules.isEmpty());
        assertTrue(rules.size() >= 4); // At least phone, idcard, email, token rules
    }

    @Test
    void testOfWithCustomRules() {
        // Arrange
        List<MaskingRule> customRules = new ArrayList<>();
        customRules.add(MaskingRule.builder()
                .name("custom")
                .fieldNames("customField")
                .type(MaskingRule.Type.FULL)
                .build());

        // Act
        MaskingRuleRegistry registry = MaskingRuleRegistry.of(customRules);

        // Assert
        assertNotNull(registry);
        assertTrue(registry.isSensitive("customField"));
        assertFalse(registry.isSensitive("password")); // Default rules not included
    }

    @Test
    void testBuilderAddRule() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("test")
                .fieldNames("testField")
                .type(MaskingRule.Type.FULL)
                .build();

        // Act
        MaskingRuleRegistry registry = MaskingRuleRegistry.builder()
                .addRule(rule)
                .build();

        // Assert
        assertNotNull(registry);
        assertTrue(registry.isSensitive("testField"));
        assertEquals(1, registry.getRules().size());
    }

    @Test
    void testBuilderAddDefaultRules() {
        // Act
        MaskingRuleRegistry registry = MaskingRuleRegistry.builder()
                .addDefaultRules()
                .build();

        // Assert
        assertNotNull(registry);
        assertTrue(registry.isSensitive("password"));
        assertTrue(registry.isSensitive("token"));
        assertTrue(registry.isSensitive("phone"));
        assertTrue(registry.isSensitive("email"));
    }

    @Test
    void testBuilderAddCustomAndDefaultRules() {
        // Arrange
        MaskingRule customRule = MaskingRule.builder()
                .name("custom")
                .fieldNames("customField")
                .type(MaskingRule.Type.FULL)
                .build();

        // Act
        MaskingRuleRegistry registry = MaskingRuleRegistry.builder()
                .addRule(customRule)
                .addDefaultRules()
                .build();

        // Assert
        assertNotNull(registry);
        assertTrue(registry.isSensitive("customField"));
        assertTrue(registry.isSensitive("password"));
        assertTrue(registry.isSensitive("token"));
    }

    @Test
    void testBuilderMultipleRules() {
        // Arrange
        MaskingRule rule1 = MaskingRule.builder()
                .name("rule1")
                .fieldNames("field1")
                .type(MaskingRule.Type.FULL)
                .build();

        MaskingRule rule2 = MaskingRule.builder()
                .name("rule2")
                .fieldNames("field2")
                .type(MaskingRule.Type.PARTIAL)
                .keepPrefix(2)
                .keepSuffix(2)
                .build();

        // Act
        MaskingRuleRegistry registry = MaskingRuleRegistry.builder()
                .addRule(rule1)
                .addRule(rule2)
                .build();

        // Assert
        assertNotNull(registry);
        assertTrue(registry.isSensitive("field1"));
        assertTrue(registry.isSensitive("field2"));
        assertEquals(2, registry.getRules().size());
    }

    @Test
    void testRulesImmutability() {
        // Arrange
        MaskingRuleRegistry registry = MaskingRuleRegistry.createDefault();

        // Act
        List<MaskingRule> rules = registry.getRules();
        int originalSize = rules.size();

        // Try to modify the returned list
        MaskingRule newRule = MaskingRule.builder()
                .name("new")
                .fieldNames("newField")
                .type(MaskingRule.Type.FULL)
                .build();
        rules.add(newRule);

        // Assert - original registry should not be affected
        assertEquals(originalSize, registry.getRules().size());
        assertFalse(registry.isSensitive("newField"));
    }
}
