---
lang: en-us
title: Dataset API
description: 
---

# Dataset API

## Interface Overview

The Knowledge Base service interface provides comprehensive knowledge base management integration capabilities,
including core functions such as knowledge base creation, document management, segment management, and data retrieval.
All interfaces require valid API keys for authentication.
Use the `DifyDataset` interface instance.
> All of the following interfaces include apikey. The apikey field is optional and defaults to the apikey in the
> configuration file when empty.

## 1. Knowledge Base Management

### 1.1 Create Knowledge Base

#### Method

```java
DatasetResponse create(DatasetCreateRequest request);
```

#### Request Parameters

DatasetCreateRequest

| Parameter         | Type                  | Required | Description                |
|-------------------|-----------------------|----------|----------------------------|
| name              | String                | Yes      | Knowledge base name        |
| description       | String                | No       | Knowledge base description |
| permission        | PermissionEnum        | No       | Permission settings        |
| provider          | ProviderEnum          | No       | Provider                   |
| indexingTechnique | IndexingTechniqueEnum | No       | Indexing technique         |

**ProviderEnum Values**

| Value    | Description             |
|----------|-------------------------|
| vendor   | Upload file             |
| external | External knowledge base |

**PermissionEnum Values**

| Enum Value       | Code Value       | Description          |
|------------------|------------------|----------------------|
| ONLY_ME          | only_me          | Only self            |
| ALL_TEAM_MEMBERS | all_team_members | All team members     |
| PARTIAL_MEMBERS  | partial_members  | Partial team members |

**IndexingTechniqueEnum Values**

| Enum Value   | Code Value   | Description  |
|--------------|--------------|--------------|
| HIGH_QUALITY | high_quality | High quality |
| ECONOMY      | economy      | Economy      |

#### Response Parameters

DatasetResponse

| Parameter         | Type                  | Description                |
|-------------------|-----------------------|----------------------------|
| id                | String                | Knowledge base ID          |
| name              | String                | Knowledge base name        |
| description       | String                | Knowledge base description |
| permission        | PermissionEnum        | Permission                 |
| dataSourceType    | String                | Data source type           |
| indexingTechnique | IndexingTechniqueEnum | Indexing technique         |
| appCount          | Integer               | Application count          |
| documentCount     | Integer               | Document count             |
| wordCount         | Integer               | Word count                 |
| createdBy         | String                | Created by                 |
| createdAt         | Long                  | Creation timestamp         |
| updatedBy         | String                | Updated by                 |
| updatedAt         | Long                  | Update timestamp           |

### 1.2 Paginated Query of Knowledge Base List

#### Method

```java
DifyPageResult<DatasetResponse> page(DatasetPageRequest request);
```

#### Request Parameters

DatasetPageRequest

| Parameter | Type    | Required | Description                  |
|-----------|---------|----------|------------------------------|
| page      | Integer | No       | Page number, default 1       |
| limit     | Integer | No       | Records per page, default 20 |

#### Response Parameters

`DifyPageResult<DatasetResponse>`

| Parameter | Type                    | Description         |
|-----------|-------------------------|---------------------|
| list      | `List<DatasetResponse>` | Knowledge base list |
| total     | Long                    | Total records       |
| page      | Integer                 | Current page        |
| limit     | Integer                 | Records per page    |
| pages     | Integer                 | Total pages         |

DatasetResponse See 1.1

### 1.3 Knowledge Base Details

#### Method

```java
DatasetInfoResponse info(DatasetInfoRequest request);
```

#### Request Parameters

DatasetInfoRequest

| Parameter | Type   | Required | Description       |
|-----------|--------|----------|-------------------|
| datasetId | String | Yes      | Knowledge base ID |

#### Response Parameters

DatasetInfoResponse

| Parameter              | Type                  | Description                    |
|------------------------|-----------------------|--------------------------------|
| id                     | String                | Knowledge base ID              |
| name                   | String                | Knowledge base name            |
| description            | String                | Knowledge base description     |
| permission             | PermissionEnum        | Permission                     |
| dataSourceType         | String                | Data source type               |
| indexingTechnique      | IndexingTechniqueEnum | Indexing technique             |
| appCount               | Integer               | Application count              |
| documentCount          | Integer               | Document count                 |
| wordCount              | Integer               | Word count                     |
| createdBy              | String                | Created by                     |
| createdAt              | Long                  | Creation timestamp             |
| updatedBy              | String                | Updated by                     |
| updatedAt              | Long                  | Update timestamp               |
| embeddingModel         | String                | Embedding model                |
| embeddingModelProvider | String                | Embedding model provider       |
| embeddingAvailable     | Boolean               | Whether embedding is available |
| retrievalModelDict     | RetrievalModel        | Retrieval model dictionary     |
| tags                   | `List<String>`        | Tag list                       |
| docForm                | DocFormEnum           | Document form                  |
| externalKnowledgeInfo  | ExternalKnowledgeInfo | External knowledge info        |

**ExternalKnowledgeInfo Object Structure**

| Field                        | Type   | Description                          |
|------------------------------|--------|--------------------------------------|
| externalKnowledgeId          | String | External knowledge base ID           |
| externalKnowledgeApiId       | String | External knowledge base API_ID       |
| externalKnowledgeApiName     | String | External knowledge base API name     |
| externalKnowledgeApiEndpoint | String | External knowledge base API endpoint |

### 1.4 Update Knowledge Base

#### Method

```java
DatasetInfoResponse update(DatasetUpdateRequest request);
```

#### Request Parameters

DatasetUpdateRequest

| Parameter              | Type                  | Required | Description              |
|------------------------|-----------------------|----------|--------------------------|
| datasetId              | String                | Yes      | Knowledge base ID        |
| indexingTechnique      | IndexingTechniqueEnum | No       | Indexing mode            |
| permission             | PermissionEnum        | No       | Permission               |
| retrievalModel         | RetrievalModel        | No       | Retrieval model          |
| embeddingModel         | String                | No       | Embedding model          |
| embeddingModelProvider | String                | No       | Embedding model provider |
| partialMemberList      | `List<String>`        | No       | Partial member list      |

