---
lang: en-US
title: Logging Security
description: Log masking and SSE safety hardening
---

# Logging Security

## Overview

To protect sensitive information from being leaked into logs, dify-spring-boot-starter provides comprehensive log masking functionality. This feature is enabled by default and can automatically identify and mask sensitive data, including API Keys, Tokens, passwords, Cookies, etc.

Additionally, the framework provides security protection for SSE (Server-Sent Events) streaming responses to avoid blocking issues caused by log recording.

## Features

### 🔒 Sensitive Information Masking

Automatically masks the following types of sensitive information:

1. **HTTP Headers**
   - `Authorization`
   - `Cookie`
   - `Token`
   - `API-Key` / `ApiKey`
   - Other headers containing sensitive keywords

2. **URL Parameters**
   - `api_key` / `apikey`
   - `token`
   - `authorization`
   - `password`
   - `secret`
   - `access_token` / `accessToken`
   - `refresh_token` / `refreshToken`

3. **Cookies**
   - All cookies are displayed as `***MASKED***` in logs

4. **Request/Response Body**
   - Uses intelligent MaskingEngine to identify and mask sensitive fields
   - Supports multiple formats including JSON, form data, etc.

### 🚀 SSE Streaming Response Protection

- Automatically detects `Content-Type: text/event-stream` responses
- SSE responses only log headers, not body
- Avoids blocking streaming consumption due to log recording
- Prevents memory overflow and connection timeout

## Configuration

### Spring Boot Configuration

```yaml
dify:
  client-config:
    logging: true                    # Enable logging, default true
    logging-mask-enabled: true       # Enable log masking, default true
```

### Pure Java Configuration

```java
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;

// Create client configuration
DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
clientConfig.setLogging(true);                  // Enable logging
clientConfig.setLoggingMaskEnabled(true);       // Enable masking (default)

// Create HTTP client factory
JavaHttpClientFactory factory = new JavaHttpClientFactory(new JacksonJsonMapper());

// Create client
DifyChatClient client = DifyChatBuilder.builder()
    .baseUrl("https://api.dify.ai")
    .clientConfig(clientConfig)
    .httpClientFactory(factory)
    .build();
```

## Configuration Parameters

### logging

- **Type**: `Boolean`
- **Default**: `true`
- **Description**: Whether to enable logging. Must be used with `DEBUG` log level.

### loggingMaskEnabled

- **Type**: `Boolean`
- **Default**: `true`
- **Description**: Whether to enable log masking. Recommended to keep enabled in production to protect sensitive information.

## Usage Examples

### Example 1: Default Configuration (Recommended)

```yaml
dify:
  url: https://api.dify.ai
  client-config:
    logging: true
    logging-mask-enabled: true  # Enabled by default, can be omitted
```

**Log Output Example:**

```
【Dify】Request: POST https://api.dify.ai/v1/chat-messages?api_key=***MASKED***
【Dify】Request Headers: {Authorization: ***MASKED***, Content-Type: application/json}
【Dify】Request Body: {"query":"Hello","user":"user-123"}
【Dify】Response: 200 OK (123ms)
【Dify】Response Headers: {Content-Type: application/json}
【Dify】Response Body: {"answer":"Hello! How can I help you?"}
```

### Example 2: Debug Mode (Masking Disabled)

```yaml
dify:
  url: https://api.dify.ai
  client-config:
    logging: true
    logging-mask-enabled: false  # Disable masking for debugging
```

**Log Output Example:**

```
【Dify】Request: POST https://api.dify.ai/v1/chat-messages?api_key=app-abc123def456
【Dify】Request Headers: {Authorization: Bearer app-abc123def456, Content-Type: application/json}
【Dify】Request Body: {"query":"Hello","user":"user-123"}
【Dify】Response: 200 OK (123ms)
【Dify】Response Headers: {Content-Type: application/json}
【Dify】Response Body: {"answer":"Hello! How can I help you?"}
```

### Example 3: SSE Streaming Response

```yaml
dify:
  url: https://api.dify.ai
  client-config:
    logging: true
    logging-mask-enabled: true
```

**Log Output Example:**

```
【Dify】Request: POST https://api.dify.ai/v1/chat-messages
【Dify】Request Headers: {Authorization: ***MASKED***, Content-Type: application/json}
【Dify】Request Body: {"query":"Hello","response_mode":"streaming","user":"user-123"}
【Dify】Response: 200 OK (45ms)
【Dify】Response Headers: {Content-Type: text/event-stream}
【Dify】SSE response detected, skipping body logging to avoid blocking stream
```

