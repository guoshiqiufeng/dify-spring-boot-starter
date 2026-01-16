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
package io.github.guoshiqiufeng.dify.core.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Collection utilities for common operations
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/1/5 14:27
 */
public class CollUtil {

    /**
     * Check if a collection is empty or null.
     *
     * @param collection the collection to check
     * @return true if the collection is null or empty, false otherwise
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Check if a collection is not empty.
     *
     * @param collection the collection to check
     * @return true if the collection is not null and not empty, false otherwise
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Check if a map is empty or null.
     *
     * @param map the map to check
     * @return true if the map is null or empty, false otherwise
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Check if a map is not empty.
     *
     * @param map the map to check
     * @return true if the map is not null and not empty, false otherwise
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * Get the size of a collection, return 0 if null.
     *
     * @param collection the collection to get size from
     * @return the size of the collection, or 0 if null
     */
    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * Get the size of a map, return 0 if null.
     *
     * @param map the map to get size from
     * @return the size of the map, or 0 if null
     */
    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }
}
