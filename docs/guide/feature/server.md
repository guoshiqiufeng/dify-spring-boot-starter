---
lang: zh-cn
title: Server API
description: 
---

# Server API

## 接口概述

服务器 API 提供了全面的功能，用于与 Dify 平台交互，包括管理应用程序、检索和初始化应用程序及数据集的 API 密钥。所有接口都需要有效的
API 密钥进行身份验证。
使用 `DifyServer` 接口实例。

## 1. 应用管理

### 1.1 获取所有应用

#### 方法

```java
List<AppsResponseVO> apps(String name);
```

#### 请求参数

| 参数名  | 类型     | 是否必须 | 描述                             |
|------|--------|------|--------------------------------|
| name | String | 否    | 应用名称，用于过滤应用列表（可选，传入空字符串时表示不过滤） |

#### 响应参数

AppsResponseVO

| 参数名                 | 类型             | 描述           |
|---------------------|----------------|--------------|
| id                  | String         | 应用ID         |
| name                | String         | 应用名称         |
| maxActiveRequests   | Integer        | 最大活跃请求数      |
| description         | String         | 应用描述         |
| mode                | String         | 应用模式         |
| iconType            | String         | 图标类型         |
| icon                | String         | 图标           |
| iconBackground      | String         | 图标背景         |
| iconUrl             | String         | 图标URL        |
| modelConfig         | ModelConfig    | 模型配置         |
| workflow            | Object         | 工作流信息        |
| useIconAsAnswerIcon | Boolean        | 是否使用图标作为答案图标 |
| createdBy           | String         | 创建者ID        |
| createdAt           | Long           | 创建时间（时间戳）    |
| updatedBy           | String         | 更新者ID        |
| updatedAt           | Long           | 更新时间（时间戳）    |
| tags                | `List<String>` | 应用标签         |

ModelConfig

| 参数名       | 类型     | 描述        |
|-----------|--------|-----------|
| model     | Model  | 模型信息      |
| prePrompt | String | 预提示文本     |
| createdBy | String | 创建者ID     |
| createdAt | Long   | 创建时间（时间戳） |
| updatedBy | String | 更新者ID     |
| updatedAt | Long   | 更新时间（时间戳） |

Model

| 参数名              | 类型               | 描述    |
|------------------|------------------|-------|
| provider         | String           | 模型提供商 |
| name             | String           | 模型名称  |
| mode             | String           | 模型模式  |
| completionParams | CompletionParams | 完成参数  |

CompletionParams

| 参数名  | 类型             | 描述   |
|------|----------------|------|
| stop | `List<String>` | 停止序列 |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetApps() {
    // 获取所有应用
    List<AppsResponseVO> apps = difyServer.apps("");

    // 获取带名称过滤的应用
    List<AppsResponseVO> filteredApps = difyServer.apps("myApp");
}
```

### 1.2 根据ID获取应用

#### 方法

```java
AppsResponseVO app(String appId);
```

#### 请求参数

| 参数名   | 类型     | 是否必须 | 描述    |
|-------|--------|------|-------|
| appId | String | 是    | 应用 ID |

#### 响应参数

与上面定义的 AppsResponseVO 结构相同。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetApp() {
    AppsResponseVO app = difyServer.app("app-123456789");
}
```

### 1.3 获取应用API密钥

#### 方法

```java
List<ApiKeyResponseVO> getAppApiKey(String id);
```

#### 请求参数

| 参数名 | 类型     | 是否必须 | 描述    |
|-----|--------|------|-------|
| id  | String | 是    | 应用 ID |

#### 响应参数

ApiKeyResponseVO

| 参数名   | 类型     | 描述        |
|-------|--------|-----------|
| id    | String | API 密钥 ID |
| token | String | API 密钥令牌值 |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetAppApiKeys() {
    List<ApiKeyResponseVO> apiKeys = difyServer.getAppApiKey("app-123456789");
}
```

### 1.4 初始化应用API密钥

#### 方法

```java
List<ApiKeyResponseVO> initAppApiKey(String id);
```

#### 请求参数

| 参数名 | 类型     | 是否必须 | 描述    |
|-----|--------|------|-------|
| id  | String | 是    | 应用 ID |

#### 响应参数

与上面定义的 ApiKeyResponseVO 结构相同。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testInitAppApiKey() {
    List<ApiKeyResponseVO> apiKeys = difyServer.initAppApiKey("app-123456789");
}
```

## 2. 知识库管理

### 2.1 获取知识库API密钥

#### 方法

```java
List<DatasetApiKeyResponseVO> getDatasetApiKey();
```

#### 响应参数

DatasetApiKeyResponseVO

| 参数名        | 类型     | 描述          |
|------------|--------|-------------|
| id         | String | API 密钥 ID   |
| type       | String | API 密钥类型    |
| token      | String | API 密钥令牌值   |
| lastUsedAt | Long   | 上次使用时间（时间戳） |
| createdAt  | Long   | 创建时间（时间戳）   |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDatasetApiKeys() {
    List<DatasetApiKeyResponseVO> datasetApiKeys = difyServer.getDatasetApiKey();
}
```

### 2.2 初始化知识库API密钥

#### 方法

```java
List<DatasetApiKeyResponseVO> initDatasetApiKey();
```

#### 响应参数

与上面定义的 DatasetApiKeyResponseVO 结构相同。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testInitDatasetApiKey() {
    List<DatasetApiKeyResponseVO> datasetApiKeys = difyServer.initDatasetApiKey();
}
```
