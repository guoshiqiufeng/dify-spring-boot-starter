## dify-spring-boot-starter

## ![cover-v5-optimized](./docs/.vuepress/public/images/icon.png)

<p align="center">
  <a href="https://guoshiqiufeng.github.io/dify-spring-boot-starter/">文档</a> ·
  <a href="https://github.com/guoshiqiufeng/dify-spring-boot-starter-examples">示例</a> ·
  <a href="https://deepwiki.com/guoshiqiufeng/dify-spring-boot-starter">DeepWiki</a>
</p>

<div align="center">

[![Maven central](https://img.shields.io/maven-central/v/io.github.guoshiqiufeng.dify/dify-spring-boot-starter.svg?style=flat-square)](https://search.maven.org/search?q=g:io.github.guoshiqiufeng.dify%20AND%20a:dify-spring-boot-starter)
[![License](https://img.shields.io/:license-apache-brightgreen.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![CodeQL](https://github.com/guoshiqiufeng/dify-spring-boot-starter/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/guoshiqiufeng/dify-spring-boot-starter/actions/workflows/github-code-scanning/codeql)
[![codecov](https://codecov.io/gh/guoshiqiufeng/dify-spring-boot-starter/graph/badge.svg?token=NVQ2SGEQ79)](https://codecov.io/gh/guoshiqiufeng/dify-spring-boot-starter)

</div>

阅读其他语言版本: [English](README.md)

### 介绍

为dify提供 springboot starter,简化开发

### 开发框架

- Spring Boot 4/3/2

### 运行最低版本

- Spring Boot 2
- Java 8

### 推荐运行版本

- Spring Boot 4/3

### 功能

- 聊天
- 后台
- 工作流
- 知识库

### 使用

#### maven 镜像仓库

- 国内用户建议使用腾讯镜像仓库，腾讯会自动同步。`https://mirrors.cloud.tencent.com/nexus/repository/maven-public`
- 不建议使用阿里云maven 镜像仓库，同步比较慢。

#### 引入统一版本依赖，不用再使用时指定版本号

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

#### 引入starter依赖

- springboot3.1 及以上

```xml

<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter</artifactId>
</dependency>
```

- springboot4

> dify-spring-boot-starter v1.6.0 以上版本可用

```xml

<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot4-starter</artifactId>
</dependency>
```

- springboot2、springboot3.0.x

> dify-spring-boot-starter v0.9.0 以上版本可用

```xml

<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot2-starter</artifactId>
</dependency>
```

#### 自动加载

##### yml 配置

```yaml
dify:
  url: http://192.168.1.10 # 请替换为实际的 Dify 服务地址
  server:
    email: admin@admin.com # 请替换为实际的 Dify 服务邮箱，若不需要调用 server相关接口可不填
    password: admin123456 # 请替换为实际的 Dify 服务密码，若不需要调用 server相关接口可不填
    password-encryption: false # 密码加密开关，默认为 true。Dify 1.11.2 及以上版本需要开启（或者密码直接使用 Base64密文可不开启），1.11.2 以下版本需要设置为 false；
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # 请替换为实际的知识库api-key, 若不需要调用知识库可不填
```

##### 获取消息建议

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

#### 构造器

> dify-spring-boot-starter v0.9.0 以上版本可用

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

更多使用参考查看

- [文档](https://guoshiqiufeng.github.io/dify-spring-boot-starter)
- [examples](https://github.com/guoshiqiufeng/dify-spring-boot-starter-examples)

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=guoshiqiufeng/dify-spring-boot-starter&type=Date)](https://www.star-history.com/#guoshiqiufeng/dify-spring-boot-starter&Date)

![Alt](https://repobeats.axiom.co/api/embed/55a4a85285edbb431a80f6261b7aa1c0a8dc3612.svg "Repobeats analytics image")
