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

### 性能优化配置

#### 连接池配置

针对高并发场景，可以配置连接池参数以提升性能。Dify SDK 支持两种 HTTP 客户端实现：

##### OkHttp 连接池配置（Java 原生项目）

```java
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;

DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();

// 连接池配置
clientConfig.setMaxIdleConnections(10);      // 最大空闲连接数，默认 5（最小值 1）
clientConfig.setKeepAliveSeconds(300);       // 连接保活时间（秒），默认 300（最小值 1）
clientConfig.setMaxRequests(128);            // 最大并发请求数，默认 64（最小值 1）
clientConfig.setMaxRequestsPerHost(10);      // 每个主机最大并发请求数，默认 5（最小值 1）
clientConfig.setCallTimeout(60);             // 调用超时时间（秒），0 表示不设置

// SSE 超时配置
clientConfig.setSseReadTimeout(0);           // SSE 读取超时（秒）
                                             // null（默认）= 使用 readTimeout
                                             // 0 = 禁用超时（适用于长时间流式对话）
                                             // >0 = 使用指定超时时间

// 创建客户端
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(new JavaHttpClientFactory(JacksonJsonMapper.getInstance()))
        .build();
```

##### Spring 连接池配置（Spring 项目）

Spring 集成模块支持 WebClient（reactor-netty）和 RestClient（Apache HttpClient 5）的连接池配置：

```java
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import org.springframework.web.reactive.function.client.WebClient;

DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();

// 连接池配置（与 OkHttp 参数一致）
clientConfig.setMaxIdleConnections(10);      // 最大空闲连接数，默认 5（最小值 1）
clientConfig.setKeepAliveSeconds(300);       // 连接保活时间（秒），默认 300（最小值 1）
clientConfig.setMaxRequests(128);            // 最大并发请求数，默认 64（最小值 1）
clientConfig.setMaxRequestsPerHost(10);      // 每个主机最大并发请求数，默认 5（最小值 1）
clientConfig.setCallTimeout(60);             // 调用超时时间（秒），0 表示不设置

// SSE 超时配置
clientConfig.setSseReadTimeout(0);           // SSE 读取超时（秒）
                                             // null（默认）= 使用 readTimeout
                                             // 0 = 禁用超时（适用于长时间流式对话）
                                             // >0 = 使用指定超时时间

// 创建客户端（自动使用 WebClient 和 RestClient 连接池）
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(new SpringHttpClientFactory(
                WebClient.builder(),
                JacksonJsonMapper.getInstance()))
        .build();
```

**Spring 参数映射说明**：

| OkHttp 参数 | WebClient (reactor-netty) | RestClient (HttpClient 5) |
|------------|---------------------------|---------------------------|
| maxRequests | maxConnections | maxTotal |
| maxRequestsPerHost | *(不支持)* | defaultMaxPerRoute |
| keepAliveSeconds | maxIdleTime | connection TTL |
| callTimeout | responseTimeout | responseTimeout |

**Spring Boot 自动配置**：

在 Spring Boot 项目中，连接池配置会自动生效：

```yaml
# application.yml
dify:
  client-config:
    max-idle-connections: 10
    keep-alive-seconds: 300
    max-requests: 128
    max-requests-per-host: 10
    call-timeout: 60
```

**自定义连接池实现**：

