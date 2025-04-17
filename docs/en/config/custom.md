---
lang: en-US
title: Customized Configuration
description: 
---

# Customized Configuration

## Client

> springboot3 uses WebClient + RestClient, springboot2 uses WebClient.

### WebClient

> Support for custom webClientBuilder, overriding the default instance

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

> Support for custom restClientBuilder, overriding the default instance

```java

@Bean
public RestClient.Builder restClientBuilder() {
    return RestClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            ;
}
```
