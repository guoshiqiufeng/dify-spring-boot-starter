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
package io.github.guoshiqiufeng.dify.client.spring5.server;

import io.github.guoshiqiufeng.dify.client.spring5.BaseClientTest;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.constant.ServerUriConstant;
import io.github.guoshiqiufeng.dify.server.dto.request.DifyLoginRequestVO;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyServerDefaultClient}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/25 09:40
 */
@SuppressWarnings("unchecked")
public class DifyServerDefaultClientTest extends BaseClientTest {

    private DifyServerDefaultClient difyServerDefaultClient;

    @BeforeEach
    public void setup() {
        super.setup();
        // Create real client with mocked WebClient
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        DifyProperties.Server serverConfig = new DifyProperties.Server();
        serverConfig.setEmail("test@example.com");
        serverConfig.setPassword("password123");
        difyServerDefaultClient = new DifyServerDefaultClient(serverConfig, new DifyServerTokenDefault(),
                "https://api.dify.ai", clientConfig, webClientBuilderMock);
    }

    @Test
    public void testLogin() {
        // Prepare test data
        String email = "test@example.com";
        String password = "password123";

        // Create expected response
        LoginResultResponseVO resultResponseVO = new LoginResultResponseVO();
        resultResponseVO.setResult(DifyResult.SUCCESS);

        LoginResponseVO responseVO = new LoginResponseVO();
        responseVO.setAccessToken("test-access-token");
        responseVO.setRefreshToken("test-refresh-token");
        resultResponseVO.setData(responseVO);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(LoginResultResponseVO.class)).thenReturn(Mono.just(resultResponseVO));

