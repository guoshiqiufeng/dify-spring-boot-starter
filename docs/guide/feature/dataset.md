---
lang: zh-cn
title: Dataset API
description: 
---

# Dataset API

## 接口概述

知识库服务接口提供完整的知识库管理功能集成能力，包含知识库创建、文档管理、分段管理、数据检索等核心功能。所有接口均需要有效的API密钥进行身份验证。
使用`DifyDataset`接口实例。
> 以下所有接口都包含apikey，apikey 字段为选填，为空时默认使用配置文件中的 apikey

## 1. 知识库管理

### 1.1 创建知识库

#### 方法

```java
DatasetResponse create(DatasetCreateRequest request);
```

#### 请求参数

DatasetCreateRequest

| 参数名               | 类型                    | 是否必须 | 描述    |
|-------------------|-----------------------|------|-------|
| name              | String                | 是    | 知识库名称 |
| description       | String                | 否    | 知识库描述 |
| permission        | PermissionEnum        | 否    | 权限设置  |
| provider          | ProviderEnum          | 否    | 所属    |
| indexingTechnique | IndexingTechniqueEnum | 否    | 索引技术  |

**ProviderEnum 枚举值**

| 枚举值         | 描述  |
|-------------|-----|
| SELF_BUILT  | 自建  |
| THIRD_PARTY | 第三方 |

**PermissionEnum 枚举值**

| 枚举值     | 描述    |
|---------|-------|
| ONLY_ME | 仅自己可见 |
| TEAM    | 团队可见  |
| PUBLIC  | 公开可见  |

**IndexingTechniqueEnum 枚举值**

| 枚举值          | 描述  |
|--------------|-----|
| HIGH_QUALITY | 高质量 |
| ECONOMY      | 经济型 |

#### 响应参数

DatasetResponse

| 参数名               | 类型                    | 描述     |
|-------------------|-----------------------|--------|
| id                | String                | 知识库 id |
| name              | String                | 知识库名称  |
| description       | String                | 知识库描述  |
| permission        | PermissionEnum        | 权限     |
| dataSourceType    | String                | 数据源类型  |
| indexingTechnique | IndexingTechniqueEnum | 索引技术   |
| appCount          | Integer               | 应用数量   |
| documentCount     | Integer               | 文档数量   |
| wordCount         | Integer               | 单词数量   |
| createdBy         | String                | 创建人    |
| createdAt         | Long                  | 创建时间戳  |
| updatedBy         | String                | 更新人    |
| updatedAt         | Long                  | 更新时间戳  |

### 1.2 分页查询知识库列表

#### 方法

```java
DifyPageResult<DatasetResponse> page(DatasetPageRequest request);
```

#### 请求参数

DatasetPageRequest

| 参数名   | 类型      | 是否必须 | 描述          |
|-------|---------|------|-------------|
| page  | Integer | 否    | 页码，默认1      |
| limit | Integer | 否    | 每页记录数，默认20条 |

#### 响应参数

`DifyPageResult<DatasetResponse>`

| 参数名   | 类型                      | 描述    |
|-------|-------------------------|-------|
| list  | `List<DatasetResponse>` | 知识库列表 |
| total | Long                    | 总记录数  |
| page  | Integer                 | 当前页码  |
| limit | Integer                 | 每页记录数 |
| pages | Integer                 | 总页数   |

DatasetResponse 查看 1.1

### 1.3 删除知识库

#### 方法

```java
void delete(String datasetId);

void delete(String datasetId, String apiKey);
```

#### 请求参数

| 参数名       | 类型     | 是否必须 | 描述     |
|-----------|--------|------|--------|
| datasetId | String | 是    | 知识库 id |

#### 响应参数

无

## 2. 文档管理

### 2.1 通过文本创建文档

#### 方法

```java
DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request);
```

#### 请求参数

DocumentCreateByTextRequest

| 参数名                    | 类型                    | 是否必须 | 描述                |
|------------------------|-----------------------|------|-------------------|
| datasetId              | String                | 是    | 知识库 id            |
| name                   | String                | 是    | 文档名称              |
| text                   | String                | 是    | 文档内容              |
| docType                | DocTypeEnum           | 否    | 文档类型              |
| docMetadata            | `Map<String, Object>` | 否    | 文档元数据（如提供文档类型则必填） |
| indexingTechnique      | IndexingTechniqueEnum | 否    | 索引模式              |
| docForm                | DocFormEnum           | 否    | 文档形式              |
| docLanguage            | String                | 否    | 文档语言              |
| processRule            | ProcessRule           | 否    | 处理规则              |
| retrievalModel         | RetrievalModel        | 否    | 检索模型              |
| embeddingModel         | String                | 否    | 嵌入模型              |
| embeddingModelProvider | String                | 否    | 嵌入模型提供商           |

**DocTypeEnum 枚举值**

