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
 * Unit tests for MaskingRule
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
class MaskingRuleTest {

    @Test
    void testFullMasking() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("password")
                .fieldNames("password", "secret")
                .type(MaskingRule.Type.FULL)
                .build();

        // Act & Assert
        assertEquals("***MASKED***", rule.apply("mySecretPassword123"));
        assertEquals("***MASKED***", rule.apply("short"));
        assertEquals("***MASKED***", rule.apply("a"));
    }

    @Test
    void testPartialMaskingPhone() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("phone")
                .fieldNames("phone", "mobile")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.PHONE)
                .keepPrefix(3)
                .keepSuffix(4)
                .maskChar('*')
                .minLength(7)
                .build();

        // Act & Assert
        assertEquals("138****5678", rule.apply("13812345678"));
        assertEquals("186****1234", rule.apply("18600001234"));
    }

    @Test
    void testPartialMaskingPhoneTooShort() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("phone")
                .fieldNames("phone")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.PHONE)
                .keepPrefix(3)
                .keepSuffix(4)
                .minLength(7)
                .build();

        // Act & Assert
        assertEquals("***MASKED***", rule.apply("123")); // Too short
        assertEquals("***MASKED***", rule.apply("12345")); // Below minLength
    }

    @Test
    void testPartialMaskingIdCard() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("idcard")
                .fieldNames("idcard", "id_card")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.ID_CARD)
                .keepPrefix(6)
                .keepSuffix(4)
                .maskChar('*')
                .minLength(10)
                .build();

        // Act & Assert
        assertEquals("110101********1234", rule.apply("110101199001011234"));
        assertEquals("320102********5678", rule.apply("320102198505055678"));
    }

    @Test
    void testPartialMaskingIdCardTooShort() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("idcard")
                .fieldNames("idcard")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.ID_CARD)
                .keepPrefix(6)
                .keepSuffix(4)
                .minLength(10)
                .build();

        // Act & Assert
        assertEquals("***MASKED***", rule.apply("12345")); // Too short
        assertEquals("***MASKED***", rule.apply("123456789")); // Below minLength
    }

    @Test
    void testPartialMaskingEmail() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("email")
                .fieldNames("email", "mail")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.EMAIL)
                .keepPrefix(2)
                .keepSuffix(1)
                .maskChar('*')
                .minLength(3)
                .build();

        // Act & Assert
        assertEquals("jo*n@example.com", rule.apply("john@example.com"));
        assertEquals("te*t@test.com", rule.apply("test@test.com")); // keepPrefix=2, keepSuffix=1
        assertEquals("ab*d@domain.org", rule.apply("abcd@domain.org")); // 4 chars: keep 2+1, mask 1
    }

    @Test
    void testPartialMaskingEmailShortLocalPart() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("email")
                .fieldNames("email")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.EMAIL)
                .keepPrefix(2)
                .keepSuffix(1)
                .maskChar('*')
                .minLength(3)
                .build();

        // Act & Assert
        assertEquals("**@test.com", rule.apply("ab@test.com")); // Local part too short
    }

    @Test
    void testPartialMaskingEmailInvalid() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("email")
                .fieldNames("email")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.EMAIL)
                .keepPrefix(2)
                .keepSuffix(1)
                .minLength(3)
                .build();

        // Act & Assert
        assertEquals("***MASKED***", rule.apply("notanemail")); // No @ sign
        assertEquals("***MASKED***", rule.apply("@example.com")); // No local part
    }

    @Test
    void testPartialMaskingGeneric() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("generic")
                .fieldNames("field")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.GENERIC)
                .keepPrefix(2)
                .keepSuffix(2)
                .maskChar('#')
                .minLength(5)
                .build();

        // Act & Assert
        assertEquals("ab###fg", rule.apply("abcdefg"));
        assertEquals("12####90", rule.apply("12345690"));
    }

    @Test
    void testPartialMaskingGenericTooShort() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("generic")
                .fieldNames("field")
                .type(MaskingRule.Type.PARTIAL)
                .keepPrefix(2)
                .keepSuffix(2)
                .minLength(5)
                .build();

        // Act & Assert
        assertEquals("***MASKED***", rule.apply("abc")); // Too short
        assertEquals("***MASKED***", rule.apply("abcd")); // Below minLength
    }

    @Test
    void testApplyWithNullValue() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("test")
                .fieldNames("test")
                .type(MaskingRule.Type.FULL)
                .build();

        // Act & Assert
        assertNull(rule.apply(null));
    }

    @Test
    void testApplyWithEmptyValue() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("test")
                .fieldNames("test")
                .type(MaskingRule.Type.FULL)
                .build();

        // Act & Assert
        assertEquals("", rule.apply(""));
    }

    @Test
    void testMatchesField() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("test")
                .fieldNames("password", "secret", "token")
                .type(MaskingRule.Type.FULL)
                .build();

        // Act & Assert
        assertTrue(rule.matchesField("password"));
        assertTrue(rule.matchesField("secret"));
        assertTrue(rule.matchesField("token"));
        assertFalse(rule.matchesField("username"));
        assertFalse(rule.matchesField("other"));
    }

    @Test
    void testGetters() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("testRule")
                .fieldNames("field1", "field2")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.PHONE)
                .keepPrefix(3)
                .keepSuffix(4)
                .maskChar('*')
                .minLength(7)
                .build();

        // Act & Assert
        assertEquals("testRule", rule.getName());
        assertEquals(2, rule.getFieldNames().size());
        assertTrue(rule.getFieldNames().contains("field1"));
        assertTrue(rule.getFieldNames().contains("field2"));
        assertEquals(MaskingRule.Type.PARTIAL, rule.getType());
        assertEquals(MaskingRule.ValueKind.PHONE, rule.getKind());
        assertEquals(3, rule.getKeepPrefix());
        assertEquals(4, rule.getKeepSuffix());
        assertEquals('*', rule.getMaskChar());
        assertEquals(7, rule.getMinLength());
    }

    @Test
    void testBuilderDefaults() {
        // Arrange & Act
        MaskingRule rule = MaskingRule.builder()
                .name("test")
                .fieldNames("field")
                .build();

        // Assert
        assertEquals(MaskingRule.Type.FULL, rule.getType());
        assertEquals(MaskingRule.ValueKind.GENERIC, rule.getKind());
        assertEquals(0, rule.getKeepPrefix());
        assertEquals(0, rule.getKeepSuffix());
        assertEquals('*', rule.getMaskChar());
        assertEquals(0, rule.getMinLength());
    }

    @Test
    void testBuilderMissingName() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            MaskingRule.builder()
                    .fieldNames("field")
                    .build();
        });
    }

    @Test
    void testBuilderEmptyName() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            MaskingRule.builder()
                    .name("")
                    .fieldNames("field")
                    .build();
        });
    }

    @Test
    void testBuilderMissingFieldNames() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            MaskingRule.builder()
                    .name("test")
                    .build();
        });
    }

    @Test
    void testFieldNamesImmutability() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("test")
                .fieldNames("field1", "field2")
                .build();

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> {
            rule.getFieldNames().add("field3");
        });
    }

    @Test
    void testFieldNamesCaseInsensitive() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("test")
                .fieldNames("Password", "SECRET", "Token")
                .type(MaskingRule.Type.FULL)
                .build();

        // Act & Assert - field names should be stored in lowercase
        assertTrue(rule.matchesField("password"));
        assertTrue(rule.matchesField("secret"));
        assertTrue(rule.matchesField("token"));
    }

    @Test
    void testPartialMaskingWithZeroMaskLength() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("test")
                .fieldNames("field")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.GENERIC)
                .keepPrefix(3)
                .keepSuffix(4)
                .minLength(7)
                .build();

        // Act - value length equals keepPrefix + keepSuffix
        String result = rule.apply("abcdefg");

        // Assert
        assertEquals("abc", result.substring(0, 3));
        assertEquals("defg", result.substring(3));
    }

    @Test
    void testTokenMasking() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("token")
                .fieldNames("token", "apikey")
                .type(MaskingRule.Type.FULL)
                .kind(MaskingRule.ValueKind.TOKEN)
                .build();

        // Act & Assert
        assertEquals("***MASKED***", rule.apply("sk-1234567890abcdef"));
        assertEquals("***MASKED***", rule.apply("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"));
    }

    @Test
    void testCustomMaskChar() {
        // Arrange
        MaskingRule rule = MaskingRule.builder()
                .name("custom")
                .fieldNames("field")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.GENERIC)
                .keepPrefix(2)
                .keepSuffix(2)
                .maskChar('#')
                .minLength(5)
                .build();

        // Act
        String result = rule.apply("abcdefgh");

        // Assert
        assertTrue(result.contains("####"));
        assertEquals("ab####gh", result);
    }

    @Test
    void testPhoneMaskingWithTooShortValue() {
        // Arrange - Test the value.length() < keepPrefix + keepSuffix branch
        // Need: value.length() >= minLength but < keepPrefix + keepSuffix
        MaskingRule rule = MaskingRule.builder()
                .name("phone")
                .fieldNames("phone")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.PHONE)
                .keepPrefix(4)
                .keepSuffix(4)
                .maskChar('*')
                .minLength(7) // minLength = 7, but keepPrefix + keepSuffix = 8
                .build();

        // Act - value length is 7 (>= minLength) but < keepPrefix + keepSuffix (8)
        String result = rule.apply("1234567");

        // Assert - should return full masking due to insufficient length
        assertEquals("***MASKED***", result);
    }

    @Test
    void testIdCardMaskingWithTooShortValue() {
        // Arrange - Test the value.length() < keepPrefix + keepSuffix branch
        // Need: value.length() >= minLength but < keepPrefix + keepSuffix
        MaskingRule rule = MaskingRule.builder()
                .name("idcard")
                .fieldNames("idcard")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.ID_CARD)
                .keepPrefix(6)
                .keepSuffix(5)
                .maskChar('*')
                .minLength(10) // minLength = 10, but keepPrefix + keepSuffix = 11
                .build();

        // Act - value length is 10 (>= minLength) but < keepPrefix + keepSuffix (11)
        String result = rule.apply("1234567890");

        // Assert - should return full masking due to insufficient length
        assertEquals("***MASKED***", result);
    }

    @Test
    void testGenericMaskingWithTooShortValue() {
        // Arrange - Test the value.length() < keepPrefix + keepSuffix branch
        // Need: value.length() >= minLength but < keepPrefix + keepSuffix
        MaskingRule rule = MaskingRule.builder()
                .name("generic")
                .fieldNames("field")
                .type(MaskingRule.Type.PARTIAL)
                .kind(MaskingRule.ValueKind.GENERIC)
                .keepPrefix(4)
                .keepSuffix(4)
                .maskChar('*')
                .minLength(6) // minLength = 6, but keepPrefix + keepSuffix = 8
                .build();

        // Act - value length is 7 (>= minLength) but < keepPrefix + keepSuffix (8)
        String result = rule.apply("1234567");

        // Assert - should return full masking due to insufficient length
        assertEquals("***MASKED***", result);
    }
}
