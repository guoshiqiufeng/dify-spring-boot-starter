package io.github.guoshiqiufeng.dify.server.client;

import io.github.guoshiqiufeng.dify.core.client.BaseDifyClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.server.constant.ServerUriConstant;
import io.github.guoshiqiufeng.dify.server.dto.request.DifyLoginRequestVO;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/9 10:14
 */
@Slf4j
public class DifyServerClient extends BaseDifyClient {

    private final DifyProperties.Server difyServerProperties;

    private DifyServerToken difyServerToken;

    public DifyServerClient(DifyProperties.Server difyServerProperties) {
        this(difyServerProperties, new DifyServerTokenDefault());
    }

    public DifyServerClient(DifyProperties.Server difyServerProperties, DifyServerToken difyServerToken) {
        super();
        this.difyServerProperties = difyServerProperties;
        this.difyServerToken = difyServerToken;
    }

    public DifyServerClient(DifyProperties.Server difyServerProperties, DifyServerToken difyServerToken, String baseUrl) {
        super(baseUrl);
        this.difyServerProperties = difyServerProperties;
        this.difyServerToken = difyServerToken;
    }

    public DifyServerClient(DifyProperties.Server difyServerProperties, DifyServerToken difyServerToken, String baseUrl, RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
        super(baseUrl, restClientBuilder, webClientBuilder);
        this.difyServerProperties = difyServerProperties;
        this.difyServerToken = difyServerToken;
    }

    public List<AppsResponseVO> apps(String name) {
        List<AppsResponseVO> result = new ArrayList<>();
        appPages(name, 1, result);
        return result;
    }

    public AppsResponseVO app(String appId) {
        return executeWithRetry(
                () -> restClient.get()
                        .uri(ServerUriConstant.APPS + "/{appId}", appId)
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(AppsResponseVO.class)
        );
    }

    public List<ApiKeyResponseVO> getAppApiKey(String appId) {
        ApiKeyResultResponseVO tmp = executeWithRetry(
                () -> restClient.get()
                        .uri(ServerUriConstant.APPS + "/{appId}/api-keys", appId)
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(ApiKeyResultResponseVO.class)
        );
        if (tmp == null) {
            return new ArrayList<>();
        }
        return tmp.getData();
    }

    public List<ApiKeyResponseVO> initAppApiKey(String appId) {
        ApiKeyResponseVO apiKeyResponseVO = executeWithRetry(
                () -> restClient.post()
                        .uri(ServerUriConstant.APPS + "/{appId}/api-keys", appId)
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(ApiKeyResponseVO.class)
        );
        return apiKeyResponseVO != null ? new ArrayList<>(List.of(apiKeyResponseVO)) : null;
    }

    public List<DatasetApiKeyResponseVO> getDatasetApiKey() {
        DatasetApiKeyResultVO result = executeWithRetry(
                () -> restClient.get()
                        .uri(ServerUriConstant.DATASETS + "/api-keys")
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(DatasetApiKeyResultVO.class)
        );
        return result != null ? result.getData() : null;
    }

    public List<DatasetApiKeyResponseVO> initDatasetApiKey() {
        DatasetApiKeyResponseVO result = executeWithRetry(
                () -> restClient.post()
                        .uri(ServerUriConstant.DATASETS + "/api-keys")
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(DatasetApiKeyResponseVO.class)
        );
        return result != null ? new ArrayList<>(List.of(result)) : null;
    }

    private void appPages(String name, int page, List<AppsResponseVO> result) {
        AppsResponseResultVO tmp = executeWithRetry(
                () -> restClient.get()
                        .uri(ServerUriConstant.APPS + "?name={name}&page={page}&limit=100", name, page)
                        .headers(this::addAuthorizationHeader)
                        .retrieve()
                        .onStatus(responseErrorHandler)
                        .body(AppsResponseResultVO.class)
        );

        if (tmp == null) {
            return;
        }
        result.addAll(tmp.getData());
        if (tmp.getHasMore()) {
            appPages(name, page + 1, result);
        }
    }

    private void addAuthorizationHeader(HttpHeaders headers) {
        difyServerToken.addAuthorizationHeader(headers, this);
    }

    private <T> T executeWithRetry(RequestSupplier<T> supplier) {
        return difyServerToken.executeWithRetry(supplier, this);
    }

    LoginResponseVO login() {
        Assert.notNull(difyServerProperties, "The difyServerProperties can not be null.");
        DifyLoginRequestVO requestVO = DifyLoginRequestVO.build(difyServerProperties.getEmail(), difyServerProperties.getPassword());
        LoginResultResponseVO result = restClient.post()
                .uri(ServerUriConstant.LOGIN)
                .body(requestVO)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(LoginResultResponseVO.class);
        if (result != null && DifyResult.SUCCESS.equals(result.getResult())) {
            return result.getData();
        }
        return null;
    }

    LoginResponseVO refreshToken(String refreshToken) {
        Map<String, String> requestVO = Map.of("refresh_token", refreshToken);
        LoginResultResponseVO result = restClient.post()
                .uri(ServerUriConstant.REFRESH_TOKEN)
                .body(requestVO)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(LoginResultResponseVO.class);

        if (result != null && DifyResult.SUCCESS.equals(result.getResult())) {
            return result.getData();
        }
        return null;
    }


}
