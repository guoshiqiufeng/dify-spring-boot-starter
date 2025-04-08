## dify-spring-boot-starter

[![Maven central](https://img.shields.io/maven-central/v/io.github.guoshiqiufeng.dify/dify-spring-boot-starter.svg?style=flat-square)](https://search.maven.org/search?q=g:io.github.guoshiqiufeng.dify%20AND%20a:dify-spring-boot-starter)
[![License](https://img.shields.io/:license-apache-brightgreen.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![CodeQL](https://github.com/guoshiqiufeng/dify-spring-boot-starter/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/guoshiqiufeng/dify-spring-boot-starter/actions/workflows/github-code-scanning/codeql)

阅读其他语言版本: [English](README.md)

### 介绍

为dify提供 springboot starter,简化开发

### 文档

- https://guoshiqiufeng.github.io/dify-spring-boot-starter/

### 开发框架

- Spring Boot 3

### 运行最低版本

- Spring Boot 3
- Java 17

### 功能

- 聊天
- 后台
- 工作流
- 知识库

### 使用

#### 引入统一版本依赖，不用再使用时指定版本号

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.guoshiqiufeng.dify</groupId>
            <artifactId>dify-bom</artifactId>
            <version>0.7.0</version>
            <type>import</type>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### 引入starter依赖

```xml

<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter</artifactId>
</dependency>
```

#### yml 配置

```yaml
dify:
  url: http://192.168.1.10 # 请替换为实际的 Dify 服务地址
  server:
    email: admin@admin.com # 请替换为实际的 Dify 服务邮箱，若不需要调用 server相关接口可不填
    password: admin123456 # 请替换为实际的 Dify 服务密码，若不需要调用 server相关接口可不填
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # 请替换为实际的知识库api-key, 若不需要调用知识库可不填
```

#### 获取消息建议

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

更多使用参考查看

- [文档](https://guoshiqiufeng.github.io/dify-spring-boot-starter)
- [examples](https://github.com/guoshiqiufeng/dify-spring-boot-starter-examples)

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=guoshiqiufeng/dify-spring-boot-starter&type=Date)](https://www.star-history.com/#guoshiqiufeng/dify-spring-boot-starter&Date)
