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
package io.github.guoshiqiufeng.dify.server.dto.response;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AppsResponse}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class AppsResponseTest {

    @Test
    public void testDefaultConstructor() {
        // Act
        AppsResponse vo = new AppsResponse();

        // Assert
        assertNull(vo.getId());
        assertNull(vo.getName());
        assertNull(vo.getMaxActiveRequests());
        assertNull(vo.getDescription());
        assertNull(vo.getMode());
        assertNull(vo.getIconType());
        assertNull(vo.getIcon());
        assertNull(vo.getIconBackground());
        assertNull(vo.getIconUrl());
        assertNull(vo.getEnableSite());
        assertNull(vo.getEnableApi());
        assertNull(vo.getModelConfig());
        assertNull(vo.getWorkflow());
        assertNull(vo.getSite());
        assertNull(vo.getApiBaseUrl());
        assertNull(vo.getUseIconAsAnswerIcon());
        assertNull(vo.getCreatedBy());
        assertNull(vo.getCreatedAt());
        assertNull(vo.getUpdatedBy());
        assertNull(vo.getUpdatedAt());
        assertNull(vo.getTags());
        assertNull(vo.getDeletedTools());
    }

    @Test
    public void testSettersAndGetters() {
        // Arrange
        String id = "app-123";
        String name = "Test App";
        Integer maxActiveRequests = 5;
        String description = "Test Description";
        String mode = "completion";
        String iconType = "emoji";
        String icon = "🚀";
        String iconBackground = "#FFFFFF";
        String iconUrl = "https://example.com/icon.png";
        Boolean enableSite = true;
        Boolean enableApi = true;
        Map<String, Object> modelConfig = new HashMap<>();
        modelConfig.put("testKey", "testValue");
        Map<String, Object> workflow = new HashMap<>();
        workflow.put("workflowKey", "workflowValue");
        Map<String, Object> site = new HashMap<>();
        site.put("siteKey", "siteValue");
        String apiBaseUrl = "https://api.example.com";
        Boolean useIconAsAnswerIcon = false;
        String createdBy = "user-123";
        Long createdAt = 1619712000L;
        String updatedBy = "user-456";
        Long updatedAt = 1619798400L;
        List<String> tags = Arrays.asList("tag1", "tag2");
        List<String> deletedTools = Collections.singletonList("tool1");

        // Act
        AppsResponse vo = new AppsResponse();
        vo.setId(id);
        vo.setName(name);
        vo.setMaxActiveRequests(maxActiveRequests);
        vo.setDescription(description);
        vo.setMode(mode);
        vo.setIconType(iconType);
        vo.setIcon(icon);
        vo.setIconBackground(iconBackground);
        vo.setIconUrl(iconUrl);
        vo.setEnableSite(enableSite);
        vo.setEnableApi(enableApi);
        vo.setModelConfig(modelConfig);
        vo.setWorkflow(workflow);
        vo.setSite(site);
        vo.setApiBaseUrl(apiBaseUrl);
        vo.setUseIconAsAnswerIcon(useIconAsAnswerIcon);
        vo.setCreatedBy(createdBy);
        vo.setCreatedAt(createdAt);
        vo.setUpdatedBy(updatedBy);
        vo.setUpdatedAt(updatedAt);
        vo.setTags(tags);
        vo.setDeletedTools(deletedTools);

        // Assert
        assertEquals(id, vo.getId());
        assertEquals(name, vo.getName());
        assertEquals(maxActiveRequests, vo.getMaxActiveRequests());
        assertEquals(description, vo.getDescription());
        assertEquals(mode, vo.getMode());
        assertEquals(iconType, vo.getIconType());
        assertEquals(icon, vo.getIcon());
        assertEquals(iconBackground, vo.getIconBackground());
        assertEquals(iconUrl, vo.getIconUrl());
        assertEquals(enableSite, vo.getEnableSite());
        assertEquals(enableApi, vo.getEnableApi());
        assertEquals(modelConfig, vo.getModelConfig());
        assertEquals(workflow, vo.getWorkflow());
        assertEquals(site, vo.getSite());
        assertEquals(apiBaseUrl, vo.getApiBaseUrl());
        assertEquals(useIconAsAnswerIcon, vo.getUseIconAsAnswerIcon());
        assertEquals(createdBy, vo.getCreatedBy());
        assertEquals(createdAt, vo.getCreatedAt());
        assertEquals(updatedBy, vo.getUpdatedBy());
        assertEquals(updatedAt, vo.getUpdatedAt());
        assertEquals(tags, vo.getTags());
        assertEquals(deletedTools, vo.getDeletedTools());
    }

    @Test
    public void testNestedModelConfigClass() {
        // Arrange
        AppsResponse.ModelConfig modelConfig = new AppsResponse.ModelConfig();
        AppsResponse.Model model = new AppsResponse.Model();
        String prePrompt = "You are a helpful assistant";
        String createdBy = "user-123";
        Long createdAt = 1619712000L;
        String updatedBy = "user-456";
        Long updatedAt = 1619798400L;

        // Act
        modelConfig.setModel(model);
        modelConfig.setPrePrompt(prePrompt);
        modelConfig.setCreatedBy(createdBy);
        modelConfig.setCreatedAt(createdAt);
        modelConfig.setUpdatedBy(updatedBy);
        modelConfig.setUpdatedAt(updatedAt);

        // Assert
        assertSame(model, modelConfig.getModel());
        assertEquals(prePrompt, modelConfig.getPrePrompt());
        assertEquals(createdBy, modelConfig.getCreatedBy());
        assertEquals(createdAt, modelConfig.getCreatedAt());
        assertEquals(updatedBy, modelConfig.getUpdatedBy());
        assertEquals(updatedAt, modelConfig.getUpdatedAt());
    }

    @Test
    public void testNestedModelClass() {
        // Arrange
        AppsResponse.Model model = new AppsResponse.Model();
        String provider = "openai";
        String name = "gpt-4";
        String mode = "chat";
        AppsResponse.CompletionParams completionParams = new AppsResponse.CompletionParams();

        // Act
        model.setProvider(provider);
        model.setName(name);
        model.setMode(mode);
        model.setCompletionParams(completionParams);

        // Assert
        assertEquals(provider, model.getProvider());
        assertEquals(name, model.getName());
        assertEquals(mode, model.getMode());
        assertSame(completionParams, model.getCompletionParams());
    }

    @Test
    public void testNestedCompletionParamsClass() {
        // Arrange
        AppsResponse.CompletionParams completionParams = new AppsResponse.CompletionParams();
        List<String> stop = Arrays.asList("###", "END");
        String format = "json";

        // Act
        completionParams.setStop(stop);
        completionParams.setFormat(format);

        // Assert
        assertEquals(stop, completionParams.getStop());
        assertEquals(format, completionParams.getFormat());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        AppsResponse vo1 = new AppsResponse();
        vo1.setId("app-123");
        vo1.setName("Test App");

        AppsResponse vo2 = new AppsResponse();
        vo2.setId("app-123");
        vo2.setName("Test App");

        AppsResponse vo3 = new AppsResponse();
        vo3.setId("app-456");
        vo3.setName("Different App");

        // Assert
        assertEquals(vo1, vo2);
        assertEquals(vo1.hashCode(), vo2.hashCode());
        assertNotEquals(vo1, vo3);
        assertNotEquals(vo1.hashCode(), vo3.hashCode());
    }

    @Test
    public void testNestedClassesEqualsAndHashCode() {
        // Arrange - Model
        AppsResponse.Model model1 = new AppsResponse.Model();
        model1.setProvider("openai");
        model1.setName("gpt-4");

        AppsResponse.Model model2 = new AppsResponse.Model();
        model2.setProvider("openai");
        model2.setName("gpt-4");

        AppsResponse.Model model3 = new AppsResponse.Model();
        model3.setProvider("anthropic");
        model3.setName("claude-v2");

        // Arrange - CompletionParams
        AppsResponse.CompletionParams params1 = new AppsResponse.CompletionParams();
        params1.setFormat("json");
        params1.setStop(Arrays.asList("###", "END"));

        AppsResponse.CompletionParams params2 = new AppsResponse.CompletionParams();
        params2.setFormat("json");
        params2.setStop(Arrays.asList("###", "END"));

        AppsResponse.CompletionParams params3 = new AppsResponse.CompletionParams();
        params3.setFormat("text");
        params3.setStop(Collections.singletonList("STOP"));

        // Assert - Model
        assertEquals(model1, model2);
        assertEquals(model1.hashCode(), model2.hashCode());
        assertNotEquals(model1, model3);
        assertNotEquals(model1.hashCode(), model3.hashCode());

        // Assert - CompletionParams
        assertEquals(params1, params2);
        assertEquals(params1.hashCode(), params2.hashCode());
        assertNotEquals(params1, params3);
        assertNotEquals(params1.hashCode(), params3.hashCode());
    }

    @Test
    public void testSerializable() {
        // Just verify that the class implements Serializable
        AppsResponse vo = new AppsResponse();
        assertInstanceOf(Serializable.class, vo);

        AppsResponse.ModelConfig modelConfig = new AppsResponse.ModelConfig();
        assertInstanceOf(Serializable.class, modelConfig);

        AppsResponse.Model model = new AppsResponse.Model();
        assertInstanceOf(Serializable.class, model);

        AppsResponse.CompletionParams completionParams = new AppsResponse.CompletionParams();
        assertInstanceOf(Serializable.class, completionParams);
    }
}
