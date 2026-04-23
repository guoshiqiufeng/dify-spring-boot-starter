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

### 1.4 Create Application

> Available since `2.3.0`

#### Method

```java
AppsResponse createApp(AppCreateRequest request);
```

#### Request Parameters

AppCreateRequest

| Parameter      | Type   | Required | Description                                                                  |
|----------------|--------|----------|------------------------------------------------------------------------------|
| name           | String | Yes      | Application name                                                             |
| mode           | String | Yes      | Application mode: chat, agent-chat, advanced-chat, workflow, completion      |
| description    | String | No       | Application description                                                      |
| iconType       | String | No       | Icon type                                                                    |
| icon           | String | No       | Icon content                                                                 |
| iconBackground | String | No       | Icon background color                                                        |

#### Response Parameters

Same as the `AppsResponse` defined in section 1.1.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testCreateApp() {
    AppCreateRequest request = new AppCreateRequest();
    request.setName("My Agent");
    request.setMode("agent-chat");
    request.setDescription("Agent application sample");

    AppsResponse app = difyServer.createApp(request);
}
```

### 1.5 Update Application

> Available since `2.3.0`

#### Method

```java
AppsResponse updateApp(String appId, AppUpdateRequest request);
```

#### Request Parameters

| Parameter | Type             | Required | Description                 |
|-----------|------------------|----------|-----------------------------|
| appId     | String           | Yes      | Application ID              |
| request   | AppUpdateRequest | Yes      | Fields to update            |

AppUpdateRequest

| Parameter           | Type    | Required | Description                                 |
|---------------------|---------|----------|---------------------------------------------|
| name                | String  | Yes      | Application name                            |
| description         | String  | No       | Application description                     |
| iconType            | String  | No       | Icon type                                   |
| icon                | String  | No       | Icon content                                |
| iconBackground      | String  | No       | Icon background color                       |
| useIconAsAnswerIcon | Boolean | No       | Whether to use the icon as the answer icon  |
| maxActiveRequests   | Integer | No       | Maximum active requests (`0` means unlimited) |

#### Response Parameters

Same as the `AppsResponse` defined in section 1.1.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testUpdateApp() {
    AppUpdateRequest request = new AppUpdateRequest();
    request.setName("Renamed App");
    request.setDescription("Updated description");

    AppsResponse app = difyServer.updateApp("app-123456789", request);
}
```

### 1.6 Delete Application

> Available since `2.3.0`

#### Method

```java
void deleteApp(String appId);
```

#### Request Parameters

| Parameter | Type   | Required | Description    |
|-----------|--------|----------|----------------|
| appId     | String | Yes      | Application ID |

#### Response Parameters

