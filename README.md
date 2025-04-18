## dify-spring-boot-starter

[![Maven central](https://img.shields.io/maven-central/v/io.github.guoshiqiufeng.dify/dify-spring-boot-starter.svg?style=flat-square)](https://search.maven.org/search?q=g:io.github.guoshiqiufeng.dify%20AND%20a:dify-spring-boot-starter)
[![License](https://img.shields.io/:license-apache-brightgreen.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![CodeQL](https://github.com/guoshiqiufeng/dify-spring-boot-starter/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/guoshiqiufeng/dify-spring-boot-starter/actions/workflows/github-code-scanning/codeql)

Read in other languages: [简体中文](README-zh.md)

### Introduction

Provide springboot starter for dify to simplify development.

### Documentation

https://guoshiqiufeng.github.io/dify-spring-boot-starter/en

### Development Framework

- Spring Boot 3/2

### Running the minimum version

- Spring Boot 2
- Java 8

### Recommended Running Versions

- Spring Boot 3

### Features

- chat
- server
- workflow
- dataset (Knowledge)

### Use

#### Introduces a uniform version dependency, so you don't have to specify a version number when you use it.

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.guoshiqiufeng.dify</groupId>
            <artifactId>dify-bom</artifactId>
            <version>0.8.4</version>
            <type>import</type>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### Introducing starter dependencies

- springboot3

```xml

<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter</artifactId>
</dependency>
```

- springboot2
> dify-spring-boot-starter v0.9.0 or above is available.
```xml

<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot2-starter</artifactId>
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
> dify-spring-boot-starter v0.9.0 or above is available.
```java
DifyServer difyServer = DifyServerBuilder.create(
        DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .baseUrl("https://your-dify-api.example.com")
                .serverProperties(new DifyProperties.Server("admin@example.com", "password"))
                .serverToken(new DifyServerTokenDefault())
                .clientConfig(new DifyProperties.ClientConfig())
                .restClientBuilder(RestClient.builder())
                .webClientBuilder(WebClient.builder())
                .build());
```

For more usage references check the

- [documentation](https://guoshiqiufeng.github.io/dify-spring-boot-starter/en/)
- [examples](https://github.com/guoshiqiufeng/dify-spring-boot-starter-examples)

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=guoshiqiufeng/dify-spring-boot-starter&type=Date)](https://www.star-history.com/#guoshiqiufeng/dify-spring-boot-starter&Date)
