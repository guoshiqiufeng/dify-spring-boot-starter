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
package io.github.guoshiqiufeng.dify.chat.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.chat.enums.IconTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JSON serialization/deserialization tests for {@link AppSiteResponse}
 *
 * @author yanghq
 * @version 1.1.0
 * @since 2024/5/30 14:30
 */
public class AppSiteResponseJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testDeserializeAppSiteResponse() throws Exception {
        // JSON with snake_case field names as used in the API
        String json = "{\n" +
                "  \"title\": \"Test App\",\n" +
                "  \"chat_color_theme\": \"#FF5733\",\n" +
                "  \"chat_color_theme_inverted\": true,\n" +
                "  \"icon_type\": \"emoji\",\n" +
                "  \"icon\": \"🤖\",\n" +
                "  \"icon_background\": \"#FFFFFF\",\n" +
                "  \"icon_url\": \"https://example.com/icon.png\",\n" +
                "  \"description\": \"Test description\",\n" +
                "  \"copyright\": \"© 2024 Test Company\",\n" +
                "  \"privacy_policy\": \"https://example.com/privacy\",\n" +
                "  \"custom_disclaimer\": \"Test disclaimer\",\n" +
                "  \"default_language\": \"en-US\",\n" +
                "  \"show_workflow_steps\": true,\n" +
                "  \"use_icon_as_answer_icon\": false\n" +
                "}";

        // Deserialize JSON to object
        AppSiteResponse response = objectMapper.readValue(json, AppSiteResponse.class);

        // Verify fields are correctly mapped
        assertNotNull(response);
        assertEquals("Test App", response.getTitle());
        assertEquals("#FF5733", response.getChatColorTheme());
        assertEquals(true, response.getChatColorThemeInverted());
        assertEquals(IconTypeEnum.emoji, response.getIconType());
        assertEquals("🤖", response.getIcon());
        assertEquals("#FFFFFF", response.getIconBackground());
        assertEquals("https://example.com/icon.png", response.getIconUrl());
        assertEquals("Test description", response.getDescription());
        assertEquals("© 2024 Test Company", response.getCopyright());
        assertEquals("https://example.com/privacy", response.getPrivacyPolicy());
        assertEquals("Test disclaimer", response.getCustomDisclaimer());
        assertEquals("en-US", response.getDefaultLanguage());
        assertEquals(true, response.getShowWorkflowSteps());
        assertEquals(false, response.getUseIconAsAnswerIcon());
    }

    @Test
    public void testDeserializeWithImageIconType() throws Exception {
        // JSON with icon_type set to "image"
        String json = "{\n" +
                "  \"title\": \"Test App\",\n" +
                "  \"icon_type\": \"image\",\n" +
                "  \"icon\": \"https://example.com/image.png\"\n" +
                "}";

        // Deserialize JSON to object
        AppSiteResponse response = objectMapper.readValue(json, AppSiteResponse.class);

        // Verify icon_type is correctly mapped to the enum
        assertNotNull(response);
        assertEquals("Test App", response.getTitle());
        assertEquals(IconTypeEnum.image, response.getIconType());
        assertEquals("https://example.com/image.png", response.getIcon());
    }
}