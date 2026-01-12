---
lang: zh-CN
title: 使用配置
description: 
---

## Spring Boot 2/3

```yaml
dify:
  url: http://192.168.1.10 # 请替换为实际的 Dify 服务地址
  server:
    email: admin@admin.com # 请替换为实际的 Dify 服务邮箱，若不需要调用 server相关接口可不填
    password: admin123456 # 请替换为实际的 Dify 服务密码，若不需要调用 server相关接口可不填
    password-encryption: false # 密码加密开关，默认为 true。Dify 1.11.2 及以上版本需要开启（或者密码直接使用 Base64密文可不开启），1.11.2 以下版本需要设置为 false；
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # 请替换为实际的知识库api-key, 若不需要调用知识库可不填
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
- 描述：是否跳过null字段，默认提交数据时，会过滤掉null字段

##### logging

- 类型：`Boolean`
- 默认值：`true`
- 非必填
- 描述：是否打印请求日志，开启此参数并配置`io.github.guoshiqiufeng.dify.client`日志级别为 debug则打印请求、响应日志

