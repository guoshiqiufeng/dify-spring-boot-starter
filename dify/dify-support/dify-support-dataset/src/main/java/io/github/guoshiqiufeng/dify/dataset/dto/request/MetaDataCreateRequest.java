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
package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 10:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MetaDataCreateRequest extends BaseDatasetRequest implements Serializable {
    private static final long serialVersionUID = 3246895121202690206L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonAlias("datasetId")
    private String datasetId;

    /**
     * 元数据类型，必填
     */
    private String type;

    /**
     * 元数据名称，必填
     */
    private String name;
}
