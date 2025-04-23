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
package io.github.guoshiqiufeng.dify.server.impl;

import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponseVO;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponseVO;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyServerClientImpl}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 15:02
 */
class DifyServerClientImplTest {


    private DifyServerClient difyServerClient;

    private DifyServerClientImpl difyServerClientImpl;

    @BeforeEach
    void setUp() {
        difyServerClient = mock(DifyServerClient.class);
        difyServerClientImpl = new DifyServerClientImpl(difyServerClient);
    }

    @Test
    void testApps() {
        // Arrange
        String mode = "chat";
        String name = "test-app";

        AppsResponseVO app1 = new AppsResponseVO();
        app1.setId("app-123");
        app1.setName("Test App 1");

        AppsResponseVO app2 = new AppsResponseVO();
        app2.setId("app-456");
        app2.setName("Test App 2");

        List<AppsResponseVO> expectedApps = Arrays.asList(app1, app2);

        when(difyServerClient.apps(mode, name)).thenReturn(expectedApps);

        // Act
        List<AppsResponseVO> actualApps = difyServerClientImpl.apps(mode, name);

        // Assert
        assertNotNull(actualApps);
        assertEquals(expectedApps.size(), actualApps.size());
        assertEquals(expectedApps.get(0).getId(), actualApps.get(0).getId());
        assertEquals(expectedApps.get(1).getName(), actualApps.get(1).getName());
        verify(difyServerClient, times(1)).apps(mode, name);
    }

    @Test
    void testApp() {
        // Arrange
        String appId = "app-123";

        AppsResponseVO expectedApp = new AppsResponseVO();
        expectedApp.setId(appId);
        expectedApp.setName("Test App");

        when(difyServerClient.app(appId)).thenReturn(expectedApp);

        // Act
        AppsResponseVO actualApp = difyServerClientImpl.app(appId);

        // Assert
        assertNotNull(actualApp);
        assertEquals(expectedApp.getId(), actualApp.getId());
        assertEquals(expectedApp.getName(), actualApp.getName());
        verify(difyServerClient, times(1)).app(appId);
    }

    @Test
    void testGetAppApiKey() {
        // Arrange
        String appId = "app-123";

        ApiKeyResponseVO apiKey1 = new ApiKeyResponseVO();
        apiKey1.setId("key-123");
        apiKey1.setToken("test-api-key-1");

        ApiKeyResponseVO apiKey2 = new ApiKeyResponseVO();
        apiKey2.setId("key-456");
        apiKey2.setToken("test-api-key-2");

        List<ApiKeyResponseVO> expectedApiKeys = Arrays.asList(apiKey1, apiKey2);

        when(difyServerClient.getAppApiKey(appId)).thenReturn(expectedApiKeys);

        // Act
        List<ApiKeyResponseVO> actualApiKeys = difyServerClientImpl.getAppApiKey(appId);

        // Assert
        assertNotNull(actualApiKeys);
        assertEquals(expectedApiKeys.size(), actualApiKeys.size());
        assertEquals(expectedApiKeys.get(0).getId(), actualApiKeys.get(0).getId());
        assertEquals(expectedApiKeys.get(1).getToken(), actualApiKeys.get(1).getToken());
        verify(difyServerClient, times(1)).getAppApiKey(appId);
    }

    @Test
    void testInitAppApiKey() {
        // Arrange
        String appId = "app-123";

        ApiKeyResponseVO apiKey = new ApiKeyResponseVO();
        apiKey.setId("key-123");
        apiKey.setToken("new-api-key");

        List<ApiKeyResponseVO> expectedApiKeys = List.of(apiKey);

        when(difyServerClient.initAppApiKey(appId)).thenReturn(expectedApiKeys);

        // Act
        List<ApiKeyResponseVO> actualApiKeys = difyServerClientImpl.initAppApiKey(appId);

        // Assert
        assertNotNull(actualApiKeys);
        assertEquals(expectedApiKeys.size(), actualApiKeys.size());
        assertEquals(expectedApiKeys.get(0).getId(), actualApiKeys.get(0).getId());
        assertEquals(expectedApiKeys.get(0).getToken(), actualApiKeys.get(0).getToken());
        verify(difyServerClient, times(1)).initAppApiKey(appId);
    }

    @Test
    void testGetDatasetApiKey() {
        // Arrange
        DatasetApiKeyResponseVO datasetApiKey1 = new DatasetApiKeyResponseVO();
        datasetApiKey1.setId("dkey-123");
        datasetApiKey1.setToken("dataset-api-key-1");

        DatasetApiKeyResponseVO datasetApiKey2 = new DatasetApiKeyResponseVO();
        datasetApiKey2.setId("dkey-456");
        datasetApiKey2.setToken("dataset-api-key-2");

        List<DatasetApiKeyResponseVO> expectedDatasetApiKeys = Arrays.asList(datasetApiKey1, datasetApiKey2);

        when(difyServerClient.getDatasetApiKey()).thenReturn(expectedDatasetApiKeys);

        // Act
        List<DatasetApiKeyResponseVO> actualDatasetApiKeys = difyServerClientImpl.getDatasetApiKey();

        // Assert
        assertNotNull(actualDatasetApiKeys);
        assertEquals(expectedDatasetApiKeys.size(), actualDatasetApiKeys.size());
        assertEquals(expectedDatasetApiKeys.get(0).getId(), actualDatasetApiKeys.get(0).getId());
        assertEquals(expectedDatasetApiKeys.get(1).getToken(), actualDatasetApiKeys.get(1).getToken());
        verify(difyServerClient, times(1)).getDatasetApiKey();
    }

    @Test
    void testInitDatasetApiKey() {
        // Arrange
        DatasetApiKeyResponseVO datasetApiKey = new DatasetApiKeyResponseVO();
        datasetApiKey.setId("dkey-123");
        datasetApiKey.setToken("new-dataset-api-key");

        List<DatasetApiKeyResponseVO> expectedDatasetApiKeys = List.of(datasetApiKey);

        when(difyServerClient.initDatasetApiKey()).thenReturn(expectedDatasetApiKeys);

        // Act
        List<DatasetApiKeyResponseVO> actualDatasetApiKeys = difyServerClientImpl.initDatasetApiKey();

        // Assert
        assertNotNull(actualDatasetApiKeys);
        assertEquals(expectedDatasetApiKeys.size(), actualDatasetApiKeys.size());
        assertEquals(expectedDatasetApiKeys.get(0).getId(), actualDatasetApiKeys.get(0).getId());
        assertEquals(expectedDatasetApiKeys.get(0).getToken(), actualDatasetApiKeys.get(0).getToken());
        verify(difyServerClient, times(1)).initDatasetApiKey();
    }
}
