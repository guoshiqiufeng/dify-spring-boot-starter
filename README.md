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

### Introduction

Provide springboot starter for dify to simplify development.

### Development Framework

- Spring Boot 4/3/2

### Running the minimum version

- Spring Boot 2
- Java 8

### Recommended Running Versions

- Spring Boot 4/3

### Features

- chat
- server
- workflow
- dataset (Knowledge)
- status (Monitoring)

### Use

#### Introduces a uniform version dependency, so you don't have to specify a version number when you use it.

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.guoshiqiufeng.dify</groupId>
            <artifactId>dify-bom</artifactId>
            <version>1.8.0</version>
            <type>import</type>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### Introducing starter dependencies

- springboot3.1 and above

```xml

<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter</artifactId>
</dependency>
```

- springboot4

> dify-spring-boot-starter v1.6.0 or above is available.

```xml

<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot4-starter</artifactId>
</dependency>
```

- springboot2、springboot3.0.x

> dify-spring-boot-starter v0.9.0 or above is available.

```xml

<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot2-starter</artifactId>
</dependency>
```

- Pure Java Projects

> dify-spring-boot-starter v2.0.0 or above is available.

```xml

<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-java-starter</artifactId>
</dependency>
```

#### autoloading

##### yml configuration

```yaml
dify:
  url: http://192.168.1.10 # Please replace with the actual Dify service address
  server:
    email: admin@admin.com # Please replace the actual Dify service mailbox, if you do not need to call the server-related interfaces can not be filled in!
    password: admin123456 # Please replace the password with the actual Dify service password, if you don't need to call the server-related interfaces can not be filled in!
    password-encryption: false # Password encryption switch, default is true, need to be enabled for Dify 1.11.2 and above (or unenabled for Base64 cipher), need to be set to false for versions below 1.11.2;
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # Please replace with the actual Dify dataset API key, if you don't need to call the dataset-related interfaces can not be filled in!
```

##### Get message suggestions

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

#### Builder

> dify-spring-boot-starter v2.0.0 or above is available.

**Pure Java Projects**:

```java
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;

// Create HTTP client factory (OkHttp)
JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

// Create DifyServerClient
DifyServerClient difyServerClient = DifyServerBuilder.builder()
        .baseUrl("https://your-dify-api.example.com")
        .httpClientFactory(httpClientFactory)
        .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
        .build();

// Create DifyServer
DifyServer difyServer = DifyServerBuilder.create(difyServerClient);
```

**Spring Projects**:

```java
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.client.RestClient;

// Create HTTP client factory (Spring)
// SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(new JacksonJsonMapper());
SpringHttpClientFactory httpClientFactory = new SpringHttpClientFactory(
        WebClient.builder(),
        RestClient.builder(),  // Spring 6.1+ / Spring Boot 3.2+
        new JacksonJsonMapper()
);

// Create DifyServerClient
DifyServerClient difyServerClient = DifyServerBuilder.builder()
        .baseUrl("https://your-dify-api.example.com")
        .httpClientFactory(httpClientFactory)
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
