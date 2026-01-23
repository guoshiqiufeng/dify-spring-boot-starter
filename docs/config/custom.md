---
lang: zh-CN
title: 自定义配置
description:
---

# 自定义配置

## Spring Boot 项目自定义

### HTTP 客户端自定义

#### WebClient

适用于所有 Spring Boot 版本（2.x/3.x/4.x）。支持自定义 `WebClient.Builder`，覆盖默认实例。

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

适用于 Spring Boot 3.2+ 和 Spring Boot 4.x。支持自定义 `RestClient.Builder`，覆盖默认实例。

```java
@Bean
public RestClient.Builder restClientBuilder() {
    return RestClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .requestFactory(new JdkClientHttpRequestFactory());
}
```

> **注意**: Spring Boot 2.x 不支持 RestClient，框架会自动使用 WebClient。

### JSON 编解码器自定义

#### 使用自定义 JsonMapper (Jackson)

```java
@Bean
public JsonMapper jsonMapper() {
    return JacksonJsonMapper.getInstance();
}
```

### 完整自定义示例

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

## 纯 Java 项目自定义

对于纯 Java 项目，您可以通过 `HttpClientFactory` 的链式方法进行自定义配置。

### 添加默认请求头

```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;

// 创建基础工厂
JavaHttpClientFactory baseFactory = new JavaHttpClientFactory(JacksonJsonMapper.getInstance());

// 添加默认请求头
HttpClientFactory customFactory = baseFactory
        .defaultHeader("X-Custom-Header", "custom-value")
        .defaultHeader("User-Agent", "MyApp/1.0");

// 使用自定义工厂创建客户端
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(customFactory)
        .build();
```

### 添加自定义拦截器

```java
import okhttp3.Interceptor;
import okhttp3.Response;

// 创建自定义拦截器
Interceptor loggingInterceptor = chain -> {
    okhttp3.Request request = chain.request();
    System.out.println("Request: " + request.url());
    Response response = chain.proceed(request);
    System.out.println("Response: " + response.code());
    return response;
};

// 创建工厂并添加拦截器
HttpClientFactory customFactory = new JavaHttpClientFactory(JacksonJsonMapper.getInstance())
        .interceptor(loggingInterceptor);

// 使用自定义工厂创建客户端
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(customFactory)
        .build();
```

### 配置超时时间

超时时间通过 `ClientConfig` 配置：

```java
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;

// 创建客户端配置
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setConnectTimeout(60); // 连接超时 60 秒
clientConfig.setReadTimeout(60);    // 读取超时 60 秒
clientConfig.setWriteTimeout(60);   // 写入超时 60 秒
clientConfig.setLogging(true);      // 启用日志

// 使用配置创建客户端
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(new JavaHttpClientFactory(JacksonJsonMapper.getInstance()))
        .build();
```

### 自定义 JSON 编解码器

#### 使用 Jackson 编解码器（默认）

```java
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;

// 使用默认的 Jackson 映射器
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(JacksonJsonMapper.getInstance());
```

#### 使用 Gson 编解码器

```java
import io.github.guoshiqiufeng.dify.client.codec.gson.GsonJsonMapper;

// 使用 Gson 映射器
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(GsonJsonMapper.getInstance());
```

#### 使用 Jackson 3 编解码器（Spring Boot 4.x）

```java
import io.github.guoshiqiufeng.dify.client.codec.jackson3.Jackson3JsonMapper;

// 使用 Jackson 3 映射器
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(Jackson3JsonMapper.getInstance());
```

> **注意**: 所有 JSON 映射器都是单例模式，使用 `getInstance()` 方法获取实例。如果需要自定义配置，可以通过 `getObjectMapper()` 或 `getGson()` 获取底层实例进行配置。

### 完整自定义示例

```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import okhttp3.Interceptor;

// 1. 使用 Jackson 映射器
JacksonJsonMapper jsonMapper = JacksonJsonMapper.getInstance();

// 2. 创建自定义拦截器
Interceptor authInterceptor = chain -> {
    okhttp3.Request request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer token")
            .build();
    return chain.proceed(request);
};

// 3. 创建 HTTP 客户端工厂并配置
HttpClientFactory httpClientFactory = new JavaHttpClientFactory(jsonMapper)
        .defaultHeader("X-App-Version", "1.0.0")
        .interceptor(authInterceptor);

// 4. 创建客户端配置
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setConnectTimeout(60);
clientConfig.setReadTimeout(60);
clientConfig.setWriteTimeout(60);
clientConfig.setLogging(true);
clientConfig.setSkipNull(true);

// 5. 使用自定义配置创建客户端
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(httpClientFactory)
        .build();
```

## 版本说明

- **Spring Boot 2.x**: 仅支持 WebClient
- **Spring Boot 3.0-3.1**: 仅支持 WebClient
- **Spring Boot 3.2+**: 支持 WebClient 和 RestClient
- **Spring Boot 4.x**: 支持 WebClient 和 RestClient
- **纯 Java 项目**: 使用 OkHttp 客户端

