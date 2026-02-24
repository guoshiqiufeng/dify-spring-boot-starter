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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test coverage for LogMaskingUtils.maskUrl method
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/10
 */
class LogMaskingUtilsUrlTest {

    @Test
    void testMaskUrl_noQuery_returnsOriginal() {
        String url = "https://api.example.com/v1/chat";
        assertEquals(url, LogMaskingUtils.maskUrl(url));
    }

    @Test
    void testMaskUrl_nullUrl_returnsNull() {
        assertNull(LogMaskingUtils.maskUrl(null));
    }

    @Test
    void testMaskUrl_masksApiKey() {
        String url = "https://api.example.com/v1/chat?api_key=secret123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?api_key=***", masked);
    }

    @Test
    void testMaskUrl_masksApiKeyCamelCase() {
        String url = "https://api.example.com/v1/chat?apiKey=secret123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?apiKey=***", masked);
    }

    @Test
    void testMaskUrl_masksApiKeyHyphen() {
        String url = "https://api.example.com/v1/chat?api-key=secret123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?api-key=***", masked);
    }

    @Test
    void testMaskUrl_masksAccessToken() {
        String url = "https://api.example.com/v1/chat?access_token=bearer123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?access_token=***", masked);
    }

    @Test
    void testMaskUrl_masksAccessTokenCamelCase() {
        String url = "https://api.example.com/v1/chat?accessToken=bearer123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?accessToken=***", masked);
    }

    @Test
    void testMaskUrl_masksRefreshToken() {
        String url = "https://api.example.com/v1/chat?refresh_token=refresh123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?refresh_token=***", masked);
    }

    @Test
    void testMaskUrl_masksRefreshTokenCamelCase() {
        String url = "https://api.example.com/v1/chat?refreshToken=refresh123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?refreshToken=***", masked);
    }

    @Test
    void testMaskUrl_masksBearerToken() {
        String url = "https://api.example.com/v1/chat?bearer_token=bearer123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?bearer_token=***", masked);
    }

    @Test
    void testMaskUrl_masksBearerTokenCamelCase() {
        String url = "https://api.example.com/v1/chat?bearerToken=bearer123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?bearerToken=***", masked);
    }

    @Test
    void testMaskUrl_masksSessionToken() {
        String url = "https://api.example.com/v1/chat?session_token=session123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?session_token=***", masked);
    }

    @Test
    void testMaskUrl_masksSessionTokenCamelCase() {
        String url = "https://api.example.com/v1/chat?sessionToken=session123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?sessionToken=***", masked);
    }

    @Test
    void testMaskUrl_masksPassword() {
        String url = "https://api.example.com/v1/chat?password=pass123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?password=***", masked);
    }

    @Test
    void testMaskUrl_masksSecret() {
        String url = "https://api.example.com/v1/chat?secret=secret123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?secret=***", masked);
    }

    @Test
    void testMaskUrl_masksAuthorization() {
        String url = "https://api.example.com/v1/chat?authorization=auth123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?authorization=***", masked);
    }

    @Test
    void testMaskUrl_masksAuth() {
        String url = "https://api.example.com/v1/chat?auth=auth123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?auth=***", masked);
    }

    @Test
    void testMaskUrl_masksToken() {
        String url = "https://api.example.com/v1/chat?token=token123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?token=***", masked);
    }

    @Test
    void testMaskUrl_caseInsensitive() {
        String url = "https://api.example.com/v1/chat?API_KEY=secret123&AccessToken=bearer123";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?API_KEY=***&AccessToken=***", masked);
    }

    @Test
    void testMaskUrl_multipleParams_onlySensitiveMasked() {
        String url = "https://api.example.com/v1/chat?user=john&api_key=secret123&page=1&token=abc";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?user=john&api_key=***&page=1&token=***", masked);
    }

    @Test
    void testMaskUrl_urlEncodedValues_masked() {
        String url = "https://api.example.com/v1/chat?api_key=secret%20123&user=john%20doe";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?api_key=***&user=john%20doe", masked);
    }

    @Test
    void testMaskUrl_emptyQueryValue() {
        String url = "https://api.example.com/v1/chat?api_key=&user=john";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?api_key=***&user=john", masked);
    }

    @Test
    void testMaskUrl_trailingQuestionMark() {
        String url = "https://api.example.com/v1/chat?";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?", masked);
    }

    @Test
    void testMaskUrl_complexUrl_withFragment() {
        String url = "https://api.example.com/v1/chat?api_key=secret123&user=john#section";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?api_key=***&user=john#section", masked);
    }

    @Test
    void testMaskUrl_multipleSensitiveParams() {
        String url = "https://api.example.com/v1/chat?api_key=key1&access_token=token1&password=pass1";
        String masked = LogMaskingUtils.maskUrl(url);
        assertEquals("https://api.example.com/v1/chat?api_key=***&access_token=***&password=***", masked);
    }
}
