---
lang: zh-CN
title: 自定义配置
description: 
---

# 自定义配置

## Chat Webclient

> 支持自定义 dataset webclient，覆盖默认实例

```java

public WebClient difyChatWebClient(DifyServerProperties properties) {
    if (properties == null) {
        log.error("Dify server properties must not be null");
        return null;
    }

    return WebClient.builder()
            .baseUrl(properties.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
}
```

## Dataset Webclient

> 支持自定义 dataset webclient，覆盖默认实例

```java

@Bean
public WebClient difyDatasetWebClient(DifyServerProperties properties) {
    if (properties == null) {
        return null;
    }
    String apiKey = Optional.ofNullable(properties.getDataset())
            .map(DifyServerProperties.Dataset::getApiKey)
            .orElse("");

    return WebClient.builder()
            .baseUrl(properties.getUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
}
```

## Workflow Webclient

> 支持自定义 dataset webclient，覆盖默认实例

```java

public WebClient difyWorkflowWebClient(DifyServerProperties properties) {
    if (properties == null) {
        log.error("Dify server properties must not be null");
        return null;
    }

    return WebClient.builder()
            .baseUrl(properties.getUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
}
```