| 枚举值                | 描述         |
|--------------------|------------|
| book               | 图书         |
| web_page           | 网页         |
| paper              | 学术论文/文章    |
| social_media_post  | 社交媒体帖子     |
| wikipedia_entry    | 维基百科条目     |
| personal_document  | 个人文档       |
| business_document  | 商业文档       |
| im_chat_log        | 即时通讯记录     |
| synced_from_notion | Notion同步文档 |
| synced_from_github | GitHub同步文档 |
| others             | 其他文档类型     |

**DocFormEnum 枚举值**

| 枚举值                | 描述                                 |
|--------------------|------------------------------------|
| text_model         | 文档直接embedding，经济模式默认为该模式           |
| hierarchical_model | parent-child模式                     |
| qa_model           | Q&A模式：为分片文档生成Q&A对，然后对问题进行embedding |

**ProcessRule 对象结构**

| 字段名   | 类型         | 描述    |
|-------|------------|-------|
| mode  | ModeEnum   | 处理模式  |
| rules | CustomRule | 自定义规则 |

**ModeEnum 对象结构**

| 枚举值          | 描述             |
|--------------|----------------|
| automatic    | 自动             |
| hierarchical | parent-child模式 |
| custom       | 自定义            |

**CustomRule 对象结构**

| 字段名                  | 类型                        | 描述       |
|----------------------|---------------------------|----------|
| preProcessingRules   | `List<PreProcessingRule>` | 预处理规则列表  |
| segmentation         | Segmentation              | 分段规则     |
| parentMode           | ParentModeEnum            | 父分段的召回模式 |
| subChunkSegmentation | SubChunkSegmentation      | 子分段规则    |

**PreProcessingRule 对象结构**

| 字段名     | 类型                        | 描述                                                    |
|---------|---------------------------|-------------------------------------------------------|
| id      | PreProcessingRuleTypeEnum | 预处理规则的唯一标识符，如 remove_extra_spaces, remove_urls_emails |
| enabled | Boolean                   | 是否选中该规则，不传入文档 ID 时代表默认值                               |

**Segmentation 对象结构**

| 字段名       | 类型      | 描述                           |
|-----------|---------|------------------------------|
| separator | String  | 自定义分段标识符，目前仅允许设置一个分隔符。默认为 \n |
| maxTokens | Integer | 最大长度（token）默认为 1000          |

**SubChunkSegmentation 对象结构**

| 字段名          | 类型      | 描述             |
|--------------|---------|----------------|
| separator    | String  | 自定义子分段标识符      |
| maxTokens    | Integer | 子分段最大长度（token） |
| chunkOverlap | Integer | 子分段重叠长度        |

**ParentModeEnum 枚举值**

| 枚举值       | 描述   |
|-----------|------|
| FULL_DOC  | 全文召回 |
| PARAGRAPH | 段落召回 |

**RetrievalModel 对象结构**

| 字段名                   | 类型                   | 描述       |
|-----------------------|----------------------|----------|
| searchMethod          | SearchMethodEnum     | 搜索方法     |
| rerankingEnable       | Boolean              | 是否启用重排序  |
| rerankingModel        | RerankingModel       | 重排序模型    |
| weights               | RerankingModelWeight | 权重       |
| topK                  | Integer              | 返回结果数量   |
| scoreThresholdEnabled | Boolean              | 是否启用分数阈值 |
| scoreThreshold        | Float                | 分数阈值     |

**RerankingModel 对象结构**

| 字段名                   | 类型     | 描述       |
|-----------------------|--------|----------|
| rerankingProviderName | String | 重排序提供商名称 |
| rerankingModelName    | String | 重排序模型名称  |

**RerankingModelWeight 对象结构**

| 字段名            | 类型             | 描述             |
|----------------|----------------|----------------|
| weightType     | String         | 默认为 customized |
| vectorSetting  | VectorSetting  | 语义权重           |
| keywordSetting | KeywordSetting | 关键词权重          |

**VectorSetting 对象结构**

| 字段名                   | 类型     | 描述      |
|-----------------------|--------|---------|
| vectorWeight          | Float  | 语义权重    |
| embeddingModelName    | String | 嵌入模型    |
| embeddingProviderName | String | 嵌入模型提供商 |

**RerankingModelWeight 对象结构**

| 字段名           | 类型    | 描述    |
|---------------|-------|-------|
| keywordWeight | Float | 关键词权重 |

#### 响应参数

DocumentCreateResponse

| 参数名      | 类型           | 描述   |
|----------|--------------|------|
| document | DocumentInfo | 文档信息 |
| batch    | String       | 批次号  |

**DocumentInfo 对象结构**

