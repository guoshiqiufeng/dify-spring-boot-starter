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
 * Dify Dataset Client Interface
 * Provides methods to interact with Dify's dataset API for managing datasets,
 * documents, segments, and related operations.
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/8 13:46
 */
public interface DifyDatasetClient {

    /**
     * Creates a new dataset
     *
     * @param request The dataset creation request containing dataset details
     * @return The created dataset response
     */
    DatasetResponse create(DatasetCreateRequest request);

    /**
     * Retrieves a paginated list of datasets
     *
     * @param request The request parameters for fetching datasets
     * @return Paginated result containing dataset data
     */
    DifyPageResult<DatasetResponse> page(DatasetPageRequest request);

    /**
     * info
     *
     * @param request request parameters
     * @return dataset data
     */
    DatasetInfoResponse info(DatasetInfoRequest request);

    /**
     * update
     *
     * @param request request parameters
     * @return dataset data
     */
    DatasetInfoResponse update(DatasetUpdateRequest request);

    /**
     * Deletes a dataset
     *
     * @param datasetId The ID of the dataset to delete
     * @param apiKey    The API key for authentication and authorization to the Dify API
     */
    void delete(String datasetId, String apiKey);

    /**
     * Creates a document in a dataset using text content
     *
     * @param request The document creation request containing text data
     * @return The created document response
     */
    DocumentCreateResponse createDocumentByText(DocumentCreateByTextRequest request);

    /**
     * Creates a document in a dataset using a file
     *
     * @param request The document creation request containing file data
     * @return The created document response
     */
    DocumentCreateResponse createDocumentByFile(DocumentCreateByFileRequest request);

    /**
     * Updates an existing document with new text content
     *
     * @param request The document update request containing new text data
     * @return The updated document response
     */
    DocumentCreateResponse updateDocumentByText(DocumentUpdateByTextRequest request);

    /**
     * Updates an existing document with a new file
     *
     * @param request The document update request containing new file data
     * @return The updated document response
     */
    DocumentCreateResponse updateDocumentByFile(DocumentUpdateByFileRequest request);

    /**
     * Retrieves a paginated list of documents in a dataset
     *
     * @param request The request parameters for fetching documents
     * @return Paginated result containing document information
     */
    DifyPageResult<DocumentInfo> pageDocument(DatasetPageDocumentRequest request);

    /**
     * Checks the indexing status of a document
     *
     * @param request The request parameters for checking indexing status
     * @return The document indexing status response
     */
    DocumentIndexingStatusResponse indexingStatus(DocumentIndexingStatusRequest request);

    /**
     * Deletes a document from a dataset
     *
     * @param datasetId  The ID of the dataset containing the document
     * @param documentId The ID of the document to delete
     * @param apiKey     The API key for authentication and authorization to the Dify API
     */
    void deleteDocument(String datasetId, String documentId, String apiKey);

    /**
     * Creates a segment within a document
     *
     * @param request The segment creation request
     * @return The created segment response
     */
    SegmentResponse createSegment(SegmentCreateRequest request);

    /**
     * Retrieves a paginated list of segments
     *
     * @param request The request parameters for fetching segments
     * @return The segment response containing paginated segments
     */
    SegmentResponse pageSegment(SegmentPageRequest request);

    /**
     * Deletes a segment from a document
     *
     * @param datasetId  The ID of the dataset containing the document
     * @param documentId The ID of the document containing the segment
     * @param segmentId  The ID of the segment to delete
     * @param apiKey     The API key for authentication and authorization to the Dify API
     */
    void deleteSegment(String datasetId, String documentId, String segmentId, String apiKey);

    /**
     * Updates an existing segment
     *
     * @param request The segment update request
     * @return The segment update response
     */
    SegmentUpdateResponse updateSegment(SegmentUpdateRequest request);

    /**
     * Creates a child chunk within a segment
     *
     * @param request The segment child chunk creation request
     * @return The segment child chunk creation response
     */
    SegmentChildChunkCreateResponse createSegmentChildChunk(SegmentChildChunkCreateRequest request);

    /**
     * Retrieves a paginated list of child chunks within a segment
     *
     * @param request The request parameters for fetching segment child chunks
     * @return Paginated result containing segment child chunk data
     */
    DifyPageResult<SegmentChildChunkResponse> pageSegmentChildChunk(SegmentChildChunkPageRequest request);

    /**
     * Deletes a child chunk from a segment
     *
     * @param request The segment child chunk deletion request
     */
    void deleteSegmentChildChunk(SegmentChildChunkDeleteRequest request);

    /**
     * Updates an existing child chunk within a segment
     *
     * @param request The segment child chunk update request
     * @return The segment child chunk update response
     */
    SegmentChildChunkUpdateResponse updateSegmentChildChunk(SegmentChildChunkUpdateRequest request);

    /**
     * Retrieves file upload information for a document in a dataset
     *
     * @param datasetId  The ID of the dataset
     * @param documentId The ID of the document
     * @param apiKey     The API key for authentication and authorization to the Dify API
     * @return The upload file information response
     */
    UploadFileInfoResponse uploadFileInfo(String datasetId, String documentId, String apiKey);

    /**
     * Retrieves data from the dataset
     *
     * @param request The retrieve request parameters
     * @return The retrieve response
     */
    RetrieveResponse retrieve(RetrieveRequest request);

    /**
     * Creates metadata for a dataset
     *
     * @param request The metadata creation request
     * @return The metadata response
     */
    MetaDataResponse createMetaData(MetaDataCreateRequest request);

    /**
     * Updates existing metadata for a dataset
     *
     * @param request The metadata update request
     * @return The metadata response
     */
    MetaDataResponse updateMetaData(MetaDataUpdateRequest request);

    /**
     * Deletes metadata from a dataset
     *
     * @param datasetId  The ID of the dataset
     * @param metadataId The ID of the metadata to delete
     * @param apiKey     The API key for authentication and authorization to the Dify API
     */
    void deleteMetaData(String datasetId, String metadataId, String apiKey);

    /**
     * Performs an action on metadata
     *
     * @param request The metadata action request
     */
    void actionMetaData(MetaDataActionRequest request);

    /**
     * Updates metadata for a document
     *
     * @param request The document metadata update request
     */
    void updateDocumentMetaData(DocumentMetaDataUpdateRequest request);

    /**
     * Lists all metadata for a dataset
     *
     * @param datasetId The ID of the dataset
     * @param apiKey    The API key for authentication and authorization to the Dify API
     * @return The metadata list response
     */
    MetaDataListResponse listMetaData(String datasetId, String apiKey);

    /**
     * Lists all text embeddings available
     *
     * @param apiKey The API key for authentication and authorization to the Dify API
     * @return The text embedding list response
     */
    TextEmbeddingListResponse listTextEmbedding(String apiKey);

    /**
     * Lists all rerank
     *
     * @param apiKey The API key for authentication and authorization to the Dify API
     * @return The rerank list response
     */
    TextEmbeddingListResponse listRerank(String apiKey);

}
