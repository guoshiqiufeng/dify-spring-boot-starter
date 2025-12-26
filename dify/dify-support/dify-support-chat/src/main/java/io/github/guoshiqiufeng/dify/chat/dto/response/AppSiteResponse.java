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

import com.fasterxml.jackson.annotation.JsonAlias;
import io.github.guoshiqiufeng.dify.chat.enums.IconTypeEnum;
import lombok.Data;

/**
 * @author yanghq
 * @author yanghq
 * @version 1.1.0
 * @since 2025/5/30 14:03
 * /**
 * Response class for the site API endpoint that returns WebApp configuration settings.
 * Contains visual and functional settings for the Dify WebApp interface.
 * @since 2024/5/30 14:03
 */
@Data
public class AppSiteResponse {
    private String title;

    /**
     * Chat color theme, in hex format
     */
    @JsonAlias("chat_color_theme")
    private String chatColorTheme;

    /**
     * Whether the chat color theme is inverted
     */
    @JsonAlias("chat_color_theme_inverted")
    private Boolean chatColorThemeInverted;

    /**
     * Icon type, emoji - emoji, image - picture
     */
    @JsonAlias("icon_type")
    private IconTypeEnum iconType;

    /**
     * Icon. If it's emoji type, it's an emoji symbol; if it's image type, it's an image URL
     */
    private String icon;

    /**
     * Background color in hex format
     */
    @JsonAlias("icon_background")
    private String iconBackground;

    /**
     * Icon URL
     */
    @JsonAlias("icon_url")
    private String iconUrl;

    /**
     * Description
     */
    private String description;

    /**
     * Copyright information
     */
    private String copyright;

    /**
     * Privacy policy link
     */
    @JsonAlias("privacy_policy")
    private String privacyPolicy;

    /**
     * Custom disclaimer
     */
    @JsonAlias("custom_disclaimer")
    private String customDisclaimer;

    /**
     * Default language
     */
    @JsonAlias("default_language")
    private String defaultLanguage;

    /**
     * Whether to show workflow details
     */
    @JsonAlias("show_workflow_steps")
    private Boolean showWorkflowSteps;

    /**
     * Whether to replace 🤖 in chat with the WebApp icon
     */
    @JsonAlias("use_icon_as_answer_icon")
    private Boolean useIconAsAnswerIcon;

}
