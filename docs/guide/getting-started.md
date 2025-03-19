---
lang: zh-cn
title: 快速开始
description: 
---

<script setup>import {inject} from "vue";
const version = inject('version');
</script>

# 快速开始

我们通过一个简单的Demo来介绍如何使用 [dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter)
的功能。

## 初始化

创建一个空的Spring Boot 工程，这里我们使用 3.2.0 版本。

## 添加依赖

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter</artifactId>
    <version>{{version}}</version>
</dependency>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle (Short)" active>

```groovy:no-line-numbers:no-v-pre
implementation 'io.github.guoshiqiufeng.dify:dify-spring-boot-starter:{{version}}'
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
implementation group: 'io.github.guoshiqiufeng.dify', name: 'dify-spring-boot-starter', version: '{{version}}'
```

  </CodeGroupItem>
</CodeGroup>

## 配置

在 application.yml 中添加配置：

```yaml
dify:
  email: admin@admin.com # 请替换为实际的 Dify 服务邮箱，若不需要调用 server相关接口可不填
  password: admin123456 # 请替换为实际的 Dify 服务密码，若不需要调用 server相关接口可不填
  url: http://192.168.1.10 # 请替换为实际的 Dify 服务地址
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # 请替换为实际的知识库api-key, 若不需要调用知识库可不填
```

## 使用

获取消息建议实例

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

## 小结

通过以上几个简单的步骤，我们就实现了 dify 接口的调用。
