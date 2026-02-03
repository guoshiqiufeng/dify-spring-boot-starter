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
 * Unit tests for TokenType
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
class TokenTypeTest {

    @Test
    void testAllTokenTypes() {
        // Assert all enum values exist
        assertNotNull(TokenType.FIELD_NAME);
        assertNotNull(TokenType.STRING_VALUE);
        assertNotNull(TokenType.NUMBER_VALUE);
        assertNotNull(TokenType.BOOLEAN_VALUE);
        assertNotNull(TokenType.NULL_VALUE);
        assertNotNull(TokenType.OTHER);
    }

    @Test
    void testTokenTypeValues() {
        // Act
        TokenType[] values = TokenType.values();

        // Assert
        assertEquals(6, values.length);
        assertEquals(TokenType.FIELD_NAME, values[0]);
        assertEquals(TokenType.STRING_VALUE, values[1]);
        assertEquals(TokenType.NUMBER_VALUE, values[2]);
        assertEquals(TokenType.BOOLEAN_VALUE, values[3]);
        assertEquals(TokenType.NULL_VALUE, values[4]);
        assertEquals(TokenType.OTHER, values[5]);
    }

    @Test
    void testTokenTypeValueOf() {
        // Act & Assert
        assertEquals(TokenType.FIELD_NAME, TokenType.valueOf("FIELD_NAME"));
        assertEquals(TokenType.STRING_VALUE, TokenType.valueOf("STRING_VALUE"));
        assertEquals(TokenType.NUMBER_VALUE, TokenType.valueOf("NUMBER_VALUE"));
        assertEquals(TokenType.BOOLEAN_VALUE, TokenType.valueOf("BOOLEAN_VALUE"));
        assertEquals(TokenType.NULL_VALUE, TokenType.valueOf("NULL_VALUE"));
        assertEquals(TokenType.OTHER, TokenType.valueOf("OTHER"));
    }

    @Test
    void testTokenTypeValueOfInvalid() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            TokenType.valueOf("INVALID_TYPE");
        });
    }
}
