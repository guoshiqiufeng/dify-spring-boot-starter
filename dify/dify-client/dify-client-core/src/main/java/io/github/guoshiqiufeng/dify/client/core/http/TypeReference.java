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
package io.github.guoshiqiufeng.dify.client.core.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Type reference for capturing generic type information at runtime.
 * This is similar to Jackson's TypeReference or Spring's ParameterizedTypeReference.
 *
 * <p>Usage example:
 * <pre>
 * TypeReference&lt;List&lt;String&gt;&gt; typeRef = new TypeReference&lt;List&lt;String&gt;&gt;() {};
 * </pre>
 *
 * @param <T> the type being referenced
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public abstract class TypeReference<T> {

    private final Type type;

    /**
     * Constructor that captures the generic type.
     */
    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException("TypeReference must be parameterized");
        }
    }

    /**
     * Get the captured type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }
}
