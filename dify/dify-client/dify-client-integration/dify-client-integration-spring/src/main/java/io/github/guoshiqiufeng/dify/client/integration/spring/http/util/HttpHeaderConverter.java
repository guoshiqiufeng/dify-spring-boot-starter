package io.github.guoshiqiufeng.dify.client.integration.spring.http.util;

import lombok.experimental.UtilityClass;

/**
 * @author yanghq
 * @version 1.0
 * @since 2026/1/20 16:46
 */
@UtilityClass
public class HttpHeaderConverter {

    /**
     * Convert Spring HttpHeaders to our HttpHeaders format.
     */
    public io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders fromSpringHeaders(org.springframework.http.HttpHeaders springHeaders) {
        io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders headers = new io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders();
        if (springHeaders != null) {
            springHeaders.forEach((key, values) -> {
                for (String value : values) {
                    headers.add(key, value);
                }
            });
        }
        return headers;
    }
}
