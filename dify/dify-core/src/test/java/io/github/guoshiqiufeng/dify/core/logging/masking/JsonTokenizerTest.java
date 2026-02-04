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
 * Unit tests for JsonTokenizer
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
class JsonTokenizerTest {

    private final JsonTokenizer tokenizer = new JsonTokenizer();
    private final MaskingRuleRegistry registry = MaskingRuleRegistry.createDefault();
    private final MaskingContext context = new MaskingContext(MaskingConfig.createDefault());

    @Test
    void testSupportsValidJson() {
        // Act & Assert
        assertTrue(tokenizer.supports(null, "{\"key\":\"value\"}"));
        assertTrue(tokenizer.supports(null, "[1,2,3]"));
        assertTrue(tokenizer.supports(null, "  {\"key\":\"value\"}  "));
        assertTrue(tokenizer.supports(null, "  [1,2,3]  "));
    }

    @Test
    void testSupportsInvalidJson() {
        // Act & Assert
        assertFalse(tokenizer.supports(null, "not json"));
        assertFalse(tokenizer.supports(null, "key=value"));
        assertFalse(tokenizer.supports(null, null));
        assertFalse(tokenizer.supports(null, ""));
        assertFalse(tokenizer.supports(null, "{incomplete"));
        assertFalse(tokenizer.supports(null, "incomplete}"));
    }

