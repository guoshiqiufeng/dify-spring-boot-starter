---
lang: en-US
title: Service Status Monitoring
description: Dify service status monitoring and health checks
---

<script setup>import {inject} from "vue";
const version = inject('version');
</script>

# Service Status Monitoring

The `dify-status` module provides health check and status monitoring capabilities for Dify API services, with integration support for Spring Boot Actuator.

## Features

- 🔍 **Multi-Client Monitoring**: Monitor multiple Dify clients including Chat, Dataset, Workflow, and Server
- 🏥 **Health Checks**: Integrate with Spring Boot Actuator to provide standard health check endpoints
- ⚡ **Parallel Checks**: Support parallel execution of status checks across multiple clients for improved efficiency
- 💾 **Cache Support**: Built-in caching mechanism to avoid frequent requests with configurable TTL
- 📊 **Detailed Reports**: Provide comprehensive status reports including response times, error messages, and HTTP status codes
- 🔑 **Flexible Configuration**: Support independent API Key configuration for different clients

## Quick Start

### 1. Add Dependencies

Add dependencies to your `build.gradle`:

```gradle
dependencies {
    implementation 'io.github.guoshiqiufeng:dify-spring-boot-starter:{{version}}'
    implementation 'io.github.guoshiqiufeng:dify-status:{{version}}'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

Or add to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>io.github.guoshiqiufeng</groupId>
        <artifactId>dify-spring-boot-starter</artifactId>
        <version>{{version}}</version>
    </dependency>
    <dependency>
        <groupId>io.github.guoshiqiufeng</groupId>
        <artifactId>dify-status</artifactId>
        <version>{{version}}</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

### 2. Configuration

Configure in your `application.yml`:

```yaml
dify:
  url: http://192.168.1.10
  server:
    email: admin@admin.com
    password: admin123456
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh
  status:
    # Enable health indicator
    health-indicator-enabled: true
    # Initialize using server account (recommended)
    health-indicator-init-by-server: true
    # Or manually configure API Keys
    api-key: default-api-key
    chat-api-key:
      - chat-api-key-1
      - chat-api-key-2
    dataset-api-key: dataset-api-key
    workflow-api-key:
      - workflow-api-key-1

# Configure Actuator endpoints
management:
  endpoints:
    web:
      exposure:
        include: health
  endpoint:
    health:
      show-details: always
```

### 3. Access Health Check Endpoint

After starting your application, access the health check endpoint:

```bash
curl http://localhost:8080/actuator/health
```

Example response:

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

## Configuration Reference

### Status Monitoring Configuration

| Configuration | Type | Default | Description |
|---------------|------|---------|-------------|
| `dify.status.health-indicator-enabled` | Boolean | `false` | Enable health indicator |
| `dify.status.health-indicator-init-by-server` | Boolean | `true` | Initialize using server account (recommended) |
| `dify.status.api-key` | String | - | Default API Key for all clients without specific configuration |
| `dify.status.chat-api-key` | `List<String>` | - | API Key list for Chat client |
| `dify.status.dataset-api-key` | String | - | API Key for Dataset client |
| `dify.status.workflow-api-key` | `List<String>` | - | API Key list for Workflow client |

### Initialization Methods

#### Method 1: Using Server Account (Recommended)

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

This method automatically retrieves all available API Keys using the server account without manual configuration.

#### Method 2: Manual API Key Configuration

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

## Programmatic Usage

In addition to automatically exposing health check endpoints through Actuator, you can directly use `DifyStatusService` in your code:

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
     * Check single client status
     */
    public ClientStatusReport checkChatStatus() {
        return statusService.checkClientStatus("DifyChat", "your-api-key");
    }

    /**
     * Check all clients status (using server account)
     */
    public AggregatedStatusReport checkAllStatus() {
        return statusService.checkAllClientsStatusByServer();
    }

    /**
     * Check status with custom configuration
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

## Status Types

The system supports the following status types:

| Status | Code | Description |
|--------|------|-------------|
| Normal | `normal` | API is functioning normally |
| Not Found | `not_found_404` | Resource not found (404) |
| Unauthorized | `unauthorized_401` | Unauthorized access (401) |
| Timeout | `timeout` | Request timeout |
| Network Error | `network_error` | Network connection error |
| Server Error | `server_error` | Server error (5xx) |
| Client Error | `client_error` | Client error (4xx, excluding 401 and 404) |
| Unknown Error | `unknown_error` | Unknown error |
| Not Configured | `not_configured` | API not configured or disabled |

## Advanced Configuration

### Custom Cache Configuration

```java
StatusCheckConfig config = StatusCheckConfig.builder()
    .useCache(true)
    .cacheTtlSeconds(120L)  // Cache for 2 minutes
    .build();
```

### Disable Parallel Checks

```java
StatusCheckConfig config = StatusCheckConfig.builder()
    .parallel(false)  // Execute checks serially
    .build();
```

### Specify Clients to Check

```java
StatusCheckConfig config = StatusCheckConfig.builder()
    .clientsToCheck(Set.of("DifyChat", "DifyDataset"))  // Only check these two clients
    .build();
```

### Specify Methods to Check

```java
Map<String, List<String>> methodsToCheck = new HashMap<>();
methodsToCheck.put("DifyChat", List.of("sendMessage", "getConversations"));
methodsToCheck.put("DifyDataset", List.of("createDocument"));

StatusCheckConfig config = StatusCheckConfig.builder()
    .methodsToCheck(methodsToCheck)
    .build();
```

## Monitoring Best Practices

1. **Production Environment Recommendations**:
   - Enable caching to reduce pressure on Dify services
   - Set reasonable cache expiration times (recommended 60-300 seconds)
   - Use parallel checks for improved efficiency

2. **Security Recommendations**:
   - Do not output API Keys in logs
   - Use environment variables or configuration centers to manage sensitive information
   - Restrict access to health check endpoints

3. **Performance Optimization**:
   - Select clients to monitor based on actual needs
   - Set reasonable timeout values
   - Increase cache time appropriately in high-concurrency scenarios

## Troubleshooting

### Health Check Returns DOWN

1. Check if Dify service is running normally
2. Verify that configured API Keys are correct
3. Check network connectivity
4. Review detailed error messages and status codes

### All APIs Show unauthorized_401

- Check if API Keys are correctly configured
- Verify that API Keys have not expired or been disabled

### Long Response Times

- Check network latency
- Consider increasing timeout configuration
- Check Dify service performance

## Related Links

- [Configuration Guide](config.md)
- [Getting Started](getting-started.md)
- [GitHub Repository](https://github.com/guoshiqiufeng/dify-spring-boot-starter)
