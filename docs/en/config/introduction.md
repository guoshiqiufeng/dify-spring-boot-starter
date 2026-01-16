---
lang: en-US
title: Using configurations
description:
---

## Spring Boot Configuration

Applicable to Spring Boot 2.x/3.x/4.x projects.

```yaml
dify:
  url: http://192.168.1.10 # Dify service address
  server:
    email: admin@admin.com # Dify service email (required for Server API)
    password: admin123456 # Dify service password (required for Server API)
    password-encryption: true # Password encryption switch, default true
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # Dataset API key
  client-config:
    skip-null: true # Whether to skip null fields, default true
    logging: true # Whether to print request logs, default true
    connect-timeout: 30 # Connection timeout in seconds, default 30
    read-timeout: 30 # Read timeout in seconds, default 30
    write-timeout: 30 # Write timeout in seconds, default 30
  status:
    health-indicator-enabled: false # Enable health indicator, default false
    health-indicator-init-by-server: true # Initialize via Server, default true
```

### dify

#### url

- Type: `String`
- Default value: ``
- Required.
- Description: Dify Service Address

#### server

##### email

- Type: `String`
- Default value: ``
- Not required.
- Description：Dify service mailbox, if you don't need to call the server related interface can not fill in.

##### password

- Type: `String`
- Default value: ``
- Not required.
- Description：Dify service password, if you don't need to call server related interfaces, you can leave it out.

##### password-encryption

- Type：`Boolean`
- Default value：`true`
- Not required
- Description：Dify service password enable Base64 encryption, dify 1.11.2 and above version can be enabled, or directly set the password to Base64 encrypted ciphertext.

#### dataset

##### api-key

- Type: `String`
- Default value: ``
- Not required.
- Description: Knowledge base api-key, not required if you don't need to call the knowledge base.

#### clientConfig

> Configuration for initiating requests

##### skipNull

- Type: `Boolean`
- Default value: `true`
- Not required
- Description: Whether to skip null fields, by default null fields are filtered out when submitting data

##### logging

- Type: `Boolean`
- Default value: `true`
- Not required
- Description: Whether to print request logs, enable this parameter and configure `io.github.guoshiqiufeng.dify.client` log level to debug to print request and response logs

##### connectTimeout

- Type: `Integer`
- Default value: `30`
- Not required
- Description: Connection timeout in seconds, default 30 seconds

##### readTimeout

- Type: `Integer`
- Default value: `30`
- Not required
- Description: Read timeout in seconds, default 30 seconds

##### writeTimeout

- Type: `Integer`
- Default value: `30`
- Not required
- Description: Write timeout in seconds, default 30 seconds

#### status

> Status monitoring configuration

##### healthIndicatorEnabled

- Type: `Boolean`
- Default value: `false`
- Not required
- Description: Whether to enable health indicator for Spring Boot Actuator health checks

##### healthIndicatorInitByServer

- Type: `Boolean`
- Default value: `true`
- Not required
- Description: Whether to initialize health indicator via Server API

##### apiKey

- Type: `String`
- Default value: ``
- Not required
- Description: Common API key for status monitoring

##### datasetApiKey

- Type: `String`
- Default value: ``
- Not required
- Description: Dataset API key for Dataset client status monitoring

##### chatApiKey

- Type: `List<String>`
- Default value: `null`
- Not required
- Description: Chat API key list for Chat client status monitoring

##### workflowApiKey

- Type: `List<String>`
- Default value: `null`
- Not required
- Description: Workflow API key list for Workflow client status monitoring
