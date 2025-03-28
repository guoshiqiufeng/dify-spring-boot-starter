---
lang: en-US
title: Customized Configuration
description: 
---

# Customized Configuration

## Chat Webclient

> Support for custom dataset webclient, overriding the default instance

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

> Support for custom dataset webclient, overriding the default instance

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

> Support for custom dataset webclient, overriding the default instance

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

> Support for custom server webclient, overriding the default instance

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