No return value. Returns 204 No Content on success.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testDeleteApp() {
    difyServer.deleteApp("app-123456789");
}
```

### 1.7 Get Application API Keys

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

### 1.8 Initialize Application API Key

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

### 1.9 Delete Application API Key

#### Method

```java
void deleteAppApiKey(String appId, String apiKeyId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description      |
|----------------|--------|----------|------------------|
| appId          | String | Yes      | Application ID   |
| apiKeyId       | String | Yes      | API Key ID       |

#### Response Parameters

This method does not return a value. It returns 204 No Content on success.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testDeleteAppApiKey() {
    // Delete API key for specified application
    difyServer.deleteAppApiKey("app-123456789", "key-789012345");
}
```

### 1.10 Publish Workflow

#### Method

```java
void workflowsPublish(String appId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description    |
|----------------|--------|----------|----------------|
| appId          | String | Yes      | Application ID |

#### Response Parameters

This method does not return a value. 

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testWorkflowsPublish() {
    // Publish the specified workflow application
    difyServer.workflowsPublish("app-123456789");
}
```

### 1.11 Publish Application Model Configuration

> Available since `2.3.0`

Corresponds to the console "Publish update" action for `chat` / `agent-chat` / `completion` mode apps (prompt, model, tools, file upload, etc.). For `advanced-chat` / `workflow` apps, use [1.10 Publish Workflow](#_1-10-publish-workflow) instead.

#### Method

```java
void updateAppModelConfig(String appId, AppModelConfigRequest request);
```

#### Request Parameters

| Parameter | Type                  | Required | Description                    |
|-----------|-----------------------|----------|--------------------------------|
| appId     | String                | Yes      | Application ID                 |
| request   | AppModelConfigRequest | Yes      | Model configuration payload    |

AppModelConfigRequest

| Parameter                     | Type                             | Required | Description                                                    |
|-------------------------------|----------------------------------|----------|----------------------------------------------------------------|
| prePrompt                     | String                           | No       | Pre-prompt text (used when `prompt_type=simple`)               |
| promptType                    | String                           | No       | Prompt type, `simple` or `advanced`                            |
| chatPromptConfig              | Map&lt;String, Object>           | No       | Advanced chat prompt configuration                             |
| completionPromptConfig        | Map&lt;String, Object>           | No       | Advanced completion prompt configuration                       |
| userInputForm                 | List&lt;Map&lt;String, Object>>  | No       | User input form definition                                     |
| datasetQueryVariable          | String                           | No       | Variable used for dataset queries                              |
| moreLikeThis                  | EnabledConfig                    | No       | "More like this" feature                                       |
| openingStatement              | String                           | No       | Conversation opening greeting                                  |
| suggestedQuestions            | List&lt;String>                  | No       | Suggested questions shown with the opening statement           |
| sensitiveWordAvoidance        | SensitiveWordAvoidance           | No       | Sensitive word moderation configuration                        |
| speechToText                  | EnabledConfig                    | No       | Speech-to-text configuration                                   |
| textToSpeech                  | TextToSpeech                     | No       | Text-to-speech configuration                                   |
| fileUpload                    | FileUpload                       | No       | File upload configuration                                      |
| suggestedQuestionsAfterAnswer | EnabledConfig                    | No       | Suggested follow-up questions after each answer                |
| retrieverResource             | EnabledConfig                    | No       | Whether to expose retrieval sources (citations)                |
| agentMode                     | AgentMode                        | No       | Agent mode configuration (strategy, tools, max iterations, ...) |
| model                         | Model                            | No       | Model configuration (provider, name, mode, completion_params)  |
| datasetConfigs                | DatasetConfigs                   | No       | Dataset retrieval configuration                                |

AgentMode

| Parameter    | Type               | Required | Description                               |
|--------------|--------------------|----------|-------------------------------------------|
| enabled      | Boolean            | No       | Whether agent mode is enabled             |
| strategy     | String             | No       | Strategy, e.g. `function_call`, `react`   |
| maxIteration | Integer            | No       | Maximum iteration count                   |
| tools        | List&lt;AgentTool> | No       | Enabled tools                             |
| prompt       | String             | No       | Custom agent prompt                       |

AgentTool

| Parameter      | Type                   | Required | Description                     |
|----------------|------------------------|----------|---------------------------------|
| providerId     | String                 | No       | Tool provider ID                |
| providerType   | String                 | No       | Provider type, e.g. `builtin`   |
| providerName   | String                 | No       | Provider name                   |
| toolName       | String                 | No       | Tool name                       |
| toolLabel      | String                 | No       | Tool display label              |
| toolParameters | Map&lt;String, Object> | No       | Tool parameters                 |
| enabled        | Boolean                | No       | Whether the tool is enabled     |
| isDeleted      | Boolean                | No       | Whether the tool is deleted     |
| notAuthor      | Boolean                | No       | Whether caller is not the author |

Model

| Parameter        | Type                   | Required | Description                                      |
|------------------|------------------------|----------|--------------------------------------------------|
| provider         | String                 | No       | Model provider, e.g. `langgenius/ollama/ollama`  |
| name             | String                 | No       | Model name                                       |
| mode             | String                 | No       | Model mode, e.g. `chat`, `completion`            |
| completionParams | Map&lt;String, Object> | No       | Completion parameters such as `stop`, `temperature` |

#### Response Parameters

This method does not return a value.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testUpdateAppModelConfig() {
    AppModelConfigRequest request = new AppModelConfigRequest();
    request.setPrePrompt("hi2");
    request.setPromptType("simple");
    request.setOpeningStatement("Hi! How can I help you?");
    request.setSuggestedQuestions(List.of("hi", "hello"));

    AppModelConfigRequest.Model model = new AppModelConfigRequest.Model();
    model.setProvider("langgenius/ollama/ollama");
    model.setName("qwen2.5:latest");
    model.setMode("chat");
    model.setCompletionParams(Map.of("stop", List.of()));
    request.setModel(model);

    AppModelConfigRequest.AgentMode agentMode = new AppModelConfigRequest.AgentMode();
    agentMode.setEnabled(true);
    agentMode.setMaxIteration(5);
    agentMode.setStrategy("function_call");
    request.setAgentMode(agentMode);

    difyServer.updateAppModelConfig("app-123456789", request);
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

### 2.3 Delete Knowledge Base API Key

#### Method

```java
void deleteDatasetApiKey(String apiKeyId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description      |
|----------------|--------|----------|------------------|
| apiKeyId       | String | Yes      | API Key ID       |

#### Response Parameters

This method does not return a value. It returns 204 No Content on success.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testDeleteDatasetApiKey() {
    // Delete specified knowledge base API key
    difyServer.deleteDatasetApiKey("89f04b59-6906-4d32-a630-d2911d3b5fd8");
}
```

### 2.4 Get Dataset Indexing Status

#### Method

```java
DocumentIndexingStatusResponse getDatasetIndexingStatus(String datasetId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description |
|----------------|--------|----------|-------------|
| datasetId      | String | Yes      | Dataset ID  |

#### Response Parameters

DocumentIndexingStatusResponse

| Parameter name | Type                       | Description                  |
|----------------|----------------------------|------------------------------|
| data           | `List<ProcessingStatus>`   | Document indexing status list |

ProcessingStatus

| Parameter name       | Type    | Description                                                                                      |
|---------------------|---------|--------------------------------------------------------------------------------------------------|
| id                  | String  | Document ID                                                                                      |
| indexingStatus      | String  | Indexing status: waiting, parsing, cleaning, splitting, indexing, completed, error, paused       |
| processingStartedAt | Long    | Processing start time (timestamp)                                                                |
| parsingCompletedAt  | Long    | Parsing completion time (timestamp)                                                              |
| cleaningCompletedAt | Long    | Cleaning completion time (timestamp)                                                             |
| splittingCompletedAt| Long    | Splitting completion time (timestamp)                                                            |
| completedAt         | Long    | Completion time (timestamp)                                                                      |
| pausedAt            | Long    | Pause time (timestamp)                                                                           |
| error               | String  | Error message                                                                                    |
| stoppedAt           | Long    | Stop time (timestamp)                                                                            |
| completedSegments   | Integer | Number of completed segments                                                                     |
| totalSegments       | Integer | Total number of segments                                                                         |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDatasetIndexingStatus() {
    String datasetId = "dataset-123456789";

    // Get dataset indexing status
    DocumentIndexingStatusResponse indexingStatus = difyServer.getDatasetIndexingStatus(datasetId);

    if (indexingStatus.getData() != null && !indexingStatus.getData().isEmpty()) {
        for (DocumentIndexingStatusResponse.ProcessingStatus doc : indexingStatus.getData()) {
            System.out.println("Document ID: " + doc.getId());
            System.out.println("Indexing Status: " + doc.getIndexingStatus());
            System.out.println("Completed Segments: " + doc.getCompletedSegments() + "/" + doc.getTotalSegments());
        }
    }
}
```

### 2.5 Get Document Indexing Status

#### Method

```java
DocumentIndexingStatusResponse.ProcessingStatus getDocumentIndexingStatus(String datasetId, String documentId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description |
|----------------|--------|----------|-------------|
| datasetId      | String | Yes      | Dataset ID  |
| documentId     | String | Yes      | Document ID |

#### Response Parameters

ProcessingStatus (same structure as defined in section 2.4)

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDocumentIndexingStatus() {
    String datasetId = "dataset-123456789";
    String documentId = "doc-987654321";

    // Get document indexing status
    DocumentIndexingStatusResponse.ProcessingStatus documentStatus =
        difyServer.getDocumentIndexingStatus(datasetId, documentId);

    System.out.println("Document ID: " + documentStatus.getId());
    System.out.println("Indexing Status: " + documentStatus.getIndexingStatus());
    System.out.println("Completed Segments: " + documentStatus.getCompletedSegments() + "/" + documentStatus.getTotalSegments());

    if (documentStatus.getError() != null) {
        System.out.println("Error Message: " + documentStatus.getError());
    }
}
```

### 2.6 Get Dataset Error Documents

#### Method

```java
DatasetErrorDocumentsResponse getDatasetErrorDocuments(String datasetId);
```

#### Request Parameters

| Parameter name | Type   | Required | Description |
|----------------|--------|----------|-------------|
| datasetId      | String | Yes      | Dataset ID  |

#### Response Parameters

DatasetErrorDocumentsResponse

| Parameter name | Type                    | Description              |
|----------------|-------------------------|--------------------------|
| data           | `List<ErrorDocument>`   | Error document list      |
| total          | Integer                 | Total number of errors   |

ErrorDocument

| Parameter name  | Type   | Description                        |
|----------------|--------|------------------------------------|
| id             | String | Document ID                        |
| name           | String | Document name                      |
| error          | String | Error message                      |
| indexingStatus | String | Indexing status (usually "error")  |
| createdAt      | Long   | Creation time (timestamp)          |
| updatedAt      | Long   | Update time (timestamp)            |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDatasetErrorDocuments() {
    String datasetId = "dataset-123456789";

    // Get dataset error documents
    DatasetErrorDocumentsResponse errorDocuments = difyServer.getDatasetErrorDocuments(datasetId);

    System.out.println("Total Error Documents: " + errorDocuments.getTotal());

    if (errorDocuments.getData() != null && !errorDocuments.getData().isEmpty()) {
        for (DatasetErrorDocumentsResponse.ErrorDocument doc : errorDocuments.getData()) {
            System.out.println("Document ID: " + doc.getId());
            System.out.println("Document Name: " + doc.getName());
            System.out.println("Error Message: " + doc.getError());
        }
    } else {
        System.out.println("No error documents found");
    }
}
```

### 2.7 Retry Document Indexing

#### Method

```java
void retryDocumentIndexing(DocumentRetryRequest request);
```

#### Request Parameters

DocumentRetryRequest

| Parameter name | Type           | Required | Description                      |
|----------------|----------------|----------|----------------------------------|
| datasetId      | String         | Yes      | Dataset ID                       |
| documentIds    | `List<String>` | Yes      | List of document IDs to retry    |

#### Response Parameters

This method does not return a value. It returns 204 No Content on success.

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testRetryDocumentIndexing() {
    String datasetId = "dataset-123456789";

    // First, get error documents
    DatasetErrorDocumentsResponse errorDocuments = difyServer.getDatasetErrorDocuments(datasetId);

    if (errorDocuments.getData() != null && !errorDocuments.getData().isEmpty()) {
        // Extract error document IDs
        List<String> errorDocIds = errorDocuments.getData().stream()
            .map(DatasetErrorDocumentsResponse.ErrorDocument::getId)
            .collect(Collectors.toList());

        // Create retry request
        DocumentRetryRequest request = new DocumentRetryRequest();
        request.setDatasetId(datasetId);
        request.setDocumentIds(errorDocIds);

        // Retry document indexing
        difyServer.retryDocumentIndexing(request);
        System.out.println("Triggered indexing retry for " + errorDocIds.size() + " documents");
    } else {
        System.out.println("No error documents to retry");
    }
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

## 4. Application Statistics

### 4.1 Get Application Daily Conversation Statistics(except workflow)

#### Method

```java
List<DailyConversationsResponse> dailyConversations(String appId, LocalDateTime start, LocalDateTime end);
```

#### Request Parameters

| Parameter name | Type          | Required | Description                          |
|----------------|---------------|----------|--------------------------------------|
| appId          | String        | Yes      | Application ID                       |
| start          | LocalDateTime | Yes      | Start time, format: yyyy-MM-dd HH:mm |
| end            | LocalDateTime | Yes      | End time, format: yyyy-MM-dd HH:mm   |

#### Response Parameters

DailyConversationsResponse

| Parameter name    | Type    | Description                         |
|-------------------|---------|-------------------------------------|
| date              | String  | Date, format: yyyy-MM-dd            |
| conversationCount | Integer | Number of conversations on that day |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDailyConversations() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // Get daily conversation statistics
    List<DailyConversationsResponse> dailyStats = difyServer.dailyConversations(appId, start, end);

    if (dailyStats != null) {
        for (DailyConversationsResponse dailyStat : dailyStats) {
            System.out.println("Date: " + dailyStat.getDate());
            System.out.println("Conversation count: " + dailyStat.getConversationCount());
        }
    }
}
```

### 4.2 Get Application Daily Workflow Conversation Statistics

#### Method

```java
List<DailyWorkflowConversationsResponse> dailyWorkflowConversations(String appId, LocalDateTime start, LocalDateTime end);
```

#### Request Parameters

| Parameter name | Type          | Required | Description                          |
|----------------|---------------|----------|--------------------------------------|
| appId          | String        | Yes      | Application ID                       |
| start          | LocalDateTime | Yes      | Start time, format: yyyy-MM-dd HH:mm |
| end            | LocalDateTime | Yes      | End time, format: yyyy-MM-dd HH:mm   |

#### Response Parameters

DailyWorkflowConversationsResponse

| Parameter name | Type    | Description                         |
|----------------|---------|-------------------------------------|
| date           | String  | Date, format: yyyy-MM-dd            |
| runs           | Integer | Number of workflow runs on that day |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDailyWorkflowConversations() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // Get daily workflow conversation statistics
    List<DailyWorkflowConversationsResponse> dailyWorkflowStats = difyServer.dailyWorkflowConversations(appId, start, end);

    if (dailyWorkflowStats != null) {
        for (DailyWorkflowConversationsResponse dailyStat : dailyWorkflowStats) {
            System.out.println("Date: " + dailyStat.getDate());
            System.out.println("Workflow runs: " + dailyStat.getRuns());
        }
    }
}
```

### 4.3 Get Application Daily End Users Statistics

#### Method

```java
List<DailyEndUsersResponse> dailyEndUsers(String appId, LocalDateTime start, LocalDateTime end);
```

#### Request Parameters

| Parameter name | Type          | Required | Description                          |
|----------------|---------------|----------|--------------------------------------|
| appId          | String        | Yes      | Application ID                       |
| start          | LocalDateTime | Yes      | Start time, format: yyyy-MM-dd HH:mm |
| end            | LocalDateTime | Yes      | End time, format: yyyy-MM-dd HH:mm   |

#### Response Parameters

DailyEndUsersResponse

| Parameter name   | Type    | Description                       |
|------------------|---------|-----------------------------------|
| date             | String  | Date, format: yyyy-MM-dd          |
| terminalCount    | Integer | Number of end users on that day   |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDailyEndUsers() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // Get daily end users statistics
    List<DailyEndUsersResponse> dailyEndUsersStats = difyServer.dailyEndUsers(appId, start, end);

    if (dailyEndUsersStats != null) {
        for (DailyEndUsersResponse dailyStat : dailyEndUsersStats) {
            System.out.println("Date: " + dailyStat.getDate());
            System.out.println("End users count: " + dailyStat.getTerminalCount());
        }
    }
}
```

### 4.4 Get Application Average Session Interactions Statistics

#### Method

```java
List<AverageSessionInteractionsResponse> averageSessionInteractions(String appId, LocalDateTime start, LocalDateTime end);
```

#### Request Parameters

| Parameter name | Type          | Required | Description                          |
|----------------|---------------|----------|--------------------------------------|
| appId          | String        | Yes      | Application ID                       |
| start          | LocalDateTime | Yes      | Start time, format: yyyy-MM-dd HH:mm |
| end            | LocalDateTime | Yes      | End time, format: yyyy-MM-dd HH:mm   |

#### Response Parameters

AverageSessionInteractionsResponse

| Parameter name | Type   | Description                            |
|----------------|--------|----------------------------------------|
| date           | String | Date, format: yyyy-MM-dd               |
| interactions   | Double | Average number of interactions per session on that day |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetAverageSessionInteractions() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // Get average session interactions statistics
    List<AverageSessionInteractionsResponse> averageSessionInteractionsStats = difyServer.averageSessionInteractions(appId, start, end);

    if (averageSessionInteractionsStats != null) {
        for (AverageSessionInteractionsResponse dailyStat : averageSessionInteractionsStats) {
            System.out.println("Date: " + dailyStat.getDate());
            System.out.println("Average interactions per session: " + dailyStat.getInteractions());
        }
    }
}
```

### 4.5 Get Application Tokens Per Second Statistics

#### Method

```java
List<TokensPerSecondResponse> tokensPerSecond(String appId, LocalDateTime start, LocalDateTime end);
```

#### Request Parameters

| Parameter name | Type          | Required | Description                          |
|----------------|---------------|----------|--------------------------------------|
| appId          | String        | Yes      | Application ID                       |
| start          | LocalDateTime | Yes      | Start time, format: yyyy-MM-dd HH:mm |
| end            | LocalDateTime | Yes      | End time, format: yyyy-MM-dd HH:mm   |

#### Response Parameters

TokensPerSecondResponse

| Parameter name | Type   | Description                            |
|----------------|--------|----------------------------------------|
| date           | String | Date, format: yyyy-MM-dd               |
| tps            | Double | Tokens per second on that day          |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetTokensPerSecond() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // Get tokens per second statistics
    List<TokensPerSecondResponse> tokensPerSecondStats = difyServer.tokensPerSecond(appId, start, end);

    if (tokensPerSecondStats != null) {
        for (TokensPerSecondResponse dailyStat : tokensPerSecondStats) {
            System.out.println("Date: " + dailyStat.getDate());
            System.out.println("Tokens per second: " + dailyStat.getTps());
        }
    }
}
```

### 4.6 Get Application User Satisfaction Rate Statistics

#### Method

```java
List<UserSatisfactionRateResponse> userSatisfactionRate(String appId, LocalDateTime start, LocalDateTime end);
```

#### Request Parameters

| Parameter name | Type          | Required | Description                          |
|----------------|---------------|----------|--------------------------------------|
| appId          | String        | Yes      | Application ID                       |
| start          | LocalDateTime | Yes      | Start time, format: yyyy-MM-dd HH:mm |
| end            | LocalDateTime | Yes      | End time, format: yyyy-MM-dd HH:mm   |

#### Response Parameters

UserSatisfactionRateResponse

| Parameter name | Type   | Description                            |
|----------------|--------|----------------------------------------|
| date           | String | Date, format: yyyy-MM-dd               |
| rate           | Double | User satisfaction rate on that day     |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetUserSatisfactionRate() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // Get user satisfaction rate statistics
    List<UserSatisfactionRateResponse> userSatisfactionRateStats = difyServer.userSatisfactionRate(appId, start, end);

    if (userSatisfactionRateStats != null) {
        for (UserSatisfactionRateResponse dailyStat : userSatisfactionRateStats) {
            System.out.println("Date: " + dailyStat.getDate());
            System.out.println("User satisfaction rate: " + dailyStat.getRate());
        }
    }
}
```

### 4.7 Get Application Token Costs Statistics

#### Method

```java
List<TokenCostsResponse> tokenCosts(String appId, LocalDateTime start, LocalDateTime end);
```

#### Request Parameters

| Parameter name | Type          | Required | Description                          |
|----------------|---------------|----------|--------------------------------------|
| appId          | String        | Yes      | Application ID                       |
| start          | LocalDateTime | Yes      | Start time, format: yyyy-MM-dd HH:mm |
| end            | LocalDateTime | Yes      | End time, format: yyyy-MM-dd HH:mm   |

#### Response Parameters

TokenCostsResponse

| Parameter name | Type    | Description                            |
|----------------|---------|----------------------------------------|
| date           | String  | Date, format: yyyy-MM-dd               |
| token_count    | Integer | Number of tokens on that day           |
| total_price    | String  | Total price for that day               |
| currency       | String  | Currency type                          |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetTokenCosts() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // Get token costs statistics
    List<TokenCostsResponse> tokenCostsStats = difyServer.tokenCosts(appId, start, end);

    if (tokenCostsStats != null) {
        for (TokenCostsResponse dailyStat : tokenCostsStats) {
            System.out.println("Date: " + dailyStat.getDate());
            System.out.println("Token count: " + dailyStat.getTokenCount());
            System.out.println("Total price: " + dailyStat.getTotalPrice());
            System.out.println("Currency: " + dailyStat.getCurrency());
        }
    }
}
```

### 4.8 Get Application Daily Messages Statistics

#### Method

```java
List<DailyMessagesResponse> dailyMessages(String appId, LocalDateTime start, LocalDateTime end);
```

#### Request Parameters

| Parameter name | Type          | Required | Description                          |
|----------------|---------------|----------|--------------------------------------|
| appId          | String        | Yes      | Application ID                       |
| start          | LocalDateTime | Yes      | Start time, format: yyyy-MM-dd HH:mm |
| end            | LocalDateTime | Yes      | End time, format: yyyy-MM-dd HH:mm   |

#### Response Parameters

DailyMessagesResponse

| Parameter name | Type    | Description                            |
|----------------|---------|----------------------------------------|
| date           | String  | Date, format: yyyy-MM-dd               |
| message_count  | Integer | Number of messages on that day         |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testGetDailyMessages() {
    String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
    LocalDateTime start = LocalDateTime.of(2025, 10, 23, 0, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 30, 23, 59);

    // Get daily messages statistics
    List<DailyMessagesResponse> dailyMessagesStats = difyServer.dailyMessages(appId, start, end);

    if (dailyMessagesStats != null) {
        for (DailyMessagesResponse dailyStat : dailyMessagesStats) {
            System.out.println("Date: " + dailyStat.getDate());
            System.out.println("Message count: " + dailyStat.getMessageCount());
        }
    }
}
```

## 5. Member Management

### 5.1 Invite New Members (Create Users)

> Available since `2.3.0`
>
> Dify automatically creates an account for each invited email and returns an
> activation URL; forward the URL to the invitee to complete registration.

#### Method

```java
MemberInviteResponse inviteMembers(MemberInviteRequest request);
```

#### Request Parameters

MemberInviteRequest

| Parameter | Type           | Required | Description                                                                 |
|-----------|----------------|----------|-----------------------------------------------------------------------------|
| emails    | `List<String>` | Yes      | Email addresses to invite                                                   |
| role      | String         | Yes      | Member role. One of: admin, editor, normal, dataset_operator                |
| language  | String         | No       | Default language for the invitation email/account, e.g. `zh-Hans`, `en-US`  |

#### Response Parameters

MemberInviteResponse

| Parameter         | Type                     | Description                                       |
|-------------------|--------------------------|---------------------------------------------------|
| result            | String                   | Overall result, usually `success`                 |
| invitationResults | `List<InvitationResult>` | Per-email invitation result                        |

InvitationResult

| Parameter | Type   | Description                                       |
|-----------|--------|---------------------------------------------------|
| status    | String | `success` or `failed`                             |
| email     | String | Invited email                                     |
| url       | String | Activation URL (present when status is `success`) |
| message   | String | Failure message (present when status is `failed`) |

#### Request Example

```java

@Resource
private DifyServer difyServer;

@Test
public void testInviteMembers() {
    MemberInviteRequest request = new MemberInviteRequest();
    request.setEmails(List.of("alice@example.com", "bob@example.com"));
    request.setRole("normal");
    request.setLanguage("en-US");

    MemberInviteResponse response = difyServer.inviteMembers(request);

    for (MemberInviteResponse.InvitationResult item : response.getInvitationResults()) {
        System.out.println(item.getEmail() + " -> " + item.getStatus());
        if ("success".equals(item.getStatus())) {
            System.out.println("Activation URL: " + item.getUrl());
        }
    }
}
```
