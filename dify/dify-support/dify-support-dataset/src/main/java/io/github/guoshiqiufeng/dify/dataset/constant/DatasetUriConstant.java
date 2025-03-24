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
package io.github.guoshiqiufeng.dify.dataset.constant;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 11:34
 */
public interface DatasetUriConstant {

    String V1_URL = "/v1";

    String V1_DATASETS_URL = V1_URL + "/datasets";

    String V1_DATASET_URL = V1_DATASETS_URL + "/{}";

    String V1_DOCUMENTS_URL = V1_DATASET_URL + "/documents";

    String V1_DOCUMENT_CREATE_BY_TEXT_URL = V1_DATASET_URL + "/document/create-by-text";

    String V1_DOCUMENT_CREATE_BY_FILE_URL = V1_DATASET_URL + "/document/create-by-file";

    String V1_DOCUMENT_URL = V1_DOCUMENTS_URL + "/{}";

    String V1_DOCUMENT_INDEXING_STATUS_URL = V1_DOCUMENT_URL + "/indexing-status";

    String V1_DOCUMENT_UPDATE_BY_TEXT_URL = V1_DOCUMENT_URL + "/update-by-text";

    String V1_DOCUMENT_UPDATE_BY_FILE_URL = V1_DOCUMENT_URL + "/update-by-file";

    String V1_DOCUMENTS_SEGMENTS_URL = V1_DOCUMENT_URL + "/segments";

    String V1_DOCUMENTS_SEGMENT_URL = V1_DOCUMENT_URL + "/segments/{}";

    String V1_DOCUMENTS_UPLOAD_FILE = V1_DOCUMENT_URL + "/upload-file";

    String V1_DATASETS_RETRIEVE_URL = V1_DATASET_URL + "/retrieve";

    String V1_METADATA_CREATE_URL = V1_DATASET_URL + "/metadata";

    String V1_METADATA_UPDATE_URL = V1_METADATA_CREATE_URL + "/{}";

    String V1_METADATA_DELETE_URL = V1_METADATA_UPDATE_URL + "/{}";

    String V1_METADATA_ACTION_URL = V1_METADATA_UPDATE_URL + "/built-in/{}";

    String V1_DOCUMENT_METADATA_UPDATE_URL = V1_DATASET_URL + "documents/metadata";

    String V1_METADATA_LIST_URL = V1_METADATA_CREATE_URL;
}
