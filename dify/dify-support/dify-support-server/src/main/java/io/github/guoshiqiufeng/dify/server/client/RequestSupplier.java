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
package io.github.guoshiqiufeng.dify.server.client;

/**
 * Request Supplier Interface
 * A functional interface that represents a supplier of requests to the Dify API.
 * This interface is used to defer or lazily execute API requests and handle
 * generic request execution in a functional programming style.
 *
 * @param <T> The type of result produced by this supplier
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/09 15:44
 */
@FunctionalInterface
public interface RequestSupplier<T> {

    /**
     * Gets a result from this supplier
     *
     * @return A result of type T
     */
    T get();
}
