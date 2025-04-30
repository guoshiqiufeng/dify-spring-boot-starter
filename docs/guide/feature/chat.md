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
| inputs         | `Map<String, Object>`   | 否    | 自定义参数  |

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

| 参数名          | 类型                   | 描述    |
|--------------|----------------------|-------|
| id           | String               | 会话 id |
| name         | String               | 会话名称  |
| inputs       | `Map<String,Object>` | 输入参数  |
| status       | String               | 会话状态  |
| introduction | String               | 开场白   |
| createdAt    | Long                 | 创建时间戳 |
| updatedAt    | Long                 | 更新时间戳 |

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

| 参数名          | 类型                   | 描述    |
|--------------|----------------------|-------|
| id           | String               | 会话 id |
| name         | String               | 会话名称  |
| inputs       | `Map<String,Object>` | 输入参数  |
| status       | String               | 会话状态  |
| introduction | String               | 开场白   |
| createdAt    | Long                 | 创建时间戳 |
| updatedAt    | Long                 | 更新时间戳 |

## 3. 其他

### 3.1 文本转语音

#### 方法

```java
ResponseEntity<byte[]> textToAudio(TextToAudioRequest request);
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

#### 使用示例

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

### 3.3 获取应用参数

#### 方法

```java
AppParametersResponseVO parameters(String apiKey);
```

#### 请求参数

| 参数名    | 类型     | 是否必须 | 描述     |
|--------|--------|------|--------|
| apiKey | String | 是    | apiKey |

#### 响应参数

AppParametersResponseVO

| 参数名                           | 类型                    | 描述          |
|-------------------------------|-----------------------|-------------|
| openingStatement              | String                | 开场白         |
| suggestedQuestions            | `List<String>`        | 开场推荐问题列表    |
| suggestedQuestionsAfterAnswer | Enabled               | 启用回答后给出推荐问题 |
| speechToText                  | Enabled               | 语音转文本功能配置   |
| textToSpeech                  | TextToSpeech          | 文本转语音功能配置   |
| retrieverResource             | Enabled               | 引用和归属功能配置   |
| annotationReply               | Enabled               | 标记回复功能配置    |
| moreLikeThis                  | Enabled               | 更多类似功能配置    |
| userInputForm                 | `List<UserInputForm>` | 用户输入表单配置    |
| sensitiveWordAvoidance        | Enabled               | 敏感词规避功能配置   |
| fileUpload                    | FileUpload            | 文件上传配置      |
| systemParameters              | FileUploadConfig      | 系统参数配置      |

Enabled 对象结构:

| 参数名     | 类型      | 描述   |
|---------|---------|------|
| enabled | Boolean | 是否启用 |

TextToSpeech 对象结构:

| 参数名     | 类型      | 描述   |
|---------|---------|------|
| enabled | Boolean | 是否启用 |
| voice   | String  | 语音类型 |

FileUpload 对象结构:

| 参数名                      | 类型               | 描述          |
|--------------------------|------------------|-------------|
| enabled                  | Boolean          | 是否启用文件上传功能  |
| image                    | FileUploadImage  | 图片上传配置      |
| allowedFileTypes         | `List<String>`   | 允许的文件类型列表   |
| allowedFileExtensions    | `List<String>`   | 允许的文件扩展名列表  |
| allowedFileUploadMethods | `List<String>`   | 允许的文件上传方式列表 |
| numberLimits             | Integer          | 文件数量限制      |
| fileUploadConfig         | FileUploadConfig | 文件上传详细配置    |

FileUploadImage 对象结构:

| 参数名             | 类型             | 描述                                |
|-----------------|----------------|-----------------------------------|
| enabled         | Boolean        | 是否启用图片上传功能                        |
| numberLimits    | Integer        | 图片数量限制，默认3                        |
| transferMethods | `List<String>` | 传递方式列表，可选值：remote_url, local_file |

FileUploadConfig 对象结构:

| 参数名                     | 类型      | 描述           |
|-------------------------|---------|--------------|
| fileSizeLimit           | Integer | 文件大小限制(MB)   |
| batchCountLimit         | Integer | 批量上传数量限制     |
| imageFileSizeLimit      | Integer | 图片文件大小限制(MB) |
| videoFileSizeLimit      | Integer | 视频文件大小限制(MB) |
| audioFileSizeLimit      | Integer | 音频文件大小限制(MB) |
| workflowFileUploadLimit | Integer | 工作流文件上传限制    |

UserInputForm 对象结构:

| 参数名       | 类型        | 描述         |
|-----------|-----------|------------|
| textInput | TextInput | 文本输入控件配置   |
| paragraph | Paragraph | 段落文本输入控件配置 |
| select    | Select    | 下拉控件配置     |

TextInput 对象结构:

| 参数名          | 类型      | 描述      |
|--------------|---------|---------|
| label        | String  | 控件展示标签名 |
| variable     | String  | 控件ID    |
| required     | Boolean | 是否必填    |
| maxLength    | Integer | 最大长度限制  |
| defaultValue | String  | 默认值     |

Paragraph 对象结构:
继承自 TextInput，具有相同的字段结构

Select 对象结构:

| 参数名          | 类型             | 描述      |
|--------------|----------------|---------|
| label        | String         | 控件展示标签名 |
| variable     | String         | 控件ID    |
| required     | Boolean        | 是否必填    |
| maxLength    | Integer        | 最大长度限制  |
| defaultValue | String         | 默认值     |
| type         | String         | 下拉类型    |
| options      | `List<String>` | 选项列表    | 

### 3.4 文件上传

#### 方法

```java
FileUploadResponse fileUpload(FileUploadRequest request);
```

#### 请求参数

| 参数名    | 类型            | 是否必须 | 描述     |
|--------|---------------|------|--------|
| apiKey | String        | 是    | apiKey |
| userId | String        | 是    | 用户 id  |
| file   | MultipartFile | 是    | 上传文件   |

#### 响应参数

FileUploadResponse

| 参数名       | 类型      | 描述       |
|-----------|---------|----------|
| id        | String  | 文件 id    |
| name      | String  | 文件名称     |
| size      | Integer | 文件大小(字节) |
| extension | String  | 文件后缀     |
| mimeType  | String  | 文件MIME类型 |
| createdBy | String  | 创建人      |
| createdAt | Long    | 创建时间戳    |

### 3.5 获取应用信息

#### 方法

```java
AppInfoResponse info(String apiKey);
```

#### 请求参数

| 参数名    | 类型     | 是否必须 | 描述     |
|--------|--------|------|--------|
| apiKey | String | 是    | apiKey |

#### 响应参数

AppInfoResponse

| 参数名         | 类型             | 描述     |
|-------------|----------------|--------|
| name        | String         | 应用名称   |
| description | String         | 应用描述   |
| tags        | `List<String>` | 应用标签列表 |

### 3.6 获取应用元数据

#### 方法

```java
AppMetaResponse meta(String apiKey);
```

#### 请求参数

| 参数名    | 类型     | 是否必须 | 描述     |
|--------|--------|------|--------|
| apiKey | String | 是    | apiKey |

#### 响应参数

AppMetaResponse

| 参数名       | 类型                    | 描述     |
|-----------|-----------------------|--------|
| toolIcons | `Map<String, Object>` | 工具图标映射 |

## 4. 应用标注

> 需要 Dify 1.2.0 或更高版本

### 4.1 获取应用标注列表

#### 方法

```java
DifyPageResult<AppAnnotationResponse> pageAppAnnotation(AppAnnotationPageRequest request);
```

#### 请求参数

AppAnnotationPageRequest

| 参数名    | 类型      | 是否必须 | 描述                  |
|--------|---------|------|---------------------|
| apiKey | String  | 是    | apiKey              |
| userId | String  | 是    | 用户 id               |
| page   | Integer | 否    | 页码，默认1              |
| limit  | Integer | 否    | 每页记录数，默认20，范围 1-100 |

#### 响应参数

AppAnnotationResponse

| 参数名       | 类型      | 描述    |
|-----------|---------|-------|
| id        | String  | 标注 id |
| question  | String  | 问题内容  |
| answer    | String  | 回答内容  |
| hitCount  | Integer | 命中次数  |
| createdAt | Long    | 创建时间戳 |

### 4.2 创建应用标注

#### 方法

```java
AppAnnotationResponse createAppAnnotation(AppAnnotationCreateRequest request);
```

#### 请求参数

AppAnnotationCreateRequest

| 参数名      | 类型     | 是否必须 | 描述     |
|----------|--------|------|--------|
| apiKey   | String | 是    | apiKey |
| userId   | String | 是    | 用户 id  |
| question | String | 是    | 问题内容   |
| answer   | String | 是    | 回答内容   |

#### 响应参数

AppAnnotationResponse

| 参数名       | 类型      | 描述    |
|-----------|---------|-------|
| id        | String  | 标注 id |
| question  | String  | 问题内容  |
| answer    | String  | 回答内容  |
| hitCount  | Integer | 命中次数  |
| createdAt | Long    | 创建时间戳 |

### 4.3 更新应用标注

> 需要 Dify 1.3.1 或更高版本

#### 方法

```java
AppAnnotationResponse updateAppAnnotation(AppAnnotationUpdateRequest request);
```

#### 请求参数

AppAnnotationUpdateRequest

| 参数名          | 类型     | 是否必须 | 描述     |
|--------------|--------|------|--------|
| apiKey       | String | 是    | apiKey |
| userId       | String | 是    | 用户 id  |
| annotationId | String | 是    | 标注 id  |
| question     | String | 是    | 问题内容   |
| answer       | String | 是    | 回答内容   |

#### 响应参数

AppAnnotationResponse

| 参数名       | 类型      | 描述    |
|-----------|---------|-------|
| id        | String  | 标注 id |
| question  | String  | 问题内容  |
| answer    | String  | 回答内容  |
| hitCount  | Integer | 命中次数  |
| createdAt | Long    | 创建时间戳 |

### 4.4 删除应用标注

#### 方法

```java
 void deleteAppAnnotation(String annotationId, String apiKey);