#### Response Parameters

DatasetInfoResponse See 1.3

### 1.5 Delete Knowledge Base

#### Method

```java
void delete(String datasetId);

        void delete(String datasetId,String apiKey);
```

#### Request Parameters

| Parameter | Type   | Required | Description       |
|-----------|--------|----------|-------------------|
| datasetId | String | Yes      | Knowledge base ID |

#### Response Parameters

None

## 2. Document Management

### 2.1 Create Document by Text

#### Method

```java
DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request);
```

#### Request Parameters

DocumentCreateByTextRequest

| Parameter              | Type                   | Required | Description                                               |
|------------------------|------------------------|----------|-----------------------------------------------------------|
| datasetId              | String                 | Yes      | Knowledge base ID                                         |
| name                   | String                 | Yes      | Document name                                             |
| text                   | String                 | Yes      | Document content                                          |
| docType                | DocTypeEnum            | No       | Document type                                             |
| docMetadata            | `Map<String, Object> ` | No       | Document metadata (required if document type is provided) |
| indexingTechnique      | IndexingTechniqueEnum  | No       | Indexing mode                                             |
| docForm                | DocFormEnum            | No       | Document form                                             |
| docLanguage            | String                 | No       | Document language                                         |
| processRule            | ProcessRule            | No       | Processing rules                                          |
| retrievalModel         | RetrievalModel         | No       | Retrieval model                                           |
| embeddingModel         | String                 | No       | Embedding model                                           |
| embeddingModelProvider | String                 | No       | Embedding model provider                                  |

**DocTypeEnum Values**

| Value              | Description                |
|--------------------|----------------------------|
| book               | Book                       |
| web_page           | Web page                   |
| paper              | Academic paper/article     |
| social_media_post  | Social media post          |
| wikipedia_entry    | Wikipedia entry            |
| personal_document  | Personal document          |
| business_document  | Business document          |
| im_chat_log        | Instant messaging chat log |
| synced_from_notion | Notion synced document     |
| synced_from_github | GitHub synced document     |
| others             | Other document types       |

**DocFormEnum Values**

| Value              | Description                                                                  |
|--------------------|------------------------------------------------------------------------------|
| text_model         | Direct document embedding, economy mode defaults to this mode                |
| hierarchical_model | Parent-child mode                                                            |
| qa_model           | Q&A mode: Generate Q&A pairs for document segments, then embed the questions |

**ProcessRule Object Structure**

| Field | Type       | Description     |
|-------|------------|-----------------|
| mode  | ModeEnum   | Processing mode |
| rules | CustomRule | Custom rules    |

**ModeEnum Values**

| Value        | Description       |
|--------------|-------------------|
| automatic    | Automatic         |
| hierarchical | Parent-child mode |
| custom       | Custom            | 

**CustomRule Object Structure**

| Field                | Type                      | Description                   |
|----------------------|---------------------------|-------------------------------|
| preProcessingRules   | `List<PreProcessingRule>` | Pre-processing rules list     |
| segmentation         | Segmentation              | Segmentation rules            |
| parentMode           | ParentModeEnum            | Parent segment retrieval mode |
| subChunkSegmentation | SubChunkSegmentation      | Sub-chunk segmentation rules  |

**PreProcessingRule Object Structure**

| Field   | Type                      | Description                                                                             |
|---------|---------------------------|-----------------------------------------------------------------------------------------|
| id      | PreProcessingRuleTypeEnum | Pre-processing rule unique identifier, such as remove_extra_spaces, remove_urls_emails  |
| enabled | Boolean                   | Whether the rule is selected, represents default value when document ID is not provided |

**Segmentation Object Structure**

| Field     | Type    | Description                                                                       |
|-----------|---------|-----------------------------------------------------------------------------------|
| separator | String  | Custom segment identifier, currently only one separator is allowed. Default is \n |
| maxTokens | Integer | Maximum length (token), default is 1000                                           |

**SubChunkSegmentation Object Structure**

| Field        | Type    | Description                        |
|--------------|---------|------------------------------------|
| separator    | String  | Custom sub-segment identifier      |
| maxTokens    | Integer | Sub-segment maximum length (token) |
| chunkOverlap | Integer | Sub-segment overlap length         |

**ParentModeEnum Values**

| Value     | Description             |
|-----------|-------------------------|
| FULL_DOC  | Full document retrieval |
| PARAGRAPH | Paragraph retrieval     |

**RetrievalModel Object Structure**

| Field                 | Type                 | Description                       |
|-----------------------|----------------------|-----------------------------------|
| searchMethod          | SearchMethodEnum     | Search method                     |
| rerankingMode         | RerankingModeEnum    | Reranking mode                    |
| rerankingEnable       | Boolean              | Whether to enable reranking       |
| rerankingModel        | RerankingModel       | Reranking model                   |
| weights               | RerankingModelWeight | Weights                           |
| topK                  | Integer              | Number of results to return       |
| scoreThresholdEnabled | Boolean              | Whether to enable score threshold |
| scoreThreshold        | Float                | Score threshold                   |

**SearchMethodEnum Values**

| Value            | Description      |
|------------------|------------------|
| keyword_search   | Keyword search   |
| hybrid_search    | Hybrid search    |
| semantic_search  | Semantic search  |
| full_text_search | Full text search |

**RerankingModeEnum Values**

| Value           | Description     |
|-----------------|-----------------|
| weighted_score  | Weighted score  |
| reranking_model | Reranking model |

**RerankingModel Object Structure**

| Field                 | Type   | Description             |
|-----------------------|--------|-------------------------|
| rerankingProviderName | String | Reranking provider name |
| rerankingModelName    | String | Reranking model name    |

**RerankingModelWeight Object Structure**