| 参数名                  | 类型                    | 描述        |
|----------------------|-----------------------|-----------|
| id                   | String                | 文档 id     |
| position             | Integer               | 位置        |
| dataSourceType       | String                | 数据源类型     |
| dataSourceInfo       | `Map<String, Object>` | 数据源信息     |
| datasetProcessRuleId | String                | 知识库处理规则ID |
| name                 | String                | 文档名称      |
| createdFrom          | String                | 创建来源      |
| createdBy            | String                | 创建人       |
| createdAt            | Long                  | 创建时间戳     |
| tokens               | Integer               | 令牌数量      |
| indexingStatus       | String                | 索引状态      |
| error                | String                | 错误信息      |
| enabled              | String                | 是否启用      |
| disabledAt           | Long                  | 禁用时间      |
| disabledBy           | String                | 禁用人       |
| archived             | String                | 是否归档      |
| displayStatus        | String                | 显示状态      |
| wordCount            | String                | 单词数量      |
| hitCount             | String                | 命中次数      |
| docForm              | String                | 文档形式      |

### 2.2 通过文件创建文档

#### 方法

```java
DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request);
```

#### 请求参数

DocumentCreateByFileRequest

| 参数名                    | 类型                    | 是否必须 | 描述                |
|------------------------|-----------------------|------|-------------------|
| datasetId              | String                | 是    | 知识库 id            |
| name                   | String                | 是    | 文档名称              |
| file                   | MultipartFile         | 是    | 文档文件              |
| docType                | DocTypeEnum           | 否    | 文档类型              |
| docMetadata            | `Map<String, Object>` | 否    | 文档元数据（如提供文档类型则必填） |
| indexingTechnique      | IndexingTechniqueEnum | 否    | 索引模式              |
| docForm                | DocFormEnum           | 否    | 文档形式              |
| docLanguage            | String                | 否    | 文档语言              |
| processRule            | ProcessRule           | 否    | 处理规则              |
| retrievalModel         | RetrievalModel        | 否    | 检索模型              |
| embeddingModel         | String                | 否    | 嵌入模型              |
| embeddingModelProvider | String                | 否    | 嵌入模型提供商           |

#### 响应参数

DocumentCreateResponse

| 参数名      | 类型           | 描述   |
|----------|--------------|------|
| document | DocumentInfo | 文档信息 |
| batch    | String       | 批次号  |

### 2.3 通过文本更新文档

#### 方法

```java
DocumentCreateResponse updateDocumentByText(DocumentUpdateByTextRequest request);
```

#### 请求参数

DocumentUpdateByTextRequest

| 参数名               | 类型                    | 是否必须 | 描述                |
|-------------------|-----------------------|------|-------------------|
| datasetId         | String                | 是    | 知识库 id            |
| documentId        | String                | 是    | 文档 id             |
| name              | String                | 否    | 文档名称              |
| text              | String                | 是    | 文档内容              |
| docType           | DocTypeEnum           | 否    | 文档类型              |
| docMetadata       | `Map<String, Object>` | 否    | 文档元数据（如提供文档类型则必填） |
| indexingTechnique | IndexingTechniqueEnum | 否    | 索引模式              |
| docForm           | DocFormEnum           | 否    | 文档形式              |
| docLanguage       | String                | 否    | 文档语言              |
| processRule       | ProcessRule           | 否    | 处理规则              |

#### 响应参数

DocumentCreateResponse

| 参数名      | 类型           | 描述   |
|----------|--------------|------|
| document | DocumentInfo | 文档信息 |
| batch    | String       | 批次号  |

### 2.4 通过文件更新文档

#### 方法

```java
DocumentCreateResponse updateDocumentByFile(DocumentUpdateByFileRequest request);
```

#### 请求参数

DocumentUpdateByFileRequest

| 参数名               | 类型                    | 是否必须 | 描述                |
|-------------------|-----------------------|------|-------------------|
| datasetId         | String                | 是    | 知识库 id            |
| documentId        | String                | 是    | 文档 id             |
| name              | String                | 否    | 文档名称              |
| file              | MultipartFile         | 是    | 文档文件              |
| docType           | DocTypeEnum           | 否    | 文档类型              |
| docMetadata       | `Map<String, Object>` | 否    | 文档元数据（如提供文档类型则必填） |
| indexingTechnique | IndexingTechniqueEnum | 否    | 索引模式              |
| docForm           | DocFormEnum           | 否    | 文档形式              |
| docLanguage       | String                | 否    | 文档语言              |
| processRule       | ProcessRule           | 否    | 处理规则              |

#### 响应参数

DocumentCreateResponse

| 参数名      | 类型           | 描述   |
|----------|--------------|------|
| document | DocumentInfo | 文档信息 |
| batch    | String       | 批次号  |

### 2.5 分页查询文档列表

#### 方法

```java
DifyPageResult<DocumentInfo> pageDocument(DatasetPageDocumentRequest request);
```

#### 请求参数

DatasetPageDocumentRequest

