/*
 * Copyright (c) 2025-2026, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 10:58
 */
@Data
public class MetaDataListResponse implements Serializable {
    private static final long serialVersionUID = 1633421634204998066L;

    @JsonAlias("built_in_field_name")
    private Boolean builtInFieldEnabled;

    @JsonAlias("doc_metadata")
    private List<DocMetadata> docMetadata;

    @Data
    public static class DocMetadata implements Serializable {

        private static final long serialVersionUID = -5140673844526720576L;

        private String id;
        private String type;
        private String name;
        @JsonAlias("use_count")
        private Integer userCount;
    }
}
