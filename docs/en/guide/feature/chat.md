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

## 1. Message

### 1.1 Send a message

#### Method

```java
ChatMessageSendResponse send(ChatMessageSendRequest sendRequest);
```

#### Request Parameters

ChatMessageSendRequest

| Parameter name | Type                    | Required | Description           |
|----------------|-------------------------|----------|-----------------------|
| apiKey         | String                  | Yes      | apiKey                |
| userId         | String                  | Yes      | User id               |
| conversationId | String                  | No       | Chat session number   |
| content        | String                  | Yes      | Message content       |
| files          | `List<ChatMessageFile>` | No       | file                  |
| inputs         | `Map<String, Object>`   | No       | Customized parameters |

**ChatMessageFile Structure:**

| Parameter name | Type   | Description                                        |
|----------------|--------|----------------------------------------------------|
| type           | String | File type, default is "image"                      |
| transferMethod | String | Transfer method, default is "remote_url"           |
| url            | String | Remote URL path, when transferMethod is remote_url |
| uploadFileId   | String | Upload file ID, when transferMethod is local_file  |

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

### 1.2 Send Streaming Messages

#### Method

```java
Flux<ChatMessageSendCompletionResponse> sendChatMessageStream(ChatMessageSendRequest sendRequest);
```

#### Request Parameters

Same as the Send Message interface

#### Response parameter

Returns a message stream with the following structure:

ChatMessageSendCompletionResponse

| Parameter name | Type           | Description                           |
|----------------|----------------|---------------------------------------|
| workflowRunId  | String         | Workflow run ID                       |
| data           | CompletionData | Completion data, varies by event type |

The `CompletionData` structure varies depending on the event type:

| Event Type        | Description       | Corresponding Data Class |
|-------------------|-------------------|--------------------------|
| workflow_started  | Workflow started  | WorkflowStartedData      |
| node_started      | Node started      | NodeStartedData          |
| node_finished     | Node finished     | NodeFinishedData         |
| workflow_finished | Workflow finished | WorkflowFinishedData     |

### 1.3 Termination message flow

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

### 1.4 Message Feedback

#### Method

```java
MessageFeedbackResponse messageFeedback(MessageFeedbackRequest messageFeedbackRequest);
```

#### Request Parameters

| Parameter name | Type   | Required | Description                           |
|----------------|--------|----------|---------------------------------------|
| apiKey         | String | Yes      | apiKey                                |
| userId         | String | Yes      | userId                                |
| messageId      | String | Yes      | messageId                             |
| rating         | Rating | Yes      | rating                                |
| content        | String | Yes      | Message Feedback Specific Information |

#### Response parameter

MessageFeedbackResponse

| Parameter name | Type   | Description          |
|----------------|--------|----------------------|
| result         | String | Fixed return success |

### 1.5 Getting a list of messages

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

### 1.6 Get Suggested Responses

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

## 2. Session

### 2.1 Get session list

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

| Parameter name | Type                 | Description         |
|----------------|----------------------|---------------------|
| id             | String               | Chat session number |
| name           | String               | Session name        |
| inputs         | `Map<String,Object>` | Input parameter     |
| status         | String               | Session state       |
| introduction   | String               | Opening remarks     |
| createdAt      | Long                 | Creating timestamps |
| updatedAt      | Long                 | Updating timestamps |

### 2.2 Deleting a session

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

### 2.3 Session rename

#### Method

```java
MessageConversationsResponse renameConversation(RenameConversationRequest renameConversationRequest);
```

#### Request Parameters

| Parameter name | Type   | Required | Description                         |
|----------------|--------|----------|-------------------------------------|
| conversationId | String | Yes      | Chat session number                 |
| name           | String | Yes      | Session name                        |
| autoGenerate   | String | No       | Auto-generated title, default false |
| apiKey         | String | Yes      | apiKey                              |
| userId         | String | Yes      | userId                              |

#### Response parameter

MessageConversationsResponse

