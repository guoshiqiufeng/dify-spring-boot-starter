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
package io.github.guoshiqiufeng.dify.status.strategy;

import io.github.guoshiqiufeng.dify.core.exception.BaseException;
import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

/**
 * Abstract status check strategy
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Slf4j
public abstract class AbstractStatusCheckStrategy implements StatusCheckStrategy {

    /**
     * Execute a status check with error handling
     *
     * @param methodName    The method name being checked
     * @param endpoint      The endpoint path
     * @param checkFunction The function to execute
     * @return ApiStatusResult
     */
    protected ApiStatusResult executeCheck(String methodName, String endpoint, CheckFunction checkFunction) {
        long startTime = System.currentTimeMillis();
        ApiStatusResult.ApiStatusResultBuilder builder = ApiStatusResult.builder()
                .methodName(methodName)
                .endpoint(endpoint)
                .checkTime(LocalDateTime.now());

        try {
            checkFunction.execute();
            long responseTime = System.currentTimeMillis() - startTime;

            return builder
                    .status(ApiStatus.NORMAL)
                    .responseTimeMs(responseTime)
                    .httpStatusCode(200)
                    .build();

        } catch (HttpClientErrorException.NotFound e) {
            return builder
                    .status(ApiStatus.NOT_FOUND_404)
                    .httpStatusCode(404)
                    .errorMessage(e.getMessage())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (HttpClientErrorException.Unauthorized e) {
            return builder
                    .status(ApiStatus.UNAUTHORIZED_401)
                    .httpStatusCode(401)
                    .errorMessage(e.getMessage())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (HttpClientErrorException e) {
            return builder
                    .status(ApiStatus.CLIENT_ERROR)
                    .httpStatusCode(e.getStatusCode().value())
                    .errorMessage(e.getMessage())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (HttpServerErrorException e) {
            return builder
                    .status(ApiStatus.SERVER_ERROR)
                    .httpStatusCode(e.getStatusCode().value())
                    .errorMessage(e.getMessage())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (ResourceAccessException | TimeoutException | SocketTimeoutException e) {
            return builder
                    .status(ApiStatus.TIMEOUT)
                    .errorMessage(e.getMessage())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (BaseException e) {
            // BaseException doesn't expose code/msg getters, so we use getMessage()
            // and infer status from the message content
            ApiStatus status = inferStatusFromMessage(e.getMessage());
            return builder
                    .status(status)
                    .errorMessage(e.getMessage())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error checking status for {}: {}", methodName, e.getMessage(), e);
            return builder
                    .status(ApiStatus.UNKNOWN_ERROR)
                    .errorMessage(e.getMessage())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }

    /**
     * Infer ApiStatus from error message
     *
     * @param message Error message
     * @return ApiStatus
     */
    private ApiStatus inferStatusFromMessage(String message) {
        if (message == null) {
            return ApiStatus.UNKNOWN_ERROR;
        }
        String lowerMessage = message.toLowerCase();
        if (lowerMessage.contains("unauthorized") || lowerMessage.contains("401")) {
            return ApiStatus.UNAUTHORIZED_401;
        } else if (lowerMessage.contains("not found") || lowerMessage.contains("404")) {
            return ApiStatus.NOT_FOUND_404;
        } else if (lowerMessage.contains("timeout") || lowerMessage.contains("408")) {
            return ApiStatus.TIMEOUT;
        } else {
            return ApiStatus.CLIENT_ERROR;
        }
    }

    /**
     * Functional interface for check execution
     */
    @FunctionalInterface
    protected interface CheckFunction {
        void execute() throws Exception;
    }
}
