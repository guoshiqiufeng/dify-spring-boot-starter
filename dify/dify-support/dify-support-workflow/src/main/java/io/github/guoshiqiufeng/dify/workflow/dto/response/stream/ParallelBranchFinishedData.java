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
package io.github.guoshiqiufeng.dify.workflow.dto.response.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yanghq
 * @version 0.12.1
 * @since 2025/5/16 17:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParallelBranchFinishedData extends ParallelBranchStartedData {

    private String status;

    private String error;
}
