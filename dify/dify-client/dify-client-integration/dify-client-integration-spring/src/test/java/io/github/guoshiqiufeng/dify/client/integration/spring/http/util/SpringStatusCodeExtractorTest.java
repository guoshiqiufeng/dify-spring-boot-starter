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

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for SpringStatusCodeExtractor
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
class SpringStatusCodeExtractorTest {

    @Test
    void testGetStatusCodeValueWith200() {
        ResponseEntity<String> response = ResponseEntity.ok("Success");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(200, statusCode);
    }

    @Test
    void testGetStatusCodeValueWith201() {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.CREATED).body("Created");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(201, statusCode);
    }

    @Test
    void testGetStatusCodeValueWith204() {
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(204, statusCode);
    }

    @Test
    void testGetStatusCodeValueWith400() {
        ResponseEntity<String> response = ResponseEntity.badRequest().body("Bad Request");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(400, statusCode);
    }

    @Test
    void testGetStatusCodeValueWith401() {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(401, statusCode);
    }

    @Test
    void testGetStatusCodeValueWith403() {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(403, statusCode);
    }

    @Test
    void testGetStatusCodeValueWith404() {
        ResponseEntity<String> response = ResponseEntity.notFound().build();
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(404, statusCode);
    }

    @Test
    void testGetStatusCodeValueWith500() {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(500, statusCode);
    }

    @Test
    void testGetStatusCodeValueWith502() {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Bad Gateway");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(502, statusCode);
    }

    @Test
    void testGetStatusCodeValueWith503() {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Unavailable");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(503, statusCode);
    }

    @Test
    void testGetStatusCodeValueWithCustomStatusCode() {
        ResponseEntity<String> response = ResponseEntity.status(418).body("I'm a teapot");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(418, statusCode);
    }

    @Test
    void testGetStatusCodeValueWithNullBody() {
        ResponseEntity<String> response = ResponseEntity.ok().build();
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(200, statusCode);
    }

    @Test
    void testGetStatusCodeValueWithDifferentBodyTypes() {
        ResponseEntity<Integer> response1 = ResponseEntity.ok(123);
        assertEquals(200, SpringStatusCodeExtractor.getStatusCodeValue(response1));

        ResponseEntity<Boolean> response2 = ResponseEntity.ok(true);
        assertEquals(200, SpringStatusCodeExtractor.getStatusCodeValue(response2));

        ResponseEntity<Object> response3 = ResponseEntity.ok(new Object());
        assertEquals(200, SpringStatusCodeExtractor.getStatusCodeValue(response3));
    }

    @Test
    void testGetStatusCodeValueWithAllSuccessCodes() {
        Integer[] codes = {200, 201, 202, 203, 204, 205, 206, 207, 208, 226};
        for (int code : codes) {
            ResponseEntity<String> response = ResponseEntity.status(code).body("Success");
            assertEquals(code, SpringStatusCodeExtractor.getStatusCodeValue(response));
        }
    }

    @Test
    void testGetStatusCodeValueWithAllClientErrorCodes() {
        int[] commonClientErrors = {400, 401, 403, 404, 405, 406, 408, 409, 410, 415, 429};
        for (int code : commonClientErrors) {
            ResponseEntity<String> response = ResponseEntity.status(code).body("Client Error");
            assertEquals(code, SpringStatusCodeExtractor.getStatusCodeValue(response));
        }
    }

    @Test
    void testGetStatusCodeValueWithAllServerErrorCodes() {
        int[] commonServerErrors = {500, 501, 502, 503, 504, 505};
        for (int code : commonServerErrors) {
            ResponseEntity<String> response = ResponseEntity.status(code).body("Server Error");
            assertEquals(code, SpringStatusCodeExtractor.getStatusCodeValue(response));
        }
    }

    @Test
    void testGetStatusCodeValueWithRedirectCodes() {
        ResponseEntity<String> response301 = ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).body("Moved");
        assertEquals(301, SpringStatusCodeExtractor.getStatusCodeValue(response301));

        ResponseEntity<String> response302 = ResponseEntity.status(HttpStatus.FOUND).body("Found");
        assertEquals(302, SpringStatusCodeExtractor.getStatusCodeValue(response302));

        ResponseEntity<String> response304 = ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        assertEquals(304, SpringStatusCodeExtractor.getStatusCodeValue(response304));
    }

    @Test
    void testGetStatusCodeValueWithAccepted() {
        ResponseEntity<String> response = ResponseEntity.accepted().body("Accepted");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(202, statusCode);
    }

    @Test
    void testGetStatusCodeValueWithPartialContent() {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("Partial");
        int statusCode = SpringStatusCodeExtractor.getStatusCodeValue(response);
        assertEquals(206, statusCode);
    }
}
