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
> By default, if the current environment contains redis, then use redis to persist the token, if not, then use the
> default implementation to save the token (it will be lost if the service is restarted).

## 1. Application Management

### 1.1 Get All Applications (Non-Paginated)

#### Method

```java
List<AppsResponseVO> apps(String mode, String name);
```

#### Request Parameters

| Parameter name | Type   | Required | Description                                                      |
|----------------|--------|----------|------------------------------------------------------------------|
| mode           | String | No       | mode chat\agent-chat\completion\advanced-chat\workflow           |
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

### 1.2 Get Applications with Pagination

#### Method

```java
AppsResponseResult apps(AppsRequest appsRequest);
```

#### Request Parameters

AppsRequest

| Parameter name | Type    | Required | Description                                                                |
|----------------|---------|----------|----------------------------------------------------------------------------|
| page           | Integer | No       | Page number (default: 1)                                                   |
| limit          | Integer | No       | Number of items per page (default: 20, range: 1-100)                       |
| mode           | String  | No       | Application mode filter: chat\agent-chat\completion\advanced-chat\workflow |
| name           | String  | No       | Application name filter                                                    |
| isCreatedByMe  | Boolean | No       | Whether the application is created by the current user                     |

#### Response Parameters

AppsResponseResult

| Parameter name | Type                 | Description                  |
|----------------|----------------------|------------------------------|
| data           | `List<AppsResponse>` | Current page data list       |
| hasMore        | Boolean              | Whether there are more pages |
| limit          | Integer              | Items per page               |
| page           | Integer              | Current page number          |
| total          | Integer              | Total number of items        |

The structure of AppsResponse is the same as defined above in section 1.1.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetAppsPaginated() {
    // Create paginated request
    AppsRequest request = new AppsRequest();
    request.setPage(1);
    request.setLimit(10);
    request.setMode("chat");
    request.setName("myApp");
    request.setIsCreatedByMe(true);

    // Get paginated applications
    AppsResponseResult result = difyServer.apps(request);
    
    System.out.println("Current page: " + result.getPage());
    System.out.println("Items per page: " + result.getLimit());
    System.out.println("Total items: " + result.getTotal());
    System.out.println("Has more: " + result.getHasMore());
    System.out.println("Data size: " + result.getData().size());
}
```

### 1.3 Get Application by ID

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

### 1.4 Get Application API Keys

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

### 1.5 Initialize Application API Key

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

## 3. Chat Conversation Management

### 3.1 Get Application Chat Conversation List

#### Method

```java
DifyPageResult<ChatConversationResponse> chatConversations(ChatConversationsRequest request);
```

#### Request Parameters

ChatConversationsRequest

| Parameter name   | Type    | Required | Description                                                      |
|------------------|---------|----------|------------------------------------------------------------------|
| appId            | String  | Yes      | Application ID                                                   |
| page             | Integer | No       | Page number (default: 1)                                         |
| limit            | Integer | No       | Number of items per page (default: 10, range: 1-100)             |
| start            | String  | No       | Start time, format: yyyy-MM-dd HH:mm                             |
| end              | String  | No       | End time, format: yyyy-MM-dd HH:mm                               |
| sortBy           | String  | No       | Sort field, e.g., -created_at (sort by creation time descending) |
| annotationStatus | String  | No       | Annotation status: all, not_annotated, annotated                 |

#### Response Parameters

`DifyPageResult<ChatConversationResponse>`

| Parameter name | Type                             | Description                  |
|----------------|----------------------------------|------------------------------|
| data           | `List<ChatConversationResponse>` | Current page data list       |
| hasMore        | Boolean                          | Whether there are more pages |
| limit          | Integer                          | Items per page               |
| page           | Integer                          | Current page number          |
| total          | Integer                          | Total number of items        |

ChatConversationResponse

| Parameter name       | Type                  | Description               |
|----------------------|-----------------------|---------------------------|
| id                   | String                | Conversation ID           |
| status               | String                | Conversation status       |
| fromSource           | String                | Source                    |
| fromEndUserId        | String                | End user ID               |
| fromEndUserSessionId | String                | End user session ID       |
| fromAccountId        | String                | Account ID                |
| fromAccountName      | String                | Account name              |
| name                 | String                | Conversation name         |
| summary              | String                | Conversation summary      |
| readAt               | Long                  | Read time (timestamp)     |
| createdAt            | Long                  | Creation time (timestamp) |
| updatedAt            | Long                  | Update time (timestamp)   |
| annotated            | Boolean               | Whether annotated         |
| modelConfig          | `Map<String, Object>` | Model configuration       |
| messageCount         | Integer               | Message count             |
| userFeedbackStats    | FeedbackStats         | User feedback statistics  |
| adminFeedbackStats   | FeedbackStats         | Admin feedback statistics |
| statusCount          | StatusCount           | Status count              |

FeedbackStats

| Parameter name | Type    | Description   |
|----------------|---------|---------------|
| like           | Integer | Like count    |
| dislike        | Integer | Dislike count |

StatusCount

| Parameter name | Type    | Description           |
|----------------|---------|-----------------------|
| success        | Integer | Success count         |
| failed         | Integer | Failed count          |
| partialSuccess | Integer | Partial success count |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetChatConversations() {
    // Create request object
    ChatConversationsRequest request = new ChatConversationsRequest();
    request.setAppId("app-123456789");
    request.setPage(1);
    request.setLimit(10);
    request.setStart("2025-10-23 00:00");
    request.setEnd("2025-10-30 23:59");
    request.setAnnotationStatus("all");
    request.setSortBy("-created_at");

    // Get chat conversation list
    DifyPageResult<ChatConversationResponse> result = difyServer.chatConversations(request);
    
    System.out.println("Current page: " + result.getPage());
    System.out.println("Items per page: " + result.getLimit());
    System.out.println("Total items: " + result.getTotal());
    System.out.println("Has more: " + result.getHasMore());
    System.out.println("Data size: " + result.getData().size());
    
    for (ChatConversationResponse conversation : result.getData()) {
        System.out.println("Conversation ID: " + conversation.getId());
        System.out.println("Conversation name: " + conversation.getName());
        System.out.println("Is annotated: " + conversation.isAnnotated());
    }
}
```
