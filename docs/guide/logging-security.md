---
lang: zh-CN
title: 日志安全
description: 日志脱敏与 SSE 安全加固
---

# 日志安全

## 概述

为了保护敏感信息不被泄露到日志中，dify-spring-boot-starter 提供了完善的日志脱敏功能。该功能默认启用，可以自动识别并脱敏敏感数据，包括 API Key、Token、密码、Cookies 等。

同时，框架还提供了 SSE（Server-Sent Events）流式响应的安全保护，避免因日志记录导致的流式响应阻塞问题。

## 功能特性

### 🔒 敏感信息脱敏

自动脱敏以下类型的敏感信息：

1. **HTTP Headers**
   - `Authorization`
   - `Cookie`
   - `Token`
   - `API-Key` / `ApiKey`
   - 其他包含敏感关键词的 header

2. **URL 参数**
   - `api_key` / `apikey`
   - `token`
   - `authorization`
   - `password`
   - `secret`
   - `access_token`
   - `refresh_token`

3. **Cookies**
   - 所有 cookies 在日志中显示为 `***MASKED***`

4. **请求/响应 Body**
   - 使用智能 MaskingEngine 识别并脱敏敏感字段
   - 支持 JSON、表单数据等多种格式

### 🚀 SSE 流式响应保护

- 自动检测 `Content-Type: text/event-stream` 响应
- SSE 响应只记录 headers，不读取 body
- 避免因日志记录导致的流式消费阻塞
- 防止内存溢出和连接超时

## 配置说明

### Spring Boot 配置

```yaml
dify:
  client-config:
    logging: true                    # 启用日志记录，默认 true
    logging-mask-enabled: true       # 启用日志脱敏，默认 true
```

### 纯 Java 配置

```java
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;

// 创建客户端配置
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setLogging(true);                  // 启用日志
clientConfig.setLoggingMaskEnabled(true);       // 启用脱敏（默认值）

// 创建 HTTP 客户端工厂
JavaHttpClientFactory factory = new JavaHttpClientFactory(new JacksonJsonMapper());

// 创建客户端
DifyChatClient client = DifyChatBuilder.builder()
    .baseUrl("https://api.dify.ai")
    .clientConfig(clientConfig)
    .httpClientFactory(factory)
    .build();
```

## 配置参数

### logging

- **类型**：`Boolean`
- **默认值**：`true`
- **说明**：是否启用日志记录。需要配合日志级别 `DEBUG` 使用。

### loggingMaskEnabled

- **类型**：`Boolean`
- **默认值**：`true`
- **说明**：是否启用日志脱敏。建议生产环境保持启用以保护敏感信息。

## 使用示例

### 示例 1：默认配置（推荐）

```yaml
dify:
  url: https://api.dify.ai
  client-config:
    logging: true
    logging-mask-enabled: true  # 默认启用，可省略
```

**日志输出示例：**

```
【Dify】Request: POST https://api.dify.ai/v1/chat-messages?api_key=***MASKED***
【Dify】Request Headers: {Authorization: ***MASKED***, Content-Type: application/json}
【Dify】Request Body: {"query":"你好","user":"user-123"}
【Dify】Response: 200 OK (123ms)
【Dify】Response Headers: {Content-Type: application/json}
【Dify】Response Body: {"answer":"你好！有什么可以帮助你的吗？"}
```

### 示例 2：调试模式（关闭脱敏）

```yaml
dify:
  url: https://api.dify.ai
  client-config:
    logging: true
    logging-mask-enabled: false  # 关闭脱敏，用于调试
```

**日志输出示例：**

```
【Dify】Request: POST https://api.dify.ai/v1/chat-messages?api_key=app-abc123def456
【Dify】Request Headers: {Authorization: Bearer app-abc123def456, Content-Type: application/json}
【Dify】Request Body: {"query":"你好","user":"user-123"}
【Dify】Response: 200 OK (123ms)
【Dify】Response Headers: {Content-Type: application/json}
【Dify】Response Body: {"answer":"你好！有什么可以帮助你的吗？"}
```

### 示例 3：SSE 流式响应

```yaml
dify:
  url: https://api.dify.ai
  client-config:
    logging: true
    logging-mask-enabled: true
```

**日志输出示例：**

```
【Dify】Request: POST https://api.dify.ai/v1/chat-messages
【Dify】Request Headers: {Authorization: ***MASKED***, Content-Type: application/json}
【Dify】Request Body: {"query":"你好","response_mode":"streaming","user":"user-123"}
【Dify】Response: 200 OK (45ms)
【Dify】Response Headers: {Content-Type: text/event-stream}
【Dify】SSE response detected, skipping body logging to avoid blocking stream
```

