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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.http;

import io.github.guoshiqiufeng.dify.client.core.enums.HttpMethod;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.web.util.DefaultUriBuilder;
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.http.HttpRequestBuilder;
import io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler;
import io.github.guoshiqiufeng.dify.client.core.map.LinkedMultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.map.MultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.web.client.ResponseSpec;
import io.github.guoshiqiufeng.dify.client.core.web.util.UriBuilder;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import io.github.guoshiqiufeng.dify.client.integration.okhttp.publisher.OkHttpStreamPublisher;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * OkHttp-based implementation of HttpRequestBuilder.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class OkHttpRequestBuilder implements HttpRequestBuilder {

    private static final Logger log = LoggerFactory.getLogger(OkHttpRequestBuilder.class);
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final JavaHttpClient client;
    private final JsonMapper jsonMapper;
    private final String method;

    private URI uri;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> cookies = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    private Object body;
    private Map<String, Object> multipartData;

    /**
     * Constructor.
     *
     * @param client     Java HTTP client
     * @param jsonMapper JSON mapper
     * @param method     HTTP method
     */
    public OkHttpRequestBuilder(JavaHttpClient client, JsonMapper jsonMapper, String method) {
        this.client = client;
        this.jsonMapper = jsonMapper;
        this.method = method;
    }

    @Override
    public HttpRequestBuilder uri(String uri) {
        this.uri = new DefaultUriBuilder().path(uri).build();
        return this;
    }

    @Override
    public HttpRequestBuilder uri(String uri, Object... uriParams) {
        this.uri = new DefaultUriBuilder().path(uri).build(uriParams);
        return this;
    }

    @Override
    public HttpRequestBuilder uri(Consumer<UriBuilder> uriBuilderConsumer) {
        DefaultUriBuilder uriBuilder = new DefaultUriBuilder();
        uriBuilderConsumer.accept(uriBuilder);
        this.uri = uriBuilder.build();
        return this;
    }

    @Override
    public HttpRequestBuilder header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    public HttpRequestBuilder headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    @Override
    public HttpRequestBuilder headers(Consumer<HttpHeaders> headersConsumer) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headersConsumer.accept(httpHeaders);
        // Convert HttpHeaders to single value map (using first value)
        httpHeaders.forEach((key, values) -> {
            if (values != null && !values.isEmpty()) {
                this.headers.put(key, values.get(0));
            }
        });
        return this;
    }

    @Override
    public HttpRequestBuilder cookies(Consumer<MultiValueMap<String, String>> cookiesConsumer) {
        MultiValueMap<String, String> httpCookies = new LinkedMultiValueMap<>();
        cookiesConsumer.accept(httpCookies);
        // Convert HttpHeaders to single value map (using first value)
        httpCookies.forEach((key, values) -> {
            if (values != null && !values.isEmpty()) {
                this.cookies.put(key, values.get(0));
            }
        });
        return this;
    }

    @Override
    public HttpRequestBuilder queryParam(String name, String value) {
        this.queryParams.put(name, value);
        return this;
    }

    @Override
    public HttpRequestBuilder queryParams(Map<String, String> params) {
        this.queryParams.putAll(params);
        return this;
    }

    @Override
    public HttpRequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public HttpRequestBuilder multipart(Map<String, Object> formData) {
        this.multipartData = formData;
        return this;
    }

    @Override
    public <T> T execute(Class<T> responseType) {
        Request request = buildRequest();
        try (Response response = client.getOkHttpClient().newCall(request).execute()) {
            return handleResponse(response, responseType);
        } catch (IOException e) {
            throw new HttpClientException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> T execute(io.github.guoshiqiufeng.dify.client.core.http.TypeReference<T> typeReference) {
        Request request = buildRequest();
        try (Response response = client.getOkHttpClient().newCall(request).execute()) {
            return handleResponse(response, typeReference);
        } catch (IOException e) {
            throw new HttpClientException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> HttpResponse<T> executeForResponse(Class<T> responseType) {
        Request request = buildRequest();
        try (Response response = client.getOkHttpClient().newCall(request).execute()) {
            T responseBody = handleResponse(response, responseType);
            return buildHttpResponse(response, responseBody);
        } catch (IOException e) {
            throw new HttpClientException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> HttpResponse<T> executeForResponse(io.github.guoshiqiufeng.dify.client.core.http.TypeReference<T> typeReference) {
        Request request = buildRequest();
        try (Response response = client.getOkHttpClient().newCall(request).execute()) {
            T responseBody = handleResponse(response, typeReference);
            return buildHttpResponse(response, responseBody);
        } catch (IOException e) {
            throw new HttpClientException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> Flux<T> stream(Class<T> responseType) {
        Request request = buildRequest();
        OkHttpStreamPublisher<T> publisher = new OkHttpStreamPublisher<>(client.getOkHttpClient(), request, jsonMapper, responseType);

        return Flux.create(publisher::stream);
    }

    @Override
    public int executeForStatus() {
        Request request = buildRequest();
        try (Response response = client.getOkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleError(response);
            }
            return response.code();
        } catch (IOException e) {
            throw new HttpClientException("HTTP request failed: " + e.getMessage(), e);
        }
    }

    @Override
    public ResponseSpec retrieve() {
        return new OkHttpResponseSpec();
    }

    /**
     * OkHttp implementation of ResponseSpec.
     */
    private class OkHttpResponseSpec implements ResponseSpec {

        private final List<ResponseErrorHandler> errorHandlers = new ArrayList<>();

        @Override
        public ResponseSpec onStatus(ResponseErrorHandler errorHandler) {
            this.errorHandlers.add(errorHandler);
            return this;
        }

        @Override
        public <T> T body(Class<T> responseType) {
            HttpResponse<T> response = toEntity(responseType);
            handleErrors(response);
            return response.getBody();
        }

        @Override
        public <T> T body(io.github.guoshiqiufeng.dify.client.core.http.TypeReference<T> typeReference) {
            HttpResponse<T> response = toEntity(typeReference);
            handleErrors(response);
            return response.getBody();
        }

        @Override
        public <T> HttpResponse<T> toEntity(Class<T> responseType) {
            Request request = buildRequest();
            try (Response response = client.getOkHttpClient().newCall(request).execute()) {
                T responseBody = handleResponse(response, responseType, false);
                HttpResponse<T> httpResponse = buildHttpResponse(response, responseBody);
                handleErrors(httpResponse);
                return httpResponse;
            } catch (IOException e) {
                throw new HttpClientException("HTTP request failed: " + e.getMessage(), e);
            }
        }

        @Override
        public <T> HttpResponse<T> toEntity(io.github.guoshiqiufeng.dify.client.core.http.TypeReference<T> typeReference) {
            Request request = buildRequest();
            try (Response response = client.getOkHttpClient().newCall(request).execute()) {
                T responseBody = handleResponse(response, typeReference, false);
                HttpResponse<T> httpResponse = buildHttpResponse(response, responseBody);
                handleErrors(httpResponse);
                return httpResponse;
            } catch (IOException e) {
                throw new HttpClientException("HTTP request failed: " + e.getMessage(), e);
            }
        }

        @Override
        public HttpResponse<Void> toBodilessEntity() {
            HttpResponse<Void> response = toEntity(Void.class);
            handleErrors(response);
            return response;
        }

        @Override
        public <T> Flux<T> bodyToFlux(Class<T> responseType) {
            Request request = buildRequest();
            OkHttpStreamPublisher<T> publisher = new OkHttpStreamPublisher<>(client.getOkHttpClient(), request, jsonMapper, responseType);
            return Flux.create(publisher::stream);
        }

        @Override
        public <T> Flux<T> bodyToFlux(io.github.guoshiqiufeng.dify.client.core.http.TypeReference<T> typeReference) {
            // For OkHttp, we need to use the Class-based approach
            // TypeReference streaming is not directly supported, so we'll throw an exception
            throw new UnsupportedOperationException("bodyToFlux with TypeReference is not supported for OkHttp. Use Class-based bodyToFlux instead.");
        }

        /**
         * Handle errors using registered error handlers.
         *
         * @param response the HTTP response
         */
        private void handleErrors(HttpResponse<?> response) {
            for (ResponseErrorHandler handler : errorHandlers) {
                if (handler.getStatusPredicate().test(response.getStatusCode())) {
                    try {
                        handler.handle(response);
                    } catch (Exception e) {
                        if (e instanceof RuntimeException) {
                            throw (RuntimeException) e;
                        }
                        throw new HttpClientException("Error handler failed: " + e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * Build OkHttp Request object.
     *
     * @return OkHttp Request
     */
    private Request buildRequest() {
        // Build URL - handle potential double slash when baseUrl ends with / and uri starts with /
        String baseUrl = client.getBaseUrl();
        String path = uri != null ? uri.toString() : "";
        String fullUrl = baseUrl.endsWith("/") && path.startsWith("/")
            ? baseUrl + path.substring(1)
            : baseUrl + path;
        HttpUrl.Builder urlBuilder = HttpUrl.parse(fullUrl).newBuilder();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        HttpUrl url = urlBuilder.build();

        // Build request body
        RequestBody requestBody = buildRequestBody();

        // Build request
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .method(method, requestBody);

        // Add headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getValue() != null) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // Add cookies as Cookie header
        if (!cookies.isEmpty()) {
            String cookieHeader = cookies.entrySet().stream()
                    .filter(entry -> entry.getValue() != null)
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(java.util.stream.Collectors.joining("; "));
            if (!cookieHeader.isEmpty()) {
                requestBuilder.addHeader("Cookie", cookieHeader);
            }
        }

        return requestBuilder.build();
    }

    /**
     * Build request body based on content type.
     *
     * @return RequestBody or null
     */
    private RequestBody buildRequestBody() {
        // Check if Content-Type is multipart/form-data
        String contentType = headers.get("Content-Type");
        boolean isMultipart = contentType != null && contentType.toLowerCase().contains("multipart/form-data");

        if (isMultipart && body instanceof Map) {
            // Handle multipart data from MultipartBodyBuilder
            Map<?, ?> bodyMap = (Map<?, ?>) body;
            if (!bodyMap.isEmpty()) {
                Object firstValue = bodyMap.values().iterator().next();
                if (firstValue instanceof io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part) {
                    return buildMultipartBodyFromParts(bodyMap);
                }
            }
        }

        if (multipartData != null) {
            return buildMultipartBody();
        } else if (body != null) {
            return buildJsonBody();
        } else if (HttpMethod.POST.name().equals(method) ||
                HttpMethod.PUT.name().equals(method) || HttpMethod.PATCH.name().equals(method)) {
            // Empty body for POST/PUT/PATCH
            return RequestBody.create("", null);
        }
        return null;
    }

    /**
     * Build multipart body from MultipartBodyBuilder.Part map.
     *
     * @param bodyMap map of parts
     * @return RequestBody
     */
    private RequestBody buildMultipartBodyFromParts(Map<?, ?> bodyMap) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        @SuppressWarnings("unchecked")
        Map<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part> parts =
            (Map<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part>) bodyMap;

        for (Map.Entry<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part> entry : parts.entrySet()) {
            io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part part = entry.getValue();
            Object partValue = part.getValue();

            if (partValue instanceof byte[]) {
                // Handle file upload
                byte[] bytes = (byte[]) partValue;
                String filename = extractFilename(part.getHeader("Content-Disposition"));
                String partContentType = part.getHeader("Content-Type");
                MediaType mediaType = partContentType != null ?
                    MediaType.parse(partContentType) : MediaType.parse("application/octet-stream");

                builder.addFormDataPart(entry.getKey(), filename,
                    RequestBody.create(bytes, mediaType));
            } else if (partValue instanceof String) {
                builder.addFormDataPart(entry.getKey(), (String) partValue);
            } else if (partValue instanceof Number || partValue instanceof Boolean) {
                builder.addFormDataPart(entry.getKey(), String.valueOf(partValue));
            } else {
                // For complex objects, serialize to JSON
                try {
                    String json = client.getSkipNull() ? jsonMapper.toJsonIgnoreNull(partValue) : jsonMapper.toJson(partValue);
                    builder.addFormDataPart(entry.getKey(), json);
                } catch (Exception e) {
                    throw new HttpClientException("Failed to serialize multipart field to JSON: " + entry.getKey(), e);
                }
            }
        }

        return builder.build();
    }

    /**
     * Extract filename from Content-Disposition header.
     *
     * @param contentDisposition Content-Disposition header value
     * @return filename, or "file" if not found
     */
    private String extractFilename(String contentDisposition) {
        if (contentDisposition == null) {
            return "file";
        }

        // Parse: form-data; name="file"; filename="test.txt"
        int filenameIndex = contentDisposition.indexOf("filename=\"");
        if (filenameIndex != -1) {
            int start = filenameIndex + 10; // length of "filename=\""
            int end = contentDisposition.indexOf("\"", start);
            if (end != -1) {
                return contentDisposition.substring(start, end);
            }
        }

        return "file";
    }

    /**
     * Build JSON request body.
     *
     * @return RequestBody
     */
    private RequestBody buildJsonBody() {
        try {
            String json = client.getSkipNull() ? jsonMapper.toJsonIgnoreNull(body) : jsonMapper.toJson(body);
            return RequestBody.create(json, JSON_MEDIA_TYPE);
        } catch (Exception e) {
            throw new HttpClientException("Failed to serialize request body to JSON", e);
        }
    }

    /**
     * Build multipart form data body.
     *
     * @return RequestBody
     */
    private RequestBody buildMultipartBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (Map.Entry<String, Object> entry : multipartData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                builder.addFormDataPart(key, (String) value);
            } else if (value instanceof byte[]) {
                builder.addFormDataPart(key, "file", RequestBody.create((byte[]) value, MediaType.parse("application/octet-stream")));
            } else {
                // Convert to JSON string
                try {
                    String json = client.getSkipNull() ? jsonMapper.toJsonIgnoreNull(value) : jsonMapper.toJson(value);
                    builder.addFormDataPart(key, json);
                } catch (Exception e) {
                    throw new HttpClientException("Failed to serialize multipart field to JSON: " + key, e);
                }
            }
        }

        return builder.build();
    }

    /**
     * Handle response and deserialize body.
     *
     * @param response     OkHttp response
     * @param responseType response type class
     * @param <T>          response type
     * @return deserialized response body
     * @throws IOException if deserialization fails
     */
    private <T> T handleResponse(Response response, Class<T> responseType) throws IOException {
        return handleResponse(response, responseType, true);
    }

    /**
     * Handle response and deserialize body.
     *
     * @param response     OkHttp response
     * @param responseType response type class
     * @param checkError   whether to check for errors
     * @param <T>          response type
     * @return deserialized response body
     * @throws IOException if deserialization fails
     */
    private <T> T handleResponse(Response response, Class<T> responseType, boolean checkError) throws IOException {
        if (checkError && !response.isSuccessful()) {
            handleError(response);
        }

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            return null;
        }

        String bodyString = responseBody.string();
        if (bodyString.isEmpty()) {
            return null;
        }

        // Handle byte array response
        if (responseType == byte[].class) {
            @SuppressWarnings("unchecked")
            T result = (T) bodyString.getBytes();
            return result;
        }

        // Handle String response
        if (responseType == String.class) {
            @SuppressWarnings("unchecked")
            T result = (T) bodyString;
            return result;
        }

        // Deserialize JSON
        try {
            return jsonMapper.fromJson(bodyString, responseType);
        } catch (Exception e) {
            throw new HttpClientException("Failed to deserialize response body", e);
        }
    }

    /**
     * Handle response with generic type reference.
     *
     * @param response      OkHttp response
     * @param typeReference type reference
     * @param <T>           response type
     * @return deserialized response body
     * @throws IOException if deserialization fails
     */
    private <T> T handleResponse(Response response, io.github.guoshiqiufeng.dify.client.core.http.TypeReference<T> typeReference) throws IOException {
        return handleResponse(response, typeReference, true);
    }

    /**
     * Handle response with generic type reference.
     *
     * @param response      OkHttp response
     * @param typeReference type reference
     * @param checkError    whether to check for errors
     * @param <T>           response type
     * @return deserialized response body
     * @throws IOException if deserialization fails
     */
    private <T> T handleResponse(Response response, io.github.guoshiqiufeng.dify.client.core.http.TypeReference<T> typeReference, boolean checkError) throws IOException {
        if (checkError && !response.isSuccessful()) {
            handleError(response);
        }

        ResponseBody responseBody = response.body();
        if (responseBody == null) {
            return null;
        }

        String bodyString = responseBody.string();
        if (bodyString.isEmpty()) {
            return null;
        }

        // Deserialize JSON with type reference
        try {
            return jsonMapper.fromJson(bodyString, typeReference);
        } catch (Exception e) {
            throw new HttpClientException("Failed to deserialize response body", e);
        }
    }

    /**
     * Build HttpResponse wrapper.
     *
     * @param response     OkHttp response
     * @param responseBody deserialized response body
     * @param <T>          response body type
     * @return HttpResponse
     */
    private <T> HttpResponse<T> buildHttpResponse(Response response, T responseBody) {
        Map<String, List<String>> headers = new HashMap<>(response.headers().names().size());
        for (String name : response.headers().names()) {
            headers.put(name, response.headers().values(name));
        }

        return HttpResponse.<T>builder()
                .statusCode(response.code())
                .headers(headers)
                .body(responseBody)
                .build();
    }

    /**
     * Handle HTTP error response.
     *
     * @param response OkHttp response
     * @throws IOException if reading response body fails
     */
    private void handleError(Response response) throws IOException {
        int statusCode = response.code();
        String responseBody = response.body() != null ? response.body().string() : "";

        log.error("【Dify】HTTP request failed: {} {} - Status: {}, Body: {}",
                method, response.request().url(), statusCode, responseBody);

        throw new HttpClientException(statusCode, responseBody);
    }

    /**
     * Replace URI path variables with values.
     * Only replaces placeholders in the path portion, not in query parameters.
     * Query parameters with placeholders are extracted and matched with remaining uriParams.
     *
     * @param uri       URI template with placeholders (e.g., "/users/{id}?page={page}&limit={limit}")
     * @param uriParams parameter values for both path and query variables
     * @return URI with replaced path variables
     */
    private String replaceUriVariables(String uri, Object... uriParams) {
        // Split URI into path and query parts
        int queryIndex = uri.indexOf('?');
        if (queryIndex == -1) {
            // No query parameters, replace all placeholders in path
            String result = uri;
            for (Object param : uriParams) {
                result = result.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
            }
            return result;
        }

        // Has query parameters - handle path and query separately
        String pathPart = uri.substring(0, queryIndex);
        String queryPart = uri.substring(queryIndex + 1);

        // Count placeholders in path
        int pathPlaceholderCount = 0;
        String tempPath = pathPart;
        while (tempPath.contains("{") && tempPath.contains("}")) {
            int start = tempPath.indexOf("{");
            int end = tempPath.indexOf("}", start);
            if (end > start) {
                pathPlaceholderCount++;
                tempPath = tempPath.substring(end + 1);
            } else {
                break;
            }
        }

        // Replace path variables
        String resultPath = pathPart;
        int paramIndex = 0;
        for (int i = 0; i < pathPlaceholderCount && paramIndex < uriParams.length; i++, paramIndex++) {
            resultPath = resultPath.replaceFirst("\\{[^}]+\\}", String.valueOf(uriParams[paramIndex]));
        }

        // Parse query parameters
        String[] queryPairs = queryPart.split("&");
        for (String pair : queryPairs) {
            int eqIndex = pair.indexOf('=');
            if (eqIndex > 0) {
                String key = pair.substring(0, eqIndex);
                String value = pair.substring(eqIndex + 1);

                // Check if value is a placeholder
                if (value.startsWith("{") && value.endsWith("}")) {
                    // Match with remaining uriParams
                    if (paramIndex < uriParams.length) {
                        Object paramValue = uriParams[paramIndex++];
                        // Only add non-null values to queryParams
                        if (paramValue != null) {
                            this.queryParams.put(key, String.valueOf(paramValue));
                        }
                    }
                } else {
                    // Static query parameter value
                    this.queryParams.put(key, value);
                }
            }
        }

        // Return only the path portion
        return resultPath;
    }
}
