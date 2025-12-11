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
package io.github.guoshiqiufeng.dify.client.spring5.dataset;

import io.github.guoshiqiufeng.dify.client.spring5.base.BaseDifyDefaultClient;
import io.github.guoshiqiufeng.dify.client.spring5.dto.dataset.SegmentDataResponse;
import io.github.guoshiqiufeng.dify.client.spring5.utils.DatasetHeaderUtils;
import io.github.guoshiqiufeng.dify.client.spring5.utils.WebClientUtil;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;
import io.github.guoshiqiufeng.dify.dataset.enums.document.DocActionEnum;
import io.github.guoshiqiufeng.dify.dataset.utils.MultipartBodyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/8 13:46
 */
@Slf4j
public class DifyDatasetDefaultClient extends BaseDifyDefaultClient implements DifyDatasetClient {

    public DifyDatasetDefaultClient() {
        super();
    }

    public DifyDatasetDefaultClient(String baseUrl) {
        super(baseUrl);
    }

    public DifyDatasetDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, WebClient.Builder webClientBuilder) {
        super(baseUrl, clientConfig, webClientBuilder);
    }


    @Override
    public DatasetResponse create(DatasetCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_DATASETS_URL)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DatasetResponse.class).block();
    }


    @Override
    public DifyPageResult<DatasetResponse> page(DatasetPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
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
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<DatasetResponse>>() {
                }).block();
    }

    @Override
    public DatasetInfoResponse info(DatasetInfoRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
                .uri(uri ->
                        uri.path(DatasetUriConstant.V1_DATASET_URL)
                                .build(request.getDatasetId())
                )
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DatasetInfoResponse.class).block();
    }

    @Override
    public DatasetInfoResponse update(DatasetUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.patch()
                .uri(uri ->
                        uri.path(DatasetUriConstant.V1_DATASET_URL)
                                .build(request.getDatasetId())
                )
                .bodyValue(request)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DatasetInfoResponse.class).block();
    }


    @Override
    public void delete(String datasetId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        webClient.delete()
                .uri(DatasetUriConstant.V1_DATASETS_URL + "/{datasetId}", datasetId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class).block();
    }


    @Override
    public DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_CREATE_BY_TEXT_URL, request.getDatasetId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentCreateResponse.class).block();
    }


    @Override
    public DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(request.getFile(), request);
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_CREATE_BY_FILE_URL, request.getDatasetId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentCreateResponse.class).block();
    }


    @Override
    public DocumentCreateResponse updateDocumentByText(DocumentUpdateByTextRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);

        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_UPDATE_BY_TEXT_URL, request.getDatasetId(), request.getDocumentId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentCreateResponse.class).block();
    }


    @Override
    public DocumentCreateResponse updateDocumentByFile(DocumentUpdateByFileRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(request.getFile(), request);

        // 使用 webClient 发送 POST 请求
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_UPDATE_BY_FILE_URL, request.getDatasetId(), request.getDocumentId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(builder.build())
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentCreateResponse.class).block();
    }


    @Override
    public DifyPageResult<DocumentInfo> pageDocument(DatasetPageDocumentRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENTS_URL + "?page={page}&limit={limit}&keyword={keyword}",
                        request.getDatasetId(), request.getPage(), request.getLimit(), request.getKeyword())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<DocumentInfo>>() {
                }).block();
    }

    @Override
    public DocumentInfo getDocument(String datasetId, String documentId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        return webClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENT_URL, datasetId, documentId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentInfo.class).block();
    }

    @Override
    public DocumentInfo getDocument(String datasetId, String documentId, String metadata, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DatasetUriConstant.V1_DOCUMENT_URL)
                        .queryParamIfPresent("metadata", Optional.ofNullable(metadata))
                        .build(datasetId, documentId))
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentInfo.class).block();
    }


    @Override
    public DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENT_INDEXING_STATUS_URL, request.getDatasetId(), request.getBatch())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DocumentIndexingStatusResponse>() {
                }).block();
    }


    @Override
    public void deleteDocument(String datasetId, String documentId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        webClient.delete()
                .uri(DatasetUriConstant.V1_DOCUMENT_URL, datasetId, documentId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class)
                .block();
    }


    @Override
    public SegmentResponse createSegment(SegmentCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_URL, request.getDatasetId(), request.getDocumentId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentResponse.class).block();
    }


    @Override
    public SegmentResponse pageSegment(SegmentPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_URL)
                        .queryParamIfPresent("keyword", Optional.ofNullable(request.getKeyword()).filter(m -> !m.isEmpty()))
                        .queryParamIfPresent("status", Optional.ofNullable(request.getStatus()).filter(m -> !m.isEmpty()))
                        .queryParam("page", request.getPage())
                        .queryParam("limit", request.getLimit())
                        .build(request.getDatasetId(), request.getDocumentId()))
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<SegmentResponse>() {
                }).block();
    }


    @Override
    public void deleteSegment(String datasetId, String documentId, String segmentId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        Assert.notNull(segmentId, "segmentId can not be null");

        webClient.delete()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL, datasetId, documentId, segmentId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class)
                .block();
    }


    @Override
    public SegmentUpdateResponse updateSegment(SegmentUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL, request.getDatasetId(), request.getDocumentId(), request.getSegmentId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentUpdateResponse.class).block();
    }

    @Override
    public SegmentData getSegment(String datasetId, String documentId, String segmentId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        Assert.notNull(segmentId, "segmentId can not be null");
        SegmentDataResponse body = webClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL, datasetId, documentId, segmentId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentDataResponse.class).block();
        if (body == null) {
            return null;
        }
        return body.getData();
    }

    @Override
    public SegmentChildChunkCreateResponse createSegmentChildChunk(SegmentChildChunkCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getContent(), "content can not be null");
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNKS_URL, request.getDatasetId(), request.getDocumentId(), request.getSegmentId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentChildChunkCreateResponse.class).block();
    }

    @Override
    public DifyPageResult<SegmentChildChunkResponse> pageSegmentChildChunk(SegmentChildChunkPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNKS_URL + "?keyword={keyword}&page={page}&limit={limit}",
                        request.getDatasetId(), request.getDocumentId(), request.getSegmentId(),
                        request.getKeyword(), request.getPage(), request.getLimit())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<SegmentChildChunkResponse>>() {
                }).block();
    }

    @Override
    public void deleteSegmentChildChunk(SegmentChildChunkDeleteRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getChildChunkId(), "childChunkId can not be null");
        webClient.delete()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNK_URL,
                        request.getDatasetId(), request.getDocumentId(), request.getSegmentId(), request.getChildChunkId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public SegmentChildChunkUpdateResponse updateSegmentChildChunk(SegmentChildChunkUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getChildChunkId(), "childChunkId can not be null");
        return webClient.patch()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNK_URL,
                        request.getDatasetId(), request.getDocumentId(), request.getSegmentId(), request.getChildChunkId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentChildChunkUpdateResponse.class).block();
    }


    @Override
    public UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");

        return webClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENTS_UPLOAD_FILE, datasetId, documentId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<UploadFileInfoResponse>() {
                }).block();
    }

    @Override
    public RetrieveResponse retrieve(RetrieveRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_DATASETS_RETRIEVE_URL, request.getDatasetId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(RetrieveResponse.class).block();
    }


    @Override
    public MetaDataResponse createMetaData(MetaDataCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_METADATA_CREATE_URL, request.getDatasetId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(MetaDataResponse.class).block();
    }


    @Override
    public MetaDataResponse updateMetaData(MetaDataUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.patch()
                .uri(DatasetUriConstant.V1_METADATA_UPDATE_URL, request.getDatasetId(), request.getMetaDataId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(MetaDataResponse.class).block();
    }


    @Override
    public void deleteMetaData(String datasetId, String metadataId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(metadataId, "metadataId can not be null");

        webClient.delete()
                .uri(DatasetUriConstant.V1_METADATA_DELETE_URL, datasetId, metadataId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(void.class).block();
    }


    @Override
    public void actionMetaData(MetaDataActionRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        webClient.post()
                .uri(DatasetUriConstant.V1_METADATA_ACTION_URL, request.getDatasetId(), request.getAction().name())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class).block();
    }


    @Override
    public void updateDocumentMetaData(DocumentMetaDataUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        // 使用 webClient 发送 POST 请求
        webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_METADATA_UPDATE_URL, request.getDatasetId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class).block();
    }


    @Override
    public MetaDataListResponse listMetaData(String datasetId, String apiKey) {
        Assert.notNull(datasetId, "datasetId can not be null");
        return webClient.get()
                .uri(DatasetUriConstant.V1_METADATA_LIST_URL, datasetId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(MetaDataListResponse.class).block();
    }

    @Override
    public TextEmbeddingListResponse listTextEmbedding(String apiKey) {
        return webClient.get()
                .uri(DatasetUriConstant.V1_TEXT_EMBEDDING_LIST_URL)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(TextEmbeddingListResponse.class).block();
    }

    @Override
    public TextEmbeddingListResponse listRerank(String apiKey) {
        return webClient.get()
                .uri(DatasetUriConstant.V1_RERANK_LIST_URL)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(TextEmbeddingListResponse.class).block();
    }

    @Override
    public TagInfoResponse createTag(TagCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_TAGS)
                .bodyValue(request)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(TagInfoResponse.class).block();
    }

    @Override
    public List<TagInfoResponse> listTag(String apiKey) {
        return webClient.get()
                .uri(DatasetUriConstant.V1_TAGS)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<List<TagInfoResponse>>() {
                }).block();
    }

    @Override
    public TagInfoResponse updateTag(TagUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.patch()
                .uri(DatasetUriConstant.V1_TAGS)
                .bodyValue(request)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(TagInfoResponse.class).block();
    }

    @Override
    public void deleteTag(String tagId, String apiKey) {
        // Validate input parameters
        if (tagId == null || tagId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag ID must not be null or empty");
        }
        Map<String, String> param = new HashMap<>(1);
        param.put("tag_id", tagId);
        webClient.method(HttpMethod.DELETE)
                .uri(DatasetUriConstant.V1_TAGS)
                .bodyValue(param)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class).block();
    }

    @Override
    public void bindingTag(TagBindingRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        webClient.post()
                .uri(DatasetUriConstant.V1_TAGS_BINDING)
                .bodyValue(request)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class).block();
    }

    @Override
    public void unbindingTag(TagUnbindingRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        webClient.post()
                .uri(DatasetUriConstant.V1_TAGS_UNBINDING)
                .bodyValue(request)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class).block();
    }

    @Override
    public DataSetTagsResponse listDatasetTag(String datasetId, String apiKey) {
        return webClient.get()
                .uri(DatasetUriConstant.V1_DATASET_TAGS, datasetId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DataSetTagsResponse.class).block();
    }

    @Override
    public DatasetStatusResponse changeDocumentStatus(String datasetId, Set<String> documentIds, DocActionEnum status, String apiKey) {
        Map<String, Set<String>> param = new HashMap<>(1);
        param.put("document_ids", documentIds);
        return webClient.patch()
                .uri(DatasetUriConstant.V1_DOCUMENT_STATUS, datasetId, status.name())
                .bodyValue(param)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DatasetStatusResponse.class).block();
    }

}
