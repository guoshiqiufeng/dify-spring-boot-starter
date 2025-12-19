---
lang: zh-CN
title: 服务状态监控
description: Dify 服务状态监控和健康检查
---

# 服务状态监控

`dify-status` 模块提供了对 Dify API 服务的健康检查和状态监控功能，支持与 Spring Boot Actuator 集成。

## 功能特性

- 🔍 **多客户端监控**：支持监控 Chat、Dataset、Workflow、Server 等多个 Dify 客户端
- 🏥 **健康检查**：集成 Spring Boot Actuator，提供标准的健康检查端点
- ⚡ **并行检查**：支持并行执行多个客户端的状态检查，提高效率
- 💾 **缓存支持**：内置缓存机制，避免频繁请求，可配置缓存过期时间
- 📊 **详细报告**：提供详细的状态报告，包括响应时间、错误信息、HTTP 状态码等
- 🔑 **灵活配置**：支持为不同客户端配置独立的 API Key

## 快速开始

### 1. 添加依赖

在您的 `build.gradle` 中添加依赖：

```gradle
dependencies {
    implementation 'io.github.guoshiqiufeng:dify-spring-boot-starter:1.7.0'
    implementation 'io.github.guoshiqiufeng:dify-status:1.7.0'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

或在 `pom.xml` 中添加：

```xml
<dependencies>
    <dependency>
        <groupId>io.github.guoshiqiufeng</groupId>
        <artifactId>dify-spring-boot-starter</artifactId>
        <version>1.7.0</version>
    </dependency>
    <dependency>
        <groupId>io.github.guoshiqiufeng</groupId>
        <artifactId>dify-status</artifactId>
        <version>1.7.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

### 2. 配置

在 `application.yml` 中配置：

```yaml
dify:
  url: http://192.168.1.10
  server:
    email: admin@admin.com
    password: admin123456
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh
  status:
    # 启用健康检查指示器
    health-indicator-enabled: true
    # 使用服务器账号初始化（推荐）
    health-indicator-init-by-server: true
    # 或者手动配置 API Keys
    api-key: default-api-key
    chat-api-key:
      - chat-api-key-1
      - chat-api-key-2
    dataset-api-key: dataset-api-key
    workflow-api-key:
      - workflow-api-key-1

# 配置 Actuator 端点
management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      show-details: always
```

### 3. 访问健康检查端点

启动应用后，访问健康检查端点：

```bash
curl http://localhost:8080/actuator/health
```

响应示例：

```json
{
  "status": "UP",
  "components": {
    "dify": {
      "status": "UP",
      "details": {
        "totalApis": 12,
        "healthyApis": 12,
        "unhealthyApis": 0,
        "reportTime": "2025-12-23T10:30:00",
        "clientSummary": {
          "DifyChat": "normal",
          "DifyDataset": "normal",
          "DifyWorkflow": "normal",
          "DifyServer": "normal"
        },
        "clientReports": [
          {
            "clientName": "DifyChat",
            "overallStatus": "normal",
            "totalApis": 3,
            "normalApis": 3,
            "errorApis": 0,
            "apiStatuses": [
              {
                "methodName": "sendMessage",
                "endpoint": "/v1/chat-messages",
                "status": "normal",
                "responseTimeMs": 150,
                "httpStatusCode": 200,
                "checkTime": "2025-12-23T10:30:00"
              }
            ]
          }
        ]
      }
    }
  }
}
```

## 配置说明

### 状态监控配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `dify.status.health-indicator-enabled` | Boolean | `false` | 是否启用健康检查指示器 |
| `dify.status.health-indicator-init-by-server` | Boolean | `true` | 是否使用服务器账号初始化（推荐） |
| `dify.status.api-key` | String | - | 默认 API Key，用于所有未单独配置的客户端 |
| `dify.status.chat-api-key` | List<String> | - | Chat 客户端的 API Key 列表 |
| `dify.status.dataset-api-key` | String | - | Dataset 客户端的 API Key |
| `dify.status.workflow-api-key` | List<String> | - | Workflow 客户端的 API Key 列表 |

### 初始化方式

#### 方式一：使用服务器账号（推荐）

```yaml
dify:
  url: http://192.168.1.10
  server:
    email: admin@admin.com
    password: admin123456
  status:
    health-indicator-enabled: true
    health-indicator-init-by-server: true
```

此方式会自动使用服务器账号获取所有可用的 API Key，无需手动配置。

#### 方式二：手动配置 API Keys