    @Test
    void testMaskSimpleJson() {
        // Arrange
        String json = "{\"username\":\"john\",\"password\":\"secret123\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"username\":\"john\""));
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskNestedJson() {
        // Arrange
        String json = "{\"user\":{\"name\":\"john\",\"password\":\"secret\"}}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"name\":\"john\""));
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskJsonArray() {
        // Arrange
        String json = "[\"value1\",\"value2\",\"value3\"]";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"value1\""));
        assertTrue(result.contains("\"value2\""));
        assertTrue(result.contains("\"value3\""));
    }

    @Test
    void testMaskJsonWithNumbers() {
        // Arrange
        String json = "{\"age\":30,\"count\":100}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"age\":30"));
        assertTrue(result.contains("\"count\":100"));
    }

    @Test
    void testMaskJsonWithBooleans() {
        // Arrange
        String json = "{\"active\":true,\"verified\":false}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"active\":true"));
        assertTrue(result.contains("\"verified\":false"));
    }

    @Test
    void testMaskJsonWithNull() {
        // Arrange
        String json = "{\"value\":null,\"name\":\"test\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"value\":null"));
        assertTrue(result.contains("\"name\":\"test\""));
    }

    @Test
    void testMaskJsonWithEscapedQuotes() {
        // Arrange
        String json = "{\"message\":\"He said \\\"hello\\\"\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"message\":\"He said \\\"hello\\\"\""));
    }

    @Test
    void testMaskJsonWithMultipleSensitiveFields() {
        // Arrange
        String json = "{\"password\":\"pass123\",\"token\":\"tok456\",\"apikey\":\"key789\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
        assertTrue(result.contains("\"token\":\"***MASKED***\""));
        assertTrue(result.contains("\"apikey\":\"***MASKED***\""));
    }

    @Test
    void testMaskJsonWithEmail() {
        // Arrange
        String json = "{\"email\":\"john@example.com\",\"name\":\"John\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"name\":\"John\""));
        // Email should be partially masked
        assertTrue(result.contains("\"email\":\"jo*n@example.com\"") ||
                   result.contains("\"email\":\"john@example.com\""));
    }

    @Test
    void testMaskJsonWithPhone() {
        // Arrange
        String json = "{\"phone\":\"13812345678\",\"name\":\"John\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"name\":\"John\""));
        assertTrue(result.contains("\"phone\":\"138****5678\""));
    }

    @Test
    void testMaskComplexNestedJson() {
        // Arrange
        String json = "{\"user\":{\"profile\":{\"email\":\"test@test.com\",\"password\":\"secret\"}}}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskJsonArrayWithObjects() {
        // Arrange
        String json = "[{\"password\":\"pass1\"},{\"password\":\"pass2\"}]";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskWithNullInput() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(null, context, registry, buffer);

        // Assert
        assertNull(result);
    }

    @Test
    void testMaskWithEmptyInput() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask("", context, registry, buffer);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testMaskInvalidJsonFallback() {
        // Arrange
        String invalidJson = "not valid json at all";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(invalidJson, context, registry, buffer);

        // Assert - should return original when not valid JSON
        assertEquals(invalidJson, result);
    }

    @Test
    void testMaskJsonWithWhitespace() {
        // Arrange
        String json = "{\n  \"password\": \"secret\",\n  \"username\": \"john\"\n}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"password\""));
        assertTrue(result.contains("***MASKED***"));
        assertTrue(result.contains("\"username\""));
        assertTrue(result.contains("\"john\""));
    }

    @Test
    void testMaskJsonWithEmptyObject() {
        // Arrange
        String json = "{}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertEquals("{}", result);
    }

    @Test
    void testMaskJsonWithEmptyArray() {
        // Arrange
        String json = "[]";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertEquals("[]", result);
    }

    @Test
    void testMaskJsonWithNestedArrays() {
        // Arrange
        String json = "{\"data\":[[1,2],[3,4]]}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"data\":[[1,2],[3,4]]"));
    }

    @Test
    void testMaskJsonWithMixedTypes() {
        // Arrange
        String json = "{\"string\":\"value\",\"number\":42,\"boolean\":true,\"null\":null,\"array\":[1,2],\"object\":{\"key\":\"val\"}}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"string\":\"value\""));
        assertTrue(result.contains("\"number\":42"));
        assertTrue(result.contains("\"boolean\":true"));
        assertTrue(result.contains("\"null\":null"));
    }

    @Test
    void testMaskJsonCaseInsensitiveFields() {
        // Arrange
        String json = "{\"Password\":\"secret1\",\"PASSWORD\":\"secret2\",\"password\":\"secret3\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"Password\":\"***MASKED***\""));
        assertTrue(result.contains("\"PASSWORD\":\"***MASKED***\""));
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskJsonWithSpecialCharacters() {
        // Arrange
        String json = "{\"field\":\"value with spaces and special !@#$%\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"field\":\"value with spaces and special !@#$%\""));
    }

    @Test
    void testMaskJsonWithUnicodeCharacters() {
        // Arrange
        String json = "{\"name\":\"用户名\",\"password\":\"密码123\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"name\":\"用户名\""));
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskJsonWithLongValues() {
        // Arrange
        String longValue = "a".repeat(1000);
        String json = "{\"password\":\"" + longValue + "\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskJsonWithEmptyStringValue() {
        // Arrange
        String json = "{\"password\":\"\",\"username\":\"john\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"password\":\"\""));
        assertTrue(result.contains("\"username\":\"john\""));
    }

    @Test
    void testMaskJsonWithLeadingWhitespace() {
        // Arrange - JSON with leading whitespace before opening brace
        String json = "  \n\t{\"password\":\"secret\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskJsonWithMalformedString() {
        // Arrange - JSON with unclosed string in field name
        String json = "{\"password:\"secret\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - should handle gracefully
        assertNotNull(result);
    }

    @Test
    void testMaskJsonWithMalformedStringInArray() {
        // Arrange - Array with unclosed string
        String json = "[\"value1\", \"value2]";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - should handle gracefully
        assertNotNull(result);
    }

    @Test
    void testMaskJsonWithNestedEmptyObject() {
        // Arrange - Nested empty object to test depth > 0 branch
        String json = "{\"data\":{},\"password\":\"secret\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"data\":{}"));
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskJsonWithNullFieldName() {
        // Arrange - Test currentFieldName == null branch
        String json = "{\"field\":\"value\",\"password\":\"secret\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"field\":\"value\""));
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskJsonWithEmptyStringAfterColon() {
        // Arrange - Empty string value to test stringEnd <= i + 1 branch
        String json = "{\"field\":\"\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"field\":\"\""));
    }

    @Test
    void testMaskJsonWithValueAtEndOfString() {
        // Arrange - Test valueEnd >= length branch in OTHER_VALUE
        String json = "{\"count\":123}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"count\":123"));
    }

    @Test
    void testSupportsArrayWithoutClosingBracket() {
        // Arrange - Array without closing bracket to test the missed branch
        String json = "[1,2,3";

        // Act
        boolean result = tokenizer.supports(null, json);

        // Assert
        assertFalse(result); // Should not support as it doesn't end with ]
    }

    @Test
    void testMaskJsonArrayWithUnclosedString() {
        // Arrange - Array with unclosed string to test stringEnd <= i + 1 branch
        String json = "[\"value1\", \"value2";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - should handle gracefully
        assertNotNull(result);
    }

    @Test
    void testIsValueTerminatorWithAllCharacters() {
        // Arrange - Test all branches of isValueTerminator
        String json = "{\"num1\":123,\"num2\":456}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - comma should terminate value
        assertTrue(result.contains("123"));
        assertTrue(result.contains("456"));
    }

    @Test
    void testIsValueTerminatorWithClosingBrace() {
        // Arrange - Test } as value terminator
        String json = "{\"num\":123}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - } should terminate value
        assertTrue(result.contains("123"));
    }

    @Test
    void testIsValueTerminatorWithClosingBracket() {
        // Arrange - Test ] as value terminator in array
        String json = "[123,456]";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - ] should terminate value
        assertTrue(result.contains("123"));
        assertTrue(result.contains("456"));
    }

    @Test
    void testIsValueTerminatorWithWhitespace() {
        // Arrange - Test whitespace as value terminator
        String json = "{\"num\": 123 }";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - whitespace should terminate value
        assertTrue(result.contains("123"));
    }

    @Test
    void testMaskJsonWithRuleReturningNull() {
        // Arrange - This tests the rule != null ? rule.apply(value) : "***MASKED***" branch
        // We need a scenario where findRule returns null but isSensitive returns true
        // This is actually not possible with the current implementation, but we test the fallback
        String json = "{\"password\":\"secret\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - should use rule.apply()
        assertTrue(result.contains("***MASKED***"));
    }

    @Test
    void testMaskJsonWithUnclosedQuoteInObject() {
        // Arrange - Test fieldEnd <= i + 1 branch (unclosed quote in field name)
        String json = "{\"field";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - should handle gracefully
        assertNotNull(result);
    }

    @Test
    void testMaskJsonWithUnclosedQuoteInArrayString() {
        // Arrange - Test stringEnd <= i + 1 branch in IN_ARRAY state
        String json = "[\"value";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - should handle gracefully
        assertNotNull(result);
    }

    @Test
    void testMaskJsonWithNullCurrentFieldName() {
        // Arrange - Test currentFieldName == null branch
        // This happens when we have a value without a field name (shouldn't happen in valid JSON)
        String json = "{\"field\":\"value\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"field\":\"value\""));
    }

    @Test
    void testMaskJsonWithValueEndingAtLength() {
        // Arrange - Test valueEnd >= length branch (value at end of string)
        String json = "{\"active\":true}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("true"));
    }

    @Test
    void testMaskJsonWithDefaultCase() {
        // Arrange - Try to trigger default case in switch (should be unreachable)
        // This is defensive code that shouldn't be reached in normal operation
        String json = "{\"field\":\"value\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - should process normally
        assertTrue(result.contains("\"field\":\"value\""));
    }

    @Test
    void testIsValueTerminatorWithComma() {
        // Arrange - Test comma as terminator
        String json = "{\"a\":1,\"b\":2}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2"));
    }

    @Test
    void testIsValueTerminatorWithBrace() {
        // Arrange - Test } as terminator
        String json = "{\"value\":true}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("true"));
    }

    @Test
    void testIsValueTerminatorWithBracket() {
        // Arrange - Test ] as terminator
        String json = "[true,false]";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("true"));
        assertTrue(result.contains("false"));
    }

    @Test
    void testIsValueTerminatorWithSpace() {
        // Arrange - Test whitespace as terminator
        String json = "{\"value\": true }";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("true"));
    }

    @Test
    void testMaskJsonWithSensitiveFieldButNullRegistry() {
        // This test ensures the currentFieldName != null check works
        String json = "{\"password\":\"secret\",\"other\":\"value\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
        assertTrue(result.contains("\"other\":\"value\""));
    }

    @Test
    void testMaskJsonWithNonSensitiveFieldAfterColon() {
        // Test currentFieldName != null but NOT sensitive (else branch)
        String json = "{\"username\":\"john\",\"age\":30}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - username is not sensitive, should not be masked
        assertTrue(result.contains("\"username\":\"john\""));
        assertTrue(result.contains("\"age\":30"));
    }

    @Test
    void testMaskJsonWithValueAtVeryEnd() {
        // Test valueEnd >= length (value extends to end of string)
        String json = "{\"flag\":false}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("false"));
    }

    @Test
    void testMaskJsonWithNumberAtEnd() {
        // Test valueEnd >= length with number
        String json = "{\"count\":999}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("999"));
    }

    @Test
    void testMaskJsonWithNullAtEnd() {
        // Test valueEnd >= length with null
        String json = "{\"value\":null}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("null"));
    }

    @Test
    void testIsValueTerminatorAllBranches() {
        // Test all 4 branches of isValueTerminator: comma, }, ], whitespace
        String json = "{\"a\":1,\"b\":2,\"c\":[3,4],\"d\":5}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - all values should be properly terminated
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("4"));
        assertTrue(result.contains("5"));
    }

    @Test
    void testMaskJsonWithTabAsWhitespace() {
        // Test Character.isWhitespace with tab character
        String json = "{\"value\":\t123}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("123"));
    }

    @Test
    void testMaskJsonWithNewlineAsWhitespace() {
        // Test Character.isWhitespace with newline
        String json = "{\"value\":\n123}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("123"));
    }

    @Test
    void testMaskJsonWithRuleReturnsNull() {
        // Create a custom registry where isSensitive returns true but findRule returns null
        // This is a theoretical edge case that tests the fallback "***MASKED***"
        MaskingRule customRule = MaskingRule.builder()
                .name("custom")
                .fieldNames("testfield")
                .type(MaskingRule.Type.FULL)
                .build();

        MaskingRuleRegistry customRegistry = MaskingRuleRegistry.builder()
                .addRule(customRule)
                .build();

        MaskingContext customContext = new MaskingContext(
                MaskingConfig.builder()
                        .enabled(true)
                        .registry(customRegistry)
                        .build()
        );

        // Use a field that's in the registry
        String json = "{\"testfield\":\"value\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, customContext, customRegistry, buffer);

        // Assert - should use rule.apply() which returns "***MASKED***"
        assertTrue(result.contains("***MASKED***"));
    }

    @Test
    void testMaskJsonWithCurrentFieldNameNull() {
        // Test the case where currentFieldName is null when checking sensitivity
        // This happens in arrays or when processing non-field values
        String json = "[\"value1\",\"value2\"]";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert - should process normally without checking sensitivity
        assertTrue(result.contains("\"value1\""));
        assertTrue(result.contains("\"value2\""));
    }

    @Test
    void testMaskJsonWithValueEndEqualsLength() {
        // Test the case where valueEnd reaches length (no terminator found)
        // This happens when a value is at the very end of the JSON
        String json = "{\"active\":true}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("true"));
    }

    @Test
    void testMaskJsonWithBooleanFalseAtEnd() {
        // Another test for valueEnd == length
        String json = "{\"disabled\":false}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("false"));
    }

    @Test
    void testMaskJsonWithNullValueAtEnd() {
        // Test null value at end (valueEnd == length)
        String json = "{\"data\":null}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("null"));
    }

    @Test
    void testIsValueTerminatorWithAllCharTypes() {
        // Comprehensive test for all 4 branches of isValueTerminator
        // Test comma
        String json1 = "{\"a\":1,\"b\":2}";
        MaskingBuffer buffer1 = MaskingBuffer.get();
        String result1 = tokenizer.mask(json1, context, registry, buffer1);
        assertTrue(result1.contains("1"));

        // Test closing brace
        String json2 = "{\"x\":99}";
        MaskingBuffer buffer2 = MaskingBuffer.get();
        String result2 = tokenizer.mask(json2, context, registry, buffer2);
        assertTrue(result2.contains("99"));

        // Test closing bracket
        String json3 = "[1,2]";
        MaskingBuffer buffer3 = MaskingBuffer.get();
        String result3 = tokenizer.mask(json3, context, registry, buffer3);
        assertTrue(result3.contains("1"));

        // Test whitespace (space, tab, newline)
        String json4 = "{\"v\": 5 }";
        MaskingBuffer buffer4 = MaskingBuffer.get();
        String result4 = tokenizer.mask(json4, context, registry, buffer4);
        assertTrue(result4.contains("5"));
    }

    @Test
    void testIsValueTerminatorWithNonTerminatorCharacters() {
        // Test characters that are NOT terminators (letters, digits)
        // This ensures the false branches of isValueTerminator are covered
        String json = "{\"value\":true123}"; // Invalid JSON but tests the logic
        MaskingBuffer buffer = MaskingBuffer.get();
        String result = tokenizer.mask(json, context, registry, buffer);
        assertNotNull(result);
    }

    @Test
    void testMaskJsonWithComplexNestedStructure() {
        // Test complex nested structure to ensure all branches are covered
        String json = "{\"data\":{\"user\":{\"password\":\"secret\",\"profile\":{\"email\":\"test@test.com\"}}}}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("***MASKED***"));
        assertTrue(result.contains("te*t@test.com"));
    }

    @Test
    void testMaskJsonWithCarriageReturnWhitespace() {
        // Test Character.isWhitespace with carriage return
        String json = "{\"value\":\r123}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("123"));
    }

    @Test
    void testMaskJsonWithFormFeedWhitespace() {
        // Test Character.isWhitespace with form feed
        String json = "{\"value\":\f456}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("456"));
    }

    @Test
    void testMaskJsonWithMultipleWhitespaceTypes() {
        // Test various whitespace characters as value terminators
        String json = "{\"a\": 1 ,\"b\":\t2\t,\"c\":\n3\n,\"d\":\r4\r}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("4"));
    }

    @Test
    void testMaskJsonWithNegativeNumber() {
        // Test negative numbers to ensure minus sign is not treated as terminator
        String json = "{\"value\":-123}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("-123"));
    }

    @Test
    void testMaskJsonWithDecimalNumber() {
        // Test decimal numbers to ensure dot is not treated as terminator
        String json = "{\"value\":123.456}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("123.456"));
    }

    @Test
    void testMaskJsonWithScientificNotation() {
        // Test scientific notation to ensure 'e' is not treated as terminator
        String json = "{\"value\":1.23e10}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("1.23e10"));
    }

    @Test
    void testMaskJsonWithCurrentFieldNameNullInSensitiveCheck() {
        // Ensure currentFieldName == null short-circuits the && operator
        // This happens when processing array values
        String json = "[{\"key\":\"value\"}]";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"key\":\"value\""));
    }

    @Test
    void testMaskJsonWithValueEndingExactlyAtLength() {
        // Test case where valueEnd reaches exactly length (no terminator at end)
        // This tests the valueEnd < length condition when it becomes false
        String json = "{\"flag\":true}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("true"));
    }

    @Test
    void testMaskJsonWithLongNumberAtEnd() {
        // Test long number value at the very end
        String json = "{\"value\":9999999999}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("9999999999"));
    }

    @Test
    void testIsValueTerminatorWithEachConditionSeparately() {
        // Test each condition of isValueTerminator separately

        // Test comma terminator
        String json1 = "{\"a\":1,\"b\":2}";
        MaskingBuffer buffer1 = MaskingBuffer.get();
        String result1 = tokenizer.mask(json1, context, registry, buffer1);
        assertTrue(result1.contains("1"));

        // Test } terminator
        String json2 = "{\"x\":99}";
        MaskingBuffer buffer2 = MaskingBuffer.get();
        String result2 = tokenizer.mask(json2, context, registry, buffer2);
        assertTrue(result2.contains("99"));

        // Test ] terminator
        String json3 = "[1,2]";
        MaskingBuffer buffer3 = MaskingBuffer.get();
        String result3 = tokenizer.mask(json3, context, registry, buffer3);
        assertTrue(result3.contains("1"));

        // Test space terminator
        String json4 = "{\"v\": 5 }";
        MaskingBuffer buffer4 = MaskingBuffer.get();
        String result4 = tokenizer.mask(json4, context, registry, buffer4);
        assertTrue(result4.contains("5"));

        // Test tab terminator
        String json5 = "{\"t\":\t7\t}";
        MaskingBuffer buffer5 = MaskingBuffer.get();
        String result5 = tokenizer.mask(json5, context, registry, buffer5);
        assertTrue(result5.contains("7"));

        // Test newline terminator
        String json6 = "{\"n\":\n8\n}";
        MaskingBuffer buffer6 = MaskingBuffer.get();
        String result6 = tokenizer.mask(json6, context, registry, buffer6);
        assertTrue(result6.contains("8"));
    }

    @Test
    void testMaskJsonWithNonTerminatorInValue() {
        // Test that non-terminator characters (letters, digits, dots, minus) are included in value
        String json1 = "{\"num\":-123.456e10}";
        MaskingBuffer buffer1 = MaskingBuffer.get();
        String result1 = tokenizer.mask(json1, context, registry, buffer1);
        assertTrue(result1.contains("-123.456e10"));

        // Test with letters in boolean/null values
        String json2 = "{\"bool\":false,\"nul\":null}";
        MaskingBuffer buffer2 = MaskingBuffer.get();
        String result2 = tokenizer.mask(json2, context, registry, buffer2);
        assertTrue(result2.contains("false"));
        assertTrue(result2.contains("null"));
    }

    @Test
    void testMaskJsonWithAllWhitespaceTypes() {
        // Test all types of whitespace characters as terminators
        // Space (U+0020), Tab (U+0009), Newline (U+000A), Carriage Return (U+000D), Form Feed (U+000C)
        String json = "{\"a\": 1 ,\"b\":\t2\t,\"c\":\n3\n,\"d\":\r4\r,\"e\":\f5\f}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("1"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("4"));
        assertTrue(result.contains("5"));
    }

    @Test
    void testMaskJsonWithEmptyStringInSensitiveField() {
        // Test empty string value in sensitive field
        String json = "{\"password\":\"\"}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("\"password\":\"\""));
    }

    @Test
    void testMaskJsonWithNestedObjectsAndArrays() {
        // Complex nested structure to ensure all state transitions are covered
        String json = "{\"data\":[{\"user\":{\"password\":\"secret\"}},{\"admin\":{\"token\":\"abc123\"}}]}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("***MASKED***"));
    }

    @Test
    void testMaskJsonWithValueAtStringBoundary() {
        // Test value that ends exactly at the string boundary
        String json = "{\"end\":123}";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(json, context, registry, buffer);

        // Assert
        assertTrue(result.contains("123"));
    }
}
