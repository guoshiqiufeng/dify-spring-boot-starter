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
| apiKey            | String                | Yes      | API Key                    |
| name              | String                | Yes      | Knowledge base name        |
| description       | String                | No       | Knowledge base description |
| permission        | PermissionEnum        | No       | Permission settings        |
| provider          | ProviderEnum          | No       | Provider                   |
| indexingTechnique | IndexingTechniqueEnum | No       | Indexing technique         |

**ProviderEnum Values**

| Value       | Description |
|-------------|-------------|
| SELF_BUILT  | Self-built  |
| THIRD_PARTY | Third-party |

**PermissionEnum Values**

| Value   | Description          |
|---------|----------------------|
| ONLY_ME | Visible only to self |
| TEAM    | Visible to team      |
| PUBLIC  | Publicly visible     |

**IndexingTechniqueEnum Values**

| Value        | Description  |
|--------------|--------------|
| HIGH_QUALITY | High quality |
| ECONOMY      | Economy      |

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
| apiKey    | String  | Yes      | API Key                      |
| page      | Integer | No       | Page number, default 1       |
| limit     | Integer | No       | Records per page, default 20 |

#### Response Parameters

`DifyPageResult<DatasetResponse>`

| Parameter | Type                  | Description         |
|-----------|-----------------------|---------------------|
| list      | `List<DatasetResponse>` | Knowledge base list |
| total     | Long                  | Total records       |
| page      | Integer               | Current page        |
| limit     | Integer               | Records per page    |
| pages     | Integer               | Total pages         |

DatasetResponse See 1.1

### 1.3 Delete Knowledge Base

#### Method

```java
void delete(String datasetId, String apiKey);
```

#### Request Parameters

| Parameter | Type   | Required | Description       |
|-----------|--------|----------|-------------------|
| datasetId | String | Yes      | Knowledge base ID |
| apiKey    | String | Yes      | API Key           |

## 2. Document Management

### 2.1 Create Document by Text

#### Method

```java
DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request);
```

#### Request Parameters

DocumentCreateByTextRequest

| Parameter              | Type                  | Required | Description                                               |
|------------------------|-----------------------|----------|-----------------------------------------------------------|
| apiKey                 | String                | Yes      | API Key                                                   |
| datasetId              | String                | Yes      | Knowledge base ID                                         |
| name                   | String                | Yes      | Document name                                             |
| text                   | String                | Yes      | Document content                                          |
| docType                | DocTypeEnum           | No       | Document type                                             |
| docMetadata            | `Map<String, Object> `  | No       | Document metadata (required if document type is provided) |
| indexingTechnique      | IndexingTechniqueEnum | No       | Indexing mode                                             |
| docForm                | DocFormEnum           | No       | Document form                                             |
| docLanguage            | String                | No       | Document language                                         |
| processRule            | ProcessRule           | No       | Processing rules                                          |
| retrievalModel         | RetrievalModel        | No       | Retrieval model                                           |
| embeddingModel         | String                | No       | Embedding model                                           |
| embeddingModelProvider | String                | No       | Embedding model provider                                  |

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
| mode  | String     | Processing mode |
| rules | CustomRule | Custom rules    |

**CustomRule Object Structure**

| Field                | Type                    | Description                   |
|----------------------|-------------------------|-------------------------------|
| preProcessingRules   | `List<PreProcessingRule>` | Pre-processing rules list     |
| segmentation         | Segmentation            | Segmentation rules            |
| parentMode           | ParentModeEnum          | Parent segment retrieval mode |
| subChunkSegmentation | SubChunkSegmentation    | Sub-chunk segmentation rules  |

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

| Field                 | Type             | Description                       |
|-----------------------|------------------|-----------------------------------|
| searchMethod          | SearchMethodEnum | Search method                     |
| rerankingEnable       | Boolean          | Whether to enable reranking       |
| rerankingModel        | RerankingModel   | Reranking model                   |
| weights               | Float            | Weights                           |
| topK                  | Integer          | Number of results to return       |
| scoreThresholdEnabled | Boolean          | Whether to enable score threshold |
| scoreThreshold        | Float            | Score threshold                   |