        // Execute the method
        LoginResponseVO actualResponse = difyServerDefaultClient.login();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(responseVO.getAccessToken(), actualResponse.getAccessToken());
        assertEquals(responseVO.getRefreshToken(), actualResponse.getRefreshToken());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ServerUriConstant.LOGIN);
        verify(requestBodySpecMock).bodyValue(any(DifyLoginRequestVO.class));
        verify(responseSpecMock).bodyToMono(LoginResultResponseVO.class);
    }

    @Test
    public void testRefreshToken() {
        // Prepare test data
        String refreshToken = "test-refresh-token";

        // Create expected response
        LoginResultResponseVO resultResponseVO = new LoginResultResponseVO();
        resultResponseVO.setResult(DifyResult.SUCCESS);

        LoginResponseVO responseVO = new LoginResponseVO();
        responseVO.setAccessToken("new-access-token");
        responseVO.setRefreshToken("new-refresh-token");
        resultResponseVO.setData(responseVO);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(LoginResultResponseVO.class)).thenReturn(Mono.just(resultResponseVO));

        // Execute the method
        LoginResponseVO actualResponse = difyServerDefaultClient.refreshToken(refreshToken);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(responseVO.getAccessToken(), actualResponse.getAccessToken());
        assertEquals(responseVO.getRefreshToken(), actualResponse.getRefreshToken());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ServerUriConstant.REFRESH_TOKEN);
        verify(requestBodySpecMock).bodyValue(anyMap());
        verify(responseSpecMock).bodyToMono(LoginResultResponseVO.class);
    }

    @Test
    public void testApp() {
        // Prepare test data
        String appId = "app-123456";

        // Create expected response
        AppsResponseVO expectedResponse = new AppsResponseVO();
        expectedResponse.setId(appId);
        expectedResponse.setName("Test App");
        expectedResponse.setIcon("app-icon-url");
        expectedResponse.setMode("completion");

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(AppsResponseVO.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        AppsResponseVO actualResponse = difyServerDefaultClient.app(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getIcon(), actualResponse.getIcon());
        assertEquals(expectedResponse.getMode(), actualResponse.getMode());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}"), eq(appId));
        verify(responseSpecMock).bodyToMono(AppsResponseVO.class);
    }

    @Test
    public void testApps() {
        // Prepare test data
        String mode = "completion";
        String name = "Test";

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("mode"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("name"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create expected response for first page
        AppsResponseResultVO resultResponseVO = new AppsResponseResultVO();
        List<AppsResponseVO> appsPage1 = new ArrayList<>();

        AppsResponseVO app1 = new AppsResponseVO();
        app1.setId("app-123456");
        app1.setName("Test App 1");
        app1.setMode(mode);
        appsPage1.add(app1);

        AppsResponseVO app2 = new AppsResponseVO();
        app2.setId("app-789012");
        app2.setName("Test App 2");
        app2.setMode(mode);
        appsPage1.add(app2);

        resultResponseVO.setData(appsPage1);
        resultResponseVO.setHasMore(false); // Only one page to keep the test simple

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(AppsResponseResultVO.class)).thenReturn(Mono.just(resultResponseVO));

        // Execute the method
        List<AppsResponseVO> actualResponse = difyServerDefaultClient.apps(mode, name);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.size());
        assertEquals(app1.getId(), actualResponse.get(0).getId());
        assertEquals(app1.getName(), actualResponse.get(0).getName());
        assertEquals(app2.getId(), actualResponse.get(1).getId());
        assertEquals(app2.getName(), actualResponse.get(1).getName());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(responseSpecMock).bodyToMono(AppsResponseResultVO.class);
    }

    @Test
    public void testGetAppApiKey() {
        // Prepare test data
        String appId = "app-123456";

        // Create expected response
        ApiKeyResultResponseVO resultResponseVO = new ApiKeyResultResponseVO();
        List<ApiKeyResponseVO> apiKeys = new ArrayList<>();

        ApiKeyResponseVO apiKey = new ApiKeyResponseVO();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setType("api");
        apiKeys.add(apiKey);

        resultResponseVO.setData(apiKeys);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(ApiKeyResultResponseVO.class)).thenReturn(Mono.just(resultResponseVO));

        // Execute the method
        List<ApiKeyResponseVO> actualResponse = difyServerDefaultClient.getAppApiKey(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getType(), actualResponse.get(0).getType());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(responseSpecMock).bodyToMono(ApiKeyResultResponseVO.class);
    }

    @Test
    public void testInitAppApiKey() {
        // Prepare test data
        String appId = "app-123456";

        // Create expected response
        ApiKeyResponseVO apiKey = new ApiKeyResponseVO();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setType("api");

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(ApiKeyResponseVO.class)).thenReturn(Mono.just(apiKey));

        // Execute the method
        List<ApiKeyResponseVO> actualResponse = difyServerDefaultClient.initAppApiKey(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getType(), actualResponse.get(0).getType());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(requestBodySpecMock).headers(any());
        verify(responseSpecMock).bodyToMono(ApiKeyResponseVO.class);
    }

    @Test
    public void testGetDatasetApiKey() {
        // Create expected response
        DatasetApiKeyResultVO resultResponseVO = new DatasetApiKeyResultVO();
        List<DatasetApiKeyResponseVO> apiKeys = new ArrayList<>();

        DatasetApiKeyResponseVO apiKey = new DatasetApiKeyResponseVO();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setCreatedAt(1745546400000L);
        apiKeys.add(apiKey);

        resultResponseVO.setData(apiKeys);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DatasetApiKeyResultVO.class)).thenReturn(Mono.just(resultResponseVO));

        // Execute the method
        List<DatasetApiKeyResponseVO> actualResponse = difyServerDefaultClient.getDatasetApiKey();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getCreatedAt(), actualResponse.get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(responseSpecMock).bodyToMono(DatasetApiKeyResultVO.class);
    }

    @Test
    public void testInitDatasetApiKey() {
        // Create expected response
        DatasetApiKeyResponseVO apiKey = new DatasetApiKeyResponseVO();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setCreatedAt(1745546400000L);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DatasetApiKeyResponseVO.class)).thenReturn(Mono.just(apiKey));

        // Execute the method
        List<DatasetApiKeyResponseVO> actualResponse = difyServerDefaultClient.initDatasetApiKey();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getCreatedAt(), actualResponse.get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(requestBodySpecMock).headers(any());
        verify(responseSpecMock).bodyToMono(DatasetApiKeyResponseVO.class);
    }
}
