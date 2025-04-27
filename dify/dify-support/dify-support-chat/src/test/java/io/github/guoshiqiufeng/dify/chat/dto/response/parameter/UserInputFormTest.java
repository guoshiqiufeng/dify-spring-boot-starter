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
 * Test for {@link UserInputForm}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class UserInputFormTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        UserInputForm form = new UserInputForm();
        TextInput textInput = new TextInput();
        textInput.setLabel("Text Input");

        Paragraph paragraph = new Paragraph();
        paragraph.setLabel("Paragraph");

        Select select = new Select();
        select.setLabel("Select");

        // Act
        form.setTextInput(textInput);
        form.setParagraph(paragraph);
        form.setSelect(select);

        // Assert
        assertEquals(textInput, form.getTextInput());
        assertEquals(paragraph, form.getParagraph());
        assertEquals(select, form.getSelect());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        UserInputForm form = new UserInputForm();

        // Assert
        assertNull(form.getTextInput());
        assertNull(form.getParagraph());
        assertNull(form.getSelect());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        TextInput textInput1 = new TextInput();
        textInput1.setLabel("Text Input");

        Paragraph paragraph1 = new Paragraph();
        paragraph1.setLabel("Paragraph");

        Select select1 = new Select();
        select1.setLabel("Select");

        UserInputForm form1 = new UserInputForm();
        form1.setTextInput(textInput1);
        form1.setParagraph(paragraph1);
        form1.setSelect(select1);

        TextInput textInput2 = new TextInput();
        textInput2.setLabel("Text Input");

        Paragraph paragraph2 = new Paragraph();
        paragraph2.setLabel("Paragraph");

        Select select2 = new Select();
        select2.setLabel("Select");

        UserInputForm form2 = new UserInputForm();
        form2.setTextInput(textInput2);
        form2.setParagraph(paragraph2);
        form2.setSelect(select2);

        TextInput textInput3 = new TextInput();
        textInput3.setLabel("Different Text Input");

        Paragraph paragraph3 = new Paragraph();
        paragraph3.setLabel("Different Paragraph");

        Select select3 = new Select();
        select3.setLabel("Different Select");

        UserInputForm form3 = new UserInputForm();
        form3.setTextInput(textInput3);
        form3.setParagraph(paragraph3);
        form3.setSelect(select3);

        // Assert
        assertEquals(form1, form2);
        assertEquals(form1.hashCode(), form2.hashCode());
        assertNotEquals(form1, form3);
        assertNotEquals(form1.hashCode(), form3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        TextInput textInput = new TextInput();
        textInput.setLabel("Text Input");

        Paragraph paragraph = new Paragraph();
        paragraph.setLabel("Paragraph");

        Select select = new Select();
        select.setLabel("Select");

        UserInputForm form = new UserInputForm();
        form.setTextInput(textInput);
        form.setParagraph(paragraph);
        form.setSelect(select);

        // Act
        String toString = form.toString();

        // Assert
        assertTrue(toString.contains("textInput="));
        assertTrue(toString.contains("paragraph="));
        assertTrue(toString.contains("select="));
    }
}
