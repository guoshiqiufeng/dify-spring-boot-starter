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
package io.github.guoshiqiufeng.dify.dataset.dto.request.document;

import io.github.guoshiqiufeng.dify.dataset.enums.document.PreProcessingRuleTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 15:24
 */
@Data
public class PreProcessingRule implements Serializable {
    private static final long serialVersionUID = 2662516999304017667L;

    /**
     * 预处理规则的唯一标识符 remove_extra_spaces,remove_urls_emails
     */
    private PreProcessingRuleTypeEnum id;
    /**
     * 是否选中该规则，不传入文档 ID 时代表默认值
     */
    private Boolean enabled;
}





