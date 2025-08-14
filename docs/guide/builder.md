---
lang: zh-cn
title: 客户端构建器
description: 
---

# 客户端构建器

Dify Java 客户端库使用构建器模式，使创建和配置与 Dify API 交互所需的各种客户端变得简单。若在非 SpringBoot
项目中或不想使用默认构造实例，可以使用下面的方法进行自定义构造。

## 概述

Dify 客户端库包含几个用于不同 API 端点的构建器：

- `DifyChatBuilder` - 用于聊天相关操作
- `DifyDatasetBuilder` - 用于数据集操作
- `DifyWorkflowBuilder` - 用于工作流操作
- `DifyServerBuilder` - 用于服务器管理操作

## 使用构建器

### 基本用法

创建客户端最快捷的方式是使用静态工厂方法：

```java
// 使用默认设置创建聊天客户端
DifyChat difyChat = DifyChatBuilder.create(
                DifyChatBuilder.DifyChatClientBuilder.create());

// 使用自定义基本 URL 创建聊天客户端
DifyChat difyChat = DifyChatBuilder.create(
        DifyChatBuilder.DifyChatClientBuilder.create("https://your-dify-api.example.com"));
```

### 使用流式构建器 API

要获得更多控制，可以使用流式构建器 API：

```java
DifyChat difyChat = DifyChatBuilder.create(
        DifyChatBuilder.DifyChatClientBuilder
                .builder()
                .baseUrl("https://your-dify-api.example.com")
                .clientConfig(new DifyProperties.ClientConfig())
                .build());
```

### 带身份验证的服务器客户端

数据集客户端需要apiKey凭据：

```java

DifyDataset difyDataset = DifyDatasetBuilder.create(
        DifyDatasetBuilder.DifyDatasetClientBuilder
                .builder()
                .baseUrl("https://custom-dify-api.example.com")
                .apiKey('ak-aaaa')
                .build());
```

服务器客户端需要身份验证凭据：

```java
DifyProperties.Server serverProps = new DifyProperties.Server("admin@example.com", "password");

DifyServer difyServer = DifyServerBuilder.create(
        DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .serverProperties(serverProps)
                .build());
```

## 通用配置选项

所有构建器都支持这些通用配置选项：

- `baseUrl(String)` - 设置 Dify API 的基本 URL
- `clientConfig(DifyProperties.ClientConfig)` - 配置客户端行为，如超时、重试策略等
- `restClientBuilder(RestClient.Builder)` - 提供自定义的 Spring RestClient 构建器
- `webClientBuilder(WebClient.Builder)` - 提供自定义的 Spring WebClient 构建器

## 高级用法

### 自定义 HTTP 客户端

您可以自定义 Dify 客户端使用的 HTTP 客户端：

```java
// 配置具有特殊设置的自定义 RestClient
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

### 设置客户端配置

您可以配置客户端的行为方式：

```java
// 配置客户端行为
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.

setSkipNull(false);

DifyWorkflow difyWorkflow = DifyWorkflowBuilder.create(
        DifyWorkflowBuilder.DifyWorkflowClientBuilder
                .builder()
                .clientConfig(clientConfig)
                .build());
```

## 示例：完整配置

以下是完整配置的示例：

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

## 注意事项

- 整体使用没有差别，但是在 springboot2环境下，没有 RestClient，只有 WebClient