| 参数名       | 类型      | 是否必须 | 描述          |
|-----------|---------|------|-------------|
| datasetId | String  | 是    | 知识库 id      |
| keyword   | String  | 否    | 搜索关键词       |
| page      | Integer | 否    | 页码，默认1      |
| limit     | Integer | 否    | 每页记录数，默认20条 |

#### 响应参数

`DifyPageResult<DocumentInfo>`

| 参数名   | 类型                   | 描述    |
|-------|----------------------|-------|
| list  | `List<DocumentInfo>` | 文档列表  |
| total | Long                 | 总记录数  |
| page  | Integer              | 当前页码  |
| limit | Integer              | 每页记录数 |
| pages | Integer              | 总页数   |

DocumentInfo

| 参数名                  | 类型                    | 描述        |
|----------------------|-----------------------|-----------|
| id                   | String                | 文档 id     |
| position             | Integer               | 位置        |
| dataSourceType       | String                | 数据源类型     |
| dataSourceInfo       | `Map<String, Object>` | 数据源信息     |
| datasetProcessRuleId | String                | 知识库处理规则ID |
| name                 | String                | 文档名称      |
| createdFrom          | String                | 创建来源      |
| createdBy            | String                | 创建人       |
| createdAt            | Long                  | 创建时间戳     |
| tokens               | Integer               | 令牌数量      |
| indexingStatus       | String                | 索引状态      |
| error                | String                | 错误信息      |
| enabled              | String                | 是否启用      |
| disabledAt           | Long                  | 禁用时间      |
| disabledBy           | String                | 禁用人       |
| archived             | String                | 是否归档      |
| displayStatus        | String                | 显示状态      |
| wordCount            | String                | 单词数量      |
| hitCount             | String                | 命中次数      |
| docForm              | String                | 文档形式      |

### 2.6 查询文档索引状态

#### 方法

```java
DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request);
```

#### 请求参数

DocumentIndexingStatusRequest

| 参数名       | 类型     | 是否必须 | 描述     |
|-----------|--------|------|--------|
| datasetId | String | 是    | 知识库 id |
| batch     | String | 是    | 批次号    |

#### 响应参数

DocumentIndexingStatusResponse

| 参数名  | 类型                        | 描述     |
|------|---------------------------|--------|
| data | `List<ProcessingStatus> ` | 处理状态列表 |

**ProcessingStatus 对象结构**

| 参数名                  | 类型      | 描述       |
|----------------------|---------|----------|
| id                   | String  | 文档 id    |
| indexingStatus       | String  | 索引状态     |
| processingStartedAt  | Long    | 处理开始时间戳  |
| parsingCompletedAt   | Long    | 解析完成时间戳  |
| cleaningCompletedAt  | Long    | 清洗完成时间戳  |
| splittingCompletedAt | Long    | 分片完成时间戳  |
| completedAt          | Long    | 处理完成时间戳  |
| pausedAt             | Long    | 暂停时间戳    |
| error                | String  | 错误信息     |
| stoppedAt            | Long    | 停止时间戳    |
| completedSegments    | Integer | 已完成的分片数量 |
| totalSegments        | Integer | 总分片数量    |

### 2.7 删除文档

#### 方法

```java
DocumentDeleteResponse deleteDocument(String datasetId, String documentId);

DocumentDeleteResponse deleteDocument(String datasetId, String documentId, String apiKey);
```

#### 请求参数

| 参数名        | 类型     | 是否必须 | 描述     |
|------------|--------|------|--------|
| datasetId  | String | 是    | 知识库 id |
| documentId | String | 是    | 文档 id  |

#### 响应参数

DocumentDeleteResponse

| 参数名    | 类型     | 描述           |
|--------|--------|--------------|
| result | String | 固定返回 success |

### 2.8 获取上传文件信息

#### 方法

```java
UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId);

UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId, String apiKey);
```

#### 请求参数

| 参数名        | 类型     | 是否必须 | 描述     |
|------------|--------|------|--------|
| datasetId  | String | 是    | 知识库 id |
| documentId | String | 是    | 文档 id  |

#### 响应参数

UploadFileInfoResponse

| 参数名         | 类型      | 描述      |
|-------------|---------|---------|
| id          | String  | 文件ID    |
| name        | String  | 文件名     |
| size        | Integer | 文件大小    |
| extension   | String  | 文件扩展名   |
| url         | String  | 文件访问URL |
| downloadUrl | String  | 文件下载URL |
| mimeType    | String  | MIME类型  |
| createdBy   | String  | 创建人     |
| createdAt   | Long    | 创建时间戳   |

## 3. 分段管理

### 3.1 创建分段

#### 方法

```java
SegmentResponse createSegment(SegmentCreateRequest request);
```

#### 请求参数

SegmentCreateRequest

