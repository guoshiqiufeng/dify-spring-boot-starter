---
lang: zh-CN
title: 自定义配置
description: 
---

# 自定义配置

## Client

### WebClient

> 支持自定义 webClientBuilder，覆盖默认实例

```java

@Bean
public WebClient.Builder webClientBuilder() {
    HttpClient httpClient = HttpClient.create()
            .protocol(HttpProtocol.HTTP11);
    return WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            ;
}
```

### RestClient

> 支持自定义 restClientBuilder，覆盖默认实例

```java

@Bean
public RestClient.Builder restClientBuilder() {
    return RestClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            ;
}
```
