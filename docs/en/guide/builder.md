---
lang: en-US
title: Client Builder
description: Client Builder
---

<script setup>import {inject} from "vue";
const version = inject('version');
</script>

# Client Builder

The Dify Java client library uses the builder pattern to make it easy to create and configure the various clients needed
to interact with the Dify API. If you are using a non-SpringBoot project or don't want to use the default constructor
instance, you can use the methods below for custom construction.

## Pure Java Projects

For non-Spring Boot projects, you can use the `dify-java-starter` module:

### Add Dependency

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-java-starter</artifactId>
    <version>{{version}}</version>
</dependency>
```

### Usage Example

```java
// Create Dify client
DifyClient difyClient = DifyClient.builder()
    .baseUrl("https://your-dify-api.example.com")
    .build();

// Get various service clients
DifyChat chat = difyClient.getChat();
DifyDataset dataset = difyClient.getDataset();
DifyWorkflow workflow = difyClient.getWorkflow();
DifyServer server = difyClient.getServer();
```

## Overview

The Dify client library includes several builders for different API endpoints:

- `DifyChatBuilder` - For chat-related operations
- `DifyDatasetBuilder` - For dataset operations
- `DifyWorkflowBuilder` - For workflow operations
- `DifyServerBuilder` - For server administration operations

All builders extend the `BaseDifyBuilder` abstract class, which provides common configuration methods and ensures consistent behavior across all client types.

### Builder Architecture

The builder pattern implementation follows these principles:

1. **Two-Step Creation**: Builders first create a low-level Client (e.g., `DifyChatClient`), then wrap it in a high-level service interface (e.g., `DifyChat`)
2. **Framework Agnostic**: The `dify-support-impl` module provides framework-independent implementations that work with any HTTP client factory
3. **Fluent API**: All configuration methods return the builder instance for method chaining
4. **Validation**: The `build()` method validates required parameters and throws `IllegalStateException` if `httpClientFactory` is not set
5. **Default Values**: Common properties like `baseUrl` and `clientConfig` have sensible defaults

## Spring Boot Projects

In Spring Boot projects, it's recommended to use auto-configuration. Simply add the appropriate starter dependency and configure `application.yml`.

See [Getting Started](getting-started.md) and [Configuration](config.md) for details.

## Using the Builders

### Basic Usage

The builder pattern in Dify Java SDK uses a two-step creation process:

1. **Build a Client** (e.g., `DifyChatClient`, `DifyServerClient`) using the builder's `build()` method
2. **Create a Service Interface** (e.g., `DifyChat`, `DifyServer`) from the Client using the builder's `create()` method

This separation allows for flexible configuration of the underlying HTTP client while providing a clean service interface for API operations.

### Choosing HTTP Client Factory

Choose the appropriate HTTP client factory based on your project environment:

- **Pure Java Projects**: Use `JavaHttpClientFactory` (based on OkHttp)
- **Spring Projects**: Use `SpringHttpClientFactory` (based on Spring WebClient/RestClient)
- **Spring Boot Projects**: Recommended to use auto-configuration, no manual building required

#### Pure Java Project Example

```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;

// 1. Create HTTP client factory (OkHttp)
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// 2. Create DifyChatClient
DifyChatClient difyChatClient = DifyChatBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();

// 3. Create DifyChat
DifyChat difyChat = DifyChatBuilder.create(difyChatClient);
```

#### Spring Project Example

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.client.RestClient;

// 1. Create HTTP client factory (Spring)
SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
    WebClient.builder(),
    RestClient.builder(),  // Spring 6.1+ / Spring Boot 3.2+
    new JacksonJsonMapper()
);

// 2. Create DifyChatClient
DifyChatClient difyChatClient = DifyChatBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();

// 3. Create DifyChat
DifyChat difyChat = DifyChatBuilder.create(difyChatClient);
```

**Note**: In Spring Boot 2.x or Spring 5.x environments, RestClient is not available, pass `null`:

```java
SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
    WebClient.builder(),
    null,  // Spring Boot 2.x doesn't have RestClient
    new JacksonJsonMapper()
);
```

#### Creating a Dataset Client

```java
// Use JavaHttpClientFactory (Pure Java projects)
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// Or use SpringHttpClientFactory (Spring projects)
// SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
//     WebClient.builder(), RestClient.builder(), new JacksonJsonMapper()
// );

// Create DifyDatasetClient (with API Key)
DifyDatasetClient difyDatasetClient = DifyDatasetBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .apiKey("dataset-aaabbbcccdddeeefffggghhh")
    .build();

// Create DifyDataset
DifyDataset difyDataset = DifyDatasetBuilder.create(difyDatasetClient);
```

