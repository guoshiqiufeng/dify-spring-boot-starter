/*
 * Copyright (c) 2025-2026, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.support.impl.dataset;

import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.http.HttpMethod;
import io.github.guoshiqiufeng.dify.client.core.http.MediaType;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.utils.Assert;
import io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocActionEnum;
import io.github.guoshiqiufeng.dify.support.impl.base.BaseDifyDefaultClient;
import io.github.guoshiqiufeng.dify.support.impl.dto.dataset.SegmentDataResponseDto;
import io.github.guoshiqiufeng.dify.support.impl.utils.DatasetHeaderUtils;
import io.github.guoshiqiufeng.dify.support.impl.utils.MultipartBodyUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30 13:43
 */
@Slf4j
public class DifyDatasetDefaultClient extends BaseDifyDefaultClient implements DifyDatasetClient {

    public DifyDatasetDefaultClient(HttpClient httpClient) {
        super(httpClient);
    }

    public DifyDatasetDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, HttpClientFactory httpClientFactory) {
        super(baseUrl, clientConfig, httpClientFactory);
    }


    @Override
    public DatasetResponse create(DatasetCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.post()
                .uri(DatasetUriConstant.V1_DATASETS_URL)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DatasetResponse.class);
    }


    @Override
    public DifyPageResult<DatasetResponse> page(DatasetPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.get()
                .uri(uri ->
                        uri.path(DatasetUriConstant.V1_DATASETS_URL)
                                .queryParam("page", request.getPage())
                                .queryParam("limit", request.getLimit())
                                .queryParamIfPresent("tag_ids", Optional.ofNullable(request.getTagIds()).filter(m -> !m.isEmpty()))
                                .queryParamIfPresent("keyword", Optional.ofNullable(request.getKeyword()).filter(m -> !m.isEmpty()))
                                .queryParamIfPresent("include_all", Optional.ofNullable(request.getIncludeAll()))
                                .build()
                )
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new TypeReference<DifyPageResult<DatasetResponse>>() {
                });
    }

    @Override
    public DatasetInfoResponse info(DatasetInfoRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.get()
                .uri(uri ->
                        uri.path(DatasetUriConstant.V1_DATASET_URL)
                                .build(request.getDatasetId())
                )
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DatasetInfoResponse.class);
    }

    @Override
    public DatasetInfoResponse update(DatasetUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.patch()
                .uri(uri ->
                        uri.path(DatasetUriConstant.V1_DATASET_URL)
                                .build(request.getDatasetId())
                )
                .body(request)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DatasetInfoResponse.class);
    }


    @Override
    public void delete(String datasetId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        httpClient.delete()
                .uri(DatasetUriConstant.V1_DATASETS_URL + "/{datasetId}", datasetId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(Void.class);
    }


    @Override
    public DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_CREATE_BY_TEXT_URL, request.getDatasetId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DocumentCreateResponse.class);
    }


    @Override
    public DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(request.getFile(), request);
        return httpClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_CREATE_BY_FILE_URL, request.getDatasetId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DocumentCreateResponse.class);
    }


    @Override
    public DocumentCreateResponse updateDocumentByText(DocumentUpdateByTextRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);

        return httpClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_UPDATE_BY_TEXT_URL, request.getDatasetId(), request.getDocumentId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DocumentCreateResponse.class);
    }


    @Override
    public DocumentCreateResponse updateDocumentByFile(DocumentUpdateByFileRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(request.getFile(), request);

        // 使用 httpClient 发送 POST 请求
        return httpClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_UPDATE_BY_FILE_URL, request.getDatasetId(), request.getDocumentId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(builder.build())
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DocumentCreateResponse.class);
    }


    @Override
    public DifyPageResult<DocumentInfo> pageDocument(DatasetPageDocumentRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DatasetUriConstant.V1_DOCUMENTS_URL)
                        .queryParamIfPresent("page", Optional.ofNullable(request.getPage()))
                        .queryParamIfPresent("limit", Optional.ofNullable(request.getLimit()))
                        .queryParamIfPresent("keyword", Optional.ofNullable(request.getKeyword()))
                        .build(request.getDatasetId()))
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new TypeReference<DifyPageResult<DocumentInfo>>() {
                });
    }

    @Override
    public DocumentInfo getDocument(String datasetId, String documentId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        return httpClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENT_URL, datasetId, documentId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DocumentInfo.class);
    }

    @Override
    public DocumentInfo getDocument(String datasetId, String documentId, String metadata, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        return httpClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DatasetUriConstant.V1_DOCUMENT_URL)
                        .queryParamIfPresent("metadata", Optional.ofNullable(metadata))
                        .build(datasetId, documentId))
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DocumentInfo.class);
    }


    @Override
    public DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENT_INDEXING_STATUS_URL, request.getDatasetId(), request.getBatch())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new TypeReference<DocumentIndexingStatusResponse>() {
                });
    }


    @Override
    public void deleteDocument(String datasetId, String documentId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        httpClient.delete()
                .uri(DatasetUriConstant.V1_DOCUMENT_URL, datasetId, documentId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(Void.class);
    }


    @Override
    public SegmentResponse createSegment(SegmentCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_URL, request.getDatasetId(), request.getDocumentId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(SegmentResponse.class);
    }


    @Override
    public SegmentResponse pageSegment(SegmentPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_URL)
                        .queryParamIfPresent("keyword", Optional.ofNullable(request.getKeyword()).filter(m -> !m.isEmpty()))
                        .queryParamIfPresent("status", Optional.ofNullable(request.getStatus()).filter(m -> !m.isEmpty()))
                        .queryParam("page", request.getPage())
                        .queryParam("limit", request.getLimit())
                        .build(request.getDatasetId(), request.getDocumentId()))
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new TypeReference<SegmentResponse>() {
                });
    }


    @Override
    public void deleteSegment(String datasetId, String documentId, String segmentId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        Assert.notNull(segmentId, "segmentId can not be null");

        httpClient.delete()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL, datasetId, documentId, segmentId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(Void.class);
    }


    @Override
    public SegmentUpdateResponse updateSegment(SegmentUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL, request.getDatasetId(), request.getDocumentId(), request.getSegmentId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(SegmentUpdateResponse.class);
    }

    @Override
    public SegmentData getSegment(String datasetId, String documentId, String segmentId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        Assert.notNull(segmentId, "segmentId can not be null");
        SegmentDataResponseDto body = httpClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL, datasetId, documentId, segmentId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(SegmentDataResponseDto.class);
        if (body == null) {
            return null;
        }
        return body.getData();
    }

    @Override
    public SegmentChildChunkCreateResponse createSegmentChildChunk(SegmentChildChunkCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getContent(), "content can not be null");
        return httpClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNKS_URL, request.getDatasetId(), request.getDocumentId(), request.getSegmentId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(SegmentChildChunkCreateResponse.class);
    }

    @Override
    public DifyPageResult<SegmentChildChunkResponse> pageSegmentChildChunk(SegmentChildChunkPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNKS_URL)
                        .queryParamIfPresent("keyword", Optional.ofNullable(request.getKeyword()))
                        .queryParam("page", request.getPage())
                        .queryParam("limit", request.getLimit())
                        .build(request.getDatasetId(), request.getDocumentId(), request.getSegmentId()))
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new TypeReference<DifyPageResult<SegmentChildChunkResponse>>() {
                });
    }

    @Override
    public void deleteSegmentChildChunk(SegmentChildChunkDeleteRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getChildChunkId(), "childChunkId can not be null");
        httpClient.delete()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNK_URL,
                        request.getDatasetId(), request.getDocumentId(), request.getSegmentId(), request.getChildChunkId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(Void.class);
    }

    @Override
    public SegmentChildChunkUpdateResponse updateSegmentChildChunk(SegmentChildChunkUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getChildChunkId(), "childChunkId can not be null");
        return httpClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNK_URL)
                        .build(request.getDatasetId(), request.getDocumentId(), request.getSegmentId(), request.getChildChunkId()))
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(SegmentChildChunkUpdateResponse.class);
    }


    @Override
    public UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");

        return httpClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENTS_UPLOAD_FILE, datasetId, documentId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new TypeReference<UploadFileInfoResponse>() {
                });
    }


    @Override
    public RetrieveResponse retrieve(RetrieveRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.post()
                .uri(DatasetUriConstant.V1_DATASETS_RETRIEVE_URL, request.getDatasetId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(RetrieveResponse.class);
    }


    @Override
    public MetaDataResponse createMetaData(MetaDataCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.post()
                .uri(DatasetUriConstant.V1_METADATA_CREATE_URL, request.getDatasetId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(MetaDataResponse.class);
    }


    @Override
    public MetaDataResponse updateMetaData(MetaDataUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.patch()
                .uri(DatasetUriConstant.V1_METADATA_UPDATE_URL, request.getDatasetId(), request.getMetaDataId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(MetaDataResponse.class);
    }


    @Override
    public void deleteMetaData(String datasetId, String metadataId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(metadataId, "metadataId can not be null");

        httpClient.delete()
                .uri(DatasetUriConstant.V1_METADATA_DELETE_URL, datasetId, metadataId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(void.class);
    }


    @Override
    public void actionMetaData(MetaDataActionRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        httpClient.post()
                .uri(DatasetUriConstant.V1_METADATA_ACTION_URL, request.getDatasetId(), request.getAction().name())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(Void.class);
    }


    @Override
    public void updateDocumentMetaData(DocumentMetaDataUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        // 使用 httpClient 发送 POST 请求
        httpClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_METADATA_UPDATE_URL, request.getDatasetId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(Void.class);
    }


    @Override
    public MetaDataListResponse listMetaData(String datasetId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        return httpClient.get()
                .uri(DatasetUriConstant.V1_METADATA_LIST_URL, datasetId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(MetaDataListResponse.class);
    }

    @Override
    public TextEmbeddingListResponse listTextEmbedding(String apiKey) {
        return httpClient.get()
                .uri(DatasetUriConstant.V1_TEXT_EMBEDDING_LIST_URL)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(TextEmbeddingListResponse.class);
    }

    @Override
    public TextEmbeddingListResponse listRerank(String apiKey) {
        return httpClient.get()
                .uri(DatasetUriConstant.V1_RERANK_LIST_URL)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(TextEmbeddingListResponse.class);
    }

    @Override
    public TagInfoResponse createTag(TagCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.post()
                .uri(DatasetUriConstant.V1_TAGS)
                .body(request)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(TagInfoResponse.class);
    }

    @Override
    public List<TagInfoResponse> listTag(String apiKey) {
        return httpClient.get()
                .uri(DatasetUriConstant.V1_TAGS)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new TypeReference<List<TagInfoResponse>>() {
                });
    }

    @Override
    public TagInfoResponse updateTag(TagUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return httpClient.patch()
                .uri(DatasetUriConstant.V1_TAGS)
                .body(request)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(TagInfoResponse.class);
    }

    @Override
    public void deleteTag(String tagId, String apiKey) {
        // Validate input parameters
        if (tagId == null || tagId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag ID must not be null or empty");
        }
        Map<String, String> param = new HashMap<>(1);
        param.put("tag_id", tagId);
        httpClient.method(HttpMethod.DELETE)
                .uri(DatasetUriConstant.V1_TAGS)
                .body(param)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(Void.class);
    }

    @Override
    public void bindingTag(TagBindingRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        httpClient.post()
                .uri(DatasetUriConstant.V1_TAGS_BINDING)
                .body(request)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(Void.class);
    }

    @Override
    public void unbindingTag(TagUnbindingRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        httpClient.post()
                .uri(DatasetUriConstant.V1_TAGS_UNBINDING)
                .body(request)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(Void.class);
    }

    @Override
    public DataSetTagsResponse listDatasetTag(String datasetId, String apiKey) {
        return httpClient.get()
                .uri(DatasetUriConstant.V1_DATASET_TAGS, datasetId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DataSetTagsResponse.class);
    }

    @Override
    public DatasetStatusResponse changeDocumentStatus(String datasetId, Set<String> documentIds, DocActionEnum status, String apiKey) {
        Map<String, Set<String>> param = new HashMap<>(1);
        param.put("document_ids", documentIds);
        return httpClient.patch()
                .uri(DatasetUriConstant.V1_DOCUMENT_STATUS, datasetId, status.name())
                .body(param)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DatasetStatusResponse.class);
    }
}