| Field          | Type           | Description           |
|----------------|----------------|-----------------------|
| weightType     | String         | default is customized |
| vectorSetting  | VectorSetting  | Vector weight         |
| keywordSetting | KeywordSetting | keyword weight        |

**VectorSetting Object Structure**

| Field                 | Type   | Description              |
|-----------------------|--------|--------------------------|
| vectorWeight          | Float  | Vector weight            |
| embeddingModelName    | String | Embedding model          |
| embeddingProviderName | String | Embedding model provider |

**KeywordSetting Object Structure**

| Field         | Type  | Description    |
|---------------|-------|----------------|
| keywordWeight | Float | Keyword weight |

#### Response Parameters

DocumentCreateResponse

| Parameter | Type         | Description          |
|-----------|--------------|----------------------|
| document  | DocumentInfo | Document information |
| batch     | String       | Batch number         |

**DocumentInfo Object Structure**

| Parameter            | Type                  | Description                       |
|----------------------|-----------------------|-----------------------------------|
| id                   | String                | Document ID                       |
| position             | Integer               | Position                          |
| dataSourceType       | String                | Data source type                  |
| dataSourceInfo       | `Map<String, Object>` | Data source information           |
| datasetProcessRuleId | String                | Knowledge base processing rule ID |
| name                 | String                | Document name                     |
| createdFrom          | String                | Creation source                   |
| createdBy            | String                | Created by                        |
| createdAt            | Long                  | Creation timestamp                |
| tokens               | Integer               | Token count                       |
| indexingStatus       | String                | Indexing status                   |
| error                | String                | Error message                     |
| enabled              | String                | Whether enabled                   |
| disabledAt           | Long                  | Disabled timestamp                |
| disabledBy           | String                | Disabled by                       |
| archived             | String                | Whether archived                  |
| displayStatus        | String                | Display status                    |
| wordCount            | String                | Word count                        |
| hitCount             | String                | Hit count                         |
| docForm              | String                | Document form                     |

### 2.2 Create Document by File

#### Method

```java
DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request);
```

#### Request Parameters

DocumentCreateByFileRequest

| Parameter              | Type                  | Required | Description                                               |
|------------------------|-----------------------|----------|-----------------------------------------------------------|
| datasetId              | String                | Yes      | Knowledge base ID                                         |
| name                   | String                | Yes      | Document name                                             |
| file                   | MultipartFile         | Yes      | Document file                                             |
| docType                | DocTypeEnum           | No       | Document type                                             |
| docMetadata            | `Map<String, Object>` | No       | Document metadata (required if document type is provided) |
| indexingTechnique      | IndexingTechniqueEnum | No       | Indexing mode                                             |
| docForm                | DocFormEnum           | No       | Document form                                             |
| docLanguage            | String                | No       | Document language                                         |
| processRule            | ProcessRule           | No       | Processing rules                                          |
| retrievalModel         | RetrievalModel        | No       | Retrieval model                                           |
| embeddingModel         | String                | No       | Embedding model                                           |
| embeddingModelProvider | String                | No       | Embedding model provider                                  |

#### Response Parameters

DocumentCreateResponse

| Parameter | Type         | Description          |
|-----------|--------------|----------------------|
| document  | DocumentInfo | Document information |
| batch     | String       | Batch number         |

### 2.3 Update Document by Text

#### Method

```java
DocumentCreateResponse updateDocumentByText(DocumentUpdateByTextRequest request);
```

#### Request Parameters

DocumentUpdateByTextRequest

| Parameter         | Type                  | Required | Description                                               |
|-------------------|-----------------------|----------|-----------------------------------------------------------|
| datasetId         | String                | Yes      | Knowledge base ID                                         |
| documentId        | String                | Yes      | Document ID                                               |
| name              | String                | No       | Document name                                             |
| text              | String                | Yes      | Document content                                          |
| docType           | DocTypeEnum           | No       | Document type                                             |
| docMetadata       | `Map<String, Object>` | No       | Document metadata (required if document type is provided) |
| indexingTechnique | IndexingTechniqueEnum | No       | Indexing mode                                             |
| docForm           | DocFormEnum           | No       | Document form                                             |
| docLanguage       | String                | No       | Document language                                         |
| processRule       | ProcessRule           | No       | Processing rules                                          |

#### Response Parameters

DocumentCreateResponse

| Parameter | Type         | Description          |
|-----------|--------------|----------------------|
| document  | DocumentInfo | Document information |
| batch     | String       | Batch number         |

### 2.4 Update Document by File

#### Method

```java
DocumentCreateResponse updateDocumentByFile(DocumentUpdateByFileRequest request);
```

#### Request Parameters

DocumentUpdateByFileRequest

| Parameter         | Type                   | Required | Description                                               |
|-------------------|------------------------|----------|-----------------------------------------------------------|
| datasetId         | String                 | Yes      | Knowledge base ID                                         |
| documentId        | String                 | Yes      | Document ID                                               |
| name              | String                 | No       | Document name                                             |
| file              | MultipartFile          | Yes      | Document file                                             |
| docType           | DocTypeEnum            | No       | Document type                                             |
| docMetadata       | `Map<String, Object> ` | No       | Document metadata (required if document type is provided) |
| indexingTechnique | IndexingTechniqueEnum  | No       | Indexing mode                                             |
| docForm           | DocFormEnum            | No       | Document form                                             |
| docLanguage       | String                 | No       | Document language                                         |
| processRule       | ProcessRule            | No       | Processing rules                                          |

#### Response Parameters

DocumentCreateResponse

| Parameter | Type         | Description          |
|-----------|--------------|----------------------|
| document  | DocumentInfo | Document information |
| batch     | String       | Batch number         |

### 2.5 Paginated Query of Document List

#### Method

```java
DifyPageResult<DocumentInfo> pageDocument(DatasetPageDocumentRequest request);
```

#### Request Parameters

DatasetPageDocumentRequest

