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
package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link TextInput}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class TextInputTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        TextInput textInput = new TextInput();
        String label = "test-label";
        String variable = "test-variable";
        Boolean required = true;
        Integer maxLength = 100;
        String defaultValue = "test-default";

        // Act
        textInput.setLabel(label);
        textInput.setVariable(variable);
        textInput.setRequired(required);
        textInput.setMaxLength(maxLength);
        textInput.setDefaultValue(defaultValue);

        // Assert
        assertEquals(label, textInput.getLabel());
        assertEquals(variable, textInput.getVariable());
        assertEquals(required, textInput.getRequired());
        assertEquals(maxLength, textInput.getMaxLength());
        assertEquals(defaultValue, textInput.getDefaultValue());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        TextInput textInput = new TextInput();

        // Assert
        assertNull(textInput.getLabel());
        assertNull(textInput.getVariable());
        assertNull(textInput.getRequired());
        assertNull(textInput.getMaxLength());
        assertNull(textInput.getDefaultValue());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        TextInput textInput1 = new TextInput();
        textInput1.setLabel("label1");
        textInput1.setVariable("var1");
        textInput1.setRequired(true);
        textInput1.setMaxLength(100);
        textInput1.setDefaultValue("default1");

        TextInput textInput2 = new TextInput();
        textInput2.setLabel("label1");
        textInput2.setVariable("var1");
        textInput2.setRequired(true);
        textInput2.setMaxLength(100);
        textInput2.setDefaultValue("default1");

        TextInput textInput3 = new TextInput();
        textInput3.setLabel("label2");
        textInput3.setVariable("var2");
        textInput3.setRequired(false);
        textInput3.setMaxLength(200);
        textInput3.setDefaultValue("default2");

        // Assert
        assertEquals(textInput1, textInput2);
        assertEquals(textInput1.hashCode(), textInput2.hashCode());
        assertNotEquals(textInput1, textInput3);
        assertNotEquals(textInput1.hashCode(), textInput3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        TextInput textInput = new TextInput();
        textInput.setLabel("test-label");
        textInput.setVariable("test-variable");
        textInput.setRequired(true);
        textInput.setMaxLength(100);
        textInput.setDefaultValue("test-default");

        // Act
        String toString = textInput.toString();

        // Assert
        assertTrue(toString.contains("label=test-label"));
        assertTrue(toString.contains("variable=test-variable"));
        assertTrue(toString.contains("required=true"));
        assertTrue(toString.contains("maxLength=100"));
        assertTrue(toString.contains("defaultValue=test-default"));
    }
}
