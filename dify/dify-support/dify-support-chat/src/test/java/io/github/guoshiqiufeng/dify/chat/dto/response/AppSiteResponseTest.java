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
package io.github.guoshiqiufeng.dify.chat.dto.response;

import io.github.guoshiqiufeng.dify.chat.enums.IconTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AppSiteResponse}
 *
 * @author yanghq
 * @version 1.1.0
 * @since 2024/5/30 14:03
 */
public class AppSiteResponseTest {

    @Test
    public void testAppSiteResponse() {
        // Create an instance with all fields set
        AppSiteResponse response = new AppSiteResponse();
        response.setTitle("Test App");
        response.setChatColorTheme("#FF5733");
        response.setChatColorThemeInverted(true);
        response.setIconType(IconTypeEnum.emoji);
        response.setIcon("🤖");
        response.setIconBackground("#FFFFFF");
        response.setIconUrl("https://example.com/icon.png");
        response.setDescription("Test description");
        response.setCopyright("© 2024 Test Company");
        response.setPrivacyPolicy("https://example.com/privacy");
        response.setCustomDisclaimer("Test disclaimer");
        response.setDefaultLanguage("en-US");
        response.setShowWorkflowSteps(true);
        response.setUseIconAsAnswerIcon(false);

        // Verify all getters return the expected values
        assertEquals("Test App", response.getTitle());
        assertEquals("#FF5733", response.getChatColorTheme());
        assertTrue(response.getChatColorThemeInverted());
        assertEquals(IconTypeEnum.emoji, response.getIconType());
        assertEquals("🤖", response.getIcon());
        assertEquals("#FFFFFF", response.getIconBackground());
        assertEquals("https://example.com/icon.png", response.getIconUrl());
        assertEquals("Test description", response.getDescription());
        assertEquals("© 2024 Test Company", response.getCopyright());
        assertEquals("https://example.com/privacy", response.getPrivacyPolicy());
        assertEquals("Test disclaimer", response.getCustomDisclaimer());
        assertEquals("en-US", response.getDefaultLanguage());
        assertTrue(response.getShowWorkflowSteps());
        assertFalse(response.getUseIconAsAnswerIcon());
    }

    @Test
    public void testAppSiteResponseEmptyConstructor() {
        // Create an empty instance
        AppSiteResponse response = new AppSiteResponse();

        // Verify all fields are null or default values
        assertNull(response.getTitle());
        assertNull(response.getChatColorTheme());
        assertNull(response.getChatColorThemeInverted());
        assertNull(response.getIconType());
        assertNull(response.getIcon());
        assertNull(response.getIconBackground());
        assertNull(response.getIconUrl());
        assertNull(response.getDescription());
        assertNull(response.getCopyright());
        assertNull(response.getPrivacyPolicy());
        assertNull(response.getCustomDisclaimer());
        assertNull(response.getDefaultLanguage());
        assertNull(response.getShowWorkflowSteps());
        assertNull(response.getUseIconAsAnswerIcon());
    }
}