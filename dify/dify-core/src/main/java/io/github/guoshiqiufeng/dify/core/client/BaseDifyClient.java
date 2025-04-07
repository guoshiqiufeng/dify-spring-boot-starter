package io.github.guoshiqiufeng.dify.core.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/7 16:10
 */
@Slf4j
public abstract class BaseDifyClient {

    public static final String REQUEST_BODY_NULL_ERROR = "The request body can not be null.";

    private static final String DEFAULT_BASE_URL = "http://localhost";

    protected final ResponseErrorHandler responseErrorHandler;

    protected final RestClient restClient;

    protected final WebClient webClient;

    public BaseDifyClient() {
        this(DEFAULT_BASE_URL);
    }

    public BaseDifyClient(String baseUrl) {
        this(DEFAULT_BASE_URL, RestClient.builder(), WebClient.builder());
    }

    public BaseDifyClient(String baseUrl, RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
        this.responseErrorHandler = new DifyChatResponseErrorHandler();

        Consumer<HttpHeaders> defaultHeaders = headers -> {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        };

        this.restClient = restClientBuilder.baseUrl(baseUrl).defaultHeaders(defaultHeaders).build();

        this.webClient = webClientBuilder.baseUrl(baseUrl).defaultHeaders(defaultHeaders).build();
    }

    private static class DifyChatResponseErrorHandler implements ResponseErrorHandler {


        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().isError();
        }


        public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
            if (response.getStatusCode().isError()) {
                int statusCode = response.getStatusCode().value();
                String statusText = response.getStatusText();
                String message = StreamUtils.copyToString(response.getBody(), java.nio.charset.StandardCharsets.UTF_8);
                log.warn(String.format("URI: %s, Method: %s, Status: [%s] %s - %s", url, method, statusCode, statusText, message));
                throw new RuntimeException(String.format("[%s] %s - %s", statusCode, statusText, message));
            }
        }

    }
}
