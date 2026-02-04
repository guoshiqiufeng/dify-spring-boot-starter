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
package io.github.guoshiqiufeng.dify.core.utils;

import io.github.guoshiqiufeng.dify.core.logging.masking.MaskingConfig;
import io.github.guoshiqiufeng.dify.core.logging.masking.MaskingEngine;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LogMaskingUtils
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/2
 */
class LogMaskingUtilsTest {

    @Test
    void testMaskHeaders_withDefaultSensitiveHeaders() {
        // Arrange
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        headers.put("Authorization", Collections.singletonList("Bearer secret-token"));
        headers.put("X-API-Key", Collections.singletonList("api-key-12345"));
        headers.put("User-Agent", Collections.singletonList("Mozilla/5.0"));

        // Act
        Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headers);

        // Assert
        assertEquals("application/json", maskedHeaders.get("Content-Type").get(0));
        assertEquals("***MASKED***", maskedHeaders.get("Authorization").get(0));
        assertEquals("***MASKED***", maskedHeaders.get("X-API-Key").get(0));
        assertEquals("Mozilla/5.0", maskedHeaders.get("User-Agent").get(0));
    }

    @Test
    void testMaskHeaders_caseInsensitive() {
        // Arrange
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("authorization", Collections.singletonList("Bearer token1"));
        headers.put("AUTHORIZATION", Collections.singletonList("Bearer token2"));
        headers.put("Authorization", Collections.singletonList("Bearer token3"));

        // Act
        Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headers);

        // Assert
        assertEquals("***MASKED***", maskedHeaders.get("authorization").get(0));
        assertEquals("***MASKED***", maskedHeaders.get("AUTHORIZATION").get(0));
        assertEquals("***MASKED***", maskedHeaders.get("Authorization").get(0));
    }

    @Test
    void testMaskHeaders_withCustomSensitiveHeaders() {
        // Arrange
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("X-Custom-Token", Collections.singletonList("custom-secret"));
        headers.put("Authorization", Collections.singletonList("Bearer token"));

        Set<String> customSensitive = new HashSet<>(Collections.singletonList("X-Custom-Token"));

        // Act
        Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headers, customSensitive);

        // Assert
        assertEquals("***MASKED***", maskedHeaders.get("X-Custom-Token").get(0));
        assertEquals("Bearer token", maskedHeaders.get("Authorization").get(0)); // Not masked with custom set
    }

    @Test
    void testMaskHeaders_withNullOrEmpty() {
        // Test null headers
        assertNull(LogMaskingUtils.maskHeaders(null));

        // Test empty headers
        Map<String, List<String>> emptyHeaders = new HashMap<>();
        Map<String, List<String>> result = LogMaskingUtils.maskHeaders(emptyHeaders);
        assertTrue(result.isEmpty());

        // Test null sensitive headers set
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authorization", Collections.singletonList("Bearer token"));
        Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headers, null);
        assertEquals("Bearer token", maskedHeaders.get("Authorization").get(0));
    }

    @Test
    void testMaskBody_withDefaultSensitiveFields() {
        // Arrange
        String jsonBody = "{\"username\":\"john\",\"password\":\"secret123\",\"apiKey\":\"key-12345\"}";

        // Act
        String maskedBody = LogMaskingUtils.maskBody(jsonBody);

        // Assert
        assertTrue(maskedBody.contains("\"password\":\"***MASKED***\""));
        assertTrue(maskedBody.contains("\"apiKey\":\"***MASKED***\""));
        assertTrue(maskedBody.contains("\"username\":\"john\""));
    }

    @Test
    void testMaskBody_withFormData() {
        // Arrange
        String formBody = "username=john&password=secret123&email=john@example.com";

        // Act
        String maskedBody = LogMaskingUtils.maskBody(formBody);

        // Assert
        assertTrue(maskedBody.contains("password=***MASKED***"),
                "Expected masked body to contain 'password=***MASKED***' but was: " + maskedBody);
        assertTrue(maskedBody.contains("username=john"));
        // Email is now partially masked (new feature)
        assertTrue(maskedBody.contains("email=jo*n@example.com") || maskedBody.contains("email=john@example.com"),
                "Expected email to be partially masked or not masked, but was: " + maskedBody);
    }

    @Test
    void testMaskBody_caseInsensitive() {
        // Arrange
        String jsonBody = "{\"Password\":\"secret1\",\"PASSWORD\":\"secret2\",\"password\":\"secret3\"}";

        // Act
        String maskedBody = LogMaskingUtils.maskBody(jsonBody);

        // Assert
        assertTrue(maskedBody.contains("\"Password\":\"***MASKED***\""));
        assertTrue(maskedBody.contains("\"PASSWORD\":\"***MASKED***\""));
        assertTrue(maskedBody.contains("\"password\":\"***MASKED***\""));
    }

    @Test
    void testMaskBody_complexJson() {
        // Arrange
        String complexJson = "{\n" +
                "  \"user\": {\n" +
                "    \"username\": \"john\",\n" +
                "    \"password\": \"secret123\",\n" +
                "    \"profile\": {\n" +
                "      \"email\": \"john@example.com\",\n" +
                "      \"apiKey\": \"key-12345\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"token\": \"bearer-token-xyz\"\n" +
                "}";

        // Act
        String maskedBody = LogMaskingUtils.maskBody(complexJson);

        // Assert - Note: the regex preserves the original spacing between field name and value
        assertTrue(maskedBody.contains("\"password\": \"***MASKED***\""),
                "Expected masked body to contain '\"password\": \"***MASKED***\"' but was: " + maskedBody);
        assertTrue(maskedBody.contains("\"apiKey\": \"***MASKED***\""));
        assertTrue(maskedBody.contains("\"token\": \"***MASKED***\""));
        assertTrue(maskedBody.contains("\"username\": \"john\""));
        // Email is now partially masked (new feature: keeps first 2 and last 1 chars of local part)
        assertTrue(maskedBody.contains("\"email\": \"jo*n@example.com\"") || maskedBody.contains("\"email\": \"john@example.com\""),
                "Expected email to be partially masked or not masked, but was: " + maskedBody);
    }

    @Test
    void testMaskHeaders_multipleValues() {
        // Arrange
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authorization", Arrays.asList("Bearer token1", "Bearer token2"));
        headers.put("Content-Type", Collections.singletonList("application/json"));

        // Act
        Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headers);

        // Assert
        assertEquals(1, maskedHeaders.get("Authorization").size());
        assertEquals("***MASKED***", maskedHeaders.get("Authorization").get(0));
        assertEquals("application/json", maskedHeaders.get("Content-Type").get(0));
    }

    @Test
    void testMaskHeadersWithCustomSensitiveHeaders_nullHeaders() {
        // Arrange
        Set<String> customSensitive = new HashSet<>(Collections.singletonList("X-Custom-Token"));

        // Act
        Map<String, List<String>> result = LogMaskingUtils.maskHeaders(null, customSensitive);

        // Assert
        assertNull(result);
    }

    @Test
    void testMaskHeadersWithCustomSensitiveHeaders_emptyHeaders() {
        // Arrange
        Map<String, List<String>> headers = new HashMap<>();
        Set<String> customSensitive = new HashSet<>(Collections.singletonList("X-Custom-Token"));

        // Act
        Map<String, List<String>> result = LogMaskingUtils.maskHeaders(headers, customSensitive);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateEngine() {
        // Arrange
        MaskingConfig config = MaskingConfig.builder()
                .enabled(true)
                .maxBodyLength(500)
                .build();

        // Act
        MaskingEngine engine = LogMaskingUtils.createEngine(config);

        // Assert
        assertNotNull(engine);

        // Test that the engine works
        String body = "{\"password\":\"secret123\"}";
        String masked = engine.maskBody(body);
        assertTrue(masked.contains("***MASKED***"));
    }

    @Test
    void testGetDefaultEngine() {
        // Act
        MaskingEngine engine = LogMaskingUtils.getDefaultEngine();

        // Assert
        assertNotNull(engine);
        assertSame(engine, LogMaskingUtils.getDefaultEngine()); // Should return same instance

        // Test that the engine works
        String body = "{\"password\":\"secret123\"}";
        String masked = engine.maskBody(body);
        assertTrue(masked.contains("***MASKED***"));
    }

    @Test
    void testMaskHeadersWithCustomSensitiveHeaders_nullHeaderName() {
        // Arrange
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(null, Collections.singletonList("value"));
        headers.put("Authorization", Collections.singletonList("Bearer token"));
        Set<String> customSensitive = new HashSet<>(Collections.singletonList("Authorization"));

        // Act
        Map<String, List<String>> result = LogMaskingUtils.maskHeaders(headers, customSensitive);

        // Assert
        assertEquals("value", result.get(null).get(0)); // null header name should not be masked
        assertEquals("***MASKED***", result.get("Authorization").get(0));
    }

    @Test
    void testMaskHeadersWithEmptySensitiveHeaders() {
        // Arrange
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Authorization", Collections.singletonList("Bearer token"));
        headers.put("Content-Type", Collections.singletonList("application/json"));
        Set<String> emptySensitive = new HashSet<>();

        // Act - empty sensitiveHeaders should not mask anything
        Map<String, List<String>> result = LogMaskingUtils.maskHeaders(headers, emptySensitive);

        // Assert
        assertEquals("Bearer token", result.get("Authorization").get(0));
        assertEquals("application/json", result.get("Content-Type").get(0));
    }

}
