---
lang: zh-CN
title: 自定义配置
description: 
---

# 自定义配置

## Chat Webclient

> 支持自定义 dataset webclient，覆盖默认实例

```java

public WebClient difyChatWebClient(DifyProperties properties) {
    if (properties == null) {
        log.error("Dify properties must not be null");
        return null;
    }
    HttpClient httpClient = HttpClient.create()
            .protocol(HttpProtocol.HTTP11);
    return WebClient.builder()
            .baseUrl(properties.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
}
```

## Dataset Webclient

> 支持自定义 dataset webclient，覆盖默认实例

```java

@Bean
public WebClient difyDatasetWebClient(DifyProperties properties) {
    if (properties == null) {
        return null;
    }
    String apiKey = Optional.ofNullable(properties.getDataset())
            .map(DifyProperties.Dataset::getApiKey)
            .orElse("");
    HttpClient httpClient = HttpClient.create()
            .protocol(HttpProtocol.HTTP11);
    return WebClient.builder()
            .baseUrl(properties.getUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
}
```

## Workflow Webclient

> 支持自定义 dataset webclient，覆盖默认实例

```java

public WebClient difyWorkflowWebClient(DifyProperties properties) {
    if (properties == null) {
        log.error("Dify properties must not be null");
        return null;
    }
    HttpClient httpClient = HttpClient.create()
            .protocol(HttpProtocol.HTTP11);
    return WebClient.builder()
            .baseUrl(properties.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
}
```

## Server Webclient

> 支持自定义 server webclient，覆盖默认实例

```java

public WebClient difyServerWebClient(DifyProperties properties) {
    if (properties == null) {
        log.error("Dify properties must not be null");
        return null;
    }
    HttpClient httpClient = HttpClient.create()
            .protocol(HttpProtocol.HTTP11);
    return WebClient.builder()
            .baseUrl(properties.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
}
```