| Parameter | Type    | Required | Description                  |
|-----------|---------|----------|------------------------------|
| datasetId | String  | Yes      | Knowledge base ID            |
| keyword   | String  | No       | Search keyword               |
| page      | Integer | No       | Page number, default 1       |
| limit     | Integer | No       | Records per page, default 20 |

#### Response Parameters

`DifyPageResult<DocumentInfo>`

| Parameter | Type                  | Description      |
|-----------|-----------------------|------------------|
| list      | ` List<DocumentInfo>` | Document list    |
| total     | Long                  | Total records    |
| page      | Integer               | Current page     |
| limit     | Integer               | Records per page |
| pages     | Integer               | Total pages      |

DocumentInfo

| Parameter            | Type                  | Description                       |
|----------------------|-----------------------|-----------------------------------|
| id                   | String                | Document ID                       |
| position             | Integer               | Position                          |
| dataSourceType       | String                | Data source type                  |
| dataSourceInfo       | `Map<String, Object>` | Data source information           |
| datasetProcessRuleId | String                | Knowledge base processing rule ID |
| name                 | String                | Document name                     |
| createdFrom          | String                | Creation source                   |
| createdBy            | String                | Created by                        |
| createdAt            | Long                  | Creation timestamp                |
| tokens               | Integer               | Token count                       |
| indexingStatus       | String                | Indexing status                   |
| error                | String                | Error message                     |
| enabled              | String                | Whether enabled                   |
| disabledAt           | Long                  | Disabled timestamp                |
| disabledBy           | String                | Disabled by                       |
| archived             | String                | Whether archived                  |
| displayStatus        | String                | Display status                    |
| wordCount            | String                | Word count                        |
| hitCount             | String                | Hit count                         |
| docForm              | String                | Document form                     |

### 2.6 Query Document Indexing Status

#### Method

```java
DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request);
```

#### Request Parameters

DocumentIndexingStatusRequest

| Parameter | Type   | Required | Description       |
|-----------|--------|----------|-------------------|
| datasetId | String | Yes      | Knowledge base ID |
| batch     | String | Yes      | Batch number      |

#### Response Parameters

DocumentIndexingStatusResponse

| Parameter | Type                     | Description            |
|-----------|--------------------------|------------------------|
| data      | `List<ProcessingStatus>` | Processing status list |

**ProcessingStatus Object Structure**

| Parameter            | Type    | Description                     |
|----------------------|---------|---------------------------------|
| id                   | String  | Document ID                     |
| indexingStatus       | String  | Indexing status                 |
| processingStartedAt  | Long    | Processing start timestamp      |
| parsingCompletedAt   | Long    | Parsing completion timestamp    |
| cleaningCompletedAt  | Long    | Cleaning completion timestamp   |
| splittingCompletedAt | Long    | Splitting completion timestamp  |
| completedAt          | Long    | Processing completion timestamp |
| pausedAt             | Long    | Pause timestamp                 |
| error                | String  | Error message                   |
| stoppedAt            | Long    | Stop timestamp                  |
| completedSegments    | Integer | Completed segments count        |
| totalSegments        | Integer | Total segments count            |

### 2.7 Delete Document

#### Method

```java
void deleteDocument(String datasetId,String documentId);

        void deleteDocument(String datasetId,String documentId,String apiKey);
```

#### Request Parameters

| Parameter  | Type   | Required | Description       |
|------------|--------|----------|-------------------|
| datasetId  | String | Yes      | Knowledge base ID |
| documentId | String | Yes      | Document ID       |

#### Response Parameters

None

### 2.8 Get Upload File Information

#### Method

```java
UploadFileInfoResponse uploadFileInfo(String datasetId,String documentId);

        UploadFileInfoResponse uploadFileInfo(String datasetId,String documentId,String apiKey);
```

#### Request Parameters

| Parameter  | Type   | Required | Description       |
|------------|--------|----------|-------------------|
| datasetId  | String | Yes      | Knowledge base ID |
| documentId | String | Yes      | Document ID       |

#### Response Parameters

UploadFileInfoResponse

| Parameter   | Type    | Description        |
|-------------|---------|--------------------|
| id          | String  | File ID            |
| name        | String  | File name          |
| size        | Integer | File size          |
| extension   | String  | File extension     |
| url         | String  | File access URL    |
| downloadUrl | String  | File download URL  |
| mimeType    | String  | MIME type          |
| createdBy   | String  | Created by         |
| createdAt   | Long    | Creation timestamp |

## 3. Segment Management

### 3.1 Create Segment

#### Method

```java
SegmentResponse createSegment(SegmentCreateRequest request);
```

#### Request Parameters

SegmentCreateRequest

| Parameter  | Type                 | Required | Description            |
|------------|----------------------|----------|------------------------|
| datasetId  | String               | Yes      | Knowledge base ID      |
| documentId | String               | Yes      | Document ID            |
| segments   | `List<SegmentParam>` | Yes      | Segment parameter list |

#### Response Parameters

SegmentResponse

| Parameter | Type                | Description       |
|-----------|---------------------|-------------------|
| data      | `List<SegmentData>` | Segment data list |
| docForm   | String              | Document form     |

**SegmentData Object Structure**

| Parameter      | Type    | Description        |
|----------------|---------|--------------------|
| id             | String  | Segment ID         |
| position       | Integer | Position           |
| content        | String  | Segment content    |
| answer         | String  | Answer             |
| wordCount      | Integer | Word count         |
| tokens         | Integer | Token count        |
| indexingStatus | String  | Indexing status    |
| error          | String  | Error message      |
| enabled        | String  | Whether enabled    |
| disabledAt     | Long    | Disabled timestamp |
| disabledBy     | String  | Disabled by        |
| archived       | Boolean | Whether archived   |

**SegmentParam Object Structure**

| Parameter | Type           | Description     |
|-----------|----------------|-----------------|
| content   | String         | Segment content |
| answer    | String         | Segment answer  |
| keywords  | `List<String>` | Keyword list    |

### 3.2 Paginated Query of Segment List

#### Method

```java
SegmentResponse pageSegment(SegmentPageRequest request);
```