**RerankingModel Object Structure**

| Field                 | Type   | Description             |
|-----------------------|--------|-------------------------|
| rerankingProviderName | String | Reranking provider name |
| rerankingModelName    | String | Reranking model name    |

#### Response Parameters

DocumentCreateResponse

| Parameter | Type         | Description          |
|-----------|--------------|----------------------|
| document  | DocumentInfo | Document information |
| batch     | String       | Batch number         |

**DocumentInfo Object Structure**

| Parameter            | Type                | Description                       |
|----------------------|---------------------|-----------------------------------|
| id                   | String              | Document ID                       |
| position             | Integer             | Position                          |
| dataSourceType       | String              | Data source type                  |
| dataSourceInfo       | `Map<String, Object>` | Data source information           |
| datasetProcessRuleId | String              | Knowledge base processing rule ID |
| name                 | String              | Document name                     |
| createdFrom          | String              | Creation source                   |
| createdBy            | String              | Created by                        |
| createdAt            | Long                | Creation timestamp                |
| tokens               | Integer             | Token count                       |
| indexingStatus       | String              | Indexing status                   |
| error                | String              | Error message                     |
| enabled              | String              | Whether enabled                   |
| disabledAt           | Long                | Disabled timestamp                |
| disabledBy           | String              | Disabled by                       |
| archived             | String              | Whether archived                  |
| displayStatus        | String              | Display status                    |
| wordCount            | String              | Word count                        |
| hitCount             | String              | Hit count                         |
| docForm              | String              | Document form                     |

### 2.2 Create Document by File

#### Method

```java
DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request);
```

#### Request Parameters

DocumentCreateByFileRequest

| Parameter              | Type                  | Required | Description                                               |
|------------------------|-----------------------|----------|-----------------------------------------------------------|
| apiKey                 | String                | Yes      | API Key                                                   |
| datasetId              | String                | Yes      | Knowledge base ID                                         |
| name                   | String                | Yes      | Document name                                             |
| file                   | MultipartFile         | Yes      | Document file                                             |
| docType                | DocTypeEnum           | No       | Document type                                             |
| docMetadata            | `Map<String, Object>`   | No       | Document metadata (required if document type is provided) |
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
| apiKey            | String                | Yes      | API Key                                                   |
| datasetId         | String                | Yes      | Knowledge base ID                                         |
| documentId        | String                | Yes      | Document ID                                               |
| name              | String                | No       | Document name                                             |
| text              | String                | Yes      | Document content                                          |
| docType           | DocTypeEnum           | No       | Document type                                             |
| docMetadata       | `Map<String, Object>`   | No       | Document metadata (required if document type is provided) |
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

| Parameter         | Type                  | Required | Description                                               |
|-------------------|-----------------------|----------|-----------------------------------------------------------|
| apiKey            | String                | Yes      | API Key                                                   |
| datasetId         | String                | Yes      | Knowledge base ID                                         |
| documentId        | String                | Yes      | Document ID                                               |
| name              | String                | No       | Document name                                             |
| file              | MultipartFile         | Yes      | Document file                                             |
| docType           | DocTypeEnum           | No       | Document type                                             |
| docMetadata       | `Map<String, Object> `  | No       | Document metadata (required if document type is provided) |
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

### 2.5 Paginated Query of Document List

#### Method

```java
DifyPageResult<DocumentInfo> pageDocument(DatasetPageDocumentRequest request);
```

#### Request Parameters

DatasetPageDocumentRequest

| Parameter | Type    | Required | Description                  |
|-----------|---------|----------|------------------------------|
| apiKey    | String  | Yes      | API Key                      |
| datasetId | String  | Yes      | Knowledge base ID            |
| keyword   | String  | No       | Search keyword               |
| page      | Integer | No       | Page number, default 1       |
| limit     | Integer | No       | Records per page, default 20 |

