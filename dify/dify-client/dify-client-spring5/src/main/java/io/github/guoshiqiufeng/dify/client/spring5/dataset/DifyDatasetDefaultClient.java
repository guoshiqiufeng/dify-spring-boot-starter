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

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.client.spring5.base.BaseDifyDefaultClient;
import io.github.guoshiqiufeng.dify.client.spring5.utils.WebClientUtil;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetException;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/8 13:46
 */
@Slf4j
public class DifyDatasetDefaultClient extends BaseDifyDefaultClient implements DifyDatasetClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DatasetResponse.class).block();
    }


    @Override
    public DifyPageResult<DatasetResponse> page(DatasetPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
                .uri(DatasetUriConstant.V1_DATASETS_URL + "?page={page}&limit={limit}", request.getPage(), request.getLimit())
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<DatasetResponse>>() {
                }).block();
    }


    @Override
    public void delete(String datasetId) {
        Assert.notNull(datasetId, "datasetId can not be null");
        webClient.delete()
                .uri(DatasetUriConstant.V1_DATASETS_URL + "/{datasetId}", datasetId)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class).block();
    }


    @Override
    public DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_CREATE_BY_TEXT_URL, request.getDatasetId())
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentCreateResponse.class).block();
    }


    @Override
    public DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        try {
            // 获取文件内容和类型
            byte[] fileContent = request.getFile().getBytes();
            String contentType = request.getFile().getContentType();
            if (StrUtil.isEmpty(contentType)) {
                contentType = MediaType.TEXT_PLAIN_VALUE;
            }

            // 添加文件部分
            builder.part("file", fileContent)
                    .header("Content-Disposition", "form-data; name=\"file\"; filename=\"" + request.getFile().getOriginalFilename() + "\"")
                    .header("Content-Type", contentType);

            // 添加JSON数据部分
            request.setFile(null);
            builder.part("data", toJson(request))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_CREATE_BY_FILE_URL, request.getDatasetId())
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
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentCreateResponse.class).block();
    }


    @Override
    public DocumentCreateResponse updateDocumentByFile(DocumentUpdateByFileRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        try {
            // 获取文件内容和类型
            byte[] fileContent = request.getFile().getBytes();
            String contentType = request.getFile().getContentType();
            if (StrUtil.isEmpty(contentType)) {
                contentType = MediaType.TEXT_PLAIN_VALUE;
            }

            // 添加文件部分
            builder.part("file", fileContent)
                    .header("Content-Disposition", "form-data; name=\"file\"; filename=\"" + request.getFile().getOriginalFilename() + "\"")
                    .header("Content-Type", contentType);

            // 添加JSON数据部分
            request.setFile(null);
            builder.part("data", toJson(request))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 使用 webClient 发送 POST 请求
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENT_UPDATE_BY_FILE_URL, request.getDatasetId(), request.getDocumentId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
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
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<DocumentInfo>>() {
                }).block();
    }


    @Override
    public DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENT_INDEXING_STATUS_URL, request.getDatasetId(), request.getBatch())
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DocumentIndexingStatusResponse>() {
                }).block();
    }


    @Override
    public DocumentDeleteResponse deleteDocument(String datasetId, String documentId) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        return webClient.delete()
                .uri(DatasetUriConstant.V1_DOCUMENT_URL, datasetId, documentId)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentDeleteResponse.class).block();
    }


    @Override
    public SegmentResponse createSegment(SegmentCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_URL, request.getDatasetId(), request.getDocumentId())
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentResponse.class).block();
    }


    @Override
    public SegmentResponse pageSegment(SegmentPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_URL + "?keyword={keyword}&status={status}",
                        request.getDatasetId(), request.getDocumentId(),
                        request.getKeyword(), request.getStatus())
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<SegmentResponse>() {
                }).block();
    }


    @Override
    public SegmentDeleteResponse deleteSegment(String datasetId, String documentId, String segmentId) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");
        Assert.notNull(segmentId, "segmentId can not be null");

        return webClient.delete()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL, datasetId, documentId, segmentId)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentDeleteResponse.class).block();
    }


    @Override
    public SegmentUpdateResponse updateSegment(SegmentUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL, request.getDatasetId(), request.getDocumentId(), request.getSegmentId())
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentUpdateResponse.class).block();
    }

    @Override
    public SegmentChildChunkCreateResponse createSegmentChildChunk(SegmentChildChunkCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getContent(), "content can not be null");
        return webClient.post()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNKS_URL, request.getDatasetId(), request.getDocumentId(), request.getSegmentId())
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
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<SegmentChildChunkResponse>>() {
                }).block();
    }

    @Override
    public SegmentChildChunkDeleteResponse deleteSegmentChildChunk(SegmentChildChunkDeleteRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getChildChunkId(), "childChunkId can not be null");
        return webClient.delete()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNK_URL,
                        request.getDatasetId(), request.getDocumentId(), request.getSegmentId(), request.getChildChunkId())
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentChildChunkDeleteResponse.class).block();
    }

    @Override
    public SegmentChildChunkUpdateResponse updateSegmentChildChunk(SegmentChildChunkUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getChildChunkId(), "childChunkId can not be null");
        return webClient.patch()
                .uri(DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_CHILD_CHUNK_URL,
                        request.getDatasetId(), request.getDocumentId(), request.getSegmentId(), request.getChildChunkId())
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentChildChunkUpdateResponse.class).block();
    }


    @Override
    public UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(documentId, "documentId can not be null");

        return webClient.get()
                .uri(DatasetUriConstant.V1_DOCUMENTS_UPLOAD_FILE, datasetId, documentId)
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
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(MetaDataResponse.class).block();
    }


    @Override
    public void deleteMetaData(String datasetId, String metadataId) {
        Assert.notNull(datasetId, "datasetId can not be null");
        Assert.notNull(metadataId, "metadataId can not be null");

        webClient.delete()
                .uri(DatasetUriConstant.V1_METADATA_DELETE_URL, datasetId, metadataId)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(void.class).block();
    }


    @Override
    public void actionMetaData(MetaDataActionRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        webClient.post()
                .uri(DatasetUriConstant.V1_METADATA_ACTION_URL, request.getDatasetId(), request.getAction().name())
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
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class).block();
    }


    @Override
    public MetaDataListResponse listMetaData(String datasetId) {
        Assert.notNull(datasetId, "datasetId can not be null");
        return webClient.get()
                .uri(DatasetUriConstant.V1_METADATA_LIST_URL, datasetId)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(MetaDataListResponse.class).block();
    }

    @Override
    public TextEmbeddingListResponse listTextEmbedding() {
        return webClient.get()
                .uri(DatasetUriConstant.V1_TEXT_EMBEDDING_LIST_URL)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(TextEmbeddingListResponse.class).block();
    }

    private String toJson(Object request) {
        String body = null;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            body = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }
        return body;
    }
}
