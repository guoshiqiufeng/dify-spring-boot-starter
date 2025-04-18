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
package io.github.guoshiqiufeng.dify.dataset.client;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/8 13:46
 */
public interface DifyDatasetClient {


    DatasetResponse create(DatasetCreateRequest request);


    DifyPageResult<DatasetResponse> page(DatasetPageRequest request);


    void delete(String datasetId, String apikey);


    DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request);


    DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request);


    DocumentCreateResponse updateDocumentByText(DocumentUpdateByTextRequest request);


    DocumentCreateResponse updateDocumentByFile(DocumentUpdateByFileRequest request);


    DifyPageResult<DocumentInfo> pageDocument(DatasetPageDocumentRequest request);


    DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request);


    DocumentDeleteResponse deleteDocument(String datasetId, String documentId, String apikey);


    SegmentResponse createSegment(SegmentCreateRequest request);


    SegmentResponse pageSegment(SegmentPageRequest request);


    SegmentDeleteResponse deleteSegment(String datasetId, String documentId, String segmentId, String apikey);


    SegmentUpdateResponse updateSegment(SegmentUpdateRequest request);

    SegmentChildChunkCreateResponse createSegmentChildChunk(SegmentChildChunkCreateRequest request);

    DifyPageResult<SegmentChildChunkResponse> pageSegmentChildChunk(SegmentChildChunkPageRequest request);

    SegmentChildChunkDeleteResponse deleteSegmentChildChunk(SegmentChildChunkDeleteRequest request);

    SegmentChildChunkUpdateResponse updateSegmentChildChunk(SegmentChildChunkUpdateRequest request);


    UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId, String apikey);


    RetrieveResponse retrieve(RetrieveRequest request);


    MetaDataResponse createMetaData(MetaDataCreateRequest request);


    MetaDataResponse updateMetaData(MetaDataUpdateRequest request);


    void deleteMetaData(String datasetId, String metadataId, String apikey);


    void actionMetaData(MetaDataActionRequest request);


    void updateDocumentMetaData(DocumentMetaDataUpdateRequest request);


    MetaDataListResponse listMetaData(String datasetId, String apikey);

    TextEmbeddingListResponse listTextEmbedding(String apikey);

}
