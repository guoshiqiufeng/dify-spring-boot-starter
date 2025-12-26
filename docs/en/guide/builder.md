---
lang: en-US
title: Client Builder
description: Client Builder
---

# Client Builder

The Dify Java client library uses the builder pattern to make it easy to create and configure the various clients needed
to interact with the Dify API. If you are using a non-SpringBoot project or don't want to use the default constructor
instance, you can use the methods below for custom construction.

## Pure Java Projects

For non-Spring Boot projects, you can use the `dify-java-starter` module:

### Add Dependency

```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-java-starter</artifactId>
    <version>${dify.version}</version>
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

## Spring Boot Projects

In Spring Boot projects, it's recommended to use auto-configuration. Simply add the appropriate starter dependency and configure `application.yml`.

See [Getting Started](getting-started.md) and [Configuration](config.md) for details.

## Using the Builders

### Basic Usage

The new version of the builder uses a two-step creation process:

1. Create a Client (e.g., `DifyChatClient`, `DifyServerClient`)
2. Create a service interface from the Client (e.g., `DifyChat`, `DifyServer`)

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

All builders support these common configuration options:

- `baseUrl(String)` - Set the base URL for the Dify API
- `httpClientFactory(HttpClientFactory)` - Set the HTTP client factory (required)
- `clientConfig(DifyProperties.ClientConfig)` - Configure client behavior like timeout, retry policy, etc.

### Dataset-Specific Configuration

- `apiKey(String)` - Set the dataset API Key

### Server-Specific Configuration

- `serverProperties(DifyProperties.Server)` - Set server authentication information (email and password)
- `serverToken(BaseDifyServerToken)` - Set a custom token manager

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

## Notes

- **HttpClientFactory is required**: The new version of the builder must set `httpClientFactory`, otherwise an exception will be thrown
- **Two-step creation process**: You need to create a Client first, then create a service interface from the Client
- **Choose the appropriate HTTP client factory**:
  - Pure Java projects use `JavaHttpClientFactory` (based on OkHttp)
  - Spring projects use `SpringHttpClientFactory` (based on Spring WebClient/RestClient)
  - Spring Boot projects are recommended to use auto-configuration
- **JSON codec**: Supports both Jackson and Gson JSON libraries, choose according to your project needs
- **Spring version compatibility**:
  - Spring Boot 3.2+ / Spring 6.1+ supports RestClient
  - Spring Boot 2.x / Spring 5.x only supports WebClient, pass `null` for RestClient parameter
