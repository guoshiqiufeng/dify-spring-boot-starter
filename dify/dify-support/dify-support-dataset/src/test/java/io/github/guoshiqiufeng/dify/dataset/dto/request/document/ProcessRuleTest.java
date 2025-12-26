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

import io.github.guoshiqiufeng.dify.dataset.enums.document.ModeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link ProcessRule}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class ProcessRuleTest {

    @Test
    public void testDefaultConstructor() {
        // Act
        ProcessRule processRule = new ProcessRule();

        // Assert
        assertNotNull(processRule);
        assertNull(processRule.getMode());
        assertNull(processRule.getRules());
    }

    @Test
    public void testGetterAndSetter() {
        // Arrange
        ProcessRule processRule = new ProcessRule();
        ModeEnum mode = ModeEnum.automatic;
        CustomRule rules = new CustomRule();

        // Act
        processRule.setMode(mode);
        processRule.setRules(rules);

        // Assert
        assertEquals(mode, processRule.getMode());
        assertEquals(rules, processRule.getRules());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        ProcessRule processRule1 = new ProcessRule();
        processRule1.setMode(ModeEnum.automatic);

        ProcessRule processRule2 = new ProcessRule();
        processRule2.setMode(ModeEnum.automatic);

        ProcessRule processRule3 = new ProcessRule();
        processRule3.setMode(ModeEnum.custom);
        CustomRule customRule = new CustomRule();
        processRule3.setRules(customRule);

        // Assert
        assertEquals(processRule1, processRule2);
        assertEquals(processRule1.hashCode(), processRule2.hashCode());
        assertNotEquals(processRule1, processRule3);
        assertNotEquals(processRule1.hashCode(), processRule3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        ProcessRule processRule = new ProcessRule();
        processRule.setMode(ModeEnum.automatic);

        // Act
        String toString = processRule.toString();

        // Assert
        assertTrue(toString.contains("mode=" + ModeEnum.automatic));
        assertTrue(toString.contains("rules=null"));
    }

    @Test
    public void testWithCustomMode() {
        // Arrange
        ProcessRule processRule = new ProcessRule();
        ModeEnum mode = ModeEnum.custom;
        CustomRule rules = new CustomRule();

        // Act
        processRule.setMode(mode);
        processRule.setRules(rules);

        // Assert
        assertEquals(mode, processRule.getMode());
        assertEquals(rules, processRule.getRules());
        assertNotNull(processRule.getRules());
    }

    @Test
    public void testWithHierarchicalMode() {
        // Arrange
        ProcessRule processRule = new ProcessRule();

        // Act
        processRule.setMode(ModeEnum.hierarchical);

        // Assert
        assertEquals(ModeEnum.hierarchical, processRule.getMode());
    }
}
