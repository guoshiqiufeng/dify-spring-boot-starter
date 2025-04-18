package io.github.guoshiqiufeng.dify.client.spring5.builder;

import io.github.guoshiqiufeng.dify.client.spring5.workflow.DifyWorkflowDefaultClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 10:42
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DifyWorkflowBuilderTest {

    @Test
    @Order(1)
    @DisplayName("Test create with default client")
    public void testCreateWithDefaultClient() {
        DifyWorkflow difyWorkflow = DifyWorkflowBuilder.create(
                DifyWorkflowBuilder.DifyWorkflowClientBuilder
                        .create());

        assertNotNull(difyWorkflow, "DifyWorkflow should not be null");
    }

    @Test
    @Order(2)
    @DisplayName("Test create with default client by baseUrl")
    public void testCreateWithDefaultClientByBaseUrl() {
        DifyWorkflow difyWorkflow = DifyWorkflowBuilder.create(
                DifyWorkflowBuilder.DifyWorkflowClientBuilder
                        .create("https://custom-dify-api.example.com"));

        assertNotNull(difyWorkflow, "DifyWorkflow should not be null");
    }

    @Test
    @Order(3)
    @DisplayName("Test create with builder - empty build")
    public void testCreateWithEmptyBuilder() {
        DifyWorkflowClient client = DifyWorkflowBuilder.DifyWorkflowClientBuilder
                .builder()
                .build();

        assertNotNull(client, "DifyWorkflowClient should not be null");
        assertInstanceOf(DifyWorkflowDefaultClient.class, client, "Client should be instance of DifyWorkflowDefaultClient");
    }

    @Test
    @Order(4)
    @DisplayName("Test create with builder - set baseUrl")
    public void testCreateWithBaseUrl() {
        String baseUrl = "https://custom-dify-api.example.com";

        DifyWorkflowClient client = DifyWorkflowBuilder.DifyWorkflowClientBuilder
                .builder()
                .baseUrl(baseUrl)
                .build();

        assertNotNull(client, "DifyWorkflowClient should not be null");
    }

    @Test
    @Order(5)
    @DisplayName("Test create with builder - set clientConfig")
    public void testCreateWithClientConfig() {
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);

        DifyWorkflowClient client = DifyWorkflowBuilder.DifyWorkflowClientBuilder
                .builder()
                .clientConfig(clientConfig)
                .build();

        assertNotNull(client, "DifyWorkflowClient should not be null");
    }

    @Test
    @Order(7)
    @DisplayName("Test create with builder - set webClientBuilder")
    public void testCreateWithWebClientBuilder() {
        WebClient.Builder webClientBuilder = WebClient.builder();

        DifyWorkflowClient client = DifyWorkflowBuilder.DifyWorkflowClientBuilder
                .builder()
                .webClientBuilder(webClientBuilder)
                .build();

        assertNotNull(client, "DifyWorkflowClient should not be null");
    }

    @Test
    @Order(8)
    @DisplayName("Test create with builder - set all parameters")
    public void testCreateWithAllParameters() {
        String baseUrl = "https://custom-dify-api.example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);
        WebClient.Builder webClientBuilder = WebClient.builder();

        DifyWorkflowClient client = DifyWorkflowBuilder.DifyWorkflowClientBuilder
                .builder()
                .baseUrl(baseUrl)
                .clientConfig(clientConfig)
                .webClientBuilder(webClientBuilder)
                .build();

        assertNotNull(client, "DifyWorkflowClient should not be null");
    }

    @Test
    @Order(9)
    @DisplayName("Test create DifyWorkflow with full builder chain")
    public void testCreateDifyWorkflowWithFullBuilderChain() {
        DifyWorkflow difyWorkflow = DifyWorkflowBuilder.create(
                DifyWorkflowBuilder.DifyWorkflowClientBuilder
                        .builder()
                        .baseUrl("https://custom-dify-api.example.com")
                        .clientConfig(new DifyProperties.ClientConfig())
                        .webClientBuilder(WebClient.builder())
                        .build());

        assertNotNull(difyWorkflow, "DifyWorkflow should not be null");
    }
}