| 参数名        | 类型                     | 是否必须 | 描述     |
|------------|------------------------|------|--------|
| datasetId  | String                 | 是    | 知识库 id |
| documentId | String                 | 是    | 文档 id  |
| segments   | ` List<SegmentParam> ` | 是    | 分段参数列表 |

#### 响应参数

SegmentResponse

| 参数名     | 类型                    | 描述     |
|---------|-----------------------|--------|
| data    | ` List<SegmentData> ` | 分段数据列表 |
| docForm | String                | 文档形式   |

**SegmentData 对象结构**

| 参数名            | 类型      | 描述    |
|----------------|---------|-------|
| id             | String  | 分段 id |
| position       | Integer | 位置    |
| content        | String  | 分段内容  |
| answer         | String  | 答案    |
| wordCount      | Integer | 单词数量  |
| tokens         | Integer | 令牌数量  |
| indexingStatus | String  | 索引状态  |
| error          | String  | 错误信息  |
| enabled        | String  | 是否启用  |
| disabledAt     | Long    | 禁用时间  |
| disabledBy     | String  | 禁用人   |
| archived       | Boolean | 是否归档  |

**SegmentParam 对象结构**

| 参数名      | 类型              | 描述    |
|----------|-----------------|-------|
| content  | String          | 分段内容  |
| answer   | String          | 分段答案  |
| keywords | ` List<String>` | 关键词列表 |

### 3.2 分页查询分段列表

#### 方法

```java
SegmentResponse pageSegment(SegmentPageRequest request);
```

#### 请求参数

SegmentPageRequest

| 参数名        | 类型     | 是否必须 | 描述     |
|------------|--------|------|--------|
| datasetId  | String | 是    | 知识库 id |
| documentId | String | 是    | 文档 id  |
| keyword    | String | 否    | 搜索关键词  |
| status     | String | 否    | 状态过滤   |

#### 响应参数

SegmentResponse

| 参数名     | 类型                   | 描述     |
|---------|----------------------|--------|
| data    | `List<SegmentData> ` | 分段数据列表 |
| docForm | String               | 文档形式   |

**SegmentData 对象结构**

| 参数名            | 类型      | 描述    |
|----------------|---------|-------|
| id             | String  | 分段 id |
| position       | Integer | 位置    |
| content        | String  | 分段内容  |
| answer         | String  | 答案    |
| wordCount      | Integer | 单词数量  |
| tokens         | Integer | 令牌数量  |
| indexingStatus | String  | 索引状态  |
| error          | String  | 错误信息  |
| enabled        | String  | 是否启用  |
| disabledAt     | Long    | 禁用时间  |
| disabledBy     | String  | 禁用人   |
| archived       | Boolean | 是否归档  |

### 3.3 删除分段

#### 方法

```java
SegmentDeleteResponse deleteSegment(String datasetId, String documentId, String segmentId);

SegmentDeleteResponse deleteSegment(String datasetId, String documentId, String segmentId, String apiKey);
```

#### 请求参数

| 参数名        | 类型     | 是否必须 | 描述     |
|------------|--------|------|--------|
| datasetId  | String | 是    | 知识库 id |
| documentId | String | 是    | 文档 id  |
| segmentId  | String | 是    | 分段 id  |

#### 响应参数

SegmentDeleteResponse

| 参数名    | 类型     | 描述           |
|--------|--------|--------------|
| result | String | 固定返回 success |

### 3.4 更新分段

#### 方法

```java
SegmentUpdateResponse updateSegment(SegmentUpdateRequest request);
```

#### 请求参数

SegmentUpdateRequest

| 参数名        | 类型           | 是否必须 | 描述     |
|------------|--------------|------|--------|
| datasetId  | String       | 是    | 知识库 id |
| documentId | String       | 是    | 文档 id  |
| segmentId  | String       | 是    | 分段 id  |
| segment    | SegmentParam | 是    | 分段参数   |

#### 响应参数

SegmentUpdateResponse

| 参数名     | 类型          | 描述   |
|---------|-------------|------|
| data    | SegmentData | 分段数据 |
| docForm | String      | 文档形式 |

**SegmentData 对象结构**

| 参数名            | 类型      | 描述    |
|----------------|---------|-------|
| id             | String  | 分段 id |
| position       | Integer | 位置    |
| content        | String  | 分段内容  |
| answer         | String  | 答案    |
| wordCount      | Integer | 单词数量  |
| tokens         | Integer | 令牌数量  |
| indexingStatus | String  | 索引状态  |
| error          | String  | 错误信息  |
| enabled        | String  | 是否启用  |
| disabledAt     | Long    | 禁用时间  |
| disabledBy     | String  | 禁用人   |
| archived       | Boolean | 是否归档  |

**SegmentParam 对象结构**

| 参数名      | 类型             | 描述    |
|----------|----------------|-------|
| content  | String         | 分段内容  |
| answer   | String         | 分段答案  |
| keywords | `List<String>` | 关键词列表 |

