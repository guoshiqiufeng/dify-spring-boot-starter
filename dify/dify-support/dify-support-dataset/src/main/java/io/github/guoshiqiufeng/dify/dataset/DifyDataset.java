/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.dify.dataset;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 11:33
 */
public interface DifyDataset {

    /**
     * 创建 知识库。
     *
     * @param request 知识库创建请求对象，包含创建 知识库所需的信息。
     * @return 返回创建的 知识库响应对象。
     */
    DatasetResponse create(DatasetCreateRequest request);

    /**
     * 分页查询 知识库列表。
     *
     * @param request 分页查询请求对象，包含分页参数和查询条件。
     * @return 返回分页查询结果，包含 知识库信息列表和分页信息。
     */
    DifyPageResult<DatasetResponse> page(DatasetPageRequest request);

    /**
     * 删除指定 知识库。
     *
     * @param datasetId 知识库的唯一标识符。
     */
    default void delete(String datasetId) {
        delete(datasetId, null);
    }

    /**
     * 删除指定 知识库。
     *
     * @param datasetId 知识库的唯一标识符。
     * @param apiKey    知识库的唯一标识符。
     */
    void delete(String datasetId, String apiKey);

    /**
     * 通过文本创建文档。
     *
     * @param request 文档创建请求对象，包含通过文本创建文档所需的信息。
     * @return 返回文档创建响应对象。
     */
    DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request);

    /**
     * 通过文件创建文档。
     *
     * @param request 文档创建请求对象，包含通过文件创建文档所需的信息。
     * @return 返回文档创建响应对象。
     */
    DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request);

    /**
     * 通过文本更新文档。
     *
     * @param request 文档更新请求对象，包含通过文本更新文档所需的信息。
     * @return 返回文档更新响应对象。
     */
    DocumentCreateResponse updateDocumentByText(DocumentUpdateByTextRequest request);

    /**
     * 通过文件更新文档。
     *
     * @param request 文档更新请求对象，包含通过文件更新文档所需的信息。
     * @return 返回文档更新响应对象。
     */
    DocumentCreateResponse updateDocumentByFile(DocumentUpdateByFileRequest request);

    /**
     * 分页查询文档列表。
     *
     * @param request 分页查询请求对象，包含分页参数和查询条件。
     * @return 返回分页查询结果，包含文档信息列表和分页信息。
     */
    DifyPageResult<DocumentInfo> pageDocument(DatasetPageDocumentRequest request);

    /**
     * 查询文档索引状态。
     *
     * @param request 索引状态查询请求对象，包含查询所需的信息。
     * @return 返回文档索引状态响应对象。
     */
    DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request);

    /**
     * 删除指定文档。
     *
     * @param datasetId  知识库的唯一标识符。
     * @param documentId 文档的唯一标识符。
     * @return 返回文档删除响应对象。
     */
    default DocumentDeleteResponse deleteDocument(String datasetId, String documentId) {
        return deleteDocument(datasetId, documentId, null);
    }

    /**
     * 删除指定文档。
     *
     * @param datasetId  知识库的唯一标识符。
     * @param documentId 文档的唯一标识符。
     * @param apiKey     apiKey
     * @return 返回文档删除响应对象。
     */
    DocumentDeleteResponse deleteDocument(String datasetId, String documentId, String apiKey);

    /**
     * 创建分段。
     *
     * @param request 分段创建请求对象，包含创建分段所需的信息。
     * @return 返回分段创建响应对象。
     */
    SegmentResponse createSegment(SegmentCreateRequest request);

    /**
     * 分页查询分段列表。
     *
     * @param request 分页查询请求对象，包含分页参数和查询条件。
     * @return 返回分段分页查询响应对象。
     */
    SegmentResponse pageSegment(SegmentPageRequest request);

    /**
     * 删除指定分段。
     *
     * @param datasetId  知识库的唯一标识符。
     * @param documentId 文档的唯一标识符。
     * @param segmentId  分段的唯一标识符。
     * @return 返回分段删除响应对象。
     */
    default SegmentDeleteResponse deleteSegment(String datasetId, String documentId, String segmentId) {
        return deleteSegment(datasetId, documentId, segmentId, null);
    }

    /**
     * 删除指定分段。
     *
     * @param datasetId  知识库的唯一标识符。
     * @param documentId 文档的唯一标识符。
     * @param segmentId  分段的唯一标识符。
     * @param apiKey     apiKey
     * @return 返回分段删除响应对象。
     */
    SegmentDeleteResponse deleteSegment(String datasetId, String documentId, String segmentId, String apiKey);

    /**
     * 更新分段。
     *
     * @param request 分段更新请求对象，包含更新分段所需的信息。
     * @return 返回分段更新响应对象。
     */
    SegmentUpdateResponse updateSegment(SegmentUpdateRequest request);

    /**
     * 创建子分段
     *
     * @param request 子分段创建请求对象，包含创建分段所需的信息。
     * @return 返回子分段响应对象
     */
    SegmentChildChunkCreateResponse createSegmentChildChunk(SegmentChildChunkCreateRequest request);

    /**
     * 分页查询子分段列表
     *
     * @param request 子分段分页查询请求对象，包含分页参数和查询条件。
     * @return 返回子分段分页查询响应对象
     */
    DifyPageResult<SegmentChildChunkResponse> pageSegmentChildChunk(SegmentChildChunkPageRequest request);

    /**
     * 删除指定子分段
     *
     * @param request 子分段删除请求对象
     * @return 返回子分段删除响应对象
     */
    SegmentChildChunkDeleteResponse deleteSegmentChildChunk(SegmentChildChunkDeleteRequest request);

    /**
     * 更新子分段
     *
     * @param request 子分段更新请求对象，包含更新子分段所需的信息。
     * @return 返回子分段更新响应对象
     */
    SegmentChildChunkUpdateResponse updateSegmentChildChunk(SegmentChildChunkUpdateRequest request);

    /**
     * 获取上传文件信息。
     *
     * @param datasetId  知识库的唯一标识符。
     * @param documentId 文档的唯一标识符。
     * @return 返回上传文件信息响应对象。
     */
    default UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId) {
        return uploadFileInfo(datasetId, documentId, null);
    }

    /**
     * 获取上传文件信息。
     *
     * @param datasetId  知识库的唯一标识符。
     * @param documentId 文档的唯一标识符。
     * @param apiKey     apiKey
     * @return 返回上传文件信息响应对象。
     */
    UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId, String apiKey);

    /**
     * 检索数据。
     *
     * @param request 检索请求对象，包含检索所需的信息。
     * @return 返回检索响应对象。
     */
    RetrieveResponse retrieve(RetrieveRequest request);

    /**
     * 创建元数据
     *
     * @param request 元数据创建请求参数（需包含数据集ID和元数据内容）
     * @return 创建成功的元数据详情
     */
    MetaDataResponse createMetaData(MetaDataCreateRequest request);

    /**
     * 更新元数据
     *
     * @param request 元数据更新请求参数（需包含元数据ID和更新内容）
     * @return 更新后的元数据详情
     * @throws IllegalArgumentException 参数校验异常
     */
    MetaDataResponse updateMetaData(MetaDataUpdateRequest request);

    /**
     * 删除元数据
     *
     * @param datasetId  数据集ID
     * @param metadataId 元数据ID
     */
    default void deleteMetaData(String datasetId, String metadataId) {
        deleteMetaData(datasetId, metadataId, null);
    }

    /**
     * 删除元数据
     *
     * @param datasetId  数据集ID
     * @param metadataId 元数据ID
     * @param apiKey     apiKey
     */
    void deleteMetaData(String datasetId, String metadataId, String apiKey);

    /**
     * 执行元数据业务操作（如启用/禁用等）
     *
     * @param request 操作请求参数（需包含操作类型和目标元数据ID）
     */
    void actionMetaData(MetaDataActionRequest request);

    /**
     * 更新文档关联的元数据
     *
     * @param request 文档元数据更新请求（需包含文档ID和元数据变更内容）
     */
    void updateDocumentMetaData(DocumentMetaDataUpdateRequest request);

    /**
     * 获取数据集元数据列表
     *
     * @param datasetId 数据集ID
     * @return 元数据列表及分页信息
     */
    default MetaDataListResponse listMetaData(String datasetId) {
        return listMetaData(datasetId, null);
    }

    /**
     * 获取数据集元数据列表
     *
     * @param datasetId 数据集ID
     * @param apiKey    apiKey
     * @return 元数据列表及分页信息
     */
    MetaDataListResponse listMetaData(String datasetId, String apiKey);

    /**
     * 获取嵌入模型列表
     *
     * @return 嵌入模型列表
     */
    default TextEmbeddingListResponse listTextEmbedding() {
        return listTextEmbedding(null);
    }

    /**
     * 获取嵌入模型列表
     *
     * @param apiKey apiKey
     * @return 嵌入模型列表
     */
    TextEmbeddingListResponse listTextEmbedding(String apiKey);

}
