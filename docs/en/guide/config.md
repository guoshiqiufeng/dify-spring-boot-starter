---
lang: en-US
title: Configure
description: Configure
---

# Configure

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) is very easy to configure. You only need some basic configuration to get started!

> Make sure you have dify-spring-boot-starter installed. If you haven't, check out the [Install](install.md).

## Spring Boot Auto-Configuration

For Spring Boot projects, the framework automatically configures all necessary components. You only need to provide connection parameters in the configuration file.

### Basic Configuration

```yaml
dify:
  url: http://192.168.1.10 # Dify service address
  server:
    email: admin@admin.com # Dify service email (required for Server API)
    password: admin123456 # Dify service password (required for Server API)
    password-encryption: true # Password encryption switch, default true
                               # Enable for Dify 1.11.2+ (or use Base64 cipher)
                               # Set to false for versions below 1.11.2
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # Dataset API key (required for Dataset API)
```

### Client Configuration

```yaml
dify:
  client-config:
    skip-null: true # Whether to skip null fields, default true
    logging: true # Whether to print request logs, default true
    connect-timeout: 30 # Connection timeout in seconds, default 30
    read-timeout: 30 # Read timeout in seconds, default 30
    write-timeout: 30 # Write timeout in seconds, default 30
```

### Performance Optimization Configuration

For high-concurrency scenarios and large-scale applications, you can configure the following performance optimization parameters:

```yaml
dify:
  client-config:
    # Basic timeout configuration
    connect-timeout: 30
    read-timeout: 30
    write-timeout: 30

    # Connection pool optimization (improve throughput by 20-30%)
    max-idle-connections: 10      # Max idle connections, default 5
    keep-alive-seconds: 300       # Keep-alive duration (seconds), default 300
    max-requests: 128             # Max concurrent requests, default 64
    max-requests-per-host: 10     # Max concurrent requests per host, default 5
    call-timeout: 60              # Call timeout (seconds), 0 means no limit

    # SSE streaming optimization (reduce disconnections by 90%+)
    sse-read-timeout: 0           # SSE read timeout (seconds), 0 disables timeout

    # Logging optimization (reduce memory usage by 30-50%)
    logging: true
    logging-mask-enabled: true    # Enable log masking, default true
    log-body-max-bytes: 4096      # Max body bytes in logs, default 4096 (4KB)
    log-binary-body: false        # Whether to log binary responses, default false
```

**Configuration Details**:

**Connection Pool Configuration**:
- `max-idle-connections`: Maximum number of idle connections in the pool, increase for better connection reuse
- `keep-alive-seconds`: Keep-alive duration for idle connections, connections exceeding this time will be closed
- `max-requests`: Global maximum concurrent requests, limits overall concurrency
- `max-requests-per-host`: Maximum concurrent requests per host, prevents overwhelming a single server
- `call-timeout`: Total call timeout (including connect, read, write), 0 means no limit

**SSE Optimization**:
- `sse-read-timeout`: Read timeout for SSE streaming responses (seconds)
  - `null` (default): Use the `read-timeout` value
  - `0`: Completely disable timeout, suitable for long-running streaming conversations
  - `>0`: Use the specified timeout in seconds

**Logging Optimization**:
- `logging-mask-enabled`: Enable log masking, default true. When enabled, automatically masks sensitive parameters (api_key, token, password, secret, authorization, access_token, refresh_token, etc.)
- `log-body-max-bytes`: Maximum bytes of response body to log, truncated if exceeded. Set to 0 for no limit
- `log-binary-body`: Whether to log binary responses (e.g., images, files). When enabled, logs binary content size and Content-Type. Recommended to set false to save memory

**Recommended Settings**:
- **Low concurrency** (< 10 QPS): Use default values
- **Medium concurrency** (10-100 QPS):
  ```yaml
  max-idle-connections: 10
  max-requests: 128
  max-requests-per-host: 10
  ```
- **High concurrency** (> 100 QPS):
  ```yaml
  max-idle-connections: 20
  max-requests: 256
  max-requests-per-host: 20
  ```

### Status Monitoring Configuration

```yaml
dify:
  status:
    health-indicator-enabled: false # Enable health indicator, default false
    health-indicator-init-by-server: true # Initialize via Server, default true
    api-key: your-api-key # Common API key (optional)
    dataset-api-key: dataset-key # Dataset API key (optional)
    chat-api-key: # Chat API key list (optional)
      - chat-key-1
      - chat-key-2
    workflow-api-key: # Workflow API key list (optional)
      - workflow-key-1
```

### Complete Configuration Example

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

For detailed configuration parameters, please see [Configuration Parameters](../config/introduction.md).

## Pure Java Project Configuration

For pure Java projects (without Spring Boot), you need to manually build clients. Please see [Builder Pattern](builder.md) for details.

