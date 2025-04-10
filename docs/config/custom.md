---
lang: zh-CN
title: 自定义配置
description: 
---

# 自定义配置

## Client

### WebClient

> 支持自定义 webclient，覆盖默认实例

```java

@Bean
public WebClient webClient() {
    HttpClient httpClient = HttpClient.create()
            .protocol(HttpProtocol.HTTP11);
    return WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
}
```

### RestClient

> 支持自定义 restClient，覆盖默认实例

```java

@Bean
public RestClient restClient() {
    return RestClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
}
```
