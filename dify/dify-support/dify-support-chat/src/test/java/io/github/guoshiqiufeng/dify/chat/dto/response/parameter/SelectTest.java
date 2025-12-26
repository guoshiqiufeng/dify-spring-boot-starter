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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link Select}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class SelectTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        Select select = new Select();
        String type = "select";
        List<String> options = Arrays.asList("option1", "option2", "option3");

        // Act
        select.setType(type);
        select.setOptions(options);

        // Assert
        assertEquals(type, select.getType());
        assertEquals(options, select.getOptions());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        Select select = new Select();

        // Assert
        assertNull(select.getType());
        assertNull(select.getOptions());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        Select select = new Select();
        String label = "test-label";
        String variable = "test-variable";
        Boolean required = true;
        Integer maxLength = 100;
        String defaultValue = "test-default";

        // Act
        select.setLabel(label);
        select.setVariable(variable);
        select.setRequired(required);
        select.setMaxLength(maxLength);
        select.setDefaultValue(defaultValue);

        // Assert
        assertEquals(label, select.getLabel());
        assertEquals(variable, select.getVariable());
        assertEquals(required, select.getRequired());
        assertEquals(maxLength, select.getMaxLength());
        assertEquals(defaultValue, select.getDefaultValue());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        Select select1 = new Select();
        select1.setType("select");
        select1.setOptions(Arrays.asList("option1", "option2"));
        select1.setLabel("label1");
        select1.setVariable("var1");

        Select select2 = new Select();
        select2.setType("select");
        select2.setOptions(Arrays.asList("option1", "option2"));
        select2.setLabel("label1");
        select2.setVariable("var1");

        Select select3 = new Select();
        select3.setType("dropdown");
        select3.setOptions(Arrays.asList("option3", "option4"));
        select3.setLabel("label2");
        select3.setVariable("var2");

        // Assert
        assertEquals(select1, select2);
        assertEquals(select1.hashCode(), select2.hashCode());
        assertNotEquals(select1, select3);
        assertNotEquals(select1.hashCode(), select3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        Select select = new Select();
        select.setType("select");
        select.setOptions(Arrays.asList("option1", "option2"));
        select.setLabel("test-label");
        select.setVariable("test-variable");

        // Act
        String toString = select.toString();

        // Assert
        assertTrue(toString.contains("type=select"));
        assertTrue(toString.contains("options=[option1, option2]"));
        assertTrue(toString.contains("label=test-label"));
        assertTrue(toString.contains("variable=test-variable"));
    }
}
