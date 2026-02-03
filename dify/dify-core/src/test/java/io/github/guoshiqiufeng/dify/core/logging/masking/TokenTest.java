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
 * Unit tests for Token
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
class TokenTest {

    @Test
    void testTokenCreation() {
        // Arrange & Act
        Token token = new Token(TokenType.FIELD_NAME, 0, 10, "username");

        // Assert
        assertEquals(TokenType.FIELD_NAME, token.getType());
        assertEquals(0, token.getStart());
        assertEquals(10, token.getEnd());
        assertEquals("username", token.getValue());
    }

    @Test
    void testTokenWithStringValue() {
        // Arrange & Act
        Token token = new Token(TokenType.STRING_VALUE, 15, 30, "test@example.com");

        // Assert
        assertEquals(TokenType.STRING_VALUE, token.getType());
        assertEquals(15, token.getStart());
        assertEquals(30, token.getEnd());
        assertEquals("test@example.com", token.getValue());
    }

    @Test
    void testTokenWithNumberValue() {
        // Arrange & Act
        Token token = new Token(TokenType.NUMBER_VALUE, 5, 10, "12345");

        // Assert
        assertEquals(TokenType.NUMBER_VALUE, token.getType());
        assertEquals(5, token.getStart());
        assertEquals(10, token.getEnd());
        assertEquals("12345", token.getValue());
    }

    @Test
    void testTokenWithBooleanValue() {
        // Arrange & Act
        Token token = new Token(TokenType.BOOLEAN_VALUE, 0, 4, "true");

        // Assert
        assertEquals(TokenType.BOOLEAN_VALUE, token.getType());
        assertEquals(0, token.getStart());
        assertEquals(4, token.getEnd());
        assertEquals("true", token.getValue());
    }

    @Test
    void testTokenWithNullValue() {
        // Arrange & Act
        Token token = new Token(TokenType.NULL_VALUE, 0, 4, "null");

        // Assert
        assertEquals(TokenType.NULL_VALUE, token.getType());
        assertEquals(0, token.getStart());
        assertEquals(4, token.getEnd());
        assertEquals("null", token.getValue());
    }

    @Test
    void testTokenWithOtherType() {
        // Arrange & Act
        Token token = new Token(TokenType.OTHER, 0, 1, "{");

        // Assert
        assertEquals(TokenType.OTHER, token.getType());
        assertEquals(0, token.getStart());
        assertEquals(1, token.getEnd());
        assertEquals("{", token.getValue());
    }

    @Test
    void testTokenToString() {
        // Arrange
        Token token = new Token(TokenType.FIELD_NAME, 0, 10, "username");

        // Act
        String result = token.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("FIELD_NAME"));
        assertTrue(result.contains("0"));
        assertTrue(result.contains("10"));
        assertTrue(result.contains("username"));
    }

    @Test
    void testTokenWithNullValueString() {
        // Arrange & Act
        Token token = new Token(TokenType.STRING_VALUE, 0, 0, null);

        // Assert
        assertEquals(TokenType.STRING_VALUE, token.getType());
        assertEquals(0, token.getStart());
        assertEquals(0, token.getEnd());
        assertNull(token.getValue());
    }

    @Test
    void testTokenWithEmptyValue() {
        // Arrange & Act
        Token token = new Token(TokenType.STRING_VALUE, 5, 5, "");

        // Assert
        assertEquals(TokenType.STRING_VALUE, token.getType());
        assertEquals(5, token.getStart());
        assertEquals(5, token.getEnd());
        assertEquals("", token.getValue());
    }
}
