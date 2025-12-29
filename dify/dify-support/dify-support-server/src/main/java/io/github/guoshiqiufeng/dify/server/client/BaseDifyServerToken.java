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

import io.github.guoshiqiufeng.dify.core.exception.DifyClientException;
import io.github.guoshiqiufeng.dify.server.exception.DifyServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/9 13:28
 */
@Slf4j
public abstract class BaseDifyServerToken {

    /**
     * 最大重试次数
     */
    protected static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Abstract method to add authorization header to HTTP request.
     *
     * @param headers          HTTP headers to be populated with authorization information
     * @param difyServerClient client instance containing necessary configuration
     */
    public abstract void addAuthorizationHeader(HttpHeaders headers, DifyServerClient difyServerClient);

    public abstract void addAuthorizationCookies(MultiValueMap<String, String> cookies, DifyServerClient difyServerClient);

    /**
     * Executes the given request supplier with automatic retry mechanism.
     * Will retry when encountering 401 Unauthorized errors, up to MAX_RETRY_ATTEMPTS times.
     *
     * @param supplier         request execution supplier that provides the result
     * @param difyServerClient client instance for token refresh operations
     * @param <T>              type of the response object
     * @return result from the successful request execution
     * @throws RuntimeException when max retry attempts are exhausted
     * @throws Exception        when non-retryable errors occur
     */
    public <T> T executeWithRetry(RequestSupplier<T> supplier, DifyServerClient difyServerClient) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY_ATTEMPTS) {
            try {
                return supplier.get();
            } catch (Exception e) {
                boolean noLogin = false;
                if(e instanceof DifyClientException) {
                    noLogin = ((DifyClientException) e).noLogin();
                } else if (e.getMessage() != null && e.getMessage().contains("[401]")) {
                    noLogin = true;
                }
                if (e.getMessage() != null && noLogin && retryCount < MAX_RETRY_ATTEMPTS - 1) {
                    log.warn("Token invalid, attempting to refresh token. Retry count: {}", retryCount + 1);
                    refreshOrObtainNewToken(difyServerClient);
                    retryCount++;
                } else {
                    throw e;
                }
            }
        }
        throw new RuntimeException("Max retry attempts reached");
    }

    /**
     * Abstract method to refresh or obtain new authentication token.
     *
     * @param difyServerClient client instance containing authentication information
     */
    abstract void refreshOrObtainNewToken(DifyServerClient difyServerClient);


}
