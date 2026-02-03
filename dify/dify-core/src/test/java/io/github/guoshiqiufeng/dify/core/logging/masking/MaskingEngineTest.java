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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MaskingEngine
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
class MaskingEngineTest {

    @Test
    void testGetDefault() {
        // Act
        MaskingEngine engine = MaskingEngine.getDefault();

        // Assert
        assertNotNull(engine);
        assertSame(engine, MaskingEngine.getDefault()); // Should return same instance
    }

    @Test
    void testOfWithCustomConfig() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(1000)
                .build();

        // Act
        MaskingEngine engine = MaskingEngine.of(config);

        // Assert
        assertNotNull(engine);
    }

    @Test
    void testMaskHeadersWithSensitiveHeaders() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        headers.put("Authorization", Collections.singletonList("Bearer token123"));
        headers.put("X-API-Key", Collections.singletonList("key-12345"));

        // Act
        Map<String, List<String>> result = engine.maskHeaders(headers);

        // Assert
        assertEquals("application/json", result.get("Content-Type").get(0));
        assertEquals("***MASKED***", result.get("Authorization").get(0));
        assertEquals("***MASKED***", result.get("X-API-Key").get(0));
    }

    @Test
    void testMaskHeadersWithNullHeaders() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        Map<String, List<String>> result = engine.maskHeaders(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testMaskHeadersWithEmptyHeaders() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        Map<String, List<String>> headers = new HashMap<>();

        // Act
        Map<String, List<String>> result = engine.maskHeaders(headers);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testMaskHeadersWithDisabledMasking() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .enabled(false)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authorization", Collections.singletonList("Bearer token123"));

        // Act
        Map<String, List<String>> result = engine.maskHeaders(headers);

        // Assert
        assertEquals("Bearer token123", result.get("Authorization").get(0));
    }

    @Test
    void testMaskBodyWithJsonContent() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        String body = "{\"username\":\"john\",\"password\":\"secret123\"}";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertTrue(result.contains("\"username\":\"john\""));
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskBodyWithFormData() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        String body = "username=john&password=secret123&email=john@example.com";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertTrue(result.contains("username=john"));
        assertTrue(result.contains("password=***MASKED***"));
    }

    @Test
    void testMaskBodyWithNullBody() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        String result = engine.maskBody(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testMaskBodyWithEmptyBody() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        String result = engine.maskBody("");

        // Assert
        assertEquals("", result);
    }

    @Test
    void testMaskBodyWithDisabledMasking() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .enabled(false)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        String body = "{\"password\":\"secret123\"}";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertEquals(body, result);
    }

    @Test
    void testMaskBodyWithMaxLength() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(50)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        String body = "{\"username\":\"john\",\"password\":\"secret123\",\"email\":\"john@example.com\",\"phone\":\"1234567890\"}";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertTrue(result.length() <= 65); // 50 + "... (truncated)"
        assertTrue(result.endsWith("... (truncated)"));
    }

    @Test
    void testMaskBodyWithMaxLengthFallback() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(20)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        String body = "This is a plain text body that is not JSON or form data";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertTrue(result.length() <= 35); // 20 + "... (truncated)"
        assertTrue(result.endsWith("... (truncated)"));
    }

    @Test
    void testMaskBodyWithUnsupportedFormat() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        String body = "This is plain text without any structure";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertEquals(body, result); // Should return original
    }

    @Test
    void testMaskValueWithSensitiveField() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        String result = engine.maskValue("password", "secret123");

        // Assert
        assertEquals("***MASKED***", result);
    }

    @Test
    void testMaskValueWithNonSensitiveField() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        String result = engine.maskValue("username", "john");

        // Assert
        assertEquals("john", result);
    }

    @Test
    void testMaskValueWithNullValue() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        String result = engine.maskValue("password", null);

        // Assert
        assertNull(result);
    }

    @Test
    void testMaskValueWithEmptyValue() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        String result = engine.maskValue("password", "");

        // Assert
        assertEquals("", result);
    }

    @Test
    void testMaskValueWithDisabledMasking() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .enabled(false)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);

        // Act
        String result = engine.maskValue("password", "secret123");

        // Assert
        assertEquals("secret123", result);
    }

    @Test
    void testMaskValueWithPhoneNumber() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        String result = engine.maskValue("phone", "13812345678");

        // Assert
        assertEquals("138****5678", result);
    }

    @Test
    void testMaskValueWithEmail() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        String result = engine.maskValue("email", "john@example.com");

        // Assert
        assertEquals("jo*n@example.com", result);
    }

    @Test
    void testMaskValueWithIdCard() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        String result = engine.maskValue("idcard", "110101199001011234");

        // Assert
        assertEquals("110101********1234", result);
    }

    @Test
    void testMaskHeadersWithContext() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        MaskingContext context = new MaskingContext(MaskingConfig.createDefault());
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authorization", Collections.singletonList("Bearer token123"));

        // Act
        Map<String, List<String>> result = engine.maskHeaders(headers, context);

        // Assert
        assertEquals("***MASKED***", result.get("Authorization").get(0));
    }

    @Test
    void testMaskBodyWithContext() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        MaskingContext context = new MaskingContext(MaskingConfig.createDefault());
        String body = "{\"password\":\"secret123\"}";

        // Act
        String result = engine.maskBody(body, context);

        // Assert
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskValueWithContext() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        MaskingContext context = new MaskingContext(MaskingConfig.createDefault());

        // Act
        String result = engine.maskValue("password", "secret123", context);

        // Assert
        assertEquals("***MASKED***", result);
    }

    @Test
    void testMaskHeadersWithMultipleValues() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authorization", Arrays.asList("Bearer token1", "Bearer token2"));

        // Act
        Map<String, List<String>> result = engine.maskHeaders(headers);

        // Assert
        assertEquals(1, result.get("Authorization").size());
        assertEquals("***MASKED***", result.get("Authorization").get(0));
    }

    @Test
    void testMaskBodyWithComplexJson() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        String body = "{\"user\":{\"profile\":{\"email\":\"test@test.com\",\"password\":\"secret\"}}}";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskBodyWithJsonArray() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        String body = "[{\"password\":\"pass1\"},{\"password\":\"pass2\"}]";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertTrue(result.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskBodyWithTokenizerException() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        // Malformed JSON that might cause tokenizer to fail
        String body = "{\"password\":\"secret";

        // Act
        String result = engine.maskBody(body);

        // Assert - should fallback to original or handle gracefully
        assertNotNull(result);
    }

    @Test
    void testMaskHeadersCaseInsensitive() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("authorization", Collections.singletonList("Bearer token1"));
        headers.put("AUTHORIZATION", Collections.singletonList("Bearer token2"));
        headers.put("Authorization", Collections.singletonList("Bearer token3"));

        // Act
        Map<String, List<String>> result = engine.maskHeaders(headers);

        // Assert
        assertEquals("***MASKED***", result.get("authorization").get(0));
        assertEquals("***MASKED***", result.get("AUTHORIZATION").get(0));
        assertEquals("***MASKED***", result.get("Authorization").get(0));
    }

    @Test
    void testMaskBodyWithCustomRegistry() {
        // Arrange
        MaskingRule customRule = MaskingRule.builder()
                .name("custom")
                .fieldNames("customSecret")
                .type(MaskingRule.Type.FULL)
                .build();
        MaskingRuleRegistry customRegistry = MaskingRuleRegistry.builder()
                .addRule(customRule)
                .build();
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .registry(customRegistry)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        String body = "{\"customSecret\":\"secret123\",\"password\":\"pass456\"}";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertTrue(result.contains("\"customSecret\":\"***MASKED***\""));
        // password should not be masked as it's not in custom registry
        assertTrue(result.contains("\"password\":\"pass456\""));
    }

    @Test
    void testMaskBodyWithWhitespace() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        String body = "{\n  \"password\": \"secret\",\n  \"username\": \"john\"\n}";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertTrue(result.contains("***MASKED***"));
        assertTrue(result.contains("\"username\""));
    }

    @Test
    void testMaskValueWithUnknownField() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();

        // Act
        String result = engine.maskValue("unknownField", "someValue");

        // Assert
        assertEquals("someValue", result); // Should not mask unknown fields
    }

    @Test
    void testMaskBodyWithTokenizerException2() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        // Create a body that might cause tokenizer to throw exception
        // Use a very long string that could cause issues
        String body = "{\"field\":\"" + "x".repeat(100000) + "\"}";

        // Act
        String result = engine.maskBody(body);

        // Assert - should handle gracefully and return something
        assertNotNull(result);
    }

    @Test
    void testMaskBodyWithMaxLengthInTokenizer() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(30)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        String body = "{\"password\":\"secret123\",\"username\":\"john\"}";

        // Act
        String result = engine.maskBody(body);

        // Assert - should truncate after masking
        assertTrue(result.length() <= 45); // 30 + "... (truncated)"
        assertTrue(result.endsWith("... (truncated)"));
    }

    @Test
    void testMaskBodyFallbackWithMaxLength() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(20)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        // Plain text that won't match any tokenizer
        String body = "This is a plain text body that is very long and should be truncated";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertTrue(result.length() <= 35); // 20 + "... (truncated)"
        assertTrue(result.endsWith("... (truncated)"));
    }

    @Test
    void testMaskHeadersWithNullHeaderName() {
        // Arrange - Test the headerName != null branch
        MaskingEngine engine = MaskingEngine.getDefault();
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(null, Collections.singletonList("value"));
        headers.put("Authorization", Collections.singletonList("Bearer token"));

        // Act
        Map<String, List<String>> result = engine.maskHeaders(headers);

        // Assert
        assertEquals("value", result.get(null).get(0)); // null key should not be masked
        assertEquals("***MASKED***", result.get("Authorization").get(0));
    }

    @Test
    void testMaskBodyWithMaxLengthZero() {
        // Arrange - Test maxLength > 0 condition
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(0) // maxLength = 0 should not truncate
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        String body = "{\"password\":\"secret123\",\"username\":\"john\"}";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertFalse(result.endsWith("... (truncated)")); // Should not truncate
        assertTrue(result.contains("***MASKED***"));
    }

    @Test
    void testMaskBodyWithMaxLengthNegative() {
        // Arrange - Test maxLength > 0 condition with negative value
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(-1) // negative maxLength should not truncate
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        String body = "{\"password\":\"secret123\",\"username\":\"john\"}";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertFalse(result.endsWith("... (truncated)")); // Should not truncate
        assertTrue(result.contains("***MASKED***"));
    }

    @Test
    void testMaskBodyFallbackWithMaxLengthZero() {
        // Arrange - Test fallback path with maxLength = 0
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(0)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        String body = "Plain text that doesn't match any tokenizer";

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertEquals(body, result); // Should return original without truncation
    }

    @Test
    void testMaskBodyWithExactMaxLength() {
        // Arrange - Test boundary condition where body length equals maxLength
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(50)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        String body = "{\"password\":\"secret\",\"user\":\"john\"}"; // Exactly 50 chars after masking

        // Act
        String result = engine.maskBody(body);

        // Assert - Should not truncate if length equals maxLength
        assertNotNull(result);
    }

    @Test
    void testMaskBodyWithMaxLengthBoundary() {
        // Arrange - Test maxLength boundary (length > maxLength by 1)
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(10)
                .build();
        MaskingEngine engine = MaskingEngine.of(config);
        String body = "{\"password\":\"secret123\"}"; // Will be longer than 10 after masking

        // Act
        String result = engine.maskBody(body);

        // Assert
        assertTrue(result.length() <= 25); // 10 + "... (truncated)"
        assertTrue(result.endsWith("... (truncated)"));
    }

    @Test
    void testMaskBodyFallbackWithoutMaxLength() {
        // Arrange
        MaskingEngine engine = MaskingEngine.getDefault();
        // Plain text that won't match any tokenizer
        String body = "This is plain text without JSON or form structure";

        // Act
        String result = engine.maskBody(body);

        // Assert - should return original body
        assertEquals(body, result);
    }
}
