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

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.core.config.DifyServerProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import io.github.guoshiqiufeng.dify.dataset.dto.request.*;
import io.github.guoshiqiufeng.dify.dataset.dto.response.*;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetException;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetExceptionEnum;
import io.github.guoshiqiufeng.dify.dataset.utils.WebClientUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 11:40
 */
public class DifyDatasetDefaultImpl implements DifyDataset {

    private final DifyServerProperties difyServerProperties;
    private final ObjectMapper objectMapper;

    public DifyDatasetDefaultImpl(DifyServerProperties difyServerProperties, ObjectMapper objectMapper) {
        this.difyServerProperties = difyServerProperties;
        this.objectMapper = objectMapper;
    }


    @Override
    public DatasetResponse create(DatasetCreateRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DATASETS_URL;

        // 请求体
        String body = builderBody(request);
        // 使用 WebClient 发送 POST 请求
        WebClient webClient = getWebClient(request.getApiKey());

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DatasetResponse.class)
                .block();
    }


    @Override
    public DifyPageResult<DatasetResponse> page(DatasetPageRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DATASETS_URL;

        // 使用 WebClient 发送请求
        WebClient webClient = getWebClient(request.getApiKey());
        url += "?page={}&limit={}";
        url = StrUtil.format(url, request.getPage(), request.getLimit());

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<DatasetResponse>>() {
                })
                .block();
    }

    @Override
    public void delete(String datasetId, String apiKey) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DATASETS_URL + "/" + datasetId;

        // 使用 WebClient 发送 DELETE 请求
        WebClient webClient = getWebClient(apiKey);

        webClient.delete()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENT_CREATE_BY_TEXT_URL;
        url = StrUtil.format(url, request.getDatasetId());
        // 请求体
        String body = builderBody(request);
        // 使用 WebClient 发送 POST 请求
        WebClient webClient = getWebClient(request.getApiKey());

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentCreateResponse.class)
                .block();
    }

    @Override
    public DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENT_CREATE_BY_FILE_URL;
        url = StrUtil.format(url, request.getDatasetId());
        // 请求体
        String json = builderBody(request);

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
            builder.part("data", json)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 使用 WebClient 发送 POST 请求
        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .build();

        return webClient.post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentCreateResponse.class)
                .block();
    }

    @Override
    public DocumentCreateResponse updateDocumentByText(DocumentUpdateByTextRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENT_UPDATE_BY_TEXT_URL;
        url = StrUtil.format(url, request.getDatasetId(), request.getDocumentId());
        // 请求体
        String body = builderBody(request);
        // 使用 WebClient 发送 POST 请求
        WebClient webClient = getWebClient(request.getApiKey());

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentCreateResponse.class)
                .block();
    }

    @Override
    public DocumentCreateResponse updateDocumentByFile(DocumentUpdateByFileRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENT_UPDATE_BY_FILE_URL;
        url = StrUtil.format(url, request.getDatasetId(), request.getDocumentId());
        // 请求体
        String json = builderBody(request);

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
            builder.part("data", json)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 使用 WebClient 发送 POST 请求
        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .build();

        return webClient.post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentCreateResponse.class)
                .block();
    }

    @Override
    public DifyPageResult<DocumentInfo> pageDocument(DatasetPageDocumentRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENTS_URL;
        url = StrUtil.format(url, request.getDatasetId());
        // 使用 WebClient 发送请求
        WebClient webClient = getWebClient(request.getApiKey());
        url += "?page={}&limit={}";
        url = StrUtil.format(url, request.getPage(), request.getLimit());

        if (StrUtil.isNotEmpty(request.getKeyword())) {
            url += "keyword={}";
            url = StrUtil.format(url, request.getKeyword());
        }

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<DocumentInfo>>() {
                })
                .block();
    }

    @Override
    public DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENT_INDEXING_STATUS_URL;
        url = StrUtil.format(url, request.getDatasetId(), request.getBatch());
        // 使用 WebClient 发送请求
        WebClient webClient = getWebClient(request.getApiKey());

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DocumentIndexingStatusResponse>() {
                })
                .block();
    }

    @Override
    public DocumentDeleteResponse deleteDocument(String datasetId, String documentId, String apiKey) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENT_URL;

        url = StrUtil.format(url, datasetId, documentId);

        // 使用 WebClient 发送 DELETE 请求
        WebClient webClient = getWebClient(apiKey);

        return webClient.delete()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DocumentDeleteResponse.class)
                .block();
    }

    @Override
    public SegmentResponse createSegment(SegmentCreateRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_URL;
        url = StrUtil.format(url, request.getDatasetId(), request.getDocumentId());
        // 请求体
        String body = builderBody(request);
        // 使用 WebClient 发送 POST 请求
        WebClient webClient = getWebClient(request.getApiKey());

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentResponse.class)
                .block();
    }

    @Override
    public SegmentResponse pageSegment(SegmentPageRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENTS_SEGMENTS_URL;
        url = StrUtil.format(url, request.getDatasetId(), request.getDocumentId());
        // 使用 WebClient 发送请求
        WebClient webClient = getWebClient(request.getApiKey());
        url += "?";

        if (StrUtil.isNotEmpty(request.getKeyword())) {
            url += "keyword={}";
            url = StrUtil.format(url, request.getKeyword());
        }

        if (StrUtil.isNotEmpty(request.getStatus())) {
            url += "status={}";
            url = StrUtil.format(url, request.getStatus());
        }

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<SegmentResponse>() {
                })
                .block();
    }

    @Override
    public SegmentDeleteResponse deleteSegment(String datasetId, String documentId, String segmentId, String apiKey) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL;

        url = StrUtil.format(url, datasetId, documentId, segmentId);

        // 使用 WebClient 发送 DELETE 请求
        WebClient webClient = getWebClient(apiKey);

        return webClient.delete()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentDeleteResponse.class)
                .block();
    }

    @Override
    public SegmentUpdateResponse updateSegment(SegmentUpdateRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENTS_SEGMENT_URL;
        url = StrUtil.format(url, request.getDatasetId(), request.getDocumentId(), request.getSegmentId());
        // 请求体
        String body = builderBody(request);
        // 使用 WebClient 发送 POST 请求
        WebClient webClient = getWebClient(request.getApiKey());

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(SegmentUpdateResponse.class)
                .block();
    }

    @Override
    public UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId, String apiKey) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DOCUMENTS_UPLOAD_FILE;
        url = StrUtil.format(url, datasetId, documentId);
        // 使用 WebClient 发送请求
        WebClient webClient = getWebClient(apiKey);

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<UploadFileInfoResponse>() {
                })
                .block();
    }

    @Override
    public RetrieveResponse retrieve(RetrieveRequest request) {
        // 请求地址
        String url = difyServerProperties.getUrl() + DatasetUriConstant.V1_DATASETS_RETRIEVE_URL;
        url = StrUtil.format(url, request.getDatasetId());
        // 请求体
        String body = builderBody(request);
        // 使用 WebClient 发送 POST 请求
        WebClient webClient = getWebClient(request.getApiKey());

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(RetrieveResponse.class)
                .block();
    }

    /**
     * 获取WebClient
     *
     * @param apiKey API密钥，用于身份验证
     * @return WebClient
     */
    private static WebClient getWebClient(String apiKey) {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String builderBody(Object request) {
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
