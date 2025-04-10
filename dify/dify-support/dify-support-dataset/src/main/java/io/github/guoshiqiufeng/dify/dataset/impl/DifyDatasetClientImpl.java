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
package io.github.guoshiqiufeng.dify.dataset.impl;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/8 14:44
 */
@Slf4j
public class DifyDatasetClientImpl implements DifyDataset {

    private final DifyDatasetClient difyDatasetClient;

    public DifyDatasetClientImpl(DifyDatasetClient difyDatasetClient) {
        this.difyDatasetClient = difyDatasetClient;
    }

    @Override
    public DatasetResponse create(DatasetCreateRequest request) {
        return difyDatasetClient.create(request);
    }

    @Override
    public DifyPageResult<DatasetResponse> page(DatasetPageRequest request) {
        return difyDatasetClient.page(request);
    }

    @Override
    public void delete(String datasetId) {
        difyDatasetClient.delete(datasetId);
    }

    @Override
    public DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request) {
        return difyDatasetClient.createDocumentByText(request);
    }

    @Override
    public DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request) {
        return difyDatasetClient.createDocumentByFile(request);
    }

    @Override
    public DocumentCreateResponse updateDocumentByText(DocumentUpdateByTextRequest request) {
        return difyDatasetClient.updateDocumentByText(request);
    }

    @Override
    public DocumentCreateResponse updateDocumentByFile(DocumentUpdateByFileRequest request) {
        return difyDatasetClient.updateDocumentByFile(request);
    }

    @Override
    public DifyPageResult<DocumentInfo> pageDocument(DatasetPageDocumentRequest request) {
        return difyDatasetClient.pageDocument(request);
    }

    @Override
    public DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request) {
        return difyDatasetClient.indexingStatus(request);
    }

    @Override
    public DocumentDeleteResponse deleteDocument(String datasetId, String documentId) {
        return difyDatasetClient.deleteDocument(datasetId, documentId);
    }

    @Override
    public SegmentResponse createSegment(SegmentCreateRequest request) {
        return difyDatasetClient.createSegment(request);
    }

    @Override
    public SegmentResponse pageSegment(SegmentPageRequest request) {
        return difyDatasetClient.pageSegment(request);
    }

    @Override
    public SegmentDeleteResponse deleteSegment(String datasetId, String documentId, String segmentId) {
        return difyDatasetClient.deleteSegment(datasetId, documentId, segmentId);
    }

    @Override
    public SegmentUpdateResponse updateSegment(SegmentUpdateRequest request) {
        return difyDatasetClient.updateSegment(request);
    }

    @Override
    public UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId) {
        return difyDatasetClient.uploadFileInfo(datasetId, documentId);
    }

    @Override
    public RetrieveResponse retrieve(RetrieveRequest request) {
        return difyDatasetClient.retrieve(request);
    }

    @Override
    public MetaDataResponse createMetaData(MetaDataCreateRequest request) {
        return difyDatasetClient.createMetaData(request);
    }

    @Override
    public MetaDataResponse updateMetaData(MetaDataUpdateRequest request) {
        return difyDatasetClient.updateMetaData(request);
    }

    @Override
    public void deleteMetaData(String datasetId, String metadataId) {
        difyDatasetClient.deleteMetaData(datasetId, metadataId);
    }

    @Override
    public void actionMetaData(MetaDataActionRequest request) {
        difyDatasetClient.actionMetaData(request);
    }

    @Override
    public void updateDocumentMetaData(DocumentMetaDataUpdateRequest request) {
        difyDatasetClient.updateDocumentMetaData(request);
    }

    @Override
    public MetaDataListResponse listMetaData(String datasetId) {
        return difyDatasetClient.listMetaData(datasetId);
    }

    @Override
    public TextEmbeddingListResponse listTextEmbedding() {
        return difyDatasetClient.listTextEmbedding();
    }
}
