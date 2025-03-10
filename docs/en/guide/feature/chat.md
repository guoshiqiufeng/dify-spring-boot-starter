---
lang: en-US
title: Chat API
description: 
---

# Chat API

## Interface Overview

Chat service interfaces provide complete chat functionality integration capabilities, including message sending and
receiving, session management, voice conversion and other core functions. All interfaces require a valid API key for
authentication.
Use the `DifyChat` interface instance.

## Interface List

### 1. Send a message

#### Method

```java
ChatMessageSendResponse send(ChatMessageSendRequest sendRequest);
```

#### Request Parameters

ChatMessageSendRequest

| Parameter name | Type                  | Required | Description           |
|----------------|-----------------------|----------|-----------------------|
| apiKey         | String                | Yes      | apiKey                |
| userId         | String                | Yes      | User id               |
| conversationId | String                | No       | Chat session number   |
| content        | String                | Yes      | Message content       |
| files          | `List<ChatMessageFile>` | No       | file                  |
| inputs         | Map<String, Object>   | No       | Customized parameters |

#### Response parameter

ChatMessageSendResponse

| Parameter name | Type   | Description         |
|----------------|--------|---------------------|
| conversationId | String | Chat session number |
| messageId      | String | Message id          |
| createdAt      | Long   | Creating timestamps |
| taskId         | String | Task id             |
| id             | String | id                  |
| answer         | String | answer              |

### 2. Send Streaming Messages

#### Method

```java
Flux<ChatMessageSendResponse> sendChatMessageStream(ChatMessageSendRequest sendRequest);
```

#### Request Parameters

Same as the Send Message interface

#### Response parameter

Returns a stream of messages, each of which is formatted in the same way as the send message response

### 3. Termination message flow

#### Method

```java
void stopMessagesStream(String apiKey, String taskId, String userId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description |
|----------------|--------|----------|-------------|
| apiKey         | String | Yes      | apiKey      |
| taskId         | String | Yes      | taskId      |
| userId         | String | Yes      | userId      |

### 4. Get session list

#### Method

```java
DifyPageResult<MessageConversationsResponse> conversations(MessageConversationsRequest request);
```

#### Request Parameters

| Parameter name | Type    | Required | Description                            |
|----------------|---------|----------|----------------------------------------|
| apiKey         | String  | Yes      | apiKey                                 |
| userId         | String  | Yes      | userId                                 |
| lastId         | String  | No       | Last record id                         |
| limit          | Integer | No       | Number of records per page, default 20 |
| sortBy         | String  | No       | Sort field, default -updated_at        |

#### Response parameter

MessageConversationsResponse

| Parameter name | Type               | Description         |
|----------------|--------------------|---------------------|
| id             | String             | Chat session number |
| name           | String             | Session name        |
| inputs         | Map<String,Object> | Input parameter     |
| status         | String             | Session state       |
| introduction   | String             | Opening remarks     |
| createdAt      | Long               | Creating timestamps |
| updatedAt      | Long               | Updating timestamps |

### 5. Deleting a session

#### Method

```java
void deleteConversation(String conversationId, String apiKey, String userId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description         |
|----------------|--------|----------|---------------------|
| conversationId | String | Yes      | Chat session number |
| apiKey         | String | Yes      | apiKey              |
| userId         | String | Yes      | userId              |

### 6. Getting a list of messages

#### Method

```java
DifyPageResult<MessagesResponseVO> messages(MessagesRequest request);
```

#### Request Parameters

| Parameter name | Type    | Required | Description                             |
|----------------|---------|----------|-----------------------------------------|
| apiKey         | String  | Yes      | apiKey                                  |
| userId         | String  | Yes      | userId                                  |
| conversationId | String  | Yes      | Chat session number                     |
| firstId        | String  | No       | First record id                         |
| limit          | Integer | No       | Number of records per page, default 20条 |

### 7. Get Suggested Responses

#### Method

```java
List<String> messagesSuggested(String messageId, String apiKey, String userId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description |
|----------------|--------|----------|-------------|
| messageId      | String | Yes      | messageId   |
| apiKey         | String | Yes      | apiKey      |
| userId         | String | Yes      | userId      |

#### Response parameter

Return to the list of suggested response texts

### 8. text-to-speech

#### Method

```java
void textToAudio(TextToAudioRequest request, HttpServletResponse response);
```

#### Request Parameters

| Parameter name | Type   | Required | Description  |
|----------------|--------|----------|--------------|
| apiKey         | String | Yes      | apiKey       |
| userId         | String | Yes      | userId       |
| text           | String | Yes      | Convert Text |
| messageId      | String | No       | messageId    |

#### Response parameter

Returns an audio file stream

### 9. speech-to-text

#### Method

```java
DifyTextVO audioToText(AudioToTextRequest request);
```

#### Request Parameters

| Parameter name | Type          | Required | Description |
|----------------|---------------|----------|-------------|
| apiKey         | String        | Yes      | apiKey      |
| userId         | String        | Yes      | userId      |
| file           | MultipartFile | Yes      | Audio file  |

#### Response parameter

| Parameter name | Type   | Description  |
|----------------|--------|--------------|
| text           | String | Convert Text |
