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
package io.github.guoshiqiufeng.dify.client.core.codec;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom JSON deserialization annotation
 * <p>
 * Used to specify a custom deserializer for a class.
 * The deserializer must implement the {@link JsonDeserializer} interface.
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/09
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonDeserialize {

    /**
     * The custom deserializer class
     *
     * @return deserializer class that implements JsonDeserializer
     */
    Class<? extends JsonDeserializer<?>> using();
}
