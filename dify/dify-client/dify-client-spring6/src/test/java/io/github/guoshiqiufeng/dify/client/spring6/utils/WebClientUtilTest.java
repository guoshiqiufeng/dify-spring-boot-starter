/// *
// * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package io.github.guoshiqiufeng.dify.client.spring6.utils;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.reactive.function.client.ClientResponse;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import static org.mockito.Mockito.when;
//
///**
// * @author yanghq
// * @version 1.0
// * @since 2025/4/21 14:20
// */
//public class WebClientUtilTest {
//
//    @Test
//    @DisplayName("Test exceptionFunction with 400 Bad Request")
//    public void testExceptionFunctionWithBadRequest() {
//        // Create a mock ClientResponse
//        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
//        when(clientResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
//        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Bad Request Error"));
//
//        // Call the exceptionFunction
//        Mono<? extends Throwable> result = WebClientUtil.exceptionFunction(clientResponse);
//
//        // Verify the result - the exceptionFunction returns Mono.error() which triggers onError
//        StepVerifier.create(result.flatMap(Mono::error))
//                .expectErrorMatches(throwable ->
//                        throwable instanceof RuntimeException &&
//                                throwable.getMessage().contains("400") &&
//                                throwable.getMessage().contains("Bad Request") &&
//                                throwable.getMessage().contains("Bad Request Error"))
//                .verify();
//    }
//
//    @Test
//    @DisplayName("Test exceptionFunction with 404 Not Found")
//    public void testExceptionFunctionWithNotFound() {
//        // Create a mock ClientResponse
//        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
//        when(clientResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);
//        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Resource Not Found"));
//
//        // Call the exceptionFunction
//        Mono<? extends Throwable> result = WebClientUtil.exceptionFunction(clientResponse);
//
//        // Verify the result - the exceptionFunction returns Mono.error() which triggers onError
//        StepVerifier.create(result.flatMap(Mono::error))
//                .expectErrorMatches(throwable ->
//                        throwable instanceof RuntimeException &&
//                                throwable.getMessage().contains("404") &&
//                                throwable.getMessage().contains("Not Found") &&
//                                throwable.getMessage().contains("Resource Not Found"))
//                .verify();
//    }
//
//    @Test
//    @DisplayName("Test exceptionFunction with 500 Internal Server Error")
//    public void testExceptionFunctionWithInternalServerError() {
//        // Create a mock ClientResponse
//        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
//        when(clientResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
//        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Internal Server Error"));
//
//        // Call the exceptionFunction
//        Mono<? extends Throwable> result = WebClientUtil.exceptionFunction(clientResponse);
//
//        // Verify the result - the exceptionFunction returns Mono.error() which triggers onError
//        StepVerifier.create(result.flatMap(Mono::error))
//                .expectErrorMatches(throwable ->
//                        throwable instanceof RuntimeException &&
//                                throwable.getMessage().contains("500") &&
//                                throwable.getMessage().contains("Internal Server Error"))
//                .verify();
//    }
//
//    @Test
//    @DisplayName("Test exceptionFunction with empty error body")
//    public void testExceptionFunctionWithEmptyErrorBody() {
//        // Create a mock ClientResponse
//        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
//        when(clientResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
//        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(""));
//
//        // Call the exceptionFunction
//        Mono<? extends Throwable> result = WebClientUtil.exceptionFunction(clientResponse);
//
//        // Verify the result - the exceptionFunction returns Mono.error() which triggers onError
//        StepVerifier.create(result.flatMap(Mono::error))
//                .expectErrorMatches(throwable ->
//                        throwable instanceof RuntimeException &&
//                                throwable.getMessage().contains("400") &&
//                                throwable.getMessage().contains("Bad Request"))
//                .verify();
//    }
//}
