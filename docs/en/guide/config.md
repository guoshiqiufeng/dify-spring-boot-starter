---
lang: en-US
title: Configure
description: Configure
---

# Configure

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) is very easy to configure, we just need some simple configuration!

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

