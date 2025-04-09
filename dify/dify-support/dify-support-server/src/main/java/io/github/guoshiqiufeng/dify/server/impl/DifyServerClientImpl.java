package io.github.guoshiqiufeng.dify.server.impl;

import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponseVO;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponseVO;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponseVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/9 10:42
 */
@Slf4j
public class DifyServerClientImpl implements DifyServer {

    private final DifyServerClient difyServerClient;

    public DifyServerClientImpl(DifyServerClient difyServerClient) {
        this.difyServerClient = difyServerClient;
    }

    @Override
    public List<AppsResponseVO> apps(String name) {
        return difyServerClient.apps(name);
    }

    @Override
    public AppsResponseVO app(String appId) {
        return difyServerClient.app(appId);
    }

    @Override
    public List<ApiKeyResponseVO> getAppApiKey(String appId) {
        return difyServerClient.getAppApiKey(appId);
    }

    @Override
    public List<ApiKeyResponseVO> initAppApiKey(String appId) {
        return difyServerClient.initAppApiKey(appId);
    }

    @Override
    public List<DatasetApiKeyResponseVO> getDatasetApiKey() {
        return difyServerClient.getDatasetApiKey();
    }

    @Override
    public List<DatasetApiKeyResponseVO> initDatasetApiKey() {
        return difyServerClient.initDatasetApiKey();
    }
}
