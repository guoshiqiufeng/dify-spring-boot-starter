---
lang: zh-cn
title: 客户端构建器
description: 
---

# 客户端构建器

Dify Java 客户端库使用构建器模式，使创建和配置与 Dify API 交互所需的各种客户端变得简单。若在非 SpringBoot
项目中或不想使用默认构造实例，可以使用下面的方法进行自定义构造。

## 纯 Java 项目

对于非 Spring Boot 项目，可以使用 `dify-java-starter` 模块：

### 添加依赖

```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-java-starter</artifactId>
    <version>${dify.version}</version>
</dependency>
```

### 使用示例

```java
// 创建 Dify 客户端
DifyClient difyClient = DifyClient.builder()
    .baseUrl("https://your-dify-api.example.com")
    .build();

// 获取各种服务客户端
DifyChat chat = difyClient.getChat();
DifyDataset dataset = difyClient.getDataset();
DifyWorkflow workflow = difyClient.getWorkflow();
DifyServer server = difyClient.getServer();
```

## 概述

Dify 客户端库包含几个用于不同 API 端点的构建器：

- `DifyChatBuilder` - 用于聊天相关操作
- `DifyDatasetBuilder` - 用于数据集操作
- `DifyWorkflowBuilder` - 用于工作流操作
- `DifyServerBuilder` - 用于服务器管理操作

## Spring Boot 项目

在 Spring Boot 项目中，推荐使用自动配置的方式，无需手动构建客户端。只需添加相应的 starter 依赖并配置 `application.yml` 即可。

详见 [快速开始](getting-started.md) 和 [配置](config.md)。

## 使用构建器

### 基本用法

新版本的构建器使用两步创建过程：

1. 创建 Client（如 `DifyChatClient`、`DifyServerClient`）
2. 从 Client 创建服务接口（如 `DifyChat`、`DifyServer`）

### 选择 HTTP 客户端工厂

根据项目环境选择合适的 HTTP 客户端工厂：

- **纯 Java 项目**：使用 `JavaHttpClientFactory`（基于 OkHttp）
- **Spring 项目**：使用 `SpringHttpClientFactory`（基于 Spring WebClient/RestClient）
- **Spring Boot 项目**：推荐使用自动配置，无需手动构建

#### 纯 Java 项目示例

```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;

// 1. 创建 HTTP 客户端工厂（OkHttp）
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// 2. 创建 DifyChatClient
DifyChatClient difyChatClient = DifyChatBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();

// 3. 创建 DifyChat
DifyChat difyChat = DifyChatBuilder.create(difyChatClient);
```

#### Spring 项目示例

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.client.RestClient;

// 1. 创建 HTTP 客户端工厂（Spring）
SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
    WebClient.builder(),
    RestClient.builder(),  // Spring 6.1+ / Spring Boot 3.2+
    new JacksonJsonMapper()
);

// 2. 创建 DifyChatClient
DifyChatClient difyChatClient = DifyChatBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();

// 3. 创建 DifyChat
DifyChat difyChat = DifyChatBuilder.create(difyChatClient);
```

**注意**：Spring Boot 2.x 或 Spring 5.x 环境下，RestClient 不可用，传入 `null` 即可：

```java
SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
    WebClient.builder(),
    null,  // Spring Boot 2.x 没有 RestClient
    new JacksonJsonMapper()
);
```

#### 创建 Dataset 客户端

```java
// 使用 JavaHttpClientFactory（纯 Java 项目）
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// 或使用 SpringHttpClientFactory（Spring 项目）
// SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
//     WebClient.builder(), RestClient.builder(), new JacksonJsonMapper()
// );

// 创建 DifyDatasetClient（带 API Key）
DifyDatasetClient difyDatasetClient = DifyDatasetBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .apiKey("dataset-aaabbbcccdddeeefffggghhh")
    .build();

// 创建 DifyDataset
DifyDataset difyDataset = DifyDatasetBuilder.create(difyDatasetClient);
```

#### 创建 Server 客户端

```java
// 使用 JavaHttpClientFactory（纯 Java 项目）
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// 或使用 SpringHttpClientFactory（Spring 项目）
// SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
//     WebClient.builder(), RestClient.builder(), new JacksonJsonMapper()
// );

// 创建 DifyServerClient（带服务器认证）
DifyServerClient difyServerClient = DifyServerBuilder.builder()
    .baseUrl("http://192.168.1.150")
    .httpClientFactory(httpClientFactory)
    .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
    .build();

// 创建 DifyServer
DifyServer difyServer = DifyServerBuilder.create(difyServerClient);

// 使用服务
AppsResponseResult apps = difyServer.apps(new AppsRequest());
```

#### 创建 Workflow 客户端

```java
// 使用 JavaHttpClientFactory（纯 Java 项目）
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// 或使用 SpringHttpClientFactory（Spring 项目）
// SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
//     WebClient.builder(), RestClient.builder(), new JacksonJsonMapper()
// );

// 创建 DifyWorkflowClient
DifyWorkflowClient difyWorkflowClient = DifyWorkflowBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();