#### Request Parameters

SegmentPageRequest

| Parameter  | Type   | Required | Description       |
|------------|--------|----------|-------------------|
| datasetId  | String | Yes      | Knowledge base ID |
| documentId | String | Yes      | Document ID       |
| keyword    | String | No       | Search keyword    |
| status     | String | No       | Status filter     |

#### Response Parameters

SegmentResponse

| Parameter | Type                | Description       |
|-----------|---------------------|-------------------|
| data      | `List<SegmentData>` | Segment data list |
| docForm   | String              | Document form     |

**SegmentData Object Structure**

| Parameter      | Type    | Description        |
|----------------|---------|--------------------|
| id             | String  | Segment ID         |
| position       | Integer | Position           |
| content        | String  | Segment content    |
| answer         | String  | Answer             |
| wordCount      | Integer | Word count         |
| tokens         | Integer | Token count        |
| indexingStatus | String  | Indexing status    |
| error          | String  | Error message      |
| enabled        | String  | Whether enabled    |
| disabledAt     | Long    | Disabled timestamp |
| disabledBy     | String  | Disabled by        |
| archived       | Boolean | Whether archived   |

### 3.3 Delete Segment

#### Method

```java
void deleteSegment(String datasetId,String documentId,String segmentId);

        void deleteSegment(String datasetId,String documentId,String segmentId,String apiKey);
```

#### Request Parameters

| Parameter  | Type   | Required | Description       |
|------------|--------|----------|-------------------|
| datasetId  | String | Yes      | Knowledge base ID |
| documentId | String | Yes      | Document ID       |
| segmentId  | String | Yes      | Segment ID        |

#### Response Parameters

None

### 3.4 Update Segment

#### Method

```java
SegmentUpdateResponse updateSegment(SegmentUpdateRequest request);
```

#### Request Parameters

SegmentUpdateRequest

| Parameter  | Type         | Required | Description       |
|------------|--------------|----------|-------------------|
| datasetId  | String       | Yes      | Knowledge base ID |
| documentId | String       | Yes      | Document ID       |
| segmentId  | String       | Yes      | Segment ID        |
| segment    | SegmentParam | Yes      | Segment parameter |

#### Response Parameters

SegmentUpdateResponse

| Parameter | Type        | Description   |
|-----------|-------------|---------------|
| data      | SegmentData | Segment data  |
| docForm   | String      | Document form |

**SegmentData Object Structure**

| Parameter      | Type    | Description        |
|----------------|---------|--------------------|
| id             | String  | Segment ID         |
| position       | Integer | Position           |
| content        | String  | Segment content    |
| answer         | String  | Answer             |
| wordCount      | Integer | Word count         |
| tokens         | Integer | Token count        |
| indexingStatus | String  | Indexing status    |
| error          | String  | Error message      |
| enabled        | String  | Whether enabled    |
| disabledAt     | Long    | Disabled timestamp |
| disabledBy     | String  | Disabled by        |
| archived       | Boolean | Whether archived   |

**SegmentParam Object Structure**

| Parameter | Type           | Description     |
|-----------|----------------|-----------------|
| content   | String         | Segment content |
| answer    | String         | Segment answer  |
| keywords  | `List<String>` | Keyword list    |

## 4 Child Chunk Management

Child Chunks (Sub-segments) are the next level of granularity below segments, used for more fine-grained content
organization and retrieval.

### 4.1 Create Child Chunk

#### Method

```java
SegmentChildChunkCreateResponse createSegmentChildChunk(SegmentChildChunkCreateRequest request);
```

#### Request Parameters

SegmentChildChunkCreateRequest

| Parameter  | Type   | Required | Description         |
|------------|--------|----------|---------------------|
| datasetId  | String | Yes      | Knowledge base ID   |
| documentId | String | Yes      | Document ID         |
| segmentId  | String | Yes      | Segment ID          |
| content    | String | Yes      | Child chunk content |

#### Response Parameters

SegmentChildChunkCreateResponse

| Parameter | Type                      | Description      |
|-----------|---------------------------|------------------|
| data      | SegmentChildChunkResponse | Child chunk data |

**SegmentChildChunkResponse Object Structure**

| Parameter     | Type    | Description          |
|---------------|---------|----------------------|
| id            | String  | Child chunk ID       |
| segmentId     | String  | Parent segment ID    |
| content       | String  | Child chunk content  |
| wordCount     | Integer | Word count           |
| tokens        | Integer | Token count          |
| indexNodeId   | String  | Index node ID        |
| indexNodeHash | String  | Index node hash      |
| status        | String  | Status               |
| createdBy     | String  | Created by           |
| createdAt     | Long    | Creation timestamp   |
| indexingAt    | Long    | Indexing timestamp   |
| completedAt   | Long    | Completion timestamp |
| error         | String  | Error message        |
| stoppedAt     | Long    | Stop timestamp       |

### 4.2 Paginated Query of Child Chunk List

#### Method

```java
DifyPageResult<SegmentChildChunkResponse> pageSegmentChildChunk(SegmentChildChunkPageRequest request);
```

#### Request Parameters

SegmentChildChunkPageRequest

| Parameter  | Type    | Required | Description       |
|------------|---------|----------|-------------------|
| datasetId  | String  | Yes      | Knowledge base ID |
| documentId | String  | Yes      | Document ID       |
| segmentId  | String  | Yes      | Segment ID        |
| keyword    | String  | No       | Search keyword    |
| page       | Integer | No       | Page number       |
| limit      | Integer | No       | Records per page  |

#### Response Parameters

`DifyPageResult<SegmentChildChunkResponse>`

| Parameter | Type                              | Description      |
|-----------|-----------------------------------|------------------|
| list      | `List<SegmentChildChunkResponse>` | Child chunk list |
| total     | Long                              | Total records    |
| page      | Integer                           | Current page     |
| limit     | Integer                           | Records per page |
| pages     | Integer                           | Total pages      |

