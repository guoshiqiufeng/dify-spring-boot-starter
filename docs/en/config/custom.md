---
lang: en-US
title: Customized Configuration
description: 
---

## Dataset Webclient

> Support for custom dataset webclient, overriding the default instance

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

