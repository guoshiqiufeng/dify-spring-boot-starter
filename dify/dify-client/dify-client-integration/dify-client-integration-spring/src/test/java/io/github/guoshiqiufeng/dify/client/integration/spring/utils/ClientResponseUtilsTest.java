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
package io.github.guoshiqiufeng.dify.client.integration.spring.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ClientResponseUtils
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
class ClientResponseUtilsTest {

    @Test
    void testGetStatusCodeValue() {
        // Arrange
        ClientResponse response = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .build();

        // Act
        int statusCode = ClientResponseUtils.getStatusCodeValue(response);

        // Assert
        assertEquals(200, statusCode);
    }

    @Test
    void testGetStatusCodeValueWithDifferentStatus() {
        // Arrange
        ClientResponse response = ClientResponse.create(HttpStatus.NOT_FOUND, ExchangeStrategies.withDefaults())
                .build();

        // Act
        int statusCode = ClientResponseUtils.getStatusCodeValue(response);

        // Assert
        assertEquals(404, statusCode);
    }

    @Test
    void testGetStatusCodeValueWithServerError() {
        // Arrange
        ClientResponse response = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, ExchangeStrategies.withDefaults())
                .build();

        // Act
        int statusCode = ClientResponseUtils.getStatusCodeValue(response);

        // Assert
        assertEquals(500, statusCode);
    }

    @Test
    void testCreateClientResponse() {
        // Arrange
        String originalBody = "original";
        String newBody = "new body content";
        ClientResponse originalResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/json")
                .cookie("session", "abc123")
                .build();

        // Act
        ClientResponse newResponse = ClientResponseUtils.createClientResponse(originalResponse, newBody);

        // Assert
        assertNotNull(newResponse);
        assertEquals(200, ClientResponseUtils.getStatusCodeValue(newResponse));
        assertEquals("application/json", newResponse.headers().asHttpHeaders().getFirst("Content-Type"));
        assertTrue(newResponse.cookies().containsKey("session"));

        // Verify body content
        StepVerifier.create(newResponse.bodyToMono(String.class))
                .expectNext(newBody)
                .verifyComplete();
    }

    @Test
    void testCreateClientResponseWithEmptyBody() {
        // Arrange
        String newBody = "";
        ClientResponse originalResponse = ClientResponse.create(HttpStatus.CREATED, ExchangeStrategies.withDefaults())
                .build();

        // Act
        ClientResponse newResponse = ClientResponseUtils.createClientResponse(originalResponse, newBody);

        // Assert
        assertNotNull(newResponse);
        assertEquals(201, ClientResponseUtils.getStatusCodeValue(newResponse));

        // Verify empty body
        StepVerifier.create(newResponse.bodyToMono(String.class))
                .expectNext("")
                .verifyComplete();
    }

    @Test
    void testCreateClientResponsePreservesHeaders() {
        // Arrange
        String newBody = "test";
        ClientResponse originalResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("X-Custom-Header", "custom-value")
                .header("Authorization", "Bearer token123")
                .build();

        // Act
        ClientResponse newResponse = ClientResponseUtils.createClientResponse(originalResponse, newBody);

        // Assert
        assertEquals("custom-value", newResponse.headers().asHttpHeaders().getFirst("X-Custom-Header"));
        assertEquals("Bearer token123", newResponse.headers().asHttpHeaders().getFirst("Authorization"));
    }

    @Test
    void testCreateClientResponsePreservesCookies() {
        // Arrange
        String newBody = "test";
        ClientResponse originalResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .cookie("cookie1", "value1")
                .cookie("cookie2", "value2")
                .build();

        // Act
        ClientResponse newResponse = ClientResponseUtils.createClientResponse(originalResponse, newBody);

        // Assert
        assertTrue(newResponse.cookies().containsKey("cookie1"));
        assertTrue(newResponse.cookies().containsKey("cookie2"));
    }

    @Test
    void testCreateClientResponseWithDifferentStatusCodes() {
        // Test multiple status codes
        HttpStatus[] statuses = {
                HttpStatus.OK,
                HttpStatus.CREATED,
                HttpStatus.ACCEPTED,
                HttpStatus.NO_CONTENT,
                HttpStatus.BAD_REQUEST,
                HttpStatus.UNAUTHORIZED,
                HttpStatus.FORBIDDEN,
                HttpStatus.NOT_FOUND,
                HttpStatus.INTERNAL_SERVER_ERROR
        };

        for (HttpStatus status : statuses) {
            // Arrange
            ClientResponse originalResponse = ClientResponse.create(status, ExchangeStrategies.withDefaults())
                    .build();

            // Act
            ClientResponse newResponse = ClientResponseUtils.createClientResponse(originalResponse, "test");

            // Assert
            assertEquals(status.value(), ClientResponseUtils.getStatusCodeValue(newResponse),
                    "Status code should be preserved for " + status);
        }
    }

    @Test
    void testConstructorThrowsException() throws Exception {
        // Act & Assert
        java.lang.reflect.Constructor<ClientResponseUtils> constructor =
                ClientResponseUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        java.lang.reflect.InvocationTargetException exception = assertThrows(
                java.lang.reflect.InvocationTargetException.class,
                constructor::newInstance
        );

        assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
        // Lombok @UtilityClass generates the exception message, so we just verify the exception type
    }
}
