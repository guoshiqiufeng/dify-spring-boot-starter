---
lang: zh-cn
title: 配置
description: 
---

# 配置

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) 的配置非常简单，只需要一些基础配置即可开始使用！

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

### 性能优化配置

针对高并发场景和大规模应用，可以配置以下性能优化参数：

```yaml
dify:
  client-config:
    # 基础超时配置
    connect-timeout: 30
    read-timeout: 30
    write-timeout: 30

    # 连接池优化（提升吞吐量 20-30%）
    max-idle-connections: 10      # 最大空闲连接数，默认 5
    keep-alive-seconds: 300       # 连接保活时间（秒），默认 300
    max-requests: 128             # 最大并发请求数，默认 64
    max-requests-per-host: 10     # 每个主机最大并发请求数，默认 5
    call-timeout: 60              # 调用超时时间（秒），0 表示不设置

    # SSE 流式优化（减少断流 90%+）
    sse-read-timeout: 0           # SSE 读取超时（秒），0 表示禁用超时

    # 日志优化（降低内存使用 30-50%）
    logging: true
    logging-mask-enabled: true    # 启用日志脱敏，默认 true
    log-body-max-bytes: 4096      # 日志 body 最大字节数，默认 4096（4KB）
    log-binary-body: false        # 是否记录二进制响应，默认 false
```

**配置说明**：

**连接池配置**：
- `max-idle-connections`: 连接池中保持的最大空闲连接数，增加可提高连接复用率
- `keep-alive-seconds`: 空闲连接的保活时间，超过此时间的连接将被关闭
- `max-requests`: 全局最大并发请求数，限制整体并发量
- `max-requests-per-host`: 单个主机的最大并发请求数，避免对单个服务器压力过大
- `call-timeout`: 完整调用的超时时间（包括连接、读取、写入），0 表示不限制

**SSE 优化**：
- `sse-read-timeout`: SSE 流式响应的读取超时时间（秒）
  - `null`（默认）：使用 `read-timeout` 的值
  - `0`：完全禁用超时，适用于长时间运行的流式对话
  - `>0`：使用指定的超时时间（秒）

**日志优化**：
- `logging-mask-enabled`: 是否启用日志脱敏，默认 true。启用后会自动脱敏敏感参数（api_key、token、password、secret、authorization、access_token、refresh_token 等）
- `log-body-max-bytes`: 日志中记录的响应 body 最大字节数，超过则截断。设置为 0 表示不限制
- `log-binary-body`: 是否记录二进制响应（如图片、文件）。启用后会记录二进制内容的大小和 Content-Type，建议设置为 false 以节省内存

**推荐配置**：
- **低并发场景**（< 10 QPS）：使用默认值
- **中等并发场景**（10-100 QPS）：
  ```yaml
  max-idle-connections: 10
  max-requests: 128
  max-requests-per-host: 10
  ```
- **高并发场景**（> 100 QPS）：
  ```yaml
  max-idle-connections: 20
  max-requests: 256
  max-requests-per-host: 20
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