### 4.3 Update Child Chunk

#### Method

```java
SegmentChildChunkUpdateResponse updateSegmentChildChunk(SegmentChildChunkUpdateRequest request);
```

#### Request Parameters

SegmentChildChunkUpdateRequest

| Parameter    | Type   | Required | Description         |
|--------------|--------|----------|---------------------|
| datasetId    | String | Yes      | Knowledge base ID   |
| documentId   | String | Yes      | Document ID         |
| segmentId    | String | Yes      | Segment ID          |
| childChunkId | String | Yes      | Child chunk ID      |
| content      | String | Yes      | Child chunk content |

#### Response Parameters

SegmentChildChunkUpdateResponse

| Parameter | Type                      | Description      |
|-----------|---------------------------|------------------|
| data      | SegmentChildChunkResponse | Child chunk data |

### 4.4 Delete Child Chunk

#### Method

```java
void deleteSegmentChildChunk(SegmentChildChunkDeleteRequest request);
```

#### Request Parameters

SegmentChildChunkDeleteRequest

| Parameter    | Type   | Required | Description       |
|--------------|--------|----------|-------------------|
| datasetId    | String | Yes      | Knowledge base ID |
| documentId   | String | Yes      | Document ID       |
| segmentId    | String | Yes      | Segment ID        |
| childChunkId | String | Yes      | Child chunk ID    |

#### Response Parameters

None

## 5. Data Retrieval

### 5.1 Retrieve Data

#### Method

```java
RetrieveResponse retrieve(RetrieveRequest request);
```

#### Request Parameters

RetrieveRequest

| Parameter      | Type           | Required | Description             |
|----------------|----------------|----------|-------------------------|
| datasetId      | String         | Yes      | Knowledge base ID       |
| query          | String         | Yes      | Retrieval query content |
| retrievalModel | RetrievalModel | No       | Retrieval model         |

#### Response Parameters

RetrieveResponse

| Parameter | Type                   | Description           |
|-----------|------------------------|-----------------------|
| query     | RetrieveQuery          | Query information     |
| records   | `List<RetrieveRecord>` | Retrieval record list |

**RetrieveQuery Object Structure**

| Parameter | Type   | Description   |
|-----------|--------|---------------|
| content   | String | Query content |

**RetrieveRecord Object Structure**

| Parameter    | Type         | Description               |
|--------------|--------------|---------------------------|
| segment      | Segment      | Segment information       |
| score        | Float        | Relevance score           |
| tsnePosition | TsnePosition | TSNE position information |

**TsnePosition Object Structure**

| Parameter | Type  | Description  |
|-----------|-------|--------------|
| x         | Float | X coordinate |
| y         | Float | Y coordinate |

**Segment Object Structure**

| Parameter     | Type            | Description          |
|---------------|-----------------|----------------------|
| id            | String          | Segment ID           |
| position      | Integer         | Position             |
| documentId    | String          | Document ID          |
| content       | String          | Segment content      |
| answer        | String          | Answer               |
| wordCount     | Integer         | Word count           |
| tokens        | Integer         | Token count          |
| keywords      | ` List<String>` | Keyword list         |
| indexNodeId   | String          | Index node ID        |
| indexNodeHash | String          | Index node hash      |
| hitCount      | Integer         | Hit count            |
| enabled       | String          | Whether enabled      |
| disabledAt    | Long            | Disabled timestamp   |
| disabledBy    | String          | Disabled by          |
| status        | String          | Status               |
| createdAt     | Long            | Creation timestamp   |
| createdBy     | String          | Created by           |
| indexingAt    | Long            | Indexing timestamp   |
| completedAt   | Long            | Completion timestamp |
| error         | String          | Error message        |
| stoppedAt     | Long            | Stop timestamp       |
| document      | Document        | Document information |

**Document Object Structure**

| Parameter      | Type   | Description      |
|----------------|--------|------------------|
| id             | String | Document ID      |
| dataSourceType | String | Data source type |
| name           | String | Document name    |
| docType        | String | Document type    |

## 6. Metadata Management

### 6.1 Create Metadata

#### Method

```java
MetaDataResponse createMetaData(MetaDataCreateRequest request);
```

#### Request Parameters

MetaDataCreateRequest

| Parameter | Type   | Required | Description       |
|-----------|--------|----------|-------------------|
| datasetId | String | Yes      | Knowledge base ID |
| type      | String | Yes      | Metadata type     |
| name      | String | Yes      | Metadata name     |

#### Response Parameters

MetaDataResponse

| Parameter | Type   | Description   |
|-----------|--------|---------------|
| id        | String | Metadata ID   |
| type      | String | Metadata type |
| name      | String | Metadata name |

### 6.2 Update Metadata

#### Method

```java
MetaDataResponse updateMetaData(MetaDataUpdateRequest request);
```

#### Request Parameters

MetaDataUpdateRequest

| Parameter  | Type   | Required | Description       |
|------------|--------|----------|-------------------|
| datasetId  | String | Yes      | Knowledge base ID |
| metaDataId | String | Yes      | Metadata ID       |
| name       | String | Yes      | Metadata name     |

#### Response Parameters

MetaDataResponse See 6.1

### 6.3 Delete Metadata

#### Method

```java
void deleteMetaData(String datasetId,String metadataId);

        void deleteMetaData(String datasetId,String metadataId,String apiKey);
```

#### Request Parameters

| Parameter  | Type   | Required | Description       |
|------------|--------|----------|-------------------|
| datasetId  | String | Yes      | Knowledge base ID |
| metadataId | String | Yes      | Metadata ID       |

#### Response Parameters

None

### 6.4 Metadata Operations

#### Method

```java
void actionMetaData(MetaDataActionRequest request);
```

#### Request Parameters

MetaDataActionRequest

| Parameter | Type               | Required | Description       |
|-----------|--------------------|----------|-------------------|
| datasetId | String             | Yes      | Knowledge base ID |
| action    | MetaDataActionEnum | Yes      | Operation type    |

