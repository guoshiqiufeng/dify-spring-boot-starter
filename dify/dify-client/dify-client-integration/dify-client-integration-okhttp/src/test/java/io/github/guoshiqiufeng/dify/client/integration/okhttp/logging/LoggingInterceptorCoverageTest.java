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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import okhttp3.*;
import okio.BufferedSink;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional test coverage for LoggingInterceptor edge cases
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/10
 */
class LoggingInterceptorCoverageTest {

    private Logger logger;
    private Level originalLevel;

    @BeforeEach
    void setUp() {
        // Enable DEBUG logging for LoggingInterceptor
        logger = (Logger) LoggerFactory.getLogger(LoggingInterceptor.class);
        originalLevel = logger.getLevel();
        logger.setLevel(Level.DEBUG);
    }

    @AfterEach
    void tearDown() {
        // Restore original log level
        if (logger != null && originalLevel != null) {
            logger.setLevel(originalLevel);
        }
    }

    @Test
    void testRequestBody_isOneShot_skipsLogging() throws IOException {
        LoggingInterceptor interceptor = new LoggingInterceptor(true, 4096, false);

        RequestBody oneShotBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("application/json");
            }

            @Override
            public long contentLength() {
                return 100;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("{\"test\":\"data\"}");
            }

            // Simulate OkHttp 4.x+ isOneShot method
            public boolean isOneShot() {
                return true;
            }
        };

        Request request = new Request.Builder()
                .url("https://api.example.com/test")
                .post(oneShotBody)
                .build();

        Interceptor.Chain mockChain = new MockChain(request);

        Response response = interceptor.intercept(mockChain);

        assertNotNull(response);
    }

    @Test
    void testRequestBody_isDuplex_skipsLogging() throws IOException {
        LoggingInterceptor interceptor = new LoggingInterceptor(true, 4096, false);

        RequestBody duplexBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("application/json");
            }

            @Override
            public long contentLength() {
                return 100;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("{\"test\":\"data\"}");
            }

            // Simulate OkHttp 4.x+ isDuplex method
            public boolean isDuplex() {
                return true;
            }
        };

        Request request = new Request.Builder()
                .url("https://api.example.com/test")
                .post(duplexBody)
                .build();

        Interceptor.Chain mockChain = new MockChain(request);

        Response response = interceptor.intercept(mockChain);

        assertNotNull(response);
    }

    @Test
    void testRequestBody_contentLengthUnknown_skipsBodyLogging() throws IOException {
        LoggingInterceptor interceptor = new LoggingInterceptor(true, 4096, false);

        RequestBody unknownLengthBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("application/json");
            }

            @Override
            public long contentLength() {
                return -1; // Unknown length
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("{\"test\":\"data\"}");
            }
        };

        Request request = new Request.Builder()
                .url("https://api.example.com/test")
                .post(unknownLengthBody)
                .build();

        Interceptor.Chain mockChain = new MockChain(request);

        Response response = interceptor.intercept(mockChain);

        assertNotNull(response);
    }

    @Test
    void testRequestBody_tooLarge_skipsBodyLogging() throws IOException {
        LoggingInterceptor interceptor = new LoggingInterceptor(true, 100, false);

        RequestBody largeBody = RequestBody.create(
                "x".repeat(200),
                MediaType.get("text/plain")
        );

        Request request = new Request.Builder()
                .url("https://api.example.com/test")
                .post(largeBody)
                .build();

        Interceptor.Chain mockChain = new MockChain(request);

        Response response = interceptor.intercept(mockChain);

        assertNotNull(response);
    }

    @Test
    void testResponse_contentLengthUnknown_skipsBodyLogging() throws IOException {
        LoggingInterceptor interceptor = new LoggingInterceptor(true, 4096, false);

        Request request = new Request.Builder()
                .url("https://api.example.com/test")
                .get()
                .build();

        ResponseBody unknownLengthBody = new ResponseBody() {
            @Override
            public MediaType contentType() {
                return MediaType.get("application/json");
            }

            @Override
            public long contentLength() {
                return -1; // Unknown length
            }

            @Override
            public okio.BufferedSource source() {
                return new okio.Buffer().writeUtf8("{\"result\":\"success\"}");
            }
        };

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(unknownLengthBody)
                .build();

        Interceptor.Chain mockChain = new MockChain(request, response);

        Response result = interceptor.intercept(mockChain);

        assertNotNull(result);
        assertNotNull(result.body());
    }

    @Test
    void testResponse_binaryTooLarge_withLogBinaryBodyTrue_skipsBodyRead() throws IOException {
        LoggingInterceptor interceptor = new LoggingInterceptor(true, 100, true);

        Request request = new Request.Builder()
                .url("https://api.example.com/test")
                .get()
                .build();

        byte[] binaryData = new byte[200];
        ResponseBody binaryBody = ResponseBody.create(binaryData, MediaType.get("application/octet-stream"));

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(binaryBody)
                .build();

        Interceptor.Chain mockChain = new MockChain(request, response);

        Response result = interceptor.intercept(mockChain);

        assertNotNull(result);
        assertNotNull(result.body());
        // Body should remain readable
        assertNotNull(result.body().bytes());
    }

    @Test
    void testEnableDebugLogging() throws IOException {
        // This test ensures DEBUG logging paths are executed
        LoggingInterceptor interceptor = new LoggingInterceptor(true, 4096, false);

        RequestBody body = RequestBody.create(
                "{\"test\":\"data\"}",
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.example.com/test?api_key=secret123")
                .post(body)
                .addHeader("Authorization", "Bearer token123")
                .build();

        Interceptor.Chain mockChain = new MockChain(request);

        Response response = interceptor.intercept(mockChain);

        assertNotNull(response);
    }

    @Test
    void testResponse_sseContentType_skipsBodyLogging() throws IOException {
        LoggingInterceptor interceptor = new LoggingInterceptor(true, 4096, false);

        Request request = new Request.Builder()
                .url("https://api.example.com/sse")
                .get()
                .build();

        ResponseBody sseBody = ResponseBody.create(
                "data: test\n\n",
                MediaType.get("text/event-stream")
        );

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(sseBody)
                .build();

        Interceptor.Chain mockChain = new MockChain(request, response);

        Response result = interceptor.intercept(mockChain);

        assertNotNull(result);
        assertNotNull(result.body());
    }

    /**
     * Mock Chain implementation for testing
     */
    private static class MockChain implements Interceptor.Chain {
        private final Request request;
        private final Response response;

        MockChain(Request request) {
            this(request, null);
        }

        MockChain(Request request, Response response) {
            this.request = request;
            this.response = response != null ? response : createDefaultResponse(request);
        }

        private static Response createDefaultResponse(Request request) {
            return new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(ResponseBody.create("{\"result\":\"success\"}", MediaType.get("application/json")))
                    .build();
        }

        @Override
        public Request request() {
            return request;
        }

        @Override
        public Response proceed(Request request) throws IOException {
            return response;
        }

        @Override
        public Connection connection() {
            return null;
        }

        @Override
        public Call call() {
            return null;
        }

        @Override
        public int connectTimeoutMillis() {
            return 30000;
        }

        @Override
        public Interceptor.Chain withConnectTimeout(int timeout, java.util.concurrent.TimeUnit unit) {
            return this;
        }

        @Override
        public int readTimeoutMillis() {
            return 30000;
        }

        @Override
        public Interceptor.Chain withReadTimeout(int timeout, java.util.concurrent.TimeUnit unit) {
            return this;
        }

        @Override
        public int writeTimeoutMillis() {
            return 30000;
        }

        @Override
        public Interceptor.Chain withWriteTimeout(int timeout, java.util.concurrent.TimeUnit unit) {
            return this;
        }
    }
}