#### Response Parameters

DifyPageResult<DocumentInfo>

| Parameter | Type               | Description      |
|-----------|--------------------|------------------|
| list      |` List<DocumentInfo>` | Document list    |
| total     | Long               | Total records    |
| page      | Integer            | Current page     |
| limit     | Integer            | Records per page |
| pages     | Integer            | Total pages      |

DocumentInfo

| Parameter            | Type                | Description                       |
|----------------------|---------------------|-----------------------------------|
| id                   | String              | Document ID                       |
| position             | Integer             | Position                          |
| dataSourceType       | String              | Data source type                  |
| dataSourceInfo       | `Map<String, Object>` | Data source information           |
| datasetProcessRuleId | String              | Knowledge base processing rule ID |
| name                 | String              | Document name                     |
| createdFrom          | String              | Creation source                   |
| createdBy            | String              | Created by                        |
| createdAt            | Long                | Creation timestamp                |
| tokens               | Integer             | Token count                       |
| indexingStatus       | String              | Indexing status                   |
| error                | String              | Error message                     |
| enabled              | String              | Whether enabled                   |
| disabledAt           | Long                | Disabled timestamp                |
| disabledBy           | String              | Disabled by                       |
| archived             | String              | Whether archived                  |
| displayStatus        | String              | Display status                    |
| wordCount            | String              | Word count                        |
| hitCount             | String              | Hit count                         |
| docForm              | String              | Document form                     |

### 2.6 Query Document Indexing Status

#### Method

```java
DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request);
```

#### Request Parameters

DocumentIndexingStatusRequest

| Parameter | Type   | Required | Description       |
|-----------|--------|----------|-------------------|
| apiKey    | String | Yes      | API Key           |
| datasetId | String | Yes      | Knowledge base ID |
| batch     | String | Yes      | Batch number      |

#### Response Parameters

DocumentIndexingStatusResponse

| Parameter | Type                   | Description            |
|-----------|------------------------|------------------------|
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
DocumentDeleteResponse deleteDocument(String datasetId, String documentId, String apiKey);
```

#### Request Parameters

| Parameter  | Type   | Required | Description       |
|------------|--------|----------|-------------------|
| datasetId  | String | Yes      | Knowledge base ID |
| documentId | String | Yes      | Document ID       |
| apiKey     | String | Yes      | API Key           |

#### Response Parameters

DocumentDeleteResponse

| Parameter | Type   | Description            |
|-----------|--------|------------------------|
| result    | String | Fixed return "success" |

### 2.8 Get Upload File Information

#### Method

```java
UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId, String apiKey);
```

#### Request Parameters

| Parameter  | Type   | Required | Description       |
|------------|--------|----------|-------------------|
| datasetId  | String | Yes      | Knowledge base ID |
| documentId | String | Yes      | Document ID       |
| apiKey     | String | Yes      | API Key           |

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

| Parameter  | Type               | Required | Description            |
|------------|--------------------|----------|------------------------|
| apiKey     | String             | Yes      | API Key                |
| datasetId  | String             | Yes      | Knowledge base ID      |
| documentId | String             | Yes      | Document ID            |
| segments   | `List<SegmentParam>` | Yes      | Segment parameter list |

#### Response Parameters

SegmentResponse

| Parameter | Type              | Description       |
|-----------|-------------------|-------------------|
| data      | `List<SegmentData>` | Segment data list |
| docForm   | String            | Document form     |

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

| Parameter | Type         | Description     |
|-----------|--------------|-----------------|
| content   | String       | Segment content |
| answer    | String       | Segment answer  |
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
| apiKey     | String | Yes      | API Key           |
| datasetId  | String | Yes      | Knowledge base ID |
| documentId | String | Yes      | Document ID       |
| keyword    | String | No       | Search keyword    |
| status     | String | No       | Status filter     |

#### Response Parameters

SegmentResponse

| Parameter | Type              | Description       |
|-----------|-------------------|-------------------|
| data      | `List<SegmentData>` | Segment data list |
| docForm   | String            | Document form     |

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
SegmentDeleteResponse deleteSegment(String datasetId, String documentId, String segmentId, String apiKey);
```

