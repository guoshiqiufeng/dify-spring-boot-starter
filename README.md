## dify-spring-boot-starter

## ![cover-v5-optimized](./docs/.vuepress/public/images/icon.png)

<p align="center">
  <a href="https://guoshiqiufeng.github.io/dify-spring-boot-starter/en">Documentation</a> ·
  <a href="https://github.com/guoshiqiufeng/dify-spring-boot-starter-examples">Examples</a> ·
  <a href="https://deepwiki.com/guoshiqiufeng/dify-spring-boot-starter">DeepWiki</a>
</p>

<div align="center">

[![Maven central](https://img.shields.io/maven-central/v/io.github.guoshiqiufeng.dify/dify-spring-boot-starter.svg?style=flat-square)](https://search.maven.org/search?q=g:io.github.guoshiqiufeng.dify%20AND%20a:dify-spring-boot-starter)
[![License](https://img.shields.io/:license-apache-brightgreen.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![CodeQL](https://github.com/guoshiqiufeng/dify-spring-boot-starter/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/guoshiqiufeng/dify-spring-boot-starter/actions/workflows/github-code-scanning/codeql)
[![codecov](https://codecov.io/gh/guoshiqiufeng/dify-spring-boot-starter/graph/badge.svg?token=NVQ2SGEQ79)](https://codecov.io/gh/guoshiqiufeng/dify-spring-boot-starter)

</div>

Read in other languages: [简体中文](README-zh.md)

**🎉 Version 2.0 Major Update**: Modular architecture refactoring with pure Java support! See [Changelog](CHANGELOG-2.0.md)

### Introduction

Provides Spring Boot Starter and pure Java support for Dify to simplify development.

**Version 2.0 New Features**:
- ✨ Support for pure Java projects (no Spring required)
- 🔧 Modular architecture with flexible HTTP clients
- 📦 Multiple JSON codec options (Gson, Jackson 2.x/3.x)
- 🚀 Unified client implementation, eliminating code duplication

### Supported Frameworks

- Spring Boot 4/3/2
- Pure Java Projects (2.0+)

### Minimum Requirements

- Java 8
- Spring Boot 2 (for Spring projects)

### Recommended Versions

- Java 17+
- Spring Boot 4/3

### Features

- chat
- server
- workflow
- dataset (Knowledge)
- status (Monitoring)

### Use

#### Introduce BOM for Version Management

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.guoshiqiufeng.dify</groupId>
            <artifactId>dify-bom</artifactId>
            <version>1.8.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### Add Starter Dependencies

**Spring Boot 3.1+**

```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter</artifactId>
</dependency>
```

**Spring Boot 4.x**

> Available in dify-spring-boot-starter v1.6.0+

```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot4-starter</artifactId>
</dependency>
```

**Spring Boot 2.x / 3.0.x**

> Available in dify-spring-boot-starter v0.9.0+

```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot2-starter</artifactId>
</dependency>
```

**Pure Java Projects**

> Available in dify-spring-boot-starter v2.0.0+

```xml
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-java-starter</artifactId>
</dependency>
```

#### Spring Boot Auto-Configuration

##### YAML Configuration

```yaml
dify:
  url: http://192.168.1.10 # Dify service address
  server:
    email: admin@admin.com # Dify service email (required for Server API)
    password: admin123456 # Dify service password (required for Server API)
    password-encryption: false # Password encryption switch, default true
                                # Enable for Dify 1.11.2+ (or use Base64 cipher)
                                # Set to false for versions below 1.11.2
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # Dataset API key (required for Dataset API)
```

##### Usage Example

```java
@Service
public class DifyChatService {

    @Resource
    private DifyChat difyChat;

    public List<String> messagesSuggested(String messageId, String apiKey, String userId) {
        return difyChat.messagesSuggested(messageId, apiKey, userId);
    }
}
```

#### Manual Client Building (Builder Pattern)

> Available in dify-spring-boot-starter v2.0.0+

**Pure Java Projects**:

```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.support.impl.builder.DifyServerBuilder;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;

// Create HTTP client factory (OkHttp)
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// Create client configuration
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
// Set other configurations...

// Create DifyServerClient
DifyServerClient difyServerClient = DifyServerBuilder.builder()
        .baseUrl("https://your-dify-api.example.com")
        .httpClientFactory(httpClientFactory)
        .clientConfig(clientConfig)
        .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
        .build();

// Create DifyServer
DifyServer difyServer = DifyServerBuilder.create(difyServerClient);
```

**Spring Projects**:

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.support.impl.builder.DifyServerBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.client.RestClient;

// Create HTTP client factory (Spring)
SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
        WebClient.builder(),
        RestClient.builder(),  // Spring 6.1+ / Spring Boot 3.2+
        new JacksonJsonMapper()
);

// Create client configuration
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
// Set other configurations...

// Create DifyServerClient
DifyServerClient difyServerClient = DifyServerBuilder.builder()
        .baseUrl("https://your-dify-api.example.com")
        .httpClientFactory(httpClientFactory)
        .clientConfig(clientConfig)
        .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
        .build();

// Create DifyServer
DifyServer difyServer = DifyServerBuilder.create(difyServerClient);
```

> **Note**: In Spring Boot 2.x environments, RestClient is not available, pass `null`.

For more usage references check the

- [documentation](https://guoshiqiufeng.github.io/dify-spring-boot-starter/en/)
- [examples](https://github.com/guoshiqiufeng/dify-spring-boot-starter-examples)

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=guoshiqiufeng/dify-spring-boot-starter&type=Date)](https://www.star-history.com/#guoshiqiufeng/dify-spring-boot-starter&Date)

![Alt](https://repobeats.axiom.co/api/embed/55a4a85285edbb431a80f6261b7aa1c0a8dc3612.svg "Repobeats analytics image")
