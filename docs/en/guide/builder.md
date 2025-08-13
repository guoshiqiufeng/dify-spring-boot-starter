---
lang: en-US
title: Client Builder
description: Client Builder
---

# Client Builder

The Dify Java client library uses the builder pattern to make it easy to create and configure the various clients needed
to interact with the Dify API. If you are using a non-SpringBoot project or don't want to use the default constructor
instance, you can use the methods below for custom construction.

## Overview

The Dify client library includes several builders for different API endpoints:

- `DifyChatBuilder` - For chat-related operations
- `DifyDatasetBuilder` - For dataset operations
- `DifyWorkflowBuilder` - For workflow operations
- `DifyServerBuilder` - For server administration operations

## Using the Builders

### Basic Usage

The quickest way to create a client is using the static factory methods:

```java
// Create a chat client with default settings
DifyChat difyChat = DifyChatBuilder.create(
        DifyChatBuilder.DifyChatClientBuilder.create());

// Create a chat client with a custom base URL
DifyChat difyChat = DifyChatBuilder.create(
        DifyChatBuilder.DifyChatClientBuilder.create("https://your-dify-api.example.com"));
```

### Using the Fluent Builder API

For more control, you can use the fluent builder API:

```java
DifyChat difyChat = DifyChatBuilder.create(
        DifyChatBuilder.DifyChatClientBuilder
                .builder()
                .baseUrl("https://your-dify-api.example.com")
                .clientConfig(new DifyProperties.ClientConfig())
                .build());
```

### Server Client with Authentication

The server client requires authentication credentials:

```java

DifyDataset difyDataset = DifyDatasetBuilder.create(
        DifyDatasetBuilder.DifyDatasetClientBuilder
                .builder()
                .baseUrl("https://custom-dify-api.example.com")
                .apiKey('ak-aaaa')
                .build());
```

The server client requires authentication credentials:

```java
DifyProperties.Server serverProps = new DifyProperties.Server("admin@example.com", "password");

DifyServer difyServer = DifyServerBuilder.create(
        DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .serverProperties(serverProps)
                .build());
```

## Common Configuration Options

All builders support these common configuration options:

- `baseUrl(String)` - Set the base URL for the Dify API
- `clientConfig(DifyProperties.ClientConfig)` - Configure client behavior like timeout, retry policy, etc.
- `restClientBuilder(RestClient.Builder)` - Provide a custom Spring RestClient builder
- `webClientBuilder(WebClient.Builder)` - Provide a custom Spring WebClient builder

## Advanced Usage

### Customizing HTTP Clients

You can customize the HTTP clients used by the Dify clients:

```java
// Configure a custom RestClient with special settings
RestClient.Builder customRestClientBuilder = RestClient.builder()
        .requestFactory(/* ... */)
        .defaultHeaders(headers -> {
            headers.add("Custom-Header", "Value");
        });

DifyDataset difyDataset = DifyDatasetBuilder.create(
        DifyDatasetBuilder.DifyDatasetClientBuilder
                .builder()
                .restClientBuilder(customRestClientBuilder)
                .build());
```

### Setting Client Configuration

You can configure how the client behaves:

```java
// Configure client behavior
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setSkipNull(false);

DifyWorkflow difyWorkflow = DifyWorkflowBuilder.create(
        DifyWorkflowBuilder.DifyWorkflowClientBuilder
                .builder()
                .clientConfig(clientConfig)
                .build());
```

## Example: Complete Configuration

Here's an example with full configuration:

```java
DifyServer difyServer = DifyServerBuilder.create(
        DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .baseUrl("https://your-dify-api.example.com")
                .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
                .serverToken(new DifyServerTokenDefault())
                .clientConfig(new DifyProperties.ClientConfig())
                .restClientBuilder(RestClient.builder())
                .webClientBuilder(WebClient.builder())
                .build());
```

## Notes

- The overall usage has no difference, but in the SpringBoot 2 environment, there is no RestClient, only WebClient.
