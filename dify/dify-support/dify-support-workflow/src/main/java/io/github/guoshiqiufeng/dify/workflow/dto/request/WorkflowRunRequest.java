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
package io.github.guoshiqiufeng.dify.workflow.dto.request;

import io.github.guoshiqiufeng.dify.core.enums.message.MessageFileTransferMethodEnum;
import io.github.guoshiqiufeng.dify.core.enums.message.MessageFileTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/11 14:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkflowRunRequest extends BaseWorkflowRequest implements Serializable {
    private static final long serialVersionUID = -6104692859724501911L;

    /**
     * 文件
     */
    private List<WorkflowFile> files;

    /**
     * 自定义参数
     */
    private Map<String, Object> inputs;


    @Data
    public static class WorkflowFile {
        private String type = "image";
        private String transferMethod = "remote_url";
        private String url;
        private String uploadFileId;
// Consider creating a common base class or utility class for file handling
// For example:

        public void setMessageFileType(MessageFileTypeEnum messageFileType) {
            if (messageFileType == null) {
                this.type = null;
            } else {
                this.type = messageFileType.name();
            }
        }

        public void setMessageFileTransferMethod(MessageFileTransferMethodEnum transferMethod) {
            if (transferMethod == null) {
                this.transferMethod = null;
            } else {
                this.transferMethod = transferMethod.name();
            }
        }

        public MessageFileTypeEnum getMessageFileType() {
            return this.type == null ? null : MessageFileTypeEnum.valueOf(this.type);
        }

        public MessageFileTransferMethodEnum getMessageFileTransferMethod() {
            return this.transferMethod == null ? null : MessageFileTransferMethodEnum.valueOf(this.transferMethod);
        }
    }
}