#### Creating a Server Client

```java
// Use JavaHttpClientFactory (Pure Java projects)
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// Or use SpringHttpClientFactory (Spring projects)
// SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
//     WebClient.builder(), RestClient.builder(), new JacksonJsonMapper()
// );

// Create DifyServerClient (with server authentication)
DifyServerClient difyServerClient = DifyServerBuilder.builder()
    .baseUrl("http://192.168.1.150")
    .httpClientFactory(httpClientFactory)
    .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
    .build();

// Create DifyServer
DifyServer difyServer = DifyServerBuilder.create(difyServerClient);

// Use the service
AppsResponseResult apps = difyServer.apps(new AppsRequest());
```

#### Creating a Workflow Client

```java
// Use JavaHttpClientFactory (Pure Java projects)
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// Or use SpringHttpClientFactory (Spring projects)
// SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
//     WebClient.builder(), RestClient.builder(), new JacksonJsonMapper()
// );

// Create DifyWorkflowClient
DifyWorkflowClient difyWorkflowClient = DifyWorkflowBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();

// Create DifyWorkflow
DifyWorkflow difyWorkflow = DifyWorkflowBuilder.create(difyWorkflowClient);
```

## Common Configuration Options

All builders extend `BaseDifyBuilder` and support these common configuration options:

- `baseUrl(String)` - Set the base URL for the Dify API (defaults to `https://api.dify.ai/v1` if not specified)
- `httpClientFactory(HttpClientFactory)` - Set the HTTP client factory (**required**, throws `IllegalStateException` if not set)
- `clientConfig(DifyProperties.ClientConfig)` - Configure client behavior like timeout, retry policy, logging, etc. (defaults to a new instance if not specified)

### Dataset-Specific Configuration

- `apiKey(String)` - Set the dataset API Key (automatically adds `Authorization: Bearer <apiKey>` header to all requests)

### Server-Specific Configuration

- `serverProperties(DifyProperties.Server)` - Set server authentication information (email and password) for login
- `serverToken(BaseDifyServerToken)` - Set a custom token manager (defaults to `DifyServerTokenDefault` if not specified)

## Advanced Usage

### Customizing HTTP Clients

You can customize HTTP client behavior through `HttpClientFactory`:

```java
// Create HTTP client factory with custom headers
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper())
    .defaultHeader("Custom-Header", "Value")
    .defaultHeader("Another-Header", "AnotherValue");

DifyChatClient difyChatClient = DifyChatBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();
```

### Adding Interceptors

You can add interceptors to the HTTP client (requires OkHttp Interceptor):

```java
import okhttp3.Interceptor;
import okhttp3.Response;

// Create custom interceptor
Interceptor loggingInterceptor = chain -> {
    System.out.println("Request: " + chain.request().url());
    Response response = chain.proceed(chain.request());
    System.out.println("Response: " + response.code());
    return response;
};

// Add interceptor to factory
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper())
    .interceptor(loggingInterceptor);

DifyChatClient difyChatClient = DifyChatBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();
```

### Setting Client Configuration

You can configure how the client behaves:

```java
// Configure client behavior
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setSkipNull(false);  // Don't skip null fields
clientConfig.setLogging(true);    // Enable logging

JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

DifyWorkflowClient difyWorkflowClient = DifyWorkflowBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .clientConfig(clientConfig)
    .build();

DifyWorkflow difyWorkflow = DifyWorkflowBuilder.create(difyWorkflowClient);
```

### Using Gson Instead of Jackson

You can use Gson as the JSON serialization library:

```java
import io.github.guoshiqiufeng.dify.client.codec.gson.GsonJsonMapper;

// Use Gson
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new GsonJsonMapper());

DifyChatClient difyChatClient = DifyChatBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();
```

## Example: Complete Configuration

Here's an example with full configuration:

```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import okhttp3.Interceptor;

// 1. Create custom interceptor
Interceptor loggingInterceptor = chain -> {
    System.out.println("Request: " + chain.request().url());
    return chain.proceed(chain.request());
};

// 2. Create and configure HTTP client factory
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper())
    .defaultHeader("X-Custom-Header", "CustomValue")
    .interceptor(loggingInterceptor);

// 3. Configure client behavior
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setSkipNull(true);
clientConfig.setLogging(true);

// 4. Create Server client
DifyServerClient difyServerClient = DifyServerBuilder.builder()
    .baseUrl("http://192.168.1.150")
    .httpClientFactory(httpClientFactory)
    .clientConfig(clientConfig)
    .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
    .build();

// 5. Create DifyServer service
DifyServer difyServer = DifyServerBuilder.create(difyServerClient);

// 6. Use the service
AppsResponseResult apps = difyServer.apps(new AppsRequest());
```