## 4 子分段管理

子分段（Child Chunk）是分段（Segment）的下一级细分单元，用于更细粒度的内容组织和检索。

### 4.1 创建子分段

#### 方法

```java
SegmentChildChunkCreateResponse createSegmentChildChunk(SegmentChildChunkCreateRequest request);
```

#### 请求参数

SegmentChildChunkCreateRequest

| 参数名        | 类型     | 是否必须 | 描述     |
|------------|--------|------|--------|
| datasetId  | String | 是    | 知识库 id |
| documentId | String | 是    | 文档 id  |
| segmentId  | String | 是    | 分段 id  |
| content    | String | 是    | 子分段内容  |

#### 响应参数

SegmentChildChunkCreateResponse

| 参数名  | 类型                        | 描述    |
|------|---------------------------|-------|
| data | SegmentChildChunkResponse | 子分段数据 |

**SegmentChildChunkResponse 对象结构**

| 参数名           | 类型      | 描述      |
|---------------|---------|---------|
| id            | String  | 子分段 id  |
| segmentId     | String  | 所属分段 id |
| content       | String  | 子分段内容   |
| wordCount     | Integer | 单词数量    |
| tokens        | Integer | 令牌数量    |
| indexNodeId   | String  | 索引节点ID  |
| indexNodeHash | String  | 索引节点哈希  |
| status        | String  | 状态      |
| createdBy     | String  | 创建人     |
| createdAt     | Long    | 创建时间戳   |
| indexingAt    | Long    | 索引时间戳   |
| completedAt   | Long    | 完成时间戳   |
| error         | String  | 错误信息    |
| stoppedAt     | Long    | 停止时间戳   |

### 4.2 分页查询子分段列表

#### 方法

```java
DifyPageResult<SegmentChildChunkResponse> pageSegmentChildChunk(SegmentChildChunkPageRequest request);
```

#### 请求参数

SegmentChildChunkPageRequest

| 参数名        | 类型      | 是否必须 | 描述     |
|------------|---------|------|--------|
| datasetId  | String  | 是    | 知识库 id |
| documentId | String  | 是    | 文档 id  |
| segmentId  | String  | 是    | 分段 id  |
| keyword    | String  | 否    | 搜索关键词  |
| page       | Integer | 否    | 页码     |
| limit      | Integer | 否    | 每页记录数  |

#### 响应参数

`DifyPageResult<SegmentChildChunkResponse>`

| 参数名   | 类型                                | 描述    |
|-------|-----------------------------------|-------|
| list  | `List<SegmentChildChunkResponse>` | 子分段列表 |
| total | Long                              | 总记录数  |
| page  | Integer                           | 当前页码  |
| limit | Integer                           | 每页记录数 |
| pages | Integer                           | 总页数   |

### 4.3 更新子分段

#### 方法

```java
SegmentChildChunkUpdateResponse updateSegmentChildChunk(SegmentChildChunkUpdateRequest request);
```

#### 请求参数

SegmentChildChunkUpdateRequest

| 参数名          | 类型     | 是否必须 | 描述     |
|--------------|--------|------|--------|
| datasetId    | String | 是    | 知识库 id |
| documentId   | String | 是    | 文档 id  |
| segmentId    | String | 是    | 分段 id  |
| childChunkId | String | 是    | 子分段 id |
| content      | String | 是    | 子分段内容  |

#### 响应参数

SegmentChildChunkUpdateResponse

| 参数名  | 类型                        | 描述    |
|------|---------------------------|-------|
| data | SegmentChildChunkResponse | 子分段数据 |

### 4.4 删除子分段

#### 方法

```java
SegmentChildChunkDeleteResponse deleteSegmentChildChunk(SegmentChildChunkDeleteRequest request);
```

#### 请求参数

SegmentChildChunkDeleteRequest

| 参数名          | 类型     | 是否必须 | 描述     |
|--------------|--------|------|--------|
| datasetId    | String | 是    | 知识库 id |
| documentId   | String | 是    | 文档 id  |
| segmentId    | String | 是    | 分段 id  |
| childChunkId | String | 是    | 子分段 id |

#### 响应参数

SegmentChildChunkDeleteResponse

| 参数名    | 类型     | 描述           |
|--------|--------|--------------|
| result | String | 固定返回 success |

## 5. 数据检索

### 5.1 检索数据

#### 方法

```java
RetrieveResponse retrieve(RetrieveRequest request);
```

#### 请求参数

RetrieveRequest

| 参数名            | 类型             | 是否必须 | 描述     |
|----------------|----------------|------|--------|
| datasetId      | String         | 是    | 知识库 id |
| query          | String         | 是    | 检索查询内容 |
| retrievalModel | RetrievalModel | 否    | 检索模型   |

