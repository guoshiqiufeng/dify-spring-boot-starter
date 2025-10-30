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
> 默认检测当前环境包含 redis 则使用 redis持久化 token，若不包含 redis 则使用默认实现保存 token(重启服务会丢失)

## 1. 应用管理

### 1.1 获取所有应用 (非分页)

#### 方法

```java
List<AppsResponseVO> apps(String mode, String name);
```

#### 请求参数

| 参数名  | 类型     | 是否必须 | 描述                                                   |
|------|--------|------|------------------------------------------------------|
| mode | String | 否    | 模式 chat\agent-chat\completion\advanced-chat\workflow |
| name | String | 否    | 应用名称，用于过滤应用列表（可选，传入空字符串时表示不过滤）                       |

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

### 1.2 分页获取应用

#### 方法

```java
AppsResponseResult apps(AppsRequest appsRequest);
```

#### 请求参数

AppsRequest

| 参数名           | 类型      | 是否必须 | 描述                                                       |
|---------------|---------|------|----------------------------------------------------------|
| page          | Integer | 否    | 页码（默认：1）                                                 |
| limit         | Integer | 否    | 每页数量（默认：20，范围：1-100）                                     |
| mode          | String  | 否    | 应用模式过滤：chat\agent-chat\completion\advanced-chat\workflow |
| name          | String  | 否    | 应用名称过滤                                                   |
| isCreatedByMe | Boolean | 否    | 是否为当前用户创建的应用                                             |

#### 响应参数

AppsResponseResult

| 参数名     | 类型                   | 描述      |
|---------|----------------------|---------|
| data    | `List<AppsResponse>` | 当前页数据列表 |
| hasMore | Boolean              | 是否有更多页  |
| limit   | Integer              | 每页数量    |
| page    | Integer              | 当前页码    |
| total   | Integer              | 总数据数量   |

AppsResponse 的结构与 1.1 节中定义的相同。

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetAppsPaginated() {
    // 创建分页请求
    AppsRequest request = new AppsRequest();
    request.setPage(1);
    request.setLimit(10);
    request.setMode("chat");
    request.setName("myApp");
    request.setIsCreatedByMe(true);

    // 获取分页应用列表
    AppsResponseResult result = difyServer.apps(request);

    System.out.println("当前页: " + result.getPage());
    System.out.println("每页数量: " + result.getLimit());
    System.out.println("总数: " + result.getTotal());
    System.out.println("是否有更多: " + result.getHasMore());
    System.out.println("数据大小: " + result.getData().size());
}
```

### 1.3 根据ID获取应用

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

### 1.4 获取应用API密钥

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

### 1.5 初始化应用API密钥

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

## 3. 聊天会话管理

### 3.1 获取应用的聊天会话列表

#### 方法

```java
DifyPageResult<ChatConversationResponse> chatConversations(ChatConversationsRequest request);
```

#### 请求参数

ChatConversationsRequest

| 参数名              | 类型      | 是否必须 | 描述                               |
|------------------|---------|------|----------------------------------|
| appId            | String  | 是    | 应用ID                             |
| page             | Integer | 否    | 页码（默认：1）                         |
| limit            | Integer | 否    | 每页数量（默认：10，范围：1-100）             |
| start            | String  | 否    | 开始时间，格式：yyyy-MM-dd HH:mm         |
| end              | String  | 否    | 结束时间，格式：yyyy-MM-dd HH:mm         |
| sortBy           | String  | 否    | 排序字段，例如：-created_at（按创建时间倒序）     |
| annotationStatus | String  | 否    | 注释状态：all、not_annotated、annotated |

#### 响应参数

`DifyPageResult<ChatConversationResponse>`

| 参数名     | 类型                               | 描述      |
|---------|----------------------------------|---------|
| data    | `List<ChatConversationResponse>` | 当前页数据列表 |
| hasMore | Boolean                          | 是否有更多页  |
| limit   | Integer                          | 每页数量    |
| page    | Integer                          | 当前页码    |
| total   | Integer                          | 总数据数量   |

ChatConversationResponse

| 参数名                  | 类型                    | 描述       |
|----------------------|-----------------------|----------|
| id                   | String                | 会话ID     |
| status               | String                | 会话状态     |
| fromSource           | String                | 来源       |
| fromEndUserId        | String                | 终端用户ID   |
| fromEndUserSessionId | String                | 终端用户会话ID |
| fromAccountId        | String                | 账户ID     |
| fromAccountName      | String                | 账户名称     |
| name                 | String                | 会话名称     |
| summary              | String                | 会话摘要     |
| readAt               | Long                  | 阅读时间戳    |
| createdAt            | Long                  | 创建时间戳    |
| updatedAt            | Long                  | 更新时间戳    |
| annotated            | Boolean               | 是否已标注    |
| modelConfig          | `Map<String, Object>` | 模型配置     |
| messageCount         | Integer               | 消息数量     |
| userFeedbackStats    | FeedbackStats         | 用户反馈统计   |
| adminFeedbackStats   | FeedbackStats         | 管理员反馈统计  |
| statusCount          | StatusCount           | 状态计数     |

FeedbackStats

| 参数名     | 类型      | 描述  |
|---------|---------|-----|
| like    | Integer | 点赞数 |
| dislike | Integer | 点踩数 |

StatusCount

| 参数名            | 类型      | 描述     |
|----------------|---------|--------|
| success        | Integer | 成功数量   |
| failed         | Integer | 失败数量   |
| partialSuccess | Integer | 部分成功数量 |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetChatConversations() {
    // 创建请求对象
    ChatConversationsRequest request = new ChatConversationsRequest();
    request.setAppId("app-123456789");
    request.setPage(1);
    request.setLimit(10);
    request.setStart("2025-10-23 00:00");
    request.setEnd("2025-10-30 23:59");
    request.setAnnotationStatus("all");
    request.setSortBy("-created_at");

    // 获取聊天会话列表
    DifyPageResult<ChatConversationResponse> result = difyServer.chatConversations(request);

    System.out.println("当前页: " + result.getPage());
    System.out.println("每页数量: " + result.getLimit());
    System.out.println("总数: " + result.getTotal());
    System.out.println("是否有更多: " + result.getHasMore());
    System.out.println("数据大小: " + result.getData().size());

    for (ChatConversationResponse conversation : result.getData()) {
        System.out.println("会话ID: " + conversation.getId());
        System.out.println("会话名称: " + conversation.getName());
        System.out.println("是否标注: " + conversation.isAnnotated());
    }
}
```