**MetaDataActionEnum Values**

| Value   | Description |
|---------|-------------|
| enable  | Enable      |
| disable | Disable     |

#### Response Parameters

None

### 6.5 Update Document Metadata

#### Method

```java
void updateDocumentMetaData(DocumentMetaDataUpdateRequest request);
```

#### Request Parameters

DocumentMetaDataUpdateRequest

| Parameter     | Type                  | Required | Description         |
|---------------|-----------------------|----------|---------------------|
| datasetId     | String                | Yes      | Knowledge base ID   |
| operationData | `List<OperationData>` | Yes      | Operation data list |

**OperationData Object Structure**

| Parameter    | Type             | Description   |
|--------------|------------------|---------------|
| documentId   | String           | Document ID   |
| metadataList | `List<MetaData>` | Metadata list |

**MetaData Object Structure**

| Parameter | Type   | Description    |
|-----------|--------|----------------|
| id        | String | Metadata ID    |
| type      | String | Metadata type  |
| name      | String | Metadata name  |
| value     | String | Metadata value |

#### Response Parameters

None

### 6.6 Get Metadata List

#### Method

```java
MetaDataListResponse listMetaData(String datasetId);

        MetaDataListResponse listMetaData(String datasetId,String apiKey);
```

#### Request Parameters

| Parameter | Type   | Required | Description       |
|-----------|--------|----------|-------------------|
| datasetId | String | Yes      | Knowledge base ID |

#### Response Parameters

MetaDataListResponse

| Parameter           | Type                | Description                       |
|---------------------|---------------------|-----------------------------------|
| builtInFieldEnabled | Boolean             | Whether built-in field is enabled |
| docMetadata         | `List<DocMetadata>` | Document metadata list            |

**DocMetadata Object Structure**

| Parameter | Type    | Description   |
|-----------|---------|---------------|
| id        | String  | Metadata ID   |
| type      | String  | Metadata type |
| name      | String  | Metadata name |
| userCount | Integer | Usage count   |

## 7. Embedding Models

### 7.1 Get Embedding Model List

#### Method

```java
TextEmbeddingListResponse listTextEmbedding();

        TextEmbeddingListResponse listTextEmbedding(String apikey);
```

#### Request Parameters

None

#### Response Parameters

TextEmbeddingListResponse

| Parameter | Type                  | Description          |
|-----------|-----------------------|----------------------|
| data      | `List<TextEmbedding>` | Embedding model list |

**TextEmbedding Object Structure**

| Parameter | Type                       | Description       |
|-----------|----------------------------|-------------------|
| provider  | String                     | Provider name     |
| label     | TextEmbeddingLabel         | Label information |
| iconSmall | TextEmbeddingIcon          | Small icon        |
| iconLarge | TextEmbeddingIcon          | Large icon        |
| status    | String                     | Status            |
| models    | `List<TextEmbeddingModel>` | Model list        |

**TextEmbeddingLabel Object Structure**

| Parameter | Type   | Description              |
|-----------|--------|--------------------------|
| zhHans    | String | Simplified Chinese label |
| enUs      | String | English (US) label       |

**TextEmbeddingIcon Object Structure**

| Parameter | Type   | Description                 |
|-----------|--------|-----------------------------|
| zhHans    | String | Simplified Chinese icon URL |
| enUs      | String | English (US) icon URL       |

**TextEmbeddingModel Object Structure**

| Parameter            | Type                         | Description                       |
|----------------------|------------------------------|-----------------------------------|
| model                | String                       | Model identifier                  |
| label                | TextEmbeddingLabel           | Model label                       |
| modelType            | String                       | Model type                        |
| features             | `List<ModelFeatureEnum>`     | Feature list                      |
| fetchFrom            | String                       | Fetch source                      |
| modelProperties      | TextEmbeddingModelProperties | Model properties                  |
| deprecated           | Boolean                      | Whether deprecated                |
| status               | ModelStatusEnum              | Status                            |
| loadBalancingEnabled | Boolean                      | Whether load balancing is enabled |

**TextEmbeddingModelProperties Object Structure**

| Parameter   | Type    | Description    |
|-------------|---------|----------------|
| contextSize | Integer | Context size   |
| maxChunks   | Integer | Maximum chunks |

**ModelStatusEnum Values**

| Enum Value     | Code Value     | Description    |
|----------------|----------------|----------------|
| ACTIVE         | active         | Active         |
| NO_CONFIGURE   | no-configure   | Not configured |
| QUOTA_EXCEEDED | quota-exceeded | Quota exceeded |
| NO_PERMISSION  | no-permission  | No permission  |
| DISABLED       | disabled       | Disabled       |

**ModelFeatureEnum Values**

| Enum Value       | Code Value       | Description         |
|------------------|------------------|---------------------|
| TOOL_CALL        | tool-call        | Tool call           |
| MULTI_TOOL_CALL  | multi-tool-call  | Multiple tool calls |
| AGENT_THOUGHT    | agent-thought    | Agent thought       |
| VISION           | vision           | Vision              |
| STREAM_TOOL_CALL | stream-tool-call | Stream tool call    |
| DOCUMENT         | document         | Document            |
| VIDEO            | video            | Video               |
| AUDIO            | audio            | Audio               |

### 7.2 Get RERANK List

#### Method

```java
TextEmbeddingListResponse listRerank();

        TextEmbeddingListResponse listRerank(String apikey);
```

#### Request Parameters

None

#### Response Parameters

TextEmbeddingListResponse

| Parameter | Type                  | Description          |
|-----------|-----------------------|----------------------|
| data      | `List<TextEmbedding>` | Embedding model list |

**TextEmbedding Object Structure**

| Parameter | Type                       | Description       |
|-----------|----------------------------|-------------------|
| provider  | String                     | Provider name     |
| label     | TextEmbeddingLabel         | Label information |
| iconSmall | TextEmbeddingIcon          | Small icon        |
| iconLarge | TextEmbeddingIcon          | Large icon        |
| status    | String                     | Status            |
| models    | `List<TextEmbeddingModel>` | Model list        |