#### 响应参数

RetrieveResponse

| 参数名     | 类型                     | 描述     |
|---------|------------------------|--------|
| query   | RetrieveQuery          | 查询信息   |
| records | `List<RetrieveRecord>` | 检索记录列表 |

**RetrieveQuery 对象结构**

| 参数名     | 类型     | 描述   |
|---------|--------|------|
| content | String | 查询内容 |

**RetrieveRecord 对象结构**

| 参数名          | 类型           | 描述       |
|--------------|--------------|----------|
| segment      | Segment      | 分段信息     |
| score        | Float        | 相关性得分    |
| tsnePosition | TsnePosition | TSNE位置信息 |

**TsnePosition 对象结构**

| 参数名 | 类型    | 描述  |
|-----|-------|-----|
| x   | Float | X坐标 |
| y   | Float | Y坐标 |

**Segment 对象结构**

| 参数名           | 类型             | 描述     |
|---------------|----------------|--------|
| id            | String         | 分段ID   |
| position      | Integer        | 位置     |
| documentId    | String         | 文档ID   |
| content       | String         | 分段内容   |
| answer        | String         | 答案     |
| wordCount     | Integer        | 单词数量   |
| tokens        | Integer        | 令牌数量   |
| keywords      | `List<String>` | 关键词列表  |
| indexNodeId   | String         | 索引节点ID |
| indexNodeHash | String         | 索引节点哈希 |
| hitCount      | Integer        | 命中次数   |
| enabled       | String         | 是否启用   |
| disabledAt    | Long           | 禁用时间   |
| disabledBy    | String         | 禁用人    |
| status        | String         | 状态     |
| createdAt     | Long           | 创建时间   |
| createdBy     | String         | 创建人    |
| indexingAt    | Long           | 索引时间   |
| completedAt   | Long           | 完成时间   |
| error         | String         | 错误信息   |
| stoppedAt     | Long           | 停止时间   |
| document      | Document       | 文档信息   |

**Document 对象结构**

| 参数名            | 类型     | 描述    |
|----------------|--------|-------|
| id             | String | 文档ID  |
| dataSourceType | String | 数据源类型 |
| name           | String | 文档名称  |
| docType        | String | 文档类型  |

## 6. 元数据管理

### 6.1 创建元数据

#### 方法

```java
MetaDataResponse createMetaData(MetaDataCreateRequest request);
```

#### 请求参数

MetaDataCreateRequest

| 参数名       | 类型     | 是否必须 | 描述     |
|-----------|--------|------|--------|
| datasetId | String | 是    | 知识库 id |
| type      | String | 是    | 元数据类型  |
| name      | String | 是    | 元数据名称  |

#### 响应参数

MetaDataResponse

| 参数名  | 类型     | 描述    |
|------|--------|-------|
| id   | String | 元数据ID |
| type | String | 元数据类型 |
| name | String | 元数据名称 |

### 6.2 更新元数据

#### 方法

```java
MetaDataResponse updateMetaData(MetaDataUpdateRequest request);
```

#### 请求参数

MetaDataUpdateRequest

| 参数名        | 类型     | 是否必须 | 描述     |
|------------|--------|------|--------|
| datasetId  | String | 是    | 知识库 id |
| metaDataId | String | 是    | 元数据 id |
| name       | String | 是    | 元数据名称  |

#### 响应参数

MetaDataResponse 查看 6.1

### 6.3 删除元数据

#### 方法

```java
MetaDataDeleteResponse deleteMetaData(String datasetId, String metadataId);

MetaDataDeleteResponse deleteMetaData(String datasetId, String metadataId, String apiKey);
```

#### 请求参数

| 参数名        | 类型     | 是否必须 | 描述     |
|------------|--------|------|--------|
| datasetId  | String | 是    | 知识库 id |
| metadataId | String | 是    | 元数据 id |

#### 响应参数

无

### 6.4 元数据操作

#### 方法

```java
MetaDataActionResponse actionMetaData(MetaDataActionRequest request);
```

#### 请求参数

MetaDataActionRequest

| 参数名       | 类型                 | 是否必须 | 描述     |
|-----------|--------------------|------|--------|
| datasetId | String             | 是    | 知识库 id |
| action    | MetaDataActionEnum | 是    | 操作类型   |

**MetaDataActionEnum 枚举值**

| 枚举值     | 描述 |
|---------|----|
| enable  | 启用 |
| disable | 禁用 |

#### 响应参数

无

### 6.5 更新文档元数据

#### 方法

```java
DocumentMetaDataUpdateResponse updateDocumentMetaData(DocumentMetaDataUpdateRequest request);
```

#### 请求参数

DocumentMetaDataUpdateRequest

| 参数名           | 类型                     | 是否必须 | 描述     |
|---------------|------------------------|------|--------|
| datasetId     | String                 | 是    | 知识库 id |
| operationData | `List<OperationData> ` | 是    | 操作数据列表 |

