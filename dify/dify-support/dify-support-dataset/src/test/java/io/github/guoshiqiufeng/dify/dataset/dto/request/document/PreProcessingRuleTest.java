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
package io.github.guoshiqiufeng.dify.dataset.dto.request.document;

import io.github.guoshiqiufeng.dify.dataset.enums.document.PreProcessingRuleTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link PreProcessingRule}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class PreProcessingRuleTest {

    @Test
    public void testAllArgsConstructor() {
        // Arrange
        PreProcessingRuleTypeEnum id = PreProcessingRuleTypeEnum.remove_extra_spaces;
        Boolean enabled = true;

        // Act
        PreProcessingRule rule = new PreProcessingRule(id, enabled);

        // Assert
        assertNotNull(rule);
        assertEquals(id, rule.getId());
        assertEquals(enabled, rule.getEnabled());
    }

    @Test
    public void testGetterAndSetter() {
        // Arrange
        PreProcessingRule rule = new PreProcessingRule(null, null);
        PreProcessingRuleTypeEnum id = PreProcessingRuleTypeEnum.remove_urls_emails;
        Boolean enabled = false;

        // Act
        rule.setId(id);
        rule.setEnabled(enabled);

        // Assert
        assertEquals(id, rule.getId());
        assertEquals(enabled, rule.getEnabled());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        PreProcessingRule rule1 = new PreProcessingRule(PreProcessingRuleTypeEnum.remove_extra_spaces, true);
        PreProcessingRule rule2 = new PreProcessingRule(PreProcessingRuleTypeEnum.remove_extra_spaces, true);
        PreProcessingRule rule3 = new PreProcessingRule(PreProcessingRuleTypeEnum.remove_urls_emails, false);

        // Assert
        assertEquals(rule1, rule2);
        assertEquals(rule1.hashCode(), rule2.hashCode());
        assertNotEquals(rule1, rule3);
        assertNotEquals(rule1.hashCode(), rule3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        PreProcessingRule rule = new PreProcessingRule(PreProcessingRuleTypeEnum.remove_extra_spaces, true);

        // Act
        String toString = rule.toString();

        // Assert
        assertTrue(toString.contains("id=" + PreProcessingRuleTypeEnum.remove_extra_spaces));
        assertTrue(toString.contains("enabled=true"));
    }

}