## 4. 应用统计

### 4.1 获取应用的每日对话统计

#### 方法

```java
List<DailyConversationsResponse> dailyConversations(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

DailyConversationsResponse

| 参数名               | 类型      | 描述               |
|-------------------|---------|------------------|
| date              | String  | 日期，格式：yyyy-MM-dd |
| conversationCount | Integer | 当日对话数量           |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDailyConversations() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取每日对话统计
    List<DailyConversationsResponse> dailyStats = difyServer.dailyConversations(appId, start, end);

    if (dailyStats != null) {
        for (DailyConversationsResponse dailyStat : dailyStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("对话数量: " + dailyStat.getConversationCount());
        }
    }
}
```

### 4.2 获取应用的每日终端用户统计

#### 方法

```java
List<DailyEndUsersResponse> dailyEndUsers(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

DailyEndUsersResponse

| 参数名            | 类型      | 描述               |
|----------------|---------|------------------|
| date           | String  | 日期，格式：yyyy-MM-dd |
| terminalCount  | Integer | 当日终端用户数量        |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDailyEndUsers() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取每日终端用户统计
    List<DailyEndUsersResponse> dailyEndUsersStats = difyServer.dailyEndUsers(appId, start, end);

    if (dailyEndUsersStats != null) {
        for (DailyEndUsersResponse dailyStat : dailyEndUsersStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("终端用户数量: " + dailyStat.getTerminalCount());
        }
    }
}
```

### 6. 平均会话交互统计

### 6.1 获取应用的平均会话交互统计

#### 方法

```java
List<AverageSessionInteractionsResponse> averageSessionInteractions(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

AverageSessionInteractionsResponse

| 参数名        | 类型   | 描述               |
|-------------|------|------------------|
| date        | String  | 日期，格式：yyyy-MM-dd |
| interactions| Double | 当日平均会话交互数      |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetAverageSessionInteractions() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取平均会话交互统计
    List<AverageSessionInteractionsResponse> averageSessionInteractionsStats = difyServer.averageSessionInteractions(appId, start, end);

    if (averageSessionInteractionsStats != null) {
        for (AverageSessionInteractionsResponse dailyStat : averageSessionInteractionsStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("平均会话交互数: " + dailyStat.getInteractions());
        }
    }
}
```

### 7. 每秒令牌统计

### 7.1 获取应用的每秒令牌统计

#### 方法

```java
List<TokensPerSecondResponse> tokensPerSecond(String appId, LocalDateTime start, LocalDateTime end);
```

#### 请求参数

| 参数名   | 类型            | 是否必须 | 描述                       |
|-------|---------------|------|--------------------------|
| appId | String        | 是    | 应用 ID                    |
| start | LocalDateTime | 是    | 开始时间，格式：yyyy-MM-dd HH:mm |
| end   | LocalDateTime | 是    | 结束时间，格式：yyyy-MM-dd HH:mm |

#### 响应参数

TokensPerSecondResponse

| 参数名        | 类型   | 描述               |
|-------------|------|------------------|
| date        | String  | 日期，格式：yyyy-MM-dd |
| tps         | Double | 当日每秒令牌数        |

#### 请求示例

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetTokensPerSecond() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // 获取每秒令牌统计
    List<TokensPerSecondResponse> tokensPerSecondStats = difyServer.tokensPerSecond(appId, start, end);

    if (tokensPerSecondStats != null) {
        for (TokensPerSecondResponse dailyStat : tokensPerSecondStats) {
            System.out.println("日期: " + dailyStat.getDate());
            System.out.println("每秒令牌数: " + dailyStat.getTps());
        }
    }
}
```