| Parameter name | Type                 | Description         |
|----------------|----------------------|---------------------|
| id             | String               | Chat session number |
| name           | String               | Session name        |
| inputs         | `Map<String,Object>` | Input parameter     |
| status         | String               | Session state       |
| introduction   | String               | Opening remarks     |
| createdAt      | Long                 | Creating timestamps |
| updatedAt      | Long                 | Updating timestamps |

## 3. Other

### 3.1 text-to-speech

#### Method

```java
ResponseEntity<byte[]> textToAudio(TextToAudioRequest request);
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

#### Usage example

```java
import java.io.IOException;

private void textToAudio(TextToAudioRequest request, HttpServletResponse response) {
    try {
        ResponseEntity<byte[]> responseEntity = difyChat.textToAudio(request);

        String type = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        response.setContentType(type != null ? type : "audio/mpeg");

        String contentDisposition = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        if (contentDisposition != null) {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        } else {
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audio.mp3");
        }

        if (responseEntity.getBody() != null) {
            response.getOutputStream().write(responseEntity.getBody());
            response.getOutputStream().flush();
        }

    } catch (Exception e) {
        log.error("textToAudio error: {}", e.getMessage());
        throw new RuntimeException("textToAudio error");
    }
}
```

### 3.2 speech-to-text

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

### 3.3 Get Application Parameters

#### Method

```java
AppParametersResponseVO parameters(String apiKey);
```

#### Request Parameters

| Parameter name | Type   | Required | Description |
|----------------|--------|----------|-------------|
| apiKey         | String | Yes      | apiKey      |

#### Response Parameters

AppParametersResponseVO

| Parameter name                | Type                  | Description                               |
|-------------------------------|-----------------------|-------------------------------------------|
| openingStatement              | String                | Opening statement                         |
| suggestedQuestions            | `List<String>`        | Opening recommended questions             |
| suggestedQuestionsAfterAnswer | Enabled               | Enable recommended questions after answer |
| speechToText                  | Enabled               | Speech-to-text feature config             |
| textToSpeech                  | TextToSpeech          | Text-to-speech feature config             |
| retrieverResource             | Enabled               | Reference and attribution config          |
| annotationReply               | Enabled               | Annotation reply config                   |
| moreLikeThis                  | Enabled               | More like this feature config             |
| userInputForm                 | `List<UserInputForm>` | User input form config                    |
| sensitiveWordAvoidance        | Enabled               | Sensitive word avoidance config           |
| fileUpload                    | FileUpload            | File upload config                        |
| systemParameters              | FileUploadConfig      | System parameters config                  |

Enabled Object Structure:

| Parameter name | Type    | Description     |
|----------------|---------|-----------------|
| enabled        | Boolean | Whether enabled |

TextToSpeech Object Structure:

| Parameter name | Type    | Description     |
|----------------|---------|-----------------|
| enabled        | Boolean | Whether enabled |
| voice          | String  | Voice type      |

FileUpload Object Structure:

| Parameter name           | Type             | Description                    |
|--------------------------|------------------|--------------------------------|
| enabled                  | Boolean          | Whether file upload is enabled |
| image                    | FileUploadImage  | Image upload configuration     |
| allowedFileTypes         | `List<String>  ` | Allowed file types list        |
| allowedFileExtensions    | `List<String>`   | Allowed file extensions list   |
| allowedFileUploadMethods | `List<String>`   | Allowed upload methods list    |
| numberLimits             | Integer          | File count limit               |
| fileUploadConfig         | FileUploadConfig | Detailed upload configuration  |

FileUploadImage Object Structure:

| Parameter name  | Type           | Description                              |
|-----------------|----------------|------------------------------------------|
| enabled         | Boolean        | Whether image upload is enabled          |
| numberLimits    | Integer        | Image count limit, default 3             |
| transferMethods | `List<String>` | Transfer methods: remote_url, local_file |

FileUploadConfig Object Structure:

| Parameter name          | Type    | Description                |
|-------------------------|---------|----------------------------|
| fileSizeLimit           | Integer | File size limit (MB)       |
| batchCountLimit         | Integer | Batch upload count limit   |
| imageFileSizeLimit      | Integer | Image file size limit (MB) |
| videoFileSizeLimit      | Integer | Video file size limit (MB) |
| audioFileSizeLimit      | Integer | Audio file size limit (MB) |
| workflowFileUploadLimit | Integer | Workflow file upload limit |

UserInputForm Object Structure:

| Parameter name | Type      | Description                 |
|----------------|-----------|-----------------------------|
| textInput      | TextInput | Text input control config   |
| paragraph      | Paragraph | Paragraph text input config |
| select         | Select    | Dropdown control config     |

TextInput Object Structure:

| Parameter name | Type    | Description           |
|----------------|---------|-----------------------|
| label          | String  | Control display label |
| variable       | String  | Control ID            |
| required       | Boolean | Whether required      |
| maxLength      | Integer | Maximum length limit  |
| defaultValue   | String  | Default value         |

Paragraph Object Structure:
Inherits from TextInput, has the same field structure

Select Object Structure:

| Parameter name | Type           | Description           |
|----------------|----------------|-----------------------|
| label          | String         | Control display label |
| variable       | String         | Control ID            |
| required       | Boolean        | Whether required      |
| maxLength      | Integer        | Maximum length limit  |
| defaultValue   | String         | Default value         |
| type           | String         | Dropdown type         |
| options        | `List<String>` | Options list          |

### 3.4 File Upload

#### Method

```java
FileUploadResponse fileUpload(FileUploadRequest request);
```

#### Request Parameters

| Parameter name | Type          | Required | Description    |
|----------------|---------------|----------|----------------|
| apiKey         | String        | Yes      | apiKey         |
| userId         | String        | Yes      | userId         |
| file           | MultipartFile | Yes      | File to upload |

#### Response Parameters

FileUploadResponse

| Parameter name | Type    | Description        |
|----------------|---------|--------------------|
| id             | String  | File id            |
| name           | String  | File name          |
| size           | Integer | File size (bytes)  |
| extension      | String  | File extension     |
| mimeType       | String  | File MIME type     |
| createdBy      | String  | Creator            |
| createdAt      | Long    | Creation timestamp |

### 3.5 Get Application Info

#### Method

```java
AppInfoResponse info(String apiKey);
```

#### Request Parameters

| Parameter name | Type   | Required | Description |
|----------------|--------|----------|-------------|
| apiKey         | String | Yes      | apiKey      |

#### Response Parameters

AppInfoResponse

| Parameter name | Type           | Description             |
|----------------|----------------|-------------------------|
| name           | String         | Application name        |
| description    | String         | Application description |
| tags           | `List<String>` | Application tags list   |

### 3.6 Get Application Metadata

#### Method

```java
AppMetaResponse meta(String apiKey);
```

#### Request Parameters

| Parameter name | Type   | Required | Description |
|----------------|--------|----------|-------------|
| apiKey         | String | Yes      | apiKey      |

#### Response Parameters

AppMetaResponse

| Parameter name | Type                  | Description        |
|----------------|-----------------------|--------------------|
| toolIcons      | `Map<String, Object>` | Tool icons mapping |

## 4. Application Annotation

> required Dify version 1.2.0 or higher

### 4.1 Get Application Annotations List

#### Method

```java
DifyPageResult<AppAnnotationResponse> pageAppAnnotation(AppAnnotationPageRequest request);
```

#### Request Parameters

AppAnnotationPageRequest

| Parameter name | Type    | Required | Description                               |
|----------------|---------|----------|-------------------------------------------|
| apiKey         | String  | Yes      | apiKey                                    |
| userId         | String  | Yes      | User id                                   |
| page           | Integer | No       | Page number, default 1                    |
| limit          | Integer | No       | Records per page, default 20, range 1-100 |

#### Response Parameters

AppAnnotationResponse

| Parameter name | Type    | Description        |
|----------------|---------|--------------------|
| id             | String  | Annotation id      |
| question       | String  | Question content   |
| answer         | String  | Answer content     |
| hitCount       | Integer | Hit count          |
| createdAt      | Long    | Creation timestamp |

### 4.2 Create Application Annotation

#### Method

```java
AppAnnotationResponse createAppAnnotation(AppAnnotationCreateRequest request);
```

#### Request Parameters

AppAnnotationCreateRequest

| Parameter name | Type   | Required | Description      |
|----------------|--------|----------|------------------|
| apiKey         | String | Yes      | apiKey           |
| userId         | String | Yes      | User id          |
| question       | String | Yes      | Question content |
| answer         | String | Yes      | Answer content   |

#### Response Parameters

AppAnnotationResponse

| Parameter name | Type    | Description        |
|----------------|---------|--------------------|
| id             | String  | Annotation id      |
| question       | String  | Question content   |
| answer         | String  | Answer content     |
| hitCount       | Integer | Hit count          |
| createdAt      | Long    | Creation timestamp |

### 4.3 Update Application Annotation

> required Dify version 1.3.1 or higher

#### Method

```java
AppAnnotationResponse updateAppAnnotation(AppAnnotationUpdateRequest request);
```

#### Request Parameters

AppAnnotationUpdateRequest

| Parameter name | Type   | Required | Description      |
|----------------|--------|----------|------------------|
| apiKey         | String | Yes      | apiKey           |
| userId         | String | Yes      | User id          |
| annotationId   | String | Yes      | Annotation id    |
| question       | String | Yes      | Question content |
| answer         | String | Yes      | Answer content   |

#### Response Parameters

AppAnnotationResponse

| Parameter name | Type    | Description        |
|----------------|---------|--------------------|
| id             | String  | Annotation id      |
| question       | String  | Question content   |
| answer         | String  | Answer content     |
| hitCount       | Integer | Hit count          |
| createdAt      | Long    | Creation timestamp |

### 4.4 Delete Application Annotation

#### Method

```java
void deleteAppAnnotation(String annotationId, String apiKey);
```

#### Request Parameters

| Parameter name | Type   | Required | Description   |
|----------------|--------|----------|---------------|
| annotationId   | String | Yes      | Annotation id |
| apiKey         | String | Yes      | apiKey        |

#### Response Parameters

not have

### 4.5 Annotation Reply

#### Method

```java
AppAnnotationReplyResponse annotationReply(AppAnnotationReplyRequest request);
```

#### Request Parameters

AppAnnotationReplyRequest

| Parameter name        | Type                      | Required | Description              |
|-----------------------|---------------------------|----------|--------------------------|
| apiKey                | String                    | Yes      | API key                  |
| userId                | String                    | Yes      | User ID                  |
| action                | AnnotationReplyActionEnum | Yes      | Reply action type        |
| embeddingProviderName | String                    | No       | Embedding model provider |
| embeddingModelName    | String                    | No       | Embedding model name     |
| scoreThreshold        | Float                     | No       | Score threshold          |

**AnnotationReplyActionEnum values:**

- enable - Enable annotation reply
- disable - Disable annotation reply

#### Response Parameters

AppAnnotationReplyResponse

| Parameter name | Type   | Description   |
|----------------|--------|---------------|
| jobId          | String | Job ID        |
| jobStatus      | String | Job status    |
| errorMsg       | String | Error message |

### 4.6 Query Annotation Reply Status

#### Method

```java
AppAnnotationReplyResponse queryAnnotationReply(AppAnnotationReplyQueryRequest request);
```

#### Request Parameters

AppAnnotationReplyQueryRequest

| Parameter name | Type                      | Required | Description       |
|----------------|---------------------------|----------|-------------------|
| apiKey         | String                    | Yes      | API key           |
| userId         | String                    | Yes      | User ID           |
| action         | AnnotationReplyActionEnum | Yes      | Reply action type |
| jobId          | String                    | Yes      | Job ID to query   |

#### Response Parameters

AppAnnotationReplyResponse

| Parameter name | Type   | Description   |
|----------------|--------|---------------|
| jobId          | String | Job ID        |
| jobStatus      | String | Job status    |
| errorMsg       | String | Error message |

