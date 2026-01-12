---
lang: en-US
title: Using configurations
description: 
---

## Spring Boot 2/3

```yaml
dify:
  url: http://192.168.1.10 # Please replace with the actual Dify service address
  server:
    email: admin@admin.com # Please replace the actual Dify service mailbox, if you do not need to call the server-related interfaces can not be filled in!
    password: admin123456 # Please replace the password with the actual Dify service password, if you don't need to call the server-related interfaces can not be filled in!
    password-encryption: true # Password encryption switch, default is true, need to be enabled for Dify 1.11.2 and above (or unenabled for Base64 cipher), need to be set to false for versions below 1.11.2;
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # Please replace with the actual Dify dataset API key, if you don't need to call the dataset-related interfaces can not be filled in!
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

- Type：`Boolean`
- Default value：`true`
- Not required
- Description：Whether to skip null fields, by default null fields are filtered out when submitting data

##### logging

- Type：`Boolean`
- Default value：`true`
- Not required
- Description：If or not print request logs, enable this parameter and configure `io.github.guoshiqiufeng.dedify.client`
  to print request and response logs if the log level is debug.
