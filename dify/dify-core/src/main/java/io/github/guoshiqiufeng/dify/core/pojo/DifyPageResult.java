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
package io.github.guoshiqiufeng.dify.core.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回结果类，用于封装分页查询接口的返回数据。<br>
 * 泛型 T 表示返回数据的具体类型。
 *
 * @author yanghq
 * @version 1.0
 * @since 2024/12/31 15:49
 */
@Data
public class DifyPageResult<T> implements Serializable {
    private static final long serialVersionUID = 9185003870754105941L;

    /**
     * 当前页的数据列表，类型由泛型 T 决定
     */
    private List<T> data;

    /**
     * 是否还有更多数据（即是否存在下一页）
     */
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