Spring 集成模块提供了扩展点，允许用户自定义连接池实现：

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class CustomPoolConfig {

    // 自定义 WebClient 连接池
    @Bean
    public WebClientConnectionProviderFactory customWebClientPool() {
        return (settings, poolName) -> {
            return ConnectionProvider.builder(poolName)
                .maxConnections(settings.getMaxRequestsPerHost())
                .maxIdleTime(Duration.ofSeconds(settings.getKeepAliveSeconds()))
                .metrics(true) // 启用指标监控
                .build();
        };
    }

    // 自定义 RestClient 连接池
    @Bean
    public RestClientHttpClientFactory customRestClientHttpClient() {
        return settings -> {
            PoolingHttpClientConnectionManager cm =
                PoolingHttpClientConnectionManagerBuilder.create()
                    .setMaxConnTotal(settings.getMaxRequests())
                    .setMaxConnPerRoute(settings.getMaxRequestsPerHost())
                    .build();

            return HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        };
    }
}
```

**配置说明**：
- `maxIdleConnections`: 连接池中保持的最大空闲连接数，增加可提高连接复用率
- `keepAliveSeconds`: 空闲连接的保活时间，超过此时间的连接将被关闭
- `maxRequests`: 全局最大并发请求数，限制整体并发量
- `maxRequestsPerHost`: 单个主机的最大并发请求数，避免对单个服务器压力过大
- `callTimeout`: 完整调用的超时时间（包括连接、读取、写入），0 表示不限制

**推荐配置**：
- 低并发场景（< 10 QPS）：使用默认值
- 中等并发场景（10-100 QPS）：`maxIdleConnections=10`, `maxRequests=128`, `maxRequestsPerHost=10`
- 高并发场景（> 100 QPS）：`maxIdleConnections=20`, `maxRequests=256`, `maxRequestsPerHost=20`

#### SSE 流式优化

针对 SSE（Server-Sent Events）长连接场景，可以配置专门的超时策略：

```java
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();

// SSE 优化配置
clientConfig.setSseReadTimeout(0);  // 禁用 SSE 读取超时，默认 0（禁用）

// 创建客户端
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(new JavaHttpClientFactory(JacksonJsonMapper.getInstance()))
        .build();
```

**配置说明**：
- `sseReadTimeout`: SSE 流式响应的读取超时时间（秒），设置为 0 表示禁用超时
- 对于长时间运行的 SSE 连接，建议设置为 0 以避免超时中断

#### 日志优化配置

针对大响应场景，可以配置日志限制以降低内存使用：

```java
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();

// 日志优化配置
clientConfig.setLogging(true);              // 启用日志
clientConfig.setLoggingMaskEnabled(true);   // 启用日志脱敏，默认 true
clientConfig.setLogBodyMaxBytes(8192);      // 日志 body 最大字节数，默认 4096（4KB）
clientConfig.setLogBinaryBody(false);       // 是否记录二进制响应，默认 false

// 创建客户端
DifyChatClient client = DifyChatBuilder.builder()
        .baseUrl("https://api.dify.ai")
        .clientConfig(clientConfig)
        .httpClientFactory(new JavaHttpClientFactory(JacksonJsonMapper.getInstance()))
        .build();
```

**配置说明**：
- `logBodyMaxBytes`: 日志中记录的响应 body 最大字节数，超过则截断。设置为 0 表示不限制
- `logBinaryBody`: 是否记录二进制响应（如图片、文件），建议设置为 false 以节省内存

**推荐配置**：
- 开发环境：`logBodyMaxBytes=0`（不限制），便于调试
- 测试环境：`logBodyMaxBytes=8192`（8KB），平衡可读性和性能
- 生产环境：`logBodyMaxBytes=4096`（4KB）或更小，降低内存使用

#### 完整性能优化示例

```java
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;

// 创建高性能配置
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();

// 基础超时配置
clientConfig.setConnectTimeout(30);
clientConfig.setReadTimeout(30);
clientConfig.setWriteTimeout(30);

// 连接池优化（高并发场景）
clientConfig.setMaxIdleConnections(20);
clientConfig.setKeepAliveSeconds(300);
clientConfig.setMaxRequests(256);
clientConfig.setMaxRequestsPerHost(20);
clientConfig.setCallTimeout(60);

// SSE 优化
clientConfig.setSseReadTimeout(0);

// 日志优化
clientConfig.setLogging(true);
clientConfig.setLoggingMaskEnabled(true);
clientConfig.setLogBodyMaxBytes(4096);
clientConfig.setLogBinaryBody(false);

// 创建客户端
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