```yaml
dify:
  url: http://192.168.1.10
  status:
    health-indicator-enabled: true
    health-indicator-init-by-server: false
    api-key: default-api-key
    chat-api-key:
      - chat-api-key-1
      - chat-api-key-2
    dataset-api-key: dataset-api-key
    workflow-api-key:
      - workflow-api-key-1
```

## 编程式使用

除了通过 Actuator 自动暴露健康检查端点，您也可以在代码中直接使用 `DifyStatusService`：

```java
import io.github.guoshiqiufeng.dify.status.service.DifyStatusService;
import io.github.guoshiqiufeng.dify.status.dto.AggregatedStatusReport;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.config.StatusCheckConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonitoringService {

    @Autowired
    private DifyStatusService statusService;

    /**
     * 检查单个客户端状态
     */
    public ClientStatusReport checkChatStatus() {
        return statusService.checkClientStatus("DifyChat", "your-api-key");
    }

    /**
     * 检查所有客户端状态（使用服务器账号）
     */
    public AggregatedStatusReport checkAllStatus() {
        return statusService.checkAllClientsStatusByServer();
    }

    /**
     * 使用自定义配置检查状态
     */
    public AggregatedStatusReport checkWithCustomConfig() {
        StatusCheckConfig config = StatusCheckConfig.builder()
            .apiKey("default-api-key")
            .chatApiKey(List.of("chat-key-1", "chat-key-2"))
            .datasetApiKey("dataset-key")
            .parallel(true)
            .useCache(true)
            .cacheTtlSeconds(60L)
            .timeoutMs(5000L)
            .build();

        return statusService.checkStatus(config);
    }
}
```

## 状态类型

系统支持以下状态类型：

| 状态 | 代码 | 说明 |
|------|------|------|
| 正常 | `normal` | API 运行正常 |
| 未找到 | `not_found_404` | 资源未找到（404） |
| 未授权 | `unauthorized_401` | 未授权访问（401） |
| 超时 | `timeout` | 请求超时 |
| 网络错误 | `network_error` | 网络连接错误 |
| 服务器错误 | `server_error` | 服务器错误（5xx） |
| 客户端错误 | `client_error` | 客户端错误（4xx，不包括 401 和 404） |
| 未知错误 | `unknown_error` | 未知错误 |
| 未配置 | `not_configured` | API 未配置或已禁用 |

## 高级配置

### 自定义缓存配置

```java
StatusCheckConfig config = StatusCheckConfig.builder()
    .useCache(true)
    .cacheTtlSeconds(120L)  // 缓存 2 分钟
    .build();
```

### 禁用并行检查

```java
StatusCheckConfig config = StatusCheckConfig.builder()
    .parallel(false)  // 串行执行检查
    .build();
```

### 指定检查的客户端

```java
StatusCheckConfig config = StatusCheckConfig.builder()
    .clientsToCheck(Set.of("DifyChat", "DifyDataset"))  // 只检查这两个客户端
    .build();
```

### 指定检查的方法

```java
Map<String, List<String>> methodsToCheck = new HashMap<>();
methodsToCheck.put("DifyChat", List.of("sendMessage", "getConversations"));
methodsToCheck.put("DifyDataset", List.of("createDocument"));

StatusCheckConfig config = StatusCheckConfig.builder()
    .methodsToCheck(methodsToCheck)
    .build();
```

## 监控最佳实践

1. **生产环境建议**：
   - 启用缓存以减少对 Dify 服务的压力
   - 设置合理的缓存过期时间（建议 60-300 秒）
   - 使用并行检查提高效率

2. **安全建议**：
   - 不要在日志中输出 API Key
   - 使用环境变量或配置中心管理敏感信息
   - 限制健康检查端点的访问权限

3. **性能优化**：
   - 根据实际需求选择要监控的客户端
   - 设置合理的超时时间
   - 在高并发场景下适当增加缓存时间

## 故障排查

### 健康检查返回 DOWN

1. 检查 Dify 服务是否正常运行
2. 验证配置的 API Key 是否正确
3. 检查网络连接是否正常
4. 查看详细的错误信息和状态码

### 所有 API 显示 unauthorized_401

- 检查 API Key 是否正确配置
- 验证 API Key 是否已过期或被禁用

### 响应时间过长

- 检查网络延迟
- 考虑增加超时时间配置
- 检查 Dify 服务的性能状况

## 相关链接

- [配置指南](config.md)
- [快速开始](getting-started.md)
- [GitHub 仓库](https://github.com/guoshiqiufeng/dify-spring-boot-starter)