#### Request Parameters

| Parameter  | Type   | Required | Description       |
|------------|--------|----------|-------------------|
| datasetId  | String | Yes      | Knowledge base ID |
| documentId | String | Yes      | Document ID       |
| segmentId  | String | Yes      | Segment ID        |
| apiKey     | String | Yes      | API Key           |

#### Response Parameters

SegmentDeleteResponse

| Parameter | Type   | Description            |
|-----------|--------|------------------------|
| result    | String | Fixed return "success" |

### 3.4 Update Segment

#### Method

```java
SegmentUpdateResponse updateSegment(SegmentUpdateRequest request);
```

#### Request Parameters

SegmentUpdateRequest

| Parameter  | Type         | Required | Description       |
|------------|--------------|----------|-------------------|
| apiKey     | String       | Yes      | API Key           |
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

| Parameter | Type         | Description     |
|-----------|--------------|-----------------|
| content   | String       | Segment content |
| answer    | String       | Segment answer  |
| keywords  | `List<String>` | Keyword list    |

## 4. Data Retrieval

### 4.1 Retrieve Data

#### Method

```java
RetrieveResponse retrieve(RetrieveRequest request);
```

#### Request Parameters

RetrieveRequest

| Parameter      | Type           | Required | Description             |
|----------------|----------------|----------|-------------------------|
| apiKey         | String         | Yes      | API Key                 |
| datasetId      | String         | Yes      | Knowledge base ID       |
| query          | String         | Yes      | Retrieval query content |
| retrievalModel | RetrievalModel | No       | Retrieval model         |

#### Response Parameters

RetrieveResponse

| Parameter | Type                 | Description           |
|-----------|----------------------|-----------------------|
| query     | RetrieveQuery        | Query information     |
| records   | `List<RetrieveRecord>` | Retrieval record list |

**RetrieveQuery Object Structure**

| Parameter | Type   | Description   |
|-----------|--------|---------------|
| content   | String | Query content |

**RetrieveRecord Object Structure**

| Parameter     | Type         | Description               |
|---------------|--------------|---------------------------|
| segment       | Segment      | Segment information       |
| score         | Float        | Relevance score           |
| tsne_position | TsnePosition | TSNE position information |

**TsnePosition Object Structure**

| Parameter | Type  | Description  |
|-----------|-------|--------------|
| x         | Float | X coordinate |
| y         | Float | Y coordinate |

**Segment Object Structure**

| Parameter     | Type         | Description          |
|---------------|--------------|----------------------|
| id            | String       | Segment ID           |
| position      | Integer      | Position             |
| documentId    | String       | Document ID          |
| content       | String       | Segment content      |
| answer        | String       | Answer               |
| wordCount     | Integer      | Word count           |
| tokens        | Integer      | Token count          |
| keywords      |` List<String>` | Keyword list         |
| indexNodeId   | String       | Index node ID        |
| indexNodeHash | String       | Index node hash      |
| hitCount      | Integer      | Hit count            |
| enabled       | String       | Whether enabled      |
| disabledAt    | Long         | Disabled timestamp   |
| disabledBy    | String       | Disabled by          |
| status        | String       | Status               |
| createdAt     | Long         | Creation timestamp   |
| createdBy     | String       | Created by           |
| indexingAt    | Long         | Indexing timestamp   |
| completedAt   | Long         | Completion timestamp |
| error         | String       | Error message        |
| stoppedAt     | Long         | Stop timestamp       |
| document      | Document     | Document information |

**Document Object Structure**

| Parameter      | Type   | Description      |
|----------------|--------|------------------|
| id             | String | Document ID      |
| dataSourceType | String | Data source type |
| name           | String | Document name    |
| docType        | String | Document type    |
