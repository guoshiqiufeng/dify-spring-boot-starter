---
lang: zh-CN
title: 使用配置
description:
---

## Spring Boot 配置

适用于 Spring Boot 2.x/3.x/4.x 项目。

```yaml
dify:
  url: http://192.168.1.10 # Dify 服务地址
  server:
    email: admin@admin.com # Dify 服务邮箱（调用 Server API 时需要）
    password: admin123456 # Dify 服务密码（调用 Server API 时需要）
    password-encryption: true # 密码加密开关，默认 true
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # 知识库 API Key
  client-config:
    skip-null: true # 是否跳过 null 字段，默认 true
    logging: true # 是否打印请求日志，默认 true
    connect-timeout: 30 # 连接超时时间（秒），默认 30
    read-timeout: 30 # 读取超时时间（秒），默认 30
    write-timeout: 30 # 写入超时时间（秒），默认 30
  status:
    health-indicator-enabled: false # 是否启用健康指示器，默认 false
    health-indicator-init-by-server: true # 是否通过 Server 初始化，默认 true
```

### dify

#### url

- 类型：`String`
- 默认值：``
- 必填
- 描述：Dify 服务地址

#### server

##### email

- 类型：`String`
- 默认值：``
- 非必填
- 描述：Dify 服务邮箱，若不需要调用 server相关接口可不填

##### password

- 类型：`String`
- 默认值：``
- 非必填
- 描述：Dify 服务密码，若不需要调用 server相关接口可不填

##### password-encryption

- 类型：`Boolean`
- 默认值：`true`
- 非必填
- 描述：Dify 服务密码开启 Base64加密，dify 1.11.2 及以上版本可开启，或者直接将密码设置为 Base64加密后的密文。

#### dataset

##### api-key

- 类型：`String`
- 默认值：``
- 非必填
- 描述：知识库api-key，若不需要调用知识库可不填

#### clientConfig

> 发起请求相关配置

##### skipNull

- 类型：`Boolean`
- 默认值：`true`
- 非必填
- 描述：是否跳过 null 字段，默认提交数据时会过滤掉 null 字段

##### logging

- 类型：`Boolean`
- 默认值：`true`
- 非必填
- 描述：是否打印请求日志，开启此参数并配置 `io.github.guoshiqiufeng.dify.client` 日志级别为 debug 则打印请求、响应日志

##### connectTimeout

- 类型：`Integer`
- 默认值：`30`
- 非必填
- 描述：连接超时时间（秒），默认 30 秒

##### readTimeout

- 类型：`Integer`
- 默认值：`30`
- 非必填
- 描述：读取超时时间（秒），默认 30 秒

##### writeTimeout

- 类型：`Integer`
- 默认值：`30`
- 非必填
- 描述：写入超时时间（秒），默认 30 秒

#### status

> 状态监控相关配置

##### healthIndicatorEnabled

- 类型：`Boolean`
- 默认值：`false`
- 非必填
- 描述：是否启用健康指示器，用于 Spring Boot Actuator 健康检查

##### healthIndicatorInitByServer

- 类型：`Boolean`
- 默认值：`true`
- 非必填
- 描述：是否通过 Server API 初始化健康指示器

##### apiKey

- 类型：`String`
- 默认值：``
- 非必填
- 描述：通用 API Key，用于状态监控

##### datasetApiKey

- 类型：`String`
- 默认值：``
- 非必填
- 描述：Dataset API Key，用于 Dataset 客户端状态监控

##### chatApiKey

- 类型：`List<String>`
- 默认值：`null`
- 非必填
- 描述：Chat API Key 列表，用于 Chat 客户端状态监控

##### workflowApiKey

- 类型：`List<String>`
- 默认值：`null`
- 非必填
- 描述：Workflow API Key 列表，用于 Workflow 客户端状态监控

