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
 * Test for {@link Paragraph}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class ParagraphTest {

    @Test
    public void testInheritedFields() {
        // Arrange
        Paragraph paragraph = new Paragraph();
        String label = "test-label";
        String variable = "test-variable";
        Boolean required = true;
        Integer maxLength = 100;
        String defaultValue = "test-default";

        // Act
        paragraph.setLabel(label);
        paragraph.setVariable(variable);
        paragraph.setRequired(required);
        paragraph.setMaxLength(maxLength);
        paragraph.setDefaultValue(defaultValue);

        // Assert
        assertEquals(label, paragraph.getLabel());
        assertEquals(variable, paragraph.getVariable());
        assertEquals(required, paragraph.getRequired());
        assertEquals(maxLength, paragraph.getMaxLength());
        assertEquals(defaultValue, paragraph.getDefaultValue());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        Paragraph paragraph = new Paragraph();

        // Assert
        assertNull(paragraph.getLabel());
        assertNull(paragraph.getVariable());
        assertNull(paragraph.getRequired());
        assertNull(paragraph.getMaxLength());
        assertNull(paragraph.getDefaultValue());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        Paragraph paragraph1 = new Paragraph();
        paragraph1.setLabel("label1");
        paragraph1.setVariable("var1");
        paragraph1.setRequired(true);
        paragraph1.setMaxLength(100);
        paragraph1.setDefaultValue("default1");

        Paragraph paragraph2 = new Paragraph();
        paragraph2.setLabel("label1");
        paragraph2.setVariable("var1");
        paragraph2.setRequired(true);
        paragraph2.setMaxLength(100);
        paragraph2.setDefaultValue("default1");

        Paragraph paragraph3 = new Paragraph();
        paragraph3.setLabel("label2");
        paragraph3.setVariable("var2");
        paragraph3.setRequired(false);
        paragraph3.setMaxLength(200);
        paragraph3.setDefaultValue("default2");

        // Assert
        assertEquals(paragraph1, paragraph2);
        assertEquals(paragraph1.hashCode(), paragraph2.hashCode());
        assertNotEquals(paragraph1, paragraph3);
        assertNotEquals(paragraph1.hashCode(), paragraph3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        Paragraph paragraph = new Paragraph();
        paragraph.setLabel("test-label");
        paragraph.setVariable("test-variable");
        paragraph.setRequired(true);
        paragraph.setMaxLength(100);
        paragraph.setDefaultValue("test-default");

        // Act
        String toString = paragraph.toString();

        // Assert
        assertTrue(toString.contains("label=test-label"));
        assertTrue(toString.contains("variable=test-variable"));
        assertTrue(toString.contains("required=true"));
        assertTrue(toString.contains("maxLength=100"));
        assertTrue(toString.contains("defaultValue=test-default"));
    }
}
