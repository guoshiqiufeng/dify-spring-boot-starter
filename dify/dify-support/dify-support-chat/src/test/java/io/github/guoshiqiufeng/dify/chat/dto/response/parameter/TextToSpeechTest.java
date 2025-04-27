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
 * Test for {@link TextToSpeech}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class TextToSpeechTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        TextToSpeech textToSpeech = new TextToSpeech();
        Boolean enabled = true;
        String voice = "test-voice";
        String language = "en-US";

        // Act
        textToSpeech.setEnabled(enabled);
        textToSpeech.setVoice(voice);
        textToSpeech.setLanguage(language);

        // Assert
        assertEquals(enabled, textToSpeech.getEnabled());
        assertEquals(voice, textToSpeech.getVoice());
        assertEquals(language, textToSpeech.getLanguage());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        TextToSpeech textToSpeech = new TextToSpeech();

        // Assert
        assertNull(textToSpeech.getEnabled());
        assertNull(textToSpeech.getVoice());
        assertNull(textToSpeech.getLanguage());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        TextToSpeech textToSpeech1 = new TextToSpeech();
        textToSpeech1.setEnabled(true);
        textToSpeech1.setVoice("voice1");
        textToSpeech1.setLanguage("en-US");

        TextToSpeech textToSpeech2 = new TextToSpeech();
        textToSpeech2.setEnabled(true);
        textToSpeech2.setVoice("voice1");
        textToSpeech2.setLanguage("en-US");

        TextToSpeech textToSpeech3 = new TextToSpeech();
        textToSpeech3.setEnabled(false);
        textToSpeech3.setVoice("voice2");
        textToSpeech3.setLanguage("fr-FR");

        // Assert
        assertEquals(textToSpeech1, textToSpeech2);
        assertEquals(textToSpeech1.hashCode(), textToSpeech2.hashCode());
        assertNotEquals(textToSpeech1, textToSpeech3);
        assertNotEquals(textToSpeech1.hashCode(), textToSpeech3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        TextToSpeech textToSpeech = new TextToSpeech();
        textToSpeech.setEnabled(true);
        textToSpeech.setVoice("test-voice");
        textToSpeech.setLanguage("en-US");

        // Act
        String toString = textToSpeech.toString();

        // Assert
        assertTrue(toString.contains("enabled=true"));
        assertTrue(toString.contains("voice=test-voice"));
        assertTrue(toString.contains("language=en-US"));
    }
}
