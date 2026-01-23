---
lang: en-US
title: Customized Configuration
description:
---

# Customized Configuration

## Spring Boot Project Customization

### HTTP Client Customization

#### WebClient

Applicable to all Spring Boot versions (2.x/3.x/4.x). Supports custom `WebClient.Builder` to override the default instance.

```java
@Bean
public WebClient.Builder webClientBuilder() {
    HttpClient httpClient = HttpClient.create()
            .protocol(HttpProtocol.HTTP11)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
            .responseTimeout(Duration.ofSeconds(30));

    return WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient));
}
```

#### RestClient

Applicable to Spring Boot 3.2+ and Spring Boot 4.x. Supports custom `RestClient.Builder` to override the default instance.

```java
@Bean
public RestClient.Builder restClientBuilder() {
    return RestClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .requestFactory(new JdkClientHttpRequestFactory());
}
```

> **Note**: Spring Boot 2.x does not support RestClient, the framework will automatically use WebClient.

### JSON Codec Customization

#### Using Custom JsonMapper (Jackson)

```java
@Bean
public JsonMapper jsonMapper() {
    return JacksonJsonMapper.getInstance();
}
```

### Complete Customization Example

```java
@Configuration
public class DifyCustomConfiguration {

    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .protocol(HttpProtocol.HTTP11)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .responseTimeout(Duration.ofSeconds(30));

        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.web.client.RestClient")
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Bean
    public JsonMapper jsonMapper() {
        return JacksonJsonMapper.getInstance();
    }
}
```

## Pure Java Project Customization

For pure Java projects, you can customize the configuration through the `HttpClientFactory` fluent API.

### Adding Default Headers

```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;

// Create base factory
JavaHttpClientFactory baseFactory = new JavaHttpClientFactory(JacksonJsonMapper.getInstance());

// Add default headers
HttpClientFactory customFactory = baseFactory
        .defaultHeader("X-Custom-Header", "custom-value")
        .defaultHeader("User-Agent", "MyApp/1.0");

// Use custom factory to create client
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(customFactory)
        .build();
```

### Adding Custom Interceptors

```java
import okhttp3.Interceptor;
import okhttp3.Response;

// Create custom interceptor
Interceptor loggingInterceptor = chain -> {
    okhttp3.Request request = chain.request();
    System.out.println("Request: " + request.url());
    Response response = chain.proceed(request);
    System.out.println("Response: " + response.code());
    return response;
};

// Create factory and add interceptor
HttpClientFactory customFactory = new JavaHttpClientFactory(JacksonJsonMapper.getInstance())
        .interceptor(loggingInterceptor);

// Use custom factory to create client
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(customFactory)
        .build();
```

### Configuring Timeouts

Timeouts are configured through `ClientConfig`:

```java
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;

// Create client configuration
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setConnectTimeout(60); // Connection timeout 60 seconds
clientConfig.setReadTimeout(60);    // Read timeout 60 seconds
clientConfig.setWriteTimeout(60);   // Write timeout 60 seconds
clientConfig.setLogging(true);      // Enable logging

// Use configuration to create client
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(new JavaHttpClientFactory(JacksonJsonMapper.getInstance()))
        .build();
```

### Custom JSON Codec

#### Using Jackson Codec (Default)

```java
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;

// Use default Jackson mapper
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(JacksonJsonMapper.getInstance());
```

#### Using Gson Codec

```java
import io.github.guoshiqiufeng.dify.client.codec.gson.GsonJsonMapper;

// Use Gson mapper
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(GsonJsonMapper.getInstance());
```

#### Using Jackson 3 Codec (Spring Boot 4.x)

```java
import io.github.guoshiqiufeng.dify.client.codec.jackson3.Jackson3JsonMapper;

// Use Jackson 3 mapper
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(Jackson3JsonMapper.getInstance());
```

> **Note**: All JSON mappers use the singleton pattern. Use `getInstance()` method to get instances. For custom configuration, you can access the underlying instance via `getObjectMapper()` or `getGson()`.

### Complete Customization Example

```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import okhttp3.Interceptor;

// 1. Use Jackson mapper
JacksonJsonMapper jsonMapper = JacksonJsonMapper.getInstance();

// 2. Create custom interceptor
Interceptor authInterceptor = chain -> {
    okhttp3.Request request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer token")
            .build();
    return chain.proceed(request);
};

// 3. Create HTTP client factory and configure
HttpClientFactory httpClientFactory = new JavaHttpClientFactory(jsonMapper)
        .defaultHeader("X-App-Version", "1.0.0")
        .interceptor(authInterceptor);

// 4. Create client configuration
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setConnectTimeout(60);
clientConfig.setReadTimeout(60);
clientConfig.setWriteTimeout(60);
clientConfig.setLogging(true);
clientConfig.setSkipNull(true);

// 5. Use custom configuration to create client
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(httpClientFactory)
        .build();
```

## Version Notes

- **Spring Boot 2.x**: WebClient only
- **Spring Boot 3.0-3.1**: WebClient only
- **Spring Boot 3.2+**: WebClient and RestClient supported
- **Spring Boot 4.x**: WebClient and RestClient supported
- **Pure Java Projects**: OkHttp client

