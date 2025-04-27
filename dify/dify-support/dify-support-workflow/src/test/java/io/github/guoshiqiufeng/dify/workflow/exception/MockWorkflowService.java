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
package io.github.guoshiqiufeng.dify.workflow.exception;

/**
 * Mock service class to demonstrate exception usage in tests
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class MockWorkflowService {

    /**
     * Process data and throw DifyWorkflowException if data is invalid
     *
     * @param data the data to process
     * @return the processed data
     * @throws DifyWorkflowException if the data is invalid
     */
    public String processData(String data) throws DifyWorkflowException {
        if (data == null || data.isEmpty()) {
            throw new DifyWorkflowException(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }
        return "Processed: " + data;
    }

    /**
     * Call an API and throw DifyWorkflowException if API call fails
     *
     * @param apiUrl the API URL to call
     * @return the API response
     * @throws DifyWorkflowException if the API call fails
     */
    public String callApi(String apiUrl) throws DifyWorkflowException {
        if (apiUrl == null || !apiUrl.startsWith("https://")) {
            throw new DifyWorkflowException(DifyWorkflowExceptionEnum.DIFY_API_ERROR);
        }
        return "API Response";
    }
}
