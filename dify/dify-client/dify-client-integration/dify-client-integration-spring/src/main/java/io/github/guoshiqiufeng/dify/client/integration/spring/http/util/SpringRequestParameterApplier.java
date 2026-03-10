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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.util;

import io.github.guoshiqiufeng.dify.client.core.http.util.RequestParameterProcessor;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for applying request parameters to Spring HTTP clients.
 * Provides methods for applying headers and cookies to WebClient and RestClient requests.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@UtilityClass
public class SpringRequestParameterApplier {

    /**
     * Apply headers to WebClient request spec.
     * Iterates through the headers map and adds each header to the request.
     *
     * @param spec    WebClient request body spec
     * @param headers map of header names to values
     * @return the same request spec (for method chaining)
     */
    public static WebClient.RequestBodySpec applyHeaders(WebClient.RequestBodySpec spec, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            spec.header(entry.getKey(), entry.getValue());
        }
        return spec;
    }

    /**
     * Apply cookies to WebClient request spec.
     * Iterates through the cookies map and adds each cookie to the request.
     *
     * @param spec    WebClient request body spec
     * @param cookies map of cookie names to values
     * @return the same request spec (for method chaining)
     */
    public static WebClient.RequestBodySpec applyCookies(WebClient.RequestBodySpec spec, Map<String, String> cookies) {
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            spec.cookie(entry.getKey(), entry.getValue());
        }
        return spec;
    }

    /**
     * Apply both headers and cookies to WebClient request spec.
     * This is a convenience method that combines header and cookie application.
     * Also converts cookies to a Cookie header for compatibility.
     *
     * @param spec    WebClient request body spec
     * @param headers map of header names to values
     * @param cookies map of cookie names to values
     * @return the same request spec (for method chaining)
     */
    public static WebClient.RequestBodySpec applyHeadersAndCookies(WebClient.RequestBodySpec spec,
                                                                   Map<String, String> headers,
                                                                   Map<String, String> cookies) {
        // Apply headers first
        applyHeaders(spec, headers);

        // Convert cookies to Cookie header if needed
        if (!cookies.isEmpty()) {
            String cookieHeader = RequestParameterProcessor.buildCookieHeader(cookies);
            if (!cookieHeader.isEmpty()) {
                spec.header("Cookie", cookieHeader);
            }
        }

        // Also apply cookies directly
        applyCookies(spec, cookies);

        return spec;
    }

    /**
     * Apply headers to RestClient request spec using reflection.
     * Uses reflection to support different Spring versions.
     *
     * @param requestSpec the RestClient request spec object
     * @param headers     map of header names to values
     * @return the updated request spec
     * @throws Exception if reflection fails
     */
    public static Object applyHeadersReflection(Object requestSpec, Map<String, String> headers) throws Exception {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            java.lang.reflect.Method headerMethod = findMethod(requestSpec.getClass(), "header", String.class, String[].class);
            headerMethod.setAccessible(true);
            requestSpec = headerMethod.invoke(requestSpec, entry.getKey(), new String[]{entry.getValue()});
        }
        return requestSpec;
    }

    /**
     * Apply cookies to RestClient request spec using reflection.
     * Auto-fallback to Cookie header if cookie() method not available
     *
     * @param requestSpec the RestClient request spec object
     * @param cookies     map of cookie names to values
     * @return the updated request spec
     * @throws Exception if reflection fails
     */
    public static Object applyCookiesReflection(Object requestSpec, Map<String, String> cookies) throws Exception {
        if (cookies == null || cookies.isEmpty()) {
            return requestSpec;
        }

        // 🔍 尝试查找 cookie(String, String) 方法（Spring Framework 6.2+）
        java.lang.reflect.Method cookieMethod = findMethodNoError(requestSpec.getClass(), "cookie", String.class, String.class);

        if (cookieMethod != null) {
            // ✅ 方法存在：使用原生 .cookie() API
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                cookieMethod.setAccessible(true);
                requestSpec = cookieMethod.invoke(requestSpec, entry.getKey(), entry.getValue());
            }
            return requestSpec;
        }

        // ⚠️ 方法不存在：降级使用 .header("Cookie", "k1=v1; k2=v2") 方式
        return applyCookieHeaderFallback(requestSpec, cookies);
    }

    /**
     * Fallback: apply cookies via "Cookie" request header (RFC 6265 format).
     * Compatible with Spring Framework 6.1.x and earlier.
     *
     * @param requestSpec the RestClient request spec object
     * @param cookies     map of cookie names to values
     * @return the updated request spec
     * @throws Exception if reflection fails
     */
    private static Object applyCookieHeaderFallback(Object requestSpec, Map<String, String> cookies) throws Exception {
        // 📦 拼接 Cookie 头：name1=value1; name2=value2
        String cookieHeader = cookies.entrySet().stream()
                .map(e -> encodeCookieName(e.getKey()) + "=" + encodeCookieValue(e.getValue()))
                .collect(Collectors.joining("; "));

        // 🔧 调用 header(String, String[]) 方法（RestClient 所有版本都支持）
        java.lang.reflect.Method headerMethod = findMethodNoError(requestSpec.getClass(), "header", String.class, String[].class);
        if (headerMethod == null) {
            throw new IllegalStateException("Cannot find 'header' method in " + requestSpec.getClass());
        }

        headerMethod.setAccessible(true);
        return headerMethod.invoke(requestSpec, "Cookie", new String[]{cookieHeader});
    }

    /**
     * Encode cookie name (simple validation, RFC 6265 token rules).
     */
    private static String encodeCookieName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Cookie name cannot be empty");
        }
        return name;
    }

    /**
     * Encode cookie value (URL-encode special characters).
     */
    private static String encodeCookieValue(String value) {
        if (value == null) {
            return "";
        }
        // Cookie value 可以包含大部分字符，但建议对特殊字符编码
        // 这里做简单处理：如果包含分隔符则编码
        if (value.matches(".*[;\\s].*")) {
            try {
                return java.net.URLEncoder.encode(value, "UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                return value;
            }
        }
        return value;
    }

    private static java.lang.reflect.Method findMethodNoError(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return findMethod(clazz, methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Find method by name and parameter types, searching through class hierarchy.
     * This is a helper method for reflection-based operations.
     *
     * @param clazz          class to search
     * @param methodName     method name
     * @param parameterTypes parameter types
     * @return method
     * @throws NoSuchMethodException if method not found
     */
    private static java.lang.reflect.Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            // Try declared methods
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ex) {
                // Search in superclass and interfaces
                if (clazz.getSuperclass() != null) {
                    try {
                        return findMethod(clazz.getSuperclass(), methodName, parameterTypes);
                    } catch (NoSuchMethodException ignored) {
                    }
                }
                for (Class<?> iface : clazz.getInterfaces()) {
                    try {
                        return findMethod(iface, methodName, parameterTypes);
                    } catch (NoSuchMethodException ignored) {
                    }
                }
                throw e;
            }
        }
    }
}
