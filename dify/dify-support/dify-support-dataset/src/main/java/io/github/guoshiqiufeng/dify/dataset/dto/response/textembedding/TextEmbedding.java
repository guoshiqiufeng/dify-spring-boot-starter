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
package io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/10 14:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextEmbedding implements Serializable {

    private static final long serialVersionUID = -7050862386720963430L;
    private String provider;

    private TextEmbeddingLabel label;

    @JsonAlias("icon_small")
    private TextEmbeddingIcon iconSmall;

    @JsonAlias("icon_large")
    private TextEmbeddingIcon iconLarge;

    private String status;

    private List<TextEmbeddingModel> models;

}
