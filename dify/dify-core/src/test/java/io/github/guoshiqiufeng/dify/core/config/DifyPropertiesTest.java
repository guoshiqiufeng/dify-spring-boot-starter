package io.github.guoshiqiufeng.dify.core.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DifyPropertiesTest {

    @Test
    void testProperties() {
        DifyProperties properties = new DifyProperties();
        properties.setUrl("https://test-dify.example.com");

        assertEquals("https://test-dify.example.com", properties.getUrl());

        DifyProperties.Dataset dataset = new DifyProperties.Dataset();
        dataset.setApiKey("test-api-key");
        properties.setDataset(dataset);

        assertEquals("test-api-key", properties.getDataset().getApiKey());

        DifyProperties.Server server = new DifyProperties.Server();
        server.setEmail("test@example.com");
        server.setPassword("test-password");
        properties.setServer(server);

        assertEquals("test@example.com", properties.getServer().getEmail());
        assertEquals("test-password", properties.getServer().getPassword());
    }

    @Test
    void testDatasetConstructor() {
        DifyProperties.Dataset dataset = new DifyProperties.Dataset("test-api-key");
        assertEquals("test-api-key", dataset.getApiKey());

        // Test no-args constructor
        DifyProperties.Dataset emptyDataset = new DifyProperties.Dataset();
        assertNull(emptyDataset.getApiKey());
    }

    @Test
    void testServerConstructor() {
        DifyProperties.Server server = new DifyProperties.Server("test@example.com", "test-password");
        assertEquals("test@example.com", server.getEmail());
        assertEquals("test-password", server.getPassword());

        // Test no-args constructor
        DifyProperties.Server emptyServer = new DifyProperties.Server();
        assertNull(emptyServer.getEmail());
        assertNull(emptyServer.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        DifyProperties properties1 = new DifyProperties();
        properties1.setUrl("https://test-dify.example.com");
        properties1.setDataset(new DifyProperties.Dataset("api-key-1"));
        properties1.setServer(new DifyProperties.Server("user1@example.com", "password1"));

        DifyProperties properties2 = new DifyProperties();
        properties2.setUrl("https://test-dify.example.com");
        properties2.setDataset(new DifyProperties.Dataset("api-key-1"));
        properties2.setServer(new DifyProperties.Server("user1@example.com", "password1"));

        DifyProperties properties3 = new DifyProperties();
        properties3.setUrl("https://different-url.example.com");
        properties3.setDataset(new DifyProperties.Dataset("api-key-2"));
        properties3.setServer(new DifyProperties.Server("user2@example.com", "password2"));

        // Test equals
        assertEquals(properties1, properties2);
        assertNotEquals(properties1, properties3);

        // Test hashCode
        assertEquals(properties1.hashCode(), properties2.hashCode());
        assertNotEquals(properties1.hashCode(), properties3.hashCode());
    }
}
