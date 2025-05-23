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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 17:18
 */
@Data
public class SegmentResponse implements Serializable {
    private static final long serialVersionUID = -1174425396475689757L;

    private List<SegmentData> data;

    @JsonAlias("doc_form")
    private String docForm;

    @JsonAlias("has_more")
    private Boolean hasMore;

    /**
     * 每页的最大数据条数
     */
    private Integer limit;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 总数据条数
     */
    private Integer total;
}
