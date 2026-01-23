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

import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ResponseErrorHandler
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/12
 */
class ResponseErrorHandlerTest {

    @Test
    void testOn4xxStatus() {
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        ResponseErrorHandler handler = ResponseErrorHandler.on4xxStatus(response -> {
            handlerCalled.set(true);
        });

        assertNotNull(handler);
        assertNotNull(handler.getStatusPredicate());

        // Test 4xx status codes
        assertTrue(handler.getStatusPredicate().test(400));
        assertTrue(handler.getStatusPredicate().test(404));
        assertTrue(handler.getStatusPredicate().test(499));

        // Test non-4xx status codes
        assertFalse(handler.getStatusPredicate().test(200));
        assertFalse(handler.getStatusPredicate().test(399));
        assertFalse(handler.getStatusPredicate().test(500));
        assertFalse(handler.getStatusPredicate().test(600));
    }

    @Test
    void testOn4xxStatusHandlerExecution() throws Exception {
        AtomicInteger statusCode = new AtomicInteger(0);
        ResponseErrorHandler handler = ResponseErrorHandler.on4xxStatus(response -> {
            statusCode.set(response.getStatusCode());
        });

        ResponseEntity<String> response = new ResponseEntity<>(404, new HttpHeaders(), "Not Found");
        handler.handle(response);

        assertEquals(404, statusCode.get());
    }

    @Test
    void testOn5xxStatus() {
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        ResponseErrorHandler handler = ResponseErrorHandler.on5xxStatus(response -> {
            handlerCalled.set(true);
        });

        assertNotNull(handler);
        assertNotNull(handler.getStatusPredicate());

        // Test 5xx status codes
        assertTrue(handler.getStatusPredicate().test(500));
        assertTrue(handler.getStatusPredicate().test(503));
        assertTrue(handler.getStatusPredicate().test(599));

        // Test non-5xx status codes
        assertFalse(handler.getStatusPredicate().test(200));
        assertFalse(handler.getStatusPredicate().test(404));
        assertFalse(handler.getStatusPredicate().test(499));
        assertFalse(handler.getStatusPredicate().test(600));
    }

    @Test
    void testOn5xxStatusHandlerExecution() throws Exception {
        AtomicInteger statusCode = new AtomicInteger(0);
        ResponseErrorHandler handler = ResponseErrorHandler.on5xxStatus(response -> {
            statusCode.set(response.getStatusCode());
        });

        ResponseEntity<String> response = new ResponseEntity<>(503, new HttpHeaders(), "Service Unavailable");
        handler.handle(response);

        assertEquals(503, statusCode.get());
    }

    @Test
    void testOnStatusWithCustomPredicate() {
        Predicate<Integer> customPredicate = status -> status == 429 || status == 503;
        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        ResponseErrorHandler handler = ResponseErrorHandler.onStatus(
                customPredicate,
                response -> handlerCalled.set(true)
        );

        assertNotNull(handler);
        assertNotNull(handler.getStatusPredicate());

        // Test custom predicate
        assertTrue(handler.getStatusPredicate().test(429));
        assertTrue(handler.getStatusPredicate().test(503));
        assertFalse(handler.getStatusPredicate().test(404));
        assertFalse(handler.getStatusPredicate().test(500));
    }

    @Test
    void testOnStatusHandlerExecution() throws Exception {
        AtomicInteger statusCode = new AtomicInteger(0);
        Predicate<Integer> predicate = status -> status == 429;

        ResponseErrorHandler handler = ResponseErrorHandler.onStatus(
                predicate,
                response -> statusCode.set(response.getStatusCode())
        );

        ResponseEntity<String> response = new ResponseEntity<>(429, new HttpHeaders(), "Too Many Requests");
        handler.handle(response);

        assertEquals(429, statusCode.get());
    }

    @Test
    void testHandlerThrowsException() {
        ResponseErrorHandler handler = ResponseErrorHandler.on4xxStatus(response -> {
            throw new RuntimeException("Handler error");
        });

        ResponseEntity<String> response = new ResponseEntity<>(404, new HttpHeaders(), "Not Found");

        assertThrows(RuntimeException.class, () -> handler.handle(response));
    }

