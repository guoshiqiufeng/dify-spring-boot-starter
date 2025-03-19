---
lang: en-US
title: Configure
description: Configure
---

# Configure

[dify-spring-boot-starter](https://github.com/guoshiqiufeng/dify-spring-boot-starter) is very easy to configure, we
just need some simple configuration！

> ake sure you have dify-spring-boot-starter，installed，if you haven't, check out the [Install](install.md)。

## Configuration parameters

### `application.yml` Configure connection parameters

```yaml
dify:
  email: admin@admin.com # Please replace the actual Dify service mailbox, if you do not need to call the server-related interfaces can not be filled in!
  password: admin123456 # Please replace the password with the actual Dify service password, if you don't need to call the server-related interfaces can not be filled in!
  url: http://192.168.1.10 # Please replace with the actual Dify service address
  dataset:
    api-key: dataset-aaabbbcccdddeeefffggghhh # Please replace with the actual Dify dataset API key, if you don't need to call the dataset-related interfaces can not be filled in!
```

