---
lang: zh-cn
title: Chat API
description: 
---

# Chat API

## 接口概述

聊天服务接口提供完整的聊天功能集成能力，包含消息收发、会话管理、语音转换等核心功能。所有接口均需要有效的API密钥进行身份验证。
使用`DifyChat`接口实例。

## 1. 消息

### 1.1 发送消息

#### 方法

```java
ChatMessageSendResponse send(ChatMessageSendRequest sendRequest);
```

#### 请求参数

ChatMessageSendRequest

| 参数名            | 类型                      | 是否必须 | 描述     |
|----------------|-------------------------|------|--------|
| apiKey         | String                  | 是    | apiKey |
| userId         | String                  | 是    | 用户 id  |
| conversationId | String                  | 否    | 聊天对话编号 |
| content        | String                  | 是    | 消息内容   |
| files          | `List<ChatMessageFile>` | 否    | 文件     |
| inputs         | Map<String, Object>     | 否    | 自定义参数  |

#### 响应参数

ChatMessageSendResponse

| 参数名            | 类型     | 描述    |
|----------------|--------|-------|
| conversationId | String | 会话 id |
| messageId      | String | 消息id  |
| createdAt      | Long   | 创建时间戳 |
| taskId         | String | 任务 id |
| id             | String | id    |
| answer         | String | 回答    |

### 1.2 发送流式消息

#### 方法

```java
Flux<ChatMessageSendResponse> sendChatMessageStream(ChatMessageSendRequest sendRequest);
```

#### 请求参数

与发送消息接口相同

#### 响应参数

返回消息流，每条消息格式与发送消息响应相同

### 1.3 终止消息流

#### 方法

```java
void stopMessagesStream(String apiKey, String taskId, String userId);
```

#### 请求参数

| 参数名    | 类型     | 是否必须 | 描述     |
|--------|--------|------|--------|
| apiKey | String | 是    | apiKey |
| taskId | String | 是    | 任务 id  |
| userId | String | 是    | 用户 id  |

### 1.4 消息反馈

#### 方法

```java
MessageFeedbackResponse messageFeedback(MessageFeedbackRequest messageFeedbackRequest);
```

#### 请求参数

| 参数名       | 类型     | 是否必须 | 描述        |
|-----------|--------|------|-----------|
| apiKey    | String | 是    | apiKey    |
| userId    | String | 是    | 用户 id     |
| messageId | String | 是    | 消息 id     |
| rating    | Rating | 是    | 评级        |
| content   | String | 是    | 消息反馈的具体信息 |

#### 响应参数

MessageFeedbackResponse

| 参数名    | 类型     | 描述           |
|--------|--------|--------------|
| result | String | 固定返回 success |

### 1.5 获取消息列表

#### 方法

```java
DifyPageResult<MessagesResponseVO> messages(MessagesRequest request);
```

#### 请求参数

| 参数名            | 类型      | 是否必须 | 描述          |
|----------------|---------|------|-------------|
| apiKey         | String  | 是    | apiKey      |
| userId         | String  | 是    | 用户 id       |
| conversationId | String  | 是    | 会话 id       |
| firstId        | String  | 否    | 第一条记录 id    |
| limit          | Integer | 否    | 每页记录数,默认20条 |

### 1.6 获取建议回复

#### 方法

```java
List<String> messagesSuggested(String messageId, String apiKey, String userId);
```

#### 请求参数

| 参数名       | 类型     | 是否必须 | 描述     |
|-----------|--------|------|--------|
| messageId | String | 是    | 消息 id  |
| apiKey    | String | 是    | apiKey |
| userId    | String | 是    | 用户 id  |

#### 响应参数

返回建议回复文本列表

## 2. 会话

### 2.1 获取会话列表

#### 方法

```java
DifyPageResult<MessageConversationsResponse> conversations(MessageConversationsRequest request);
```

#### 请求参数

| 参数名    | 类型      | 是否必须 | 描述                 |
|--------|---------|------|--------------------|
| apiKey | String  | 是    | apiKey             |
| userId | String  | 是    | 用户 id              |
| lastId | String  | 否    | 最后一条记录 id          |
| limit  | Integer | 否    | 每页记录数,默认20         |
| sortBy | String  | 否    | 排序字段,默认-updated_at |

#### 响应参数

MessageConversationsResponse

| 参数名          | 类型                 | 描述    |
|--------------|--------------------|-------|
| id           | String             | 会话 id |
| name         | String             | 会话名称  |
| inputs       | Map<String,Object> | 输入参数  |
| status       | String             | 会话状态  |
| introduction | String             | 开场白   |
| createdAt    | Long               | 创建时间戳 |
| updatedAt    | Long               | 更新时间戳 |

### 2.2 删除会话

#### 方法

```java
void deleteConversation(String conversationId, String apiKey, String userId);
```

#### 请求参数

| 参数名            | 类型     | 是否必须 | 描述     |
|----------------|--------|------|--------|
| conversationId | String | 是    | 会话 id  |
| apiKey         | String | 是    | apiKey |
| userId         | String | 是    | 用户 id  |

### 2.3 会话重命名

#### 方法

```java
MessageConversationsResponse renameConversation(RenameConversationRequest renameConversationRequest);
```

#### 请求参数

| 参数名            | 类型     | 是否必须 | 描述              |
|----------------|--------|------|-----------------|
| conversationId | String | 是    | 会话 id           |
| name           | String | 是    | 会话名称            |
| autoGenerate   | String | 否    | 自动生成标题，默认 false |
| apiKey         | String | 是    | apiKey          |
| userId         | String | 是    | 用户 id           |

#### 响应参数

MessageConversationsResponse

| 参数名          | 类型                 | 描述    |
|--------------|--------------------|-------|
| id           | String             | 会话 id |
| name         | String             | 会话名称  |
| inputs       | Map<String,Object> | 输入参数  |
| status       | String             | 会话状态  |
| introduction | String             | 开场白   |
| createdAt    | Long               | 创建时间戳 |
| updatedAt    | Long               | 更新时间戳 |

## 3. 其他

### 3.1 文本转语音

#### 方法

```java
void textToAudio(TextToAudioRequest request, HttpServletResponse response);
```

#### 请求参数

| 参数名       | 类型     | 是否必须 | 描述     |
|-----------|--------|------|--------|
| apiKey    | String | 是    | apiKey |
| userId    | String | 是    | 用户 id  |
| text      | String | 是    | 转换文本   |
| messageId | String | 否    | 消息 id  |

#### 响应参数

返回音频文件流

### 3.2 语音转文本

#### 方法

```java
DifyTextVO audioToText(AudioToTextRequest request);
```

#### 请求参数

| 参数名    | 类型            | 是否必须 | 描述     |
|--------|---------------|------|--------|
| apiKey | String        | 是    | apiKey |
| userId | String        | 是    | 用户 id  |
| file   | MultipartFile | 是    | 音频文件   |

#### 响应参数

| 参数名  | 类型     | 描述   |
|------|--------|------|
| text | String | 转换文本 |