## 日志级别配置

要启用日志输出，需要配置日志级别为 `DEBUG`：

### Logback 配置（Spring Boot 默认）

```xml
<configuration>
    <logger name="io.github.guoshiqiufeng.dify.client" level="DEBUG"/>
</configuration>
```

### application.yml 配置

```yaml
logging:
  level:
    io.github.guoshiqiufeng.dify.client: DEBUG
```

### Log4j2 配置

```xml
<Configuration>
    <Loggers>
        <Logger name="io.github.guoshiqiufeng.dify.client" level="DEBUG"/>
    </Loggers>
</Configuration>
```

## 安全最佳实践

### 1. 生产环境配置

```yaml
dify:
  client-config:
    logging: true                    # 启用日志，便于问题排查
    logging-mask-enabled: true       # 必须启用脱敏，保护敏感信息
```

### 2. 开发环境配置

```yaml
dify:
  client-config:
    logging: true
    logging-mask-enabled: false      # 可以关闭脱敏，便于调试
```

### 3. 日志级别建议

- **生产环境**：`INFO` 或 `WARN`（不输出详细日志）
- **测试环境**：`DEBUG`（输出详细日志，启用脱敏）
- **开发环境**：`DEBUG`（输出详细日志，可选择性关闭脱敏）

### 4. 敏感信息处理

- ✅ **推荐**：使用环境变量或配置中心管理 API Key
- ✅ **推荐**：启用日志脱敏功能
- ✅ **推荐**：定期审查日志输出，确保无敏感信息泄露
- ❌ **避免**：在代码中硬编码 API Key
- ❌ **避免**：将包含敏感信息的日志文件提交到版本控制系统

## 技术实现

### 脱敏机制

框架使用 `MaskingEngine` 进行智能脱敏：

1. **Header 脱敏**：检测 header 名称，匹配敏感关键词则脱敏
2. **URL 参数脱敏**：使用正则表达式匹配敏感参数名
3. **Body 脱敏**：使用轻量级 Tokenizer 解析 JSON/表单数据，识别敏感字段

### SSE 检测

通过检测 `Content-Type` header 判断是否为 SSE 响应：

```java
private boolean isSseResponse(MediaType contentType) {
    if (contentType == null) return false;
    return "text".equals(contentType.type())
        && "event-stream".equals(contentType.subtype());
}
```

### 性能影响

- 日志脱敏仅在 `DEBUG` 级别生效
- 生产环境使用 `INFO` 级别时，脱敏逻辑不会执行
- 脱敏操作使用高效的正则表达式和字符串操作，性能影响可忽略

## 常见问题

### Q1: 如何临时关闭脱敏进行调试？

**A:** 在配置文件中设置 `logging-mask-enabled: false`：

```yaml
dify:
  client-config:
    logging-mask-enabled: false
```

### Q2: 脱敏会影响实际的 API 调用吗？

**A:** 不会。脱敏只影响日志输出，不会修改实际的请求和响应数据。

### Q3: 如何自定义脱敏规则？

**A:** 当前版本使用内置的脱敏规则。如需自定义，可以使用 `LogMaskingUtils.createEngine()` 创建自定义 MaskingEngine：

```java
import io.github.guoshiqiufeng.dify.core.logging.masking.MaskingConfig;
import io.github.guoshiqiufeng.dify.core.logging.masking.MaskingEngine;

MaskingConfig config = MaskingConfig.builder()
    .addSensitiveField("custom_field")
    .maxBodyLength(1000)
    .build();

MaskingEngine engine = LogMaskingUtils.createEngine(config);
```

### Q4: SSE 响应为什么不记录 body？

**A:** SSE 是流式响应，如果读取 body 会阻塞流的消费，导致：
- 客户端无法实时接收数据
- 可能导致内存溢出（大型流式响应）
- 连接超时

因此，SSE 响应只记录 headers，不读取 body。

### Q5: 如何验证脱敏功能是否生效？

**A:**
1. 配置日志级别为 `DEBUG`
2. 启用 `logging-mask-enabled: true`
3. 发起包含敏感信息的请求
4. 检查日志输出，敏感信息应显示为 `***MASKED***`

## 版本历史

- **v2.1.0**：增强日志脱敏功能，添加 SSE 安全保护
- **v2.0.0**：引入 MaskingEngine，支持智能脱敏
- **v1.x**：基础日志记录功能

## 相关文档

- [使用配置](./introduction.md)
- [自定义配置](../config/custom.md)
- [状态监控](./status.md)
