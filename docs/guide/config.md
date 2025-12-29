---
lang: zh-cn
title: 配置
description: 
---

# 配置

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) 的配置异常的简单，我们仅需要一些简单的配置即可！

> 请确保您已经安装了 dify-spring-boot-starter，如果您尚未安装，请查看 [安装](install.md)。

## 配置参数

### `application.yml` 配置 连接参数

```yaml
dify:
  url: http://192.168.1.10 # 请替换为实际的 Dify 服务地址
  server:
    email: admin@admin.com # 请替换为实际的 Dify 服务邮箱，若不需要调用 server相关接口可不填
    password: admin123456 # 请替换为实际的 Dify 服务密码，若不需要调用 server相关接口可不填
    password-encryption: true # 密码加密开关，默认为 true。Dify 1.11.2 及以上版本需要开启（或者密码直接使用 Base64密文可不开启），1.11.2 以下版本需要设置为 false；
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # 请替换为实际的知识库api-key, 若不需要调用知识库可不填
```

