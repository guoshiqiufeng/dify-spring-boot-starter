---
lang: zh-cn
title: 配置
description: 
---

# 配置

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) 的配置异常的简单，我们仅需要一些简单的配置即可！

> 请确保您已经安装了 dify-spring-boot-starter，如果您尚未安装，请查看 [安装](install.md)。

## Spring Boot 自动配置

对于 Spring Boot 项目，框架会自动配置所有必要的组件，您只需要在配置文件中提供连接参数即可。

### 基础配置

```yaml
dify:
  url: http://192.168.1.10 # Dify 服务地址
  server:
    email: admin@admin.com # Dify 服务邮箱（调用 Server API 时需要）
    password: admin123456 # Dify 服务密码（调用 Server API 时需要）
    password-encryption: true # 密码加密开关，默认 true
                               # Dify 1.11.2+ 需要开启（或使用 Base64 密文）
                               # Dify 1.11.2 以下版本设置为 false
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # 知识库 API Key（调用 Dataset API 时需要）
```

### 客户端配置

```yaml
dify:
  client-config:
    skip-null: true # 是否跳过 null 字段，默认 true
    logging: true # 是否打印请求日志，默认 true
    connect-timeout: 30 # 连接超时时间（秒），默认 30
    read-timeout: 30 # 读取超时时间（秒），默认 30
    write-timeout: 30 # 写入超时时间（秒），默认 30
```

### 状态监控配置

```yaml
dify:
  status:
    health-indicator-enabled: false # 是否启用健康指示器，默认 false
    health-indicator-init-by-server: true # 是否通过 Server 初始化，默认 true
    api-key: your-api-key # 通用 API Key（可选）
    dataset-api-key: dataset-key # Dataset API Key（可选）
    chat-api-key: # Chat API Key 列表（可选）
      - chat-key-1
      - chat-key-2
    workflow-api-key: # Workflow API Key 列表（可选）
      - workflow-key-1
```

### 完整配置示例

```yaml
dify:
  url: http://192.168.1.10
  server:
    email: admin@admin.com
    password: admin123456
    password-encryption: true
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh
  client-config:
    skip-null: true
    logging: true
    connect-timeout: 30
    read-timeout: 30
    write-timeout: 30
  status:
    health-indicator-enabled: true
    health-indicator-init-by-server: true
```

详细配置参数说明请查看 [配置参数](../config/introduction.md)。

## 纯 Java 项目配置

对于纯 Java 项目（不使用 Spring Boot），您需要手动构建客户端。请查看 [构建器模式](builder.md) 了解详细信息。

