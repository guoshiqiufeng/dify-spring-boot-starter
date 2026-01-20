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

import lombok.experimental.UtilityClass;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for extracting HTTP status codes from Spring ResponseEntity.
 * Provides Spring version-agnostic status code extraction using reflection.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@UtilityClass
public class SpringStatusCodeExtractor {

    /**
     * Get status code value from ResponseEntity in a Spring version-agnostic way.
     * Uses reflection to avoid method signature binding at compile time.
     * Works with both Spring 5 (HttpStatus) and Spring 6+ (HttpStatusCode).
     *
     * @param responseEntity the response entity
     * @return status code value
     */
    public static int getStatusCodeValue(ResponseEntity<?> responseEntity) {
        try {
            // Call getStatusCode() using reflection to avoid compile-time method signature binding
            java.lang.reflect.Method getStatusCodeMethod = ResponseEntity.class.getMethod("getStatusCode");
            Object statusCode = getStatusCodeMethod.invoke(responseEntity);

            // Call value() on the result (works for both HttpStatus and HttpStatusCode)
            java.lang.reflect.Method valueMethod = statusCode.getClass().getMethod("value");
            return (int) valueMethod.invoke(statusCode);
        } catch (Exception e) {
            // Fallback: try direct call (this works if compiled with Spring 6+)
            return responseEntity.getStatusCode().value();
        }
    }
}
