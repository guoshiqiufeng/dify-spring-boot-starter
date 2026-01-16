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
package io.github.guoshiqiufeng.dify.server.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Token costs statistics response DTO
 *
 * @author yanghq
 * @version 1.5.0
 * @since 2025/10/30
 */
@Data
public class TokenCostsResponse {

    /**
     * Date in format "yyyy-MM-dd"
     */
    private String date;

    /**
     * Token count on this date
     */
    @JsonAlias("tokenCount")
    @JsonProperty("token_count")
    private Integer tokenCount;

    /**
     * Total price for tokens on this date
     */
    @JsonAlias("totalPrice")
    @JsonProperty("total_price")
    private String totalPrice;

    /**
     * Currency of the total price
     */
    private String currency;
}