// 创建 DifyWorkflow
DifyWorkflow difyWorkflow = DifyWorkflowBuilder.create(difyWorkflowClient);
```

## 通用配置选项

所有构建器都支持这些通用配置选项：

- `baseUrl(String)` - 设置 Dify API 的基本 URL
- `httpClientFactory(HttpClientFactory)` - 设置 HTTP 客户端工厂（必需）
- `clientConfig(DifyProperties.ClientConfig)` - 配置客户端行为，如超时、重试策略等

### Dataset 特有配置

- `apiKey(String)` - 设置知识库 API Key

### Server 特有配置

- `serverProperties(DifyProperties.Server)` - 设置服务器认证信息（邮箱和密码）
- `serverToken(BaseDifyServerToken)` - 设置自定义的 token 管理器

## 高级用法

### 自定义 HTTP 客户端

您可以通过 `HttpClientFactory` 自定义 HTTP 客户端的行为：

```java
// 创建带自定义 header 的 HTTP 客户端工厂
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper())
    .defaultHeader("Custom-Header", "Value")
    .defaultHeader("Another-Header", "AnotherValue");

DifyChatClient difyChatClient = DifyChatBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();
```

### 添加拦截器

您可以为 HTTP 客户端添加拦截器（需要 OkHttp Interceptor）：

```java
import okhttp3.Interceptor;
import okhttp3.Response;

// 创建自定义拦截器
Interceptor loggingInterceptor = chain -> {
    System.out.println("Request: " + chain.request().url());
    Response response = chain.proceed(chain.request());
    System.out.println("Response: " + response.code());
    return response;
};

// 添加拦截器到工厂
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper())
    .interceptor(loggingInterceptor);

DifyChatClient difyChatClient = DifyChatBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();
```

### 设置客户端配置

您可以配置客户端的行为方式：

```java
// 配置客户端行为
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setSkipNull(false);  // 不跳过 null 字段
clientConfig.setLogging(true);    // 启用日志

JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

DifyWorkflowClient difyWorkflowClient = DifyWorkflowBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .clientConfig(clientConfig)
    .build();

DifyWorkflow difyWorkflow = DifyWorkflowBuilder.create(difyWorkflowClient);
```

### 使用 Gson 替代 Jackson

您可以使用 Gson 作为 JSON 序列化库：

```java
import io.github.guoshiqiufeng.dify.client.codec.gson.GsonJsonMapper;

// 使用 Gson
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new GsonJsonMapper());

DifyChatClient difyChatClient = DifyChatBuilder.builder()
    .baseUrl("https://your-dify-api.example.com")
    .httpClientFactory(httpClientFactory)
    .build();
```

## 示例：完整配置

以下是完整配置的示例：

```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import okhttp3.Interceptor;

// 1. 创建自定义拦截器
Interceptor loggingInterceptor = chain -> {
    System.out.println("Request: " + chain.request().url());
    return chain.proceed(chain.request());
};

// 2. 创建 HTTP 客户端工厂并配置
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper())
    .defaultHeader("X-Custom-Header", "CustomValue")
    .interceptor(loggingInterceptor);

// 3. 配置客户端行为
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setSkipNull(true);
clientConfig.setLogging(true);

// 4. 创建 Server 客户端
DifyServerClient difyServerClient = DifyServerBuilder.builder()
    .baseUrl("http://192.168.1.150")
    .httpClientFactory(httpClientFactory)
    .clientConfig(clientConfig)
    .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
    .build();

// 5. 创建 DifyServer 服务
DifyServer difyServer = DifyServerBuilder.create(difyServerClient);

// 6. 使用服务
AppsResponseResult apps = difyServer.apps(new AppsRequest());
```

## 依赖说明

使用构建器需要添加以下依赖：

### 纯 Java 项目

```xml
<!-- 核心支持 -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-support-impl</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- HTTP 客户端集成（OkHttp） -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-integration-okhttp</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- JSON 编解码器（选择其一） -->
<!-- Jackson -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-codec-jackson</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- 或者 Gson -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-codec-gson</artifactId>
    <version>${dify.version}</version>
</dependency>
```

### Spring 项目

```xml
<!-- 核心支持 -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-support-impl</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- HTTP 客户端集成（Spring） -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-integration-spring</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- JSON 编解码器（选择其一） -->
<!-- Jackson -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-codec-jackson</artifactId>
    <version>${dify.version}</version>
</dependency>

<!-- 或者 Gson -->
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-client-codec-gson</artifactId>
    <version>${dify.version}</version>
</dependency>
```

## 注意事项

- **HttpClientFactory 是必需的**：新版本的构建器必须设置 `httpClientFactory`，否则会抛出异常
- **两步创建过程**：需要先创建 Client，再从 Client 创建服务接口
- **选择合适的 HTTP 客户端工厂**：
  - 纯 Java 项目使用 `JavaHttpClientFactory`（基于 OkHttp）
  - Spring 项目使用 `SpringHttpClientFactory`（基于 Spring WebClient/RestClient）
  - Spring Boot 项目推荐使用自动配置
- **JSON 编解码器**：支持 Jackson 和 Gson 两种 JSON 库，根据项目需要选择
- **Spring 版本兼容性**：
  - Spring Boot 3.2+ / Spring 6.1+ 支持 RestClient
  - Spring Boot 2.x / Spring 5.x 只支持 WebClient，RestClient 参数传 `null`


