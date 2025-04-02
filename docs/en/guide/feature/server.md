---
lang: en-US
title: Server API
description: 
---

# Server API

## Interface Overview

The Server API provides comprehensive functionality for interacting with the Dify platform, including managing
applications, retrieving and initializing API keys for both applications and datasets. All interfaces require a valid
API key for authentication.
Use the `DifyServer` interface instance.

## 1. Application Management

### 1.1 Get All Applications

#### Method

```java
List<AppsResponseVO> apps(String name);
```

#### Request Parameters

| Parameter name | Type   | Required | Description                                                      |
|----------------|--------|----------|------------------------------------------------------------------|
| name           | String | No       | Application name, used to filter the application list (optional) |

#### Response Parameters

AppsResponseVO

| Parameter name      | Type           | Description                        |
|---------------------|----------------|------------------------------------|
| id                  | String         | Application ID                     |
| name                | String         | Application name                   |
| maxActiveRequests   | Integer        | Maximum active requests            |
| description         | String         | Application description            |
| mode                | String         | Application mode                   |
| iconType            | String         | Icon type                          |
| icon                | String         | Icon                               |
| iconBackground      | String         | Icon background                    |
| iconUrl             | String         | Icon URL                           |
| modelConfig         | ModelConfig    | Model configuration                |
| workflow            | Object         | Workflow information               |
| useIconAsAnswerIcon | Boolean        | Whether to use icon as answer icon |
| createdBy           | String         | Creator ID                         |
| createdAt           | Long           | Creation time (timestamp)          |
| updatedBy           | String         | Updater ID                         |
| updatedAt           | Long           | Update time (timestamp)            |
| tags                | `List<String>` | Application tags                   |

ModelConfig

| Parameter name | Type   | Description               |
|----------------|--------|---------------------------|
| model          | Model  | Model information         |
| prePrompt      | String | Pre-prompt text           |
| createdBy      | String | Creator ID                |
| createdAt      | Long   | Creation time (timestamp) |
| updatedBy      | String | Updater ID                |
| updatedAt      | Long   | Update time (timestamp)   |

Model

| Parameter name   | Type             | Description           |
|------------------|------------------|-----------------------|
| provider         | String           | Model provider        |
| name             | String           | Model name            |
| mode             | String           | Model mode            |
| completionParams | CompletionParams | Completion parameters |

CompletionParams

| Parameter name | Type           | Description    |
|----------------|----------------|----------------|
| stop           | `List<String>` | Stop sequences |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetApps() {
    // Get all applications
    List<AppsResponseVO> apps = difyServer.apps("");

    // Get applications with name filter
    List<AppsResponseVO> filteredApps = difyServer.apps("myApp");
}
```

### 1.2 Get Application by ID

#### Method

```java
AppsResponseVO app(String appId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description    |
|----------------|--------|----------|----------------|
| appId          | String | Yes      | Application ID |

#### Response Parameters

Same as the AppsResponseVO structure defined above.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetApp() {
    AppsResponseVO app = difyServer.app("app-123456789");
}
```

### 1.3 Get Application API Keys

#### Method

```java
List<ApiKeyResponseVO> getAppApiKey(String id);
```

#### Request Parameters

| Parameter name | Type   | Required | Description    |
|----------------|--------|----------|----------------|
| id             | String | Yes      | Application ID |

#### Response Parameters

ApiKeyResponseVO

| Parameter name | Type   | Description         |
|----------------|--------|---------------------|
| id             | String | API Key ID          |
| token          | String | API Key token value |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetAppApiKeys() {
    List<ApiKeyResponseVO> apiKeys = difyServer.getAppApiKey("app-123456789");
}
```

### 1.4 Initialize Application API Key

#### Method

```java
List<ApiKeyResponseVO> initAppApiKey(String id);
```

#### Request Parameters

| Parameter name | Type   | Required | Description    |
|----------------|--------|----------|----------------|
| id             | String | Yes      | Application ID |

#### Response Parameters

Same as the ApiKeyResponseVO structure defined above.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testInitAppApiKey() {
    List<ApiKeyResponseVO> apiKeys = difyServer.initAppApiKey("app-123456789");
}
```

## 2. Knowledge Base Management

### 2.1 Get Knowledge Base API Keys

#### Method

```java
List<DatasetApiKeyResponseVO> getDatasetApiKey();
```

#### Response Parameters

DatasetApiKeyResponseVO

| Parameter name | Type   | Description                |
|----------------|--------|----------------------------|
| id             | String | API Key ID                 |
| type           | String | API Key type               |
| token          | String | API Key token value        |
| lastUsedAt     | Long   | Last used time (timestamp) |
| createdAt      | Long   | Creation time (timestamp)  |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDatasetApiKeys() {
    List<DatasetApiKeyResponseVO> datasetApiKeys = difyServer.getDatasetApiKey();
}
```

### 2.2 Initialize Knowledge Base API Key

#### Method

```java
List<DatasetApiKeyResponseVO> initDatasetApiKey();
```

#### Response Parameters

Same as the DatasetApiKeyResponseVO structure defined above.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testInitDatasetApiKey() {
    List<DatasetApiKeyResponseVO> datasetApiKeys = difyServer.initDatasetApiKey();
}
```
