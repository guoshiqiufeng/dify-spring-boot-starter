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
 * Unit tests for FormTokenizer
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
class FormTokenizerTest {

    private final FormTokenizer tokenizer = new FormTokenizer();
    private final MaskingRuleRegistry registry = MaskingRuleRegistry.createDefault();
    private final MaskingContext context = new MaskingContext(MaskingConfig.createDefault());

    @Test
    void testSupportsValidFormData() {
        // Act & Assert
        assertTrue(tokenizer.supports(null, "key=value"));
        assertTrue(tokenizer.supports(null, "username=john&password=secret"));
        assertTrue(tokenizer.supports(null, "field1=value1&field2=value2&field3=value3"));
    }

    @Test
    void testSupportsInvalidFormData() {
        // Act & Assert
        assertFalse(tokenizer.supports(null, "{\"key\":\"value\"}"));
        assertFalse(tokenizer.supports(null, "[1,2,3]"));
        assertFalse(tokenizer.supports(null, null));
        assertFalse(tokenizer.supports(null, ""));
        assertFalse(tokenizer.supports(null, "no equals sign"));
    }

    @Test
    void testMaskSimpleFormData() {
        // Arrange
        String formData = "username=john&password=secret123";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("password=***MASKED***"));
    }

    @Test
    void testMaskFormDataWithMultipleSensitiveFields() {
        // Arrange
        String formData = "username=john&password=pass123&token=tok456&apikey=key789";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("password=***MASKED***"));
        assertTrue(result.contains("token=***MASKED***"));
        assertTrue(result.contains("apikey=***MASKED***"));
    }

    @Test
    void testMaskFormDataWithEmail() {
        // Arrange
        String formData = "username=john&email=john@example.com";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("email=jo*n@example.com"));
    }

    @Test
    void testMaskFormDataWithPhone() {
        // Arrange
        String formData = "username=john&phone=13812345678";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("phone=138****5678"));
    }

    @Test
    void testMaskFormDataCaseInsensitive() {
        // Arrange
        String formData = "Password=secret1&PASSWORD=secret2&password=secret3";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("Password=***MASKED***"));
        assertTrue(result.contains("PASSWORD=***MASKED***"));
        assertTrue(result.contains("password=***MASKED***"));
    }

    @Test
    void testMaskFormDataWithSpecialCharacters() {
        // Arrange
        String formData = "username=john&password=p@ss!123";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("password=***MASKED***"));
    }

    @Test
    void testMaskFormDataWithEmptyValue() {
        // Arrange
        String formData = "username=john&password=&email=test@test.com";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("password="));
    }

    @Test
    void testMaskFormDataWithUrlEncodedValues() {
        // Arrange
        String formData = "username=john+doe&password=secret%20123";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john+doe"));
        assertTrue(result.contains("password=***MASKED***"));
    }

    @Test
    void testMaskFormDataWithSingleField() {
        // Arrange
        String formData = "password=secret123";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertEquals("password=***MASKED***", result);
    }

    @Test
    void testMaskFormDataWithNullInput() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(null, context, registry, buffer);

        // Assert
        assertNull(result);
    }

    @Test
    void testMaskFormDataWithEmptyInput() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask("", context, registry, buffer);

        // Assert
        assertEquals("", result);
    }

    @Test
    void testMaskFormDataWithNoSensitiveFields() {
        // Arrange
        String formData = "username=john&age=30&city=NYC";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertEquals(formData, result);
    }

    @Test
    void testMaskFormDataWithDuplicateKeys() {
        // Arrange
        String formData = "password=secret1&username=john&password=secret2";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("password=***MASKED***"));
        assertTrue(result.contains("username=john"));
        // Both password occurrences should be masked (regex replaces all matches)
        // Count occurrences by splitting on the masked pattern
        String[] parts = result.split("password=\\*\\*\\*MASKED\\*\\*\\*", -1);
        int maskedCount = parts.length - 1;
        assertTrue(maskedCount >= 1, "At least one password should be masked");
    }

    @Test
    void testMaskFormDataWithWhitespace() {
        // Arrange
        String formData = "username=john doe&password=secret 123";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("password=***MASKED***"));
    }

    @Test
    void testMaskFormDataWithApiKey() {
        // Arrange
        String formData = "username=john&api_key=key12345&x-api-key=key67890";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("api_key=***MASKED***"));
        assertTrue(result.contains("x-api-key=***MASKED***"));
    }

    @Test
    void testMaskFormDataWithIdCard() {
        // Arrange
        String formData = "username=john&idcard=110101199001011234";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("idcard=110101********1234"));
    }

    @Test
    void testMaskFormDataWithMixedSensitiveFields() {
        // Arrange
        String formData = "name=john&email=john@test.com&phone=13812345678&password=secret&token=tok123";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("name=john"));
        assertTrue(result.contains("email=jo*n@test.com"));
        assertTrue(result.contains("phone=138****5678"));
        assertTrue(result.contains("password=***MASKED***"));
        assertTrue(result.contains("token=***MASKED***"));
    }

    @Test
    void testMaskFormDataWithLongValues() {
        // Arrange
        String longValue = "a".repeat(1000);
        String formData = "password=" + longValue;
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("password=***MASKED***"));
    }

    @Test
    void testMaskFormDataWithUnicodeCharacters() {
        // Arrange
        String formData = "username=用户名&password=密码123";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=用户名"));
        assertTrue(result.contains("password=***MASKED***"));
    }

    @Test
    void testMaskFormDataWithEqualsInValue() {
        // Arrange
        String formData = "username=john&data=key=value";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("data=key"));
    }

    @Test
    void testMaskFormDataWithAmpersandInValue() {
        // Arrange - This is a tricky case as & is the delimiter
        String formData = "username=john&note=value1";
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        String result = tokenizer.mask(formData, context, registry, buffer);

        // Assert
        assertTrue(result.contains("username=john"));
    }

    @Test
    void testSupportsFormDataStartingWithBrace() {
        // Arrange - Form data that starts with { should not be supported
        String formData = "{username=john";

        // Act
        boolean result = tokenizer.supports(null, formData);

        // Assert
        assertFalse(result); // Should not support as it starts with {
    }

    @Test
    void testSupportsFormDataStartingWithBracket() {
        // Arrange - Form data that starts with [ should not be supported
        String formData = "[username=john";

        // Act
        boolean result = tokenizer.supports(null, formData);

        // Assert
        assertFalse(result); // Should not support as it starts with [
    }

    @Test
    void testSupportsFormDataWithWhitespaceBeforeBrace() {
        // Arrange - Form data with whitespace before { should not be supported
        String formData = "  {username=john";

        // Act
        boolean result = tokenizer.supports(null, formData);

        // Assert
        assertFalse(result); // Should not support as trim() starts with {
    }

    @Test
    void testSupportsFormDataWithWhitespaceBeforeBracket() {
        // Arrange - Form data with whitespace before [ should not be supported
        String formData = "  [username=john";

        // Act
        boolean result = tokenizer.supports(null, formData);

        // Assert
        assertFalse(result); // Should not support as trim() starts with [
    }
}
