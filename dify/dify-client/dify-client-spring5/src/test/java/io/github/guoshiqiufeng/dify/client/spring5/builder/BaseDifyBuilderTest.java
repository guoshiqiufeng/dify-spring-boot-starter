package io.github.guoshiqiufeng.dify.client.spring5.builder;

import io.github.guoshiqiufeng.dify.core.client.BaseDifyClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 10:19
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseDifyBuilderTest {

    // Concrete implementation for testing
    static class TestBuilder extends BaseDifyBuilder<TestBuilder> {
        private String testProperty;

        public TestBuilder testProperty(String testProperty) {
            this.testProperty = testProperty;
            return this;
        }

        public String getTestProperty() {
            return testProperty;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public DifyProperties.ClientConfig getClientConfig() {
            return clientConfig;
        }

        public WebClient.Builder getWebClientBuilder() {
            return webClientBuilder;
        }

        public void runInitDefaults() {
            initDefaults();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test builder fluent interface")
    public void testBuilderFluentInterface() {
        String baseUrl = "https://custom-dify-api.example.com";
        String testProperty = "test-value";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();

        WebClient.Builder webClientBuilder = WebClient.builder();

        TestBuilder builder = new TestBuilder()
                .baseUrl(baseUrl)
                .testProperty(testProperty)
                .clientConfig(clientConfig)
                .webClientBuilder(webClientBuilder);

        assertEquals(baseUrl, builder.getBaseUrl(), "baseUrl should match");
        assertEquals(testProperty, builder.getTestProperty(), "testProperty should match");
        assertEquals(clientConfig, builder.getClientConfig(), "clientConfig should match");
        assertEquals(webClientBuilder, builder.getWebClientBuilder(), "webClientBuilder should match");
    }

    @Test
    @Order(2)
    @DisplayName("Test initDefaults method")
    public void testInitDefaults() {
        TestBuilder builder = new TestBuilder();

        // Before init, everything should be null
        assertNull(builder.getBaseUrl(), "baseUrl should be null before init");
        assertNull(builder.getClientConfig(), "clientConfig should be null before init");
        assertNull(builder.getWebClientBuilder(), "webClientBuilder should be null before init");

        builder.runInitDefaults();

        // After init, default values should be set
        assertEquals(BaseDifyClient.DEFAULT_BASE_URL, builder.getBaseUrl(),
                "baseUrl should be set to default value");
        assertNotNull(builder.getClientConfig(), "clientConfig should not be null after init");
        assertNotNull(builder.getWebClientBuilder(), "webClientBuilder should not be null after init");
    }

    @Test
    @Order(3)
    @DisplayName("Test initDefaults respects user set values")
    public void testInitDefaultsRespectsUserValues() {
        String customBaseUrl = "https://custom-dify-api.example.com";
        DifyProperties.ClientConfig customClientConfig = new DifyProperties.ClientConfig();
        customClientConfig.setSkipNull(false);

        WebClient.Builder customWebClientBuilder = WebClient.builder();

        TestBuilder builder = new TestBuilder()
                .baseUrl(customBaseUrl)
                .clientConfig(customClientConfig)
                .webClientBuilder(customWebClientBuilder);

        builder.runInitDefaults();

        // Custom values should be preserved
        assertEquals(customBaseUrl, builder.getBaseUrl(), "baseUrl should keep custom value");
        assertEquals(customClientConfig, builder.getClientConfig(), "clientConfig should keep custom value");
        assertEquals(customWebClientBuilder, builder.getWebClientBuilder(), "webClientBuilder should keep custom value");
    }
}
