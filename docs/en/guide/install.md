---
lang: en-US
title: Install
description: Install
---

<script setup>import {inject} from "vue";
const version = inject('version');
</script>

# Install

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) Based on JDK21, support SpringBoot
3.x、2.x.

## SpringBoot 3.x

### All-dependent installation

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

### Chat

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter-chat</artifactId>
    <version>{{version}}</version>
</dependency>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle (Short)" active>

```groovy:no-line-numbers:no-v-pre
implementation 'io.github.guoshiqiufeng.dify:dify-spring-boot-starter-chat:{{version}}'
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
implementation group: 'io.github.guoshiqiufeng.dify', name: 'dify-spring-boot-starter-chat', version: '{{version}}'
```

  </CodeGroupItem>
</CodeGroup>

### Knowledge

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter-dataset</artifactId>
    <version>{{version}}</version>
</dependency>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle (Short)" active>

```groovy:no-line-numbers:no-v-pre
implementation 'io.github.guoshiqiufeng.dify:dify-spring-boot-starter-dataset:{{version}}'
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
implementation group: 'io.github.guoshiqiufeng.dify', name: 'dify-spring-boot-starter-dataset', version: '{{version}}'
```

  </CodeGroupItem>
</CodeGroup>

### Server

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter-server</artifactId>
    <version>{{version}}</version>
</dependency>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle (Short)" active>

```groovy:no-line-numbers:no-v-pre
implementation 'io.github.guoshiqiufeng.dify:dify-spring-boot-starter-server:{{version}}'
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
implementation group: 'io.github.guoshiqiufeng.dify', name: 'dify-spring-boot-starter-server', version: '{{version}}'
```

  </CodeGroupItem>
</CodeGroup>

### Workflow

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot-starter-workflow</artifactId>
    <version>{{version}}</version>
</dependency>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle (Short)" active>

```groovy:no-line-numbers:no-v-pre
implementation 'io.github.guoshiqiufeng.dify:dify-spring-boot-starter-workflow:{{version}}'
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
implementation group: 'io.github.guoshiqiufeng.dify', name: 'dify-spring-boot-starter-workflow', version: '{{version}}'
```

  </CodeGroupItem>
</CodeGroup>

## SpringBoot 2.x

### All-dependent installation

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot2-starter</artifactId>
    <version>{{version}}</version>
</dependency>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle (Short)" active>

```groovy:no-line-numbers:no-v-pre
implementation 'io.github.guoshiqiufeng.dify:dify-spring-boot2-starter:{{version}}'
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
implementation group: 'io.github.guoshiqiufeng.dify', name: 'dify-spring-boot2-starter', version: '{{version}}'
```

  </CodeGroupItem>
</CodeGroup>

### Chat

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot2-starter-chat</artifactId>
    <version>{{version}}</version>
</dependency>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle (Short)" active>

```groovy:no-line-numbers:no-v-pre
implementation 'io.github.guoshiqiufeng.dify:dify-spring-boot2-starter-chat:{{version}}'
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
implementation group: 'io.github.guoshiqiufeng.dify', name: 'dify-spring-boot2-starter-chat', version: '{{version}}'
```

  </CodeGroupItem>
</CodeGroup>

### Knowledge

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot2-starter-dataset</artifactId>
    <version>{{version}}</version>
</dependency>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle (Short)" active>

```groovy:no-line-numbers:no-v-pre
implementation 'io.github.guoshiqiufeng.dify:dify-spring-boot2-starter-dataset:{{version}}'
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
implementation group: 'io.github.guoshiqiufeng.dify', name: 'dify-spring-boot2-starter-dataset', version: '{{version}}'
```

  </CodeGroupItem>
</CodeGroup>

### Server

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot2-starter-server</artifactId>
    <version>{{version}}</version>
</dependency>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle (Short)" active>

```groovy:no-line-numbers:no-v-pre
implementation 'io.github.guoshiqiufeng.dify:dify-spring-boot2-starter-server:{{version}}'
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
implementation group: 'io.github.guoshiqiufeng.dify', name: 'dify-spring-boot2-starter-server', version: '{{version}}'
```

  </CodeGroupItem>
</CodeGroup>

### Workflow

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependency>
    <groupId>io.github.guoshiqiufeng.dify</groupId>
    <artifactId>dify-spring-boot2-starter-workflow</artifactId>
    <version>{{version}}</version>
</dependency>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle (Short)" active>

```groovy:no-line-numbers:no-v-pre
implementation 'io.github.guoshiqiufeng.dify:dify-spring-boot2-starter-workflow:{{version}}'
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
implementation group: 'io.github.guoshiqiufeng.dify', name: 'dify-spring-boot2-starter-workflow', version: '{{version}}'
```

  </CodeGroupItem>
</CodeGroup>

## Bom

<CodeGroup>
  <CodeGroupItem title="Maven" active>

```xml:no-line-numbers:no-v-pre
<dependencyManagement>
   <dependencies>
       <dependency>
            <groupId>io.github.guoshiqiufeng.dify</groupId>
            <artifactId>dify-bom</artifactId>
            <version>{{version}}</version>
            <type>pom</type>
            <scope>import</scope>
       </dependency>
   </dependencies>
</dependencyManagement>
```

  </CodeGroupItem>

  <CodeGroupItem title="Gradle">

```groovy:no-line-numbers:no-v-pre
dependencies {
    implementation platform("io.github.guoshiqiufeng.dify:dify-bom:{{version}}")
}
```

  </CodeGroupItem>
</CodeGroup>

## Other

### Snapshot version

the snapshots url is `https://central.sonatype.com/repository/maven-snapshots/`

> Snapshots are valid for 90 days, please do not rely on the snapshot version for a long time, the snapshot version is
> only used as a version validation test, and cannot be used in the production environment.

Central Warehouse Description: https://central.sonatype.org/publish/publish-portal-snapshots/
