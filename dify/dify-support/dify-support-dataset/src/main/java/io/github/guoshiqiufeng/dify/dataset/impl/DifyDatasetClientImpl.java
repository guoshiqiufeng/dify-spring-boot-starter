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

import java.util.List;

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
    public DatasetInfoResponse info(DatasetInfoRequest request) {
        return difyDatasetClient.info(request);
    }

    @Override
    public DatasetInfoResponse update(DatasetUpdateRequest request) {
        return difyDatasetClient.update(request);
    }

    @Override
    public void delete(String datasetId, String apiKey) {
        difyDatasetClient.delete(datasetId, apiKey);
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
    public void deleteDocument(String datasetId, String documentId, String apiKey) {
        difyDatasetClient.deleteDocument(datasetId, documentId, apiKey);
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
    public void deleteSegment(String datasetId, String documentId, String segmentId, String apiKey) {
        difyDatasetClient.deleteSegment(datasetId, documentId, segmentId, apiKey);
    }

    @Override
    public SegmentUpdateResponse updateSegment(SegmentUpdateRequest request) {
        return difyDatasetClient.updateSegment(request);
    }

    @Override
    public SegmentChildChunkCreateResponse createSegmentChildChunk(SegmentChildChunkCreateRequest request) {
        return difyDatasetClient.createSegmentChildChunk(request);
    }

    @Override
    public DifyPageResult<SegmentChildChunkResponse> pageSegmentChildChunk(SegmentChildChunkPageRequest request) {
        return difyDatasetClient.pageSegmentChildChunk(request);
    }

    @Override
    public void deleteSegmentChildChunk(SegmentChildChunkDeleteRequest request) {
        difyDatasetClient.deleteSegmentChildChunk(request);
    }

    @Override
    public SegmentChildChunkUpdateResponse updateSegmentChildChunk(SegmentChildChunkUpdateRequest request) {
        return difyDatasetClient.updateSegmentChildChunk(request);
    }

    @Override
    public UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId, String apiKey) {
        return difyDatasetClient.uploadFileInfo(datasetId, documentId, apiKey);
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
    public void deleteMetaData(String datasetId, String metadataId, String apiKey) {
        difyDatasetClient.deleteMetaData(datasetId, metadataId, apiKey);
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
    public MetaDataListResponse listMetaData(String datasetId, String apiKey) {
        return difyDatasetClient.listMetaData(datasetId, apiKey);
    }

    @Override
    public TextEmbeddingListResponse listTextEmbedding(String apiKey) {
        return difyDatasetClient.listTextEmbedding(apiKey);
    }

    @Override
    public TextEmbeddingListResponse listRerank(String apiKey) {
        return difyDatasetClient.listRerank(apiKey);
    }

    @Override
    public TagInfoResponse createTag(TagCreateRequest request) {
        return difyDatasetClient.createTag(request);
    }

    @Override
    public List<TagInfoResponse> listTag(String apiKey) {
        return difyDatasetClient.listTag(apiKey);
    }

    @Override
    public TagInfoResponse updateTag(TagUpdateRequest request) {
        return difyDatasetClient.updateTag(request);
    }

    @Override
    public void deleteTag(String tagId, String apiKey) {
        difyDatasetClient.deleteTag(tagId, apiKey);
    }

    @Override
    public void bindingTag(TagBindingRequest request) {
        difyDatasetClient.bindingTag(request);
    }

    @Override
    public void unbindingTag(TagUnbindingRequest request) {
        difyDatasetClient.unbindingTag(request);
    }

    @Override
    public DataSetTagsResponse listDatasetTag(String datasetId, String apiKey) {
        return difyDatasetClient.listDatasetTag(datasetId, apiKey);
    }
}
