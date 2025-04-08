package io.github.guoshiqiufeng.dify.core.client;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BaseDifyClientTest {

    // Test implementation of BaseDifyClient as it's an abstract class
    static class TestDifyClient extends BaseDifyClient {
        public TestDifyClient() {
            super();
        }

        public TestDifyClient(String baseUrl) {
            super(baseUrl);
        }

        public TestDifyClient(String baseUrl, RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
            super(baseUrl, restClientBuilder, webClientBuilder);
        }

        // Expose protected fields for testing
        public RestClient getRestClient() {
            return this.restClient;
        }

        public WebClient getWebClient() {
            return this.webClient;
        }
    }

    @Test
    void testDefaultConstructor() {
        TestDifyClient client = new TestDifyClient();
        assertNotNull(client.getRestClient());
        assertNotNull(client.getWebClient());
    }

    @Test
    void testBaseUrlConstructor() {
        String baseUrl = "https://custom-dify.example.com";
        TestDifyClient client = new TestDifyClient(baseUrl);
        assertNotNull(client.getRestClient());
        assertNotNull(client.getWebClient());
    }

    @Test
    void testFullConstructor() {
        String baseUrl = "https://custom-dify.example.com";
        RestClient.Builder restClientBuilder = RestClient.builder();
        WebClient.Builder webClientBuilder = WebClient.builder();

        TestDifyClient client = new TestDifyClient(baseUrl, restClientBuilder, webClientBuilder);
        assertNotNull(client.getRestClient());
        assertNotNull(client.getWebClient());
    }

    @Test
    void testConstantValues() {
        assertEquals("The request body can not be null.", BaseDifyClient.REQUEST_BODY_NULL_ERROR);
    }
}
