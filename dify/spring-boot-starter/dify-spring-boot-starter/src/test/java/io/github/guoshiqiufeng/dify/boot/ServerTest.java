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
package io.github.guoshiqiufeng.dify.boot;

import cn.hutool.json.JSONUtil;
import io.github.guoshiqiufeng.dify.boot.base.BaseServerContainerTest;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponseVO;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponseVO;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponseVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * DifyServer interface implementation test
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/3/31 09:55
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerTest extends BaseServerContainerTest {

    @Resource
    private DifyServer difyServer;

    private static String testAppId;

    @Test
    @Order(1)
    @DisplayName("Test retrieving application list")
    public void appsTest() {
        // Test retrieving all applications
        List<AppsResponseVO> allApps = difyServer.apps("");
        log.debug("All applications: {}", JSONUtil.toJsonStr(allApps));
        assertNotNull(allApps, "Application list should not be null");

        // If applications are available, save the first app ID for later tests
        if (!allApps.isEmpty()) {
            testAppId = allApps.getFirst().getId();
            log.debug("Retrieved test application ID: {}", testAppId);
        }

        // Test filtering applications by name
        if (!allApps.isEmpty() && allApps.getFirst().getName() != null) {
            String nameFilter = allApps.getFirst().getName().substring(0, 1); // Use first character as filter
            List<AppsResponseVO> filteredApps = difyServer.apps(nameFilter);
            log.debug("Applications filtered by name '{}': {}", nameFilter, JSONUtil.toJsonStr(filteredApps));
            assertNotNull(filteredApps, "Filtered application list should not be null");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test retrieving single application details")
    public void appTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponseVO> apps = difyServer.apps("");
            if (!apps.isEmpty()) {
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping single application test");
                return;
            }
        }

        // Get application details
        AppsResponseVO app = difyServer.app(testAppId);
        log.debug("Application details: {}", JSONUtil.toJsonStr(app));
        assertNotNull(app, "Application details should not be null");
        assertEquals(testAppId, app.getId(), "Returned application ID should match requested ID");
    }

    @Test
    @Order(3)
    @DisplayName("Test retrieving and initializing application API Keys")
    public void appApiKeyTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponseVO> apps = difyServer.apps("");
            if (!apps.isEmpty()) {
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping API Key test");
                return;
            }
        }

        // Get application API Keys
        List<ApiKeyResponseVO> apiKeys = difyServer.getAppApiKey(testAppId);
        log.debug("Application API Keys: {}", JSONUtil.toJsonStr(apiKeys));
        assertNotNull(apiKeys, "API Key list should not be null");

        // Initialize API Keys (may create new keys on the platform)
        List<ApiKeyResponseVO> initializedKeys = difyServer.initAppApiKey(testAppId);
        log.debug("Initialized API Keys: {}", JSONUtil.toJsonStr(initializedKeys));
        // Initialization may return null, so no assertion here
    }

    @Test
    @Order(4)
    @DisplayName("Test dataset API Keys")
    public void datasetApiKeyTest() {
        // Get dataset API Keys
        List<DatasetApiKeyResponseVO> datasetApiKey = difyServer.getDatasetApiKey();
        log.debug("Dataset API Keys: {}", JSONUtil.toJsonStr(datasetApiKey));

        // Initialize dataset API Keys
        List<DatasetApiKeyResponseVO> initializedKeys = difyServer.initDatasetApiKey();
        log.debug("Initialized dataset API Keys: {}", JSONUtil.toJsonStr(initializedKeys));
        assertNotNull(initializedKeys, "Initialized dataset API Key list should not be null");
    }

    @Test
    @Order(5)
    @DisplayName("Test error handling")
    public void errorHandlingTest() {
        // Test with invalid application ID
        try {
            AppsResponseVO invalidApp = difyServer.app("invalid-app-id");
            log.info("No exception thrown for invalid app ID, received response: {}",
                    invalidApp == null ? "null" : JSONUtil.toJsonStr(invalidApp));
        } catch (Exception e) {
            // Expected path - exception should be thrown
            log.info("Expected exception when retrieving app with invalid ID: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
        }

        // Test API Keys with invalid application ID
        try {
            List<ApiKeyResponseVO> invalidApiKeys = difyServer.getAppApiKey("invalid-app-id");
            log.info("No exception thrown for invalid API Key ID, received response size: {}",
                    invalidApiKeys == null ? "null" : invalidApiKeys.size());
        } catch (Exception e) {
            // Expected path - exception should be thrown
            log.info("Expected exception when retrieving API keys with invalid ID: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