**OperationData 对象结构**

| 参数名          | 类型                | 描述    |
|--------------|-------------------|-------|
| documentId   | String            | 文档ID  |
| metadataList | `List<MetaData> ` | 元数据列表 |

**MetaData 对象结构**

| 参数名   | 类型     | 描述    |
|-------|--------|-------|
| id    | String | 元数据ID |
| type  | String | 元数据类型 |
| name  | String | 元数据名称 |
| value | String | 元数据值  |

#### 响应参数

无

### 6.6 获取元数据列表

#### 方法

```java
MetaDataListResponse listMetaData(String datasetId);

MetaDataListResponse listMetaData(String datasetId, String apiKey);
```

#### 请求参数

| 参数名       | 类型     | 是否必须 | 描述     |
|-----------|--------|------|--------|
| datasetId | String | 是    | 知识库 id |

#### 响应参数

MetaDataListResponse

| 参数名                 | 类型                   | 描述       |
|---------------------|----------------------|----------|
| builtInFieldEnabled | Boolean              | 内置字段是否启用 |
| docMetadata         | `List<DocMetadata> ` | 文档元数据列表  |

**DocMetadata 对象结构**

| 参数名       | 类型      | 描述    |
|-----------|---------|-------|
| id        | String  | 元数据ID |
| type      | String  | 元数据类型 |
| name      | String  | 元数据名称 |
| userCount | Integer | 使用次数  |

## 7. 嵌入模型

### 7.1 获取嵌入模型列表

#### 方法

```java
TextEmbeddingListResponse listTextEmbedding();

TextEmbeddingListResponse listTextEmbedding(String apikey);
```

#### 请求参数

无

#### 响应参数

TextEmbeddingListResponse

| 参数名  | 类型                    | 描述     |
|------|-----------------------|--------|
| data | `List<TextEmbedding>` | 嵌入模型列表 |

**TextEmbedding 对象结构**

| 参数名       | 类型                         | 描述    |
|-----------|----------------------------|-------|
| provider  | String                     | 提供商名称 |
| label     | TextEmbeddingLabel         | 标签信息  |
| iconSmall | TextEmbeddingIcon          | 小图标   |
| iconLarge | TextEmbeddingIcon          | 大图标   |
| status    | String                     | 状态    |
| models    | `List<TextEmbeddingModel>` | 模型列表  |

**TextEmbeddingLabel 对象结构**

| 参数名    | 类型     | 描述       |
|--------|--------|----------|
| zhHans | String | 中文简体标签   |
| enUs   | String | 英文(美国)标签 |

**TextEmbeddingIcon 对象结构**

| 参数名    | 类型     | 描述         |
|--------|--------|------------|
| zhHans | String | 中文简体图标链接   |
| enUs   | String | 英文(美国)图标链接 |

**TextEmbeddingModel 对象结构**

| 参数名                  | 类型                           | 描述       |
|----------------------|------------------------------|----------|
| model                | String                       | 模型标识符    |
| label                | TextEmbeddingLabel           | 模型标签     |
| modelType            | String                       | 模型类型     |
| features             | `List<ModelFeatureEnum>`     | 特性列表     |
| fetchFrom            | String                       | 获取来源     |
| modelProperties      | TextEmbeddingModelProperties | 模型属性     |
| deprecated           | Boolean                      | 是否已弃用    |
| status               | ModelStatusEnum              | 状态       |
| loadBalancingEnabled | Boolean                      | 是否启用负载均衡 |

**TextEmbeddingModelProperties 对象结构**

| 参数名         | 类型      | 描述    |
|-------------|---------|-------|
| contextSize | Integer | 上下文大小 |
| maxChunks   | Integer | 最大块数量 |

**ModelStatusEnum 枚举值**

| 枚举值            | 代码值            | 描述   |
|----------------|----------------|------|
| ACTIVE         | active         | 激活   |
| NO_CONFIGURE   | no-configure   | 未配置  |
| QUOTA_EXCEEDED | quota-exceeded | 配额超出 |
| NO_PERMISSION  | no-permission  | 无权限  |
| DISABLED       | disabled       | 已禁用  |

**ModelFeatureEnum 枚举值**

| 枚举值              | 代码值              | 描述     |
|------------------|------------------|--------|
| TOOL_CALL        | tool-call        | 工具调用   |
| MULTI_TOOL_CALL  | multi-tool-call  | 多工具调用  |
| AGENT_THOUGHT    | agent-thought    | 代理思考   |
| VISION           | vision           | 视觉     |
| STREAM_TOOL_CALL | stream-tool-call | 流式工具调用 |
| DOCUMENT         | document         | 文档     |
| VIDEO            | video            | 视频     |
| AUDIO            | audio            | 音频     |
