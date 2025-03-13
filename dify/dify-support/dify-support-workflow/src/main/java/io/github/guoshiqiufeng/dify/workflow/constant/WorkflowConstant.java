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
package io.github.guoshiqiufeng.dify.workflow.constant;

/**
 * 常量
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/3/11 14:18
 */
public interface WorkflowConstant {

    String V1_URL = "/v1";

    String WORKFLOWS_URL = V1_URL + "/workflows";

    String WORKFLOW_RUN_URL = WORKFLOWS_URL + "/run";

    String WORKFLOW_TASKS_URL = WORKFLOWS_URL + "/tasks";

    String WORKFLOW_LOGS_URL = WORKFLOWS_URL + "/logs";


}
