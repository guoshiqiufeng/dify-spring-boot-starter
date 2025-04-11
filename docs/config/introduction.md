---
lang: zh-CN
title: 使用配置
description: 
---

## Spring Boot 3

```yaml
dify:
  url: http://192.168.1.10 # 请替换为实际的 Dify 服务地址
  server:
    email: admin@admin.com # 请替换为实际的 Dify 服务邮箱，若不需要调用 server相关接口可不填
    password: admin123456 # 请替换为实际的 Dify 服务密码，若不需要调用 server相关接口可不填
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


