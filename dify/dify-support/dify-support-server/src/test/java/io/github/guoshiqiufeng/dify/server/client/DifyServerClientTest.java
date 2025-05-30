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
package io.github.guoshiqiufeng.dify.server.client;

import io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponse;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponse;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponse;
import io.github.guoshiqiufeng.dify.server.dto.response.LoginResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyServerClient}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 15:15
 */
class DifyServerClientTest {

    private static DifyServerClient difyServerClient;

    @BeforeAll
    public static void setup() {
        difyServerClient = Mockito.mock(DifyServerClient.class);
    }

    @Test
    void testApps() {
        // Arrange
        String mode = "chat";
        String name = "test-app";

        AppsResponse app1 = new AppsResponse();
        app1.setId("app-123");
        app1.setName("Test App 1");

        AppsResponse app2 = new AppsResponse();
        app2.setId("app-456");
        app2.setName("Test App 2");

        List<AppsResponse> expectedApps = Arrays.asList(app1, app2);

        when(difyServerClient.apps(anyString(), anyString())).thenReturn(expectedApps);

        // Act
        List<AppsResponse> actualApps = difyServerClient.apps(mode, name);

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

        AppsResponse expectedApp = new AppsResponse();
        expectedApp.setId(appId);
        expectedApp.setName("Test App");

        when(difyServerClient.app(anyString())).thenReturn(expectedApp);

        // Act
        AppsResponse actualApp = difyServerClient.app(appId);

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

        ApiKeyResponse apiKey1 = new ApiKeyResponse();
        apiKey1.setId("key-123");
        apiKey1.setToken("test-api-key-1");

        ApiKeyResponse apiKey2 = new ApiKeyResponse();
        apiKey2.setId("key-456");
        apiKey2.setToken("test-api-key-2");

        List<ApiKeyResponse> expectedApiKeys = Arrays.asList(apiKey1, apiKey2);

        when(difyServerClient.getAppApiKey(anyString())).thenReturn(expectedApiKeys);

        // Act
        List<ApiKeyResponse> actualApiKeys = difyServerClient.getAppApiKey(appId);

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

        ApiKeyResponse apiKey = new ApiKeyResponse();
        apiKey.setId("key-123");
        apiKey.setToken("new-api-key");

        List<ApiKeyResponse> expectedApiKeys = List.of(apiKey);

        when(difyServerClient.initAppApiKey(anyString())).thenReturn(expectedApiKeys);

        // Act
        List<ApiKeyResponse> actualApiKeys = difyServerClient.initAppApiKey(appId);

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
        DatasetApiKeyResponse datasetApiKey1 = new DatasetApiKeyResponse();
        datasetApiKey1.setId("dkey-123");
        datasetApiKey1.setToken("dataset-api-key-1");

        DatasetApiKeyResponse datasetApiKey2 = new DatasetApiKeyResponse();
        datasetApiKey2.setId("dkey-456");
        datasetApiKey2.setToken("dataset-api-key-2");

        List<DatasetApiKeyResponse> expectedDatasetApiKeys = Arrays.asList(datasetApiKey1, datasetApiKey2);

        when(difyServerClient.getDatasetApiKey()).thenReturn(expectedDatasetApiKeys);

        // Act
        List<DatasetApiKeyResponse> actualDatasetApiKeys = difyServerClient.getDatasetApiKey();

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
        DatasetApiKeyResponse datasetApiKey = new DatasetApiKeyResponse();
        datasetApiKey.setId("dkey-123");
        datasetApiKey.setToken("new-dataset-api-key");

        List<DatasetApiKeyResponse> expectedDatasetApiKeys = List.of(datasetApiKey);

        when(difyServerClient.initDatasetApiKey()).thenReturn(expectedDatasetApiKeys);

        // Act
        List<DatasetApiKeyResponse> actualDatasetApiKeys = difyServerClient.initDatasetApiKey();

        // Assert
        assertNotNull(actualDatasetApiKeys);
        assertEquals(expectedDatasetApiKeys.size(), actualDatasetApiKeys.size());
        assertEquals(expectedDatasetApiKeys.get(0).getId(), actualDatasetApiKeys.get(0).getId());
        assertEquals(expectedDatasetApiKeys.get(0).getToken(), actualDatasetApiKeys.get(0).getToken());
        verify(difyServerClient, times(1)).initDatasetApiKey();
    }

    @Test
    void testLogin() {
        // Arrange
        LoginResponse expectedLogin = new LoginResponse();
        expectedLogin.setAccessToken("access-token-xyz");
        expectedLogin.setRefreshToken("refresh-token-xyz");

        when(difyServerClient.login()).thenReturn(expectedLogin);

        // Act
        LoginResponse actualLogin = difyServerClient.login();

        // Assert
        assertNotNull(actualLogin);
        assertEquals(expectedLogin.getAccessToken(), actualLogin.getAccessToken());
        assertEquals(expectedLogin.getRefreshToken(), actualLogin.getRefreshToken());
        verify(difyServerClient, times(1)).login();
    }

    @Test
    void testRefreshToken() {
        // Arrange
        String refreshToken = "refresh-token-abc";

        LoginResponse expectedLogin = new LoginResponse();
        expectedLogin.setAccessToken("new-access-token-xyz");
        expectedLogin.setRefreshToken("new-refresh-token-xyz");

        when(difyServerClient.refreshToken(anyString())).thenReturn(expectedLogin);

        // Act
        LoginResponse actualLogin = difyServerClient.refreshToken(refreshToken);

        // Assert
        assertNotNull(actualLogin);
        assertEquals(expectedLogin.getAccessToken(), actualLogin.getAccessToken());
        assertEquals(expectedLogin.getRefreshToken(), actualLogin.getRefreshToken());
        verify(difyServerClient, times(1)).refreshToken(refreshToken);
    }
}