## Log Level Configuration

To enable log output, configure the log level to `DEBUG`:

### Logback Configuration (Spring Boot Default)

```xml
<configuration>
    <logger name="io.github.guoshiqiufeng.dify.client" level="DEBUG"/>
</configuration>
```

### application.yml Configuration

```yaml
logging:
  level:
    io.github.guoshiqiufeng.dify.client: DEBUG
```

### Log4j2 Configuration

```xml
<Configuration>
    <Loggers>
        <Logger name="io.github.guoshiqiufeng.dify.client" level="DEBUG"/>
    </Loggers>
</Configuration>
```

## Security Best Practices

### 1. Production Environment Configuration

```yaml
dify:
  client-config:
    logging: true                    # Enable logging for troubleshooting
    logging-mask-enabled: true       # Must enable masking to protect sensitive info
```

### 2. Development Environment Configuration

```yaml
dify:
  client-config:
    logging: true
    logging-mask-enabled: false      # Can disable masking for debugging
```

### 3. Log Level Recommendations

- **Production**: `INFO` or `WARN` (no detailed logs)
- **Testing**: `DEBUG` (detailed logs with masking enabled)
- **Development**: `DEBUG` (detailed logs, masking optional)

### 4. Sensitive Information Handling

- ✅ **Recommended**: Use environment variables or configuration center for API Keys
- ✅ **Recommended**: Enable log masking
- ✅ **Recommended**: Regularly review log output to ensure no sensitive information leakage
- ❌ **Avoid**: Hard-coding API Keys in code
- ❌ **Avoid**: Committing log files with sensitive information to version control

## Technical Implementation

### Masking Mechanism

The framework uses `MaskingEngine` for intelligent masking:

1. **Header Masking**: Detects header names, masks if matching sensitive keywords
2. **URL Parameter Masking**: Uses regex to match sensitive parameter names
3. **Body Masking**: Uses lightweight Tokenizer to parse JSON/form data and identify sensitive fields

### SSE Detection

Determines if response is SSE by checking `Content-Type` header:

```java
private boolean isSseResponse(MediaType contentType) {
    if (contentType == null) return false;
    return "text".equals(contentType.type())
        && "event-stream".equals(contentType.subtype());
}
```

### Performance Impact

- Log masking only executes at `DEBUG` level
- In production with `INFO` level, masking logic is not executed
- Masking operations use efficient regex and string operations, negligible performance impact

## FAQ

### Q1: How to temporarily disable masking for debugging?

**A:** Set `logging-mask-enabled: false` in configuration:

```yaml
dify:
  client-config:
    logging-mask-enabled: false
```

### Q2: Does masking affect actual API calls?

**A:** No. Masking only affects log output and does not modify actual request and response data.

### Q3: How to customize masking rules?

**A:** Current version uses built-in masking rules. For customization, use `LogMaskingUtils.createEngine()` to create a custom MaskingEngine:

```java
import io.github.guoshiqiufeng.dify.core.logging.masking.MaskingConfig;
import io.github.guoshiqiufeng.dify.core.logging.masking.MaskingEngine;

MaskingConfig config = MaskingConfig.builder()
    .addSensitiveField("custom_field")
    .maxBodyLength(1000)
    .build();

MaskingEngine engine = LogMaskingUtils.createEngine(config);
```

### Q4: Why doesn't SSE response log the body?

**A:** SSE is a streaming response. Reading the body would block stream consumption, causing:
- Client cannot receive data in real-time
- Potential memory overflow (large streaming responses)
- Connection timeout

Therefore, SSE responses only log headers, not body.

### Q5: How to verify masking is working?

**A:**
1. Configure log level to `DEBUG`
2. Enable `logging-mask-enabled: true`
3. Make a request with sensitive information
4. Check log output, sensitive info should show as `***MASKED***`

## Version History

- **v2.1.0**: Enhanced log masking, added SSE safety protection
- **v2.0.0**: Introduced MaskingEngine with intelligent masking
- **v1.x**: Basic logging functionality

## Related Documentation

- [Configuration](./introduction.md)
- [Custom Configuration](../config/custom.md)
- [Status Monitoring](./status.md)
