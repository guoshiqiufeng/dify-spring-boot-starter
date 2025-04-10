---
lang: en-US
title: Customized Configuration
description: 
---

# Customized Configuration

## Client

### Webclient

> Support for custom Webclient, overriding the default instance

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

> Support for custom RestClient, overriding the default instance

```java

@Bean
public RestClient restClient() {
    return RestClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
}
```