## Dependencies

To use the builders, you need to add the following dependencies:

### Pure Java Projects

```xml
<!-- Core support -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-support-impl</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- HTTP client integration (OkHttp) -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-integration-okhttp</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- JSON codec (choose one) -->
<!-- Jackson -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-codec-jackson</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- Or Gson -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-codec-gson</artifactId>
    <version>${dify.version}</version>
</dependency>
```

### Spring Projects

```xml
<!-- Core support -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-support-impl</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- HTTP client integration (Spring) -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-integration-spring</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- JSON codec (choose one) -->
<!-- Jackson -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-codec-jackson</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- Or Gson -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-codec-gson</artifactId>
    <version>${dify.version}</version>
</dependency>
```

## Implementation Features

The `dify-support-impl` module provides comprehensive implementations with the following features:

### Error Handling

All client implementations include a custom `DifyResponseErrorHandler` that provides:

- **HTTP 401 Unauthorized**: Throws `DifyException` with "Unauthorized" message
- **HTTP 404 Not Found**: Throws `DifyException` with "Not Found" message
- **Other Errors**: Extracts error details from response body and throws `DifyException` with status code and message

### Streaming Support

The implementation includes specialized DTOs with custom deserializers for streaming responses:

- **Chat Streaming**: `ChatMessageSendCompletionResponseDto` with `ChatMessageSendCompletionResponseDeserializer`
- **Workflow Streaming**: `WorkflowRunStreamResponseDto` with `WorkflowRunStreamResponseDeserializer`
- **Dataset Operations**: `SegmentDataResponseDto` with `SegmentDataResponseDeserializer`

These deserializers handle Server-Sent Events (SSE) format and parse streaming data correctly.

### File Upload Support

The `MultipartBodyUtil` utility class provides:

- Multipart form data handling for file uploads
- Support for `DifyFile` objects with automatic content type detection
- Proper boundary and content disposition headers

### Authentication

Multiple authentication methods are supported:

- **API Key Authentication**: Automatic `Authorization: Bearer <apiKey>` header injection (Dataset operations)
- **Token-Based Authentication**: Cookie-based authentication with refresh token support (Server operations)
- **Custom Token Managers**: Implement `BaseDifyServerToken` for custom token management strategies

### Retry Mechanisms

Server operations include built-in retry logic:

- Automatic token refresh on authentication failures
- Configurable retry attempts for transient errors
- Exponential backoff support through client configuration

## Notes

- **HttpClientFactory is required**: All builders require `httpClientFactory` to be set before calling `build()`. If not set, an `IllegalStateException` will be thrown with the message "HttpClientFactory must be set before building the client"
- **Two-step creation process**:
  1. First, use the builder to create a Client (e.g., `DifyChatClient`) by calling `builder().build()`
  2. Then, create the service interface (e.g., `DifyChat`) by calling `create(client)`
- **Choose the appropriate HTTP client factory**:
  - Pure Java projects: Use `JavaHttpClientFactory` from `dify-client-integration-okhttp` (based on OkHttp)
  - Spring projects: Use `SpringHttpClientFactory` from `dify-client-integration-spring` (based on Spring WebClient/RestClient)
  - Spring Boot projects: Recommended to use auto-configuration instead of manual building
- **JSON codec**: Supports both Jackson and Gson JSON libraries. Choose according to your project needs:
  - Jackson: Use `JacksonJsonMapper` from `dify-client-codec-jackson`
  - Gson: Use `GsonJsonMapper` from `dify-client-codec-gson`
- **Spring version compatibility**:
  - Spring Boot 3.2+ / Spring 6.1+: Supports both WebClient and RestClient
  - Spring Boot 2.x / Spring 5.x: Only supports WebClient, pass `null` for RestClient parameter
- **Default values**:
  - `baseUrl`: Defaults to `BaseDifyClient.DEFAULT_BASE_URL` if not specified
  - `clientConfig`: Defaults to a new `DifyProperties.ClientConfig()` instance if not specified
  - `serverToken`: Defaults to `DifyServerTokenDefault` for server clients if not specified
- **Module location**: The builder implementations are located in the `dify-support-impl` module under the package `io.github.guoshiqiufeng.dify.support.impl.builder`
