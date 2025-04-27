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
package io.github.guoshiqiufeng.dify.dataset.dto.request.document;

import io.github.guoshiqiufeng.dify.dataset.enums.document.ParentModeEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.document.PreProcessingRuleTypeEnum;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link CustomRule}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class CustomRuleTest {

    @Test
    public void testDefaultConstructor() {
        // Act
        CustomRule customRule = new CustomRule();

        // Assert
        assertNotNull(customRule);
        assertNull(customRule.getPreProcessingRules());
        assertNull(customRule.getSegmentation());
        assertNull(customRule.getParentMode());
        assertNull(customRule.getSubChunkSegmentation());
    }

    @Test
    public void testGetterAndSetter() {
        // Arrange
        CustomRule customRule = new CustomRule();
        List<PreProcessingRule> preProcessingRules = new ArrayList<>();
        preProcessingRules.add(new PreProcessingRule(PreProcessingRuleTypeEnum.remove_extra_spaces, true));

        Segmentation segmentation = new Segmentation("\\n", 1000);
        ParentModeEnum parentMode = ParentModeEnum.PARAGRAPH;
        SubChunkSegmentation subChunkSegmentation = new SubChunkSegmentation("/n", 200, 50);

        // Act
        customRule.setPreProcessingRules(preProcessingRules);
        customRule.setSegmentation(segmentation);
        customRule.setParentMode(parentMode);
        customRule.setSubChunkSegmentation(subChunkSegmentation);

        // Assert
        assertEquals(preProcessingRules, customRule.getPreProcessingRules());
        assertEquals(segmentation, customRule.getSegmentation());
        assertEquals(parentMode, customRule.getParentMode());
        assertEquals(subChunkSegmentation, customRule.getSubChunkSegmentation());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        CustomRule customRule1 = new CustomRule();
        List<PreProcessingRule> preProcessingRules1 = new ArrayList<>();
        preProcessingRules1.add(new PreProcessingRule(PreProcessingRuleTypeEnum.remove_extra_spaces, true));
        customRule1.setPreProcessingRules(preProcessingRules1);
        customRule1.setSegmentation(new Segmentation("\\n", 1000));
        customRule1.setParentMode(ParentModeEnum.PARAGRAPH);
        customRule1.setSubChunkSegmentation(new SubChunkSegmentation("/n", 200, 50));

        CustomRule customRule2 = new CustomRule();
        List<PreProcessingRule> preProcessingRules2 = new ArrayList<>();
        preProcessingRules2.add(new PreProcessingRule(PreProcessingRuleTypeEnum.remove_extra_spaces, true));
        customRule2.setPreProcessingRules(preProcessingRules2);
        customRule2.setSegmentation(new Segmentation("\\n", 1000));
        customRule2.setParentMode(ParentModeEnum.PARAGRAPH);
        customRule2.setSubChunkSegmentation(new SubChunkSegmentation("/n", 200, 50));

        CustomRule customRule3 = new CustomRule();
        List<PreProcessingRule> preProcessingRules3 = new ArrayList<>();
        preProcessingRules3.add(new PreProcessingRule(PreProcessingRuleTypeEnum.remove_urls_emails, false));
        customRule3.setPreProcessingRules(preProcessingRules3);
        customRule3.setSegmentation(new Segmentation("\\t", 500));
        customRule3.setParentMode(ParentModeEnum.FULL_DOC);
        customRule3.setSubChunkSegmentation(new SubChunkSegmentation("\\t", 300, 100));

        // Assert
        assertEquals(customRule1, customRule2);
        assertEquals(customRule1.hashCode(), customRule2.hashCode());
        assertNotEquals(customRule1, customRule3);
        assertNotEquals(customRule1.hashCode(), customRule3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        CustomRule customRule = new CustomRule();
        List<PreProcessingRule> preProcessingRules = new ArrayList<>();
        preProcessingRules.add(new PreProcessingRule(PreProcessingRuleTypeEnum.remove_extra_spaces, true));
        customRule.setPreProcessingRules(preProcessingRules);
        customRule.setSegmentation(new Segmentation("\\n", 1000));
        customRule.setParentMode(ParentModeEnum.PARAGRAPH);
        customRule.setSubChunkSegmentation(new SubChunkSegmentation("/n", 200, 50));

        // Act
        String toString = customRule.toString();

        // Assert
        assertTrue(toString.contains("preProcessingRules="));
        assertTrue(toString.contains("segmentation="));
        assertTrue(toString.contains("parentMode=PARAGRAPH"));
        assertTrue(toString.contains("subChunkSegmentation="));
    }

    @Test
    public void testWithParentModeFULL_DOC() {
        // Arrange
        CustomRule customRule = new CustomRule();

        // Act
        customRule.setParentMode(ParentModeEnum.FULL_DOC);

        // Assert
        assertEquals(ParentModeEnum.FULL_DOC, customRule.getParentMode());
        assertEquals("full-doc", customRule.getParentMode().getCode());
    }

    @Test
    public void testWithEmptyPreProcessingRules() {
        // Arrange
        CustomRule customRule = new CustomRule();
        List<PreProcessingRule> emptyRules = new ArrayList<>();

        // Act
        customRule.setPreProcessingRules(emptyRules);

        // Assert
        assertNotNull(customRule.getPreProcessingRules());
        assertTrue(customRule.getPreProcessingRules().isEmpty());
    }
}
