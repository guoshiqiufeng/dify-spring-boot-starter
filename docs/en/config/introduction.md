---
lang: en-US
title: Using configurations
description: 
---

## Spring Boot 3

```yaml
dify:
  email: admin@admin.com # Please replace the actual Dify service mailbox, if you do not need to call the server-related interfaces can not be filled in!
  password: admin123456 # Please replace the password with the actual Dify service password, if you don't need to call the server-related interfaces can not be filled in!
  url: http://192.168.1.10 # Please replace with the actual Dify service address
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # Please replace with the actual Dify dataset API key, if you don't need to call the dataset-related interfaces can not be filled in!
```

### dify

#### url

- Type: `String`
- Default value: ``
- Required.
- Description: Dify Service Address

#### email

- Type: `String`
- Default value: ``
- Not required.
- Description：Dify service mailbox, if you don't need to call the server related interface can not fill in.

#### password

- Type: `String`
- Default value: ``
- Not required.
- Description：Dify service password, if you don't need to call server related interfaces, you can leave it out.

#### dataset

##### api-key

- Type: `String`
- Default value: ``
- Not required.
- Description: Knowledge base api-key, not required if you don't need to call the knowledge base.