    @Test
    void testMultipleHandlersWithDifferentPredicates() throws Exception {
        AtomicInteger handler1Called = new AtomicInteger(0);
        AtomicInteger handler2Called = new AtomicInteger(0);

        ResponseErrorHandler handler1 = ResponseErrorHandler.on4xxStatus(
                response -> handler1Called.incrementAndGet()
        );

        ResponseErrorHandler handler2 = ResponseErrorHandler.on5xxStatus(
                response -> handler2Called.incrementAndGet()
        );

        // Test 4xx response
        ResponseEntity<String> response404 = new ResponseEntity<>(404, new HttpHeaders(), "Not Found");
        if (handler1.getStatusPredicate().test(404)) {
            handler1.handle(response404);
        }
        if (handler2.getStatusPredicate().test(404)) {
            handler2.handle(response404);
        }

        assertEquals(1, handler1Called.get());
        assertEquals(0, handler2Called.get());

        // Test 5xx response
        ResponseEntity<String> response500 = new ResponseEntity<>(500, new HttpHeaders(), "Internal Server Error");
        if (handler1.getStatusPredicate().test(500)) {
            handler1.handle(response500);
        }
        if (handler2.getStatusPredicate().test(500)) {
            handler2.handle(response500);
        }

        assertEquals(1, handler1Called.get());
        assertEquals(1, handler2Called.get());
    }

    @Test
    void testHandlerWithNullResponse() {
        ResponseErrorHandler handler = ResponseErrorHandler.on4xxStatus(response -> {
            assertNull(response);
        });

        assertDoesNotThrow(() -> handler.handle(null));
    }

    @Test
    void testBoundaryStatusCodes() {
        ResponseErrorHandler handler4xx = ResponseErrorHandler.on4xxStatus(response -> {});
        ResponseErrorHandler handler5xx = ResponseErrorHandler.on5xxStatus(response -> {});

        // Test boundary values for 4xx
        assertTrue(handler4xx.getStatusPredicate().test(400));
        assertFalse(handler4xx.getStatusPredicate().test(399));
        assertTrue(handler4xx.getStatusPredicate().test(499));
        assertFalse(handler4xx.getStatusPredicate().test(500));

        // Test boundary values for 5xx
        assertTrue(handler5xx.getStatusPredicate().test(500));
        assertFalse(handler5xx.getStatusPredicate().test(499));
        assertTrue(handler5xx.getStatusPredicate().test(599));
        assertFalse(handler5xx.getStatusPredicate().test(600));
    }

    @Test
    void testHandlerAccessToResponseDetails() throws Exception {
        AtomicInteger statusCode = new AtomicInteger(0);
        AtomicBoolean hasHeaders = new AtomicBoolean(false);
        AtomicBoolean hasBody = new AtomicBoolean(false);

        ResponseErrorHandler handler = ResponseErrorHandler.on4xxStatus(response -> {
            statusCode.set(response.getStatusCode());
            hasHeaders.set(response.getHeaders() != null);
            hasBody.set(response.getBody() != null);
        });

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ResponseEntity<String> response = new ResponseEntity<>(404, headers, "Error message");

        handler.handle(response);

        assertEquals(404, statusCode.get());
        assertTrue(hasHeaders.get());
        assertTrue(hasBody.get());
    }

    @Test
    void testComplexPredicateLogic() {
        // Test predicate with complex logic: 4xx except 404, or 503
        Predicate<Integer> complexPredicate = status ->
                (status >= 400 && status < 500 && status != 404) || status == 503;

        ResponseErrorHandler handler = ResponseErrorHandler.onStatus(
                complexPredicate,
                response -> {}
        );

        assertTrue(handler.getStatusPredicate().test(400));
        assertTrue(handler.getStatusPredicate().test(403));
        assertFalse(handler.getStatusPredicate().test(404));
        assertTrue(handler.getStatusPredicate().test(429));
        assertFalse(handler.getStatusPredicate().test(500));
        assertTrue(handler.getStatusPredicate().test(503));
        assertFalse(handler.getStatusPredicate().test(504));
    }

    @Test
    void testHandlerChaining() throws Exception {
        AtomicInteger callCount = new AtomicInteger(0);

        ResponseErrorHandler handler = ResponseErrorHandler.on4xxStatus(response -> {
            callCount.incrementAndGet();
            // Simulate some processing
            if (response.getStatusCode() == 404) {
                throw new IllegalStateException("Resource not found");
            }
        });

        ResponseEntity<String> response404 = new ResponseEntity<>(404, new HttpHeaders(), "Not Found");
        assertThrows(IllegalStateException.class, () -> handler.handle(response404));
        assertEquals(1, callCount.get());

        ResponseEntity<String> response400 = new ResponseEntity<>(400, new HttpHeaders(), "Bad Request");
        assertDoesNotThrow(() -> handler.handle(response400));
        assertEquals(2, callCount.get());
    }
}