```

#### 请求参数

| 参数名          | 类型     | 是否必须 | 描述     |
|--------------|--------|------|--------|
| annotationId | String | 是    | 标注 id  |
| apiKey       | String | 是    | apiKey |

#### 响应参数

无

### 4.5 标注回复

#### 方法

```java
AppAnnotationReplyResponse annotationReply(AppAnnotationReplyRequest request);
```

#### 请求参数

AppAnnotationReplyRequest

| 参数名                   | 类型                        | 是否必须 | 描述      |
|-----------------------|---------------------------|------|---------|
| apiKey                | String                    | 是    | apiKey  |
| userId                | String                    | 是    | 用户 id   |
| action                | AnnotationReplyActionEnum | 是    | 回复动作类型  |
| embeddingProviderName | String                    | 否    | 嵌入模型提供商 |
| embeddingModelName    | String                    | 否    | 嵌入模型名称  |
| scoreThreshold        | Float                     | 否    | 评分阈值    |

#### 响应参数

AppAnnotationReplyResponse

| 参数名       | 类型     | 描述    |
|-----------|--------|-------|
| jobId     | String | 任务 id |
| jobStatus | String | 任务状态  |
| errorMsg  | String | 错误信息  |

### 4.6 查询标注回复状态

#### 方法

```java
AppAnnotationReplyResponse queryAnnotationReply(AppAnnotationReplyQueryRequest request);
```

#### 请求参数

AppAnnotationReplyQueryRequest

| 参数名    | 类型                        | 是否必须 | 描述        |
|--------|---------------------------|------|-----------|
| apiKey | String                    | 是    | apiKey    |
| userId | String                    | 是    | 用户 id     |
| action | AnnotationReplyActionEnum | 是    | 回复动作类型    |
| jobId  | String                    | 是    | 需要查询的任务id |

#### 响应参数

AppAnnotationReplyResponse

| 参数名       | 类型     | 描述    |
|-----------|--------|-------|
| jobId     | String | 任务 id |
| jobStatus | String | 任务状态  |
| errorMsg  | String | 错误信息  |
