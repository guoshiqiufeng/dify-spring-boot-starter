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
package io.github.guoshiqiufeng.dify.client.core.http;

import io.github.guoshiqiufeng.dify.client.core.map.MultiValueMap;

import java.io.Serializable;
import java.util.*;

/**
 * HTTP headers wrapper that provides case-insensitive header lookup and convenient methods.
 * This class implements {@link MultiValueMap} for compatibility with Spring's HttpHeaders
 * and provides case-insensitive header name matching as per HTTP specification.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-30
 */
public class HttpHeaders implements MultiValueMap<String, String>, Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, List<String>> headers;

    public static final String AUTHORIZATION = "AUTHORIZATION";
    public static final String AUTHORIZATION_BEARER_KEY = "Bearer ";

    /**
     * Create a new HttpHeaders instance.
     */
    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    /**
     * Create a new HttpHeaders instance from an existing map.
     *
     * @param headers the headers map
     */
    public HttpHeaders(Map<String, List<String>> headers) {
        this.headers = new LinkedHashMap<>();
        if (headers != null) {
            headers.forEach((key, values) -> this.headers.put(key, new ArrayList<>(values)));
        }
    }

    /**
     * Find the actual key in the map that matches the given key (case-insensitive).
     *
     * @param key the key to find
     * @return the actual key in the map, or null if not found
     */
    private String findKey(Object key) {
        if (key == null) {
            return null;
        }
        String keyStr = key.toString();
        for (String existingKey : headers.keySet()) {
            if (existingKey.equalsIgnoreCase(keyStr)) {
                return existingKey;
            }
        }
        return null;
    }

    @Override
    public String getFirst(String key) {
        List<String> values = get(key);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    /**
     * Get a specific header value or empty list if not found.
     * This method provides compatibility with the usage pattern:
     * {@code responseEntity.getHeaders().getOrEmpty("Set-Cookie")}
     *
     * @param name header name (case-insensitive)
     * @return list of header values, or empty list if not found
     */
    public List<String> getOrEmpty(String name) {
        List<String> values = get(name);
        return values != null ? values : Collections.emptyList();
    }

    @Override
    public void add(String key, String value) {
        String actualKey = findKey(key);
        if (actualKey == null) {
            actualKey = key;
        }
        List<String> values = headers.computeIfAbsent(actualKey, k -> new ArrayList<>());
        values.add(value);
    }

    @Override
    public void addAll(String key, List<? extends String> values) {
        String actualKey = findKey(key);
        if (actualKey == null) {
            actualKey = key;
        }
        List<String> currentValues = headers.computeIfAbsent(actualKey, k -> new ArrayList<>());
        currentValues.addAll(values);
    }

    @Override
    public void addAll(MultiValueMap<String, String> values) {
        for (Entry<String, List<String>> entry : values.entrySet()) {
            addAll(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void set(String key, String value) {
        String actualKey = findKey(key);
        if (actualKey != null) {
            headers.remove(actualKey);
        }
        List<String> values = new ArrayList<>();
        values.add(value);
        headers.put(key, values);
    }

    @Override
    public void setAll(Map<String, String> values) {
        values.forEach(this::set);
    }

    @Override
    public Map<String, String> toSingleValueMap() {
        Map<String, String> singleValueMap = new LinkedHashMap<>(headers.size());
        headers.forEach((key, values) -> {
            if (values != null && !values.isEmpty()) {
                singleValueMap.put(key, values.get(0));
            }
        });
        return singleValueMap;
    }

    @Override
    public int size() {
        return headers.size();
    }

    @Override
    public boolean isEmpty() {
        return headers.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return findKey(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return headers.containsValue(value);
    }

    @Override
    public List<String> get(Object key) {
        String actualKey = findKey(key);
        if (actualKey == null) {
            return null;
        }
        List<String> values = headers.get(actualKey);
        return values != null ? Collections.unmodifiableList(values) : null;
    }

    @Override
    public List<String> put(String key, List<String> value) {
        String actualKey = findKey(key);
        if (actualKey != null && !actualKey.equals(key)) {
            headers.remove(actualKey);
        }
        return headers.put(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        String actualKey = findKey(key);
        return actualKey != null ? headers.remove(actualKey) : null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        map.forEach(this::put);
    }

    @Override
    public void clear() {
        headers.clear();
    }

    @Override
    public Set<String> keySet() {
        return headers.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        return headers.values();
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        return headers.entrySet();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HttpHeaders)) {
            return false;
        }
        HttpHeaders otherHeaders = (HttpHeaders) other;
        return headers.equals(otherHeaders.headers);
    }

    @Override
    public int hashCode() {
        return headers.hashCode();
    }

    @Override
    public String toString() {
        return headers.toString();
    }
}