**TextEmbeddingLabel Object Structure**

| Parameter | Type   | Description              |
|-----------|--------|--------------------------|
| zhHans    | String | Simplified Chinese label |
| enUs      | String | English (US) label       |

**TextEmbeddingIcon Object Structure**

| Parameter | Type   | Description                 |
|-----------|--------|-----------------------------|
| zhHans    | String | Simplified Chinese icon URL |
| enUs      | String | English (US) icon URL       |

**TextEmbeddingModel Object Structure**

| Parameter            | Type                         | Description                       |
|----------------------|------------------------------|-----------------------------------|
| model                | String                       | Model identifier                  |
| label                | TextEmbeddingLabel           | Model label                       |
| modelType            | String                       | Model type                        |
| features             | `List<ModelFeatureEnum>`     | Feature list                      |
| fetchFrom            | String                       | Fetch source                      |
| modelProperties      | TextEmbeddingModelProperties | Model properties                  |
| deprecated           | Boolean                      | Whether deprecated                |
| status               | ModelStatusEnum              | Status                            |
| loadBalancingEnabled | Boolean                      | Whether load balancing is enabled |

**TextEmbeddingModelProperties Object Structure**

| Parameter   | Type    | Description    |
|-------------|---------|----------------|
| contextSize | Integer | Context size   |
| maxChunks   | Integer | Maximum chunks |

**ModelStatusEnum Values**

| Enum Value     | Code Value     | Description    |
|----------------|----------------|----------------|
| ACTIVE         | active         | Active         |
| NO_CONFIGURE   | no-configure   | Not configured |
| QUOTA_EXCEEDED | quota-exceeded | Quota exceeded |
| NO_PERMISSION  | no-permission  | No permission  |
| DISABLED       | disabled       | Disabled       |

**ModelFeatureEnum Values**

| Enum Value       | Code Value       | Description         |
|------------------|------------------|---------------------|
| TOOL_CALL        | tool-call        | Tool call           |
| MULTI_TOOL_CALL  | multi-tool-call  | Multiple tool calls |
| AGENT_THOUGHT    | agent-thought    | Agent thought       |
| VISION           | vision           | Vision              |
| STREAM_TOOL_CALL | stream-tool-call | Stream tool call    |
| DOCUMENT         | document         | Document            |
| VIDEO            | video            | Video               |
| AUDIO            | audio            | Audio               |

---

## 8. Tag Management

### 8.1 Create Tag

#### Method

```java
TagInfoResponse createTag(TagCreateRequest request);
```

#### Request Parameters

TagCreateRequest

| Parameter | Type   | Required | Description             |
|-----------|--------|----------|-------------------------|
| name      | String | Yes      | Tag name, max length 50 |

#### Response Parameters

TagInfoResponse

| Parameter    | Type    | Description        |
|--------------|---------|--------------------|
| id           | String  | Tag ID             |
| name         | String  | Tag name           |
| type         | String  | Tag type           |
| bindingCount | Integer | Number of bindings |

---

### 8.2 List Tags

#### Method

```java
List<TagInfoResponse> listTag();
        List<TagInfoResponse> listTag(String apiKey);
```

#### Request Parameters

| Parameter | Type   | Required | Description        |
|-----------|--------|----------|--------------------|
| apiKey    | String | No       | API key (optional) |

#### Response Parameters

Same as 8.1 TagInfoResponse list

---

### 8.3 Update Tag

#### Method

```java
TagInfoResponse updateTag(TagUpdateRequest request);
```

#### Request Parameters

TagUpdateRequest

| Parameter | Type   | Required | Description             |
|-----------|--------|----------|-------------------------|
| tagId     | String | Yes      | Tag ID                  |
| name      | String | Yes      | Tag name, max length 50 |

#### Response Parameters

Same as 8.1 TagInfoResponse

---

### 8.4 Delete Tag

#### Method

```java
void deleteTag(String tagId);
        void deleteTag(String tagId,String apiKey);
```

#### Request Parameters

| Parameter | Type   | Required | Description        |
|-----------|--------|----------|--------------------|
| tagId     | String | Yes      | Tag ID             |
| apiKey    | String | No       | API key (optional) |

#### Response Parameters

None

---

### 8.5 Bind Tags to Resource

#### Method

```java
void bindingTag(TagBindingRequest request);
```

#### Request Parameters

TagBindingRequest

| Parameter | Type           | Required | Description               |
|-----------|----------------|----------|---------------------------|
| tagIds    | `List<String>` | Yes      | List of tag IDs           |
| targetId  | String         | Yes      | Target ID (e.g., dataset) |

#### Response Parameters

None

---

### 8.6 Unbind Tag

#### Method

```java
void unbindingTag(TagUnbindingRequest request);
```

#### Request Parameters

TagUnbindingRequest

| Parameter | Type   | Required | Description               |
|-----------|--------|----------|---------------------------|
| tagId     | String | Yes      | Tag ID                    |
| targetId  | String | Yes      | Target ID (e.g., dataset) |

#### Response Parameters

None

---

### 8.7 List Tags Bound to Dataset

#### Method

```java
DataSetTagsResponse listDatasetTag(String datasetId);
        DataSetTagsResponse listDatasetTag(String datasetId,String apiKey);
```

#### Request Parameters

| Parameter | Type   | Required | Description        |
|-----------|--------|----------|--------------------|
| datasetId | String | Yes      | Dataset ID         |
| apiKey    | String | No       | API key (optional) |

#### Response Parameters

DataSetTagsResponse

| Parameter | Type                   | Description          |
|-----------|------------------------|----------------------|
| data      | `List<DataSetTagInfo>` | List of tag info     |
| total     | Integer                | Total number of tags |

See DataSetTagInfo structure in implementation for details.

---

> All tag-related APIs require a valid API key. If not provided, the default apikey in the configuration file is used.


