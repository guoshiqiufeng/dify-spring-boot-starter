---
lang: en-US
title: Getting-started
description: Getting-started
---

<script setup>import {inject} from "vue";
const version = inject('version');
</script>

# Getting-started

Let's go through a simple demo to introduce how to
use [dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter)
features.

## Initialization

Create an empty Spring Boot project，Here we are using version 3.2.0 .

## Adding Dependencies

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

## Configuration

Add the configuration to application.yml:

```yaml
dify:
  email: admin@admin.com # Please replace the actual Dify service mailbox, if you do not need to call the server-related interfaces can not be filled in!
  password: admin123456 # Please replace the password with the actual Dify service password, if you don't need to call the server-related interfaces can not be filled in!
  url: http://192.168.1.10 # Please replace with the actual Dify service address
```

## Use

Getting Message Suggestion Example

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

## Wrap-up

With these few simple steps, we have implemented the dify interface call.
