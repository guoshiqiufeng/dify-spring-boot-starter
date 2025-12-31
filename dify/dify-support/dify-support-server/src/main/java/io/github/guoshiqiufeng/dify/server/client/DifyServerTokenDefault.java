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

import io.github.guoshiqiufeng.dify.server.dto.response.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/9 13:29
 */
@Slf4j
public class DifyServerTokenDefault extends BaseDifyServerToken {

    private String accessToken;

    private String refreshToken;

    private String csrfToken;

    private final ReentrantLock tokenLock = new ReentrantLock();

    @Override
    public void addAuthorizationHeader(HttpHeaders headers, DifyServerClient difyServerClient) {
        if (accessToken == null) {
            obtainToken(difyServerClient);
        }
        headers.setBearerAuth(accessToken);
        headers.add("x-csrf-token", csrfToken);
    }

    @Override
    public void addAuthorizationCookies(MultiValueMap<String, String> cookies, DifyServerClient difyServerClient) {
        if (accessToken == null) {
            obtainToken(difyServerClient);
        }
        cookies.add("access_token", accessToken);
        cookies.add("csrf_token", csrfToken);
        // support Https
        cookies.add("__Host-access_token", accessToken);
        cookies.add("__Host-csrf_token", csrfToken);
    }

    private void obtainToken(DifyServerClient difyServerClient) {
        tokenLock.lock();
        try {
            if (accessToken == null) {
                LoginResponse loginResponse = difyServerClient.login();
                if (loginResponse != null) {
                    this.accessToken = loginResponse.getAccessToken();
                    this.refreshToken = loginResponse.getRefreshToken();
                    this.csrfToken = loginResponse.getCsrfToken();
                }
            }
        } finally {
            tokenLock.unlock();
        }
    }

    /**
     * refresh or obtain new token
     */
    @Override
    public void refreshOrObtainNewToken(DifyServerClient difyServerClient) {
        tokenLock.lock();
        try {
            if (refreshToken != null) {
                try {
                    LoginResponse response = difyServerClient.refreshToken(refreshToken);
                    if (response != null) {
                        this.accessToken = response.getAccessToken();
                        this.refreshToken = response.getRefreshToken();
                        this.csrfToken = response.getCsrfToken();
                        return;
                    }
                } catch (Exception e) {
                    log.warn("Failed to refresh token:{}, will attempt to login", e.getMessage());
                }
            }
            // 如果刷新token失败或没有刷新token，则重新登录
            LoginResponse loginResponse = difyServerClient.login();
            if (loginResponse != null) {
                this.accessToken = loginResponse.getAccessToken();
                this.refreshToken = loginResponse.getRefreshToken();
                this.csrfToken = loginResponse.getCsrfToken();
            }
        } finally {
            tokenLock.unlock();
        }
    }

}
