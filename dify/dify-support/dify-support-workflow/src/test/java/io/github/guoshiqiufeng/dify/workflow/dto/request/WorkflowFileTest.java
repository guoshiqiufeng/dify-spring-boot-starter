/*
 * Copyright (c) 2023-2023, fubluesky (fubluesky@foxmail.com)
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for the {@link WorkflowRunRequest.WorkflowFile} inner class.
 *
 * @author yanghq
 * @version 1.1.0
 * @since 2023/5/30
 */
public class WorkflowFileTest {

    @Test
    void shouldHandleNullMessageFileType() {
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        file.setMessageFileType(null);
        assertNull(file.getType());
        assertNull(file.getMessageFileType());
    }

    @ParameterizedTest
    @EnumSource(MessageFileTypeEnum.class)
    void shouldSetAndGetMessageFileType(MessageFileTypeEnum type) {
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        file.setMessageFileType(type);
        
        assertEquals(type.name(), file.getType());
        assertEquals(type, file.getMessageFileType());
    }

    @Test
    void shouldHandleNullMessageFileTransferMethod() {
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        file.setMessageFileTransferMethod(null);
        assertNull(file.getTransferMethod());
        assertNull(file.getMessageFileTransferMethod());
    }

    @ParameterizedTest
    @EnumSource(MessageFileTransferMethodEnum.class)
    void shouldSetAndGetMessageFileTransferMethod(MessageFileTransferMethodEnum method) {
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        file.setMessageFileTransferMethod(method);
        
        assertEquals(method.name(), file.getTransferMethod());
        assertEquals(method, file.getMessageFileTransferMethod());
    }

    @Test
    void shouldHandleStringToEnumConversion() {
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        
        // Manual setting of string values
        file.setType("image");
        file.setTransferMethod("local_file");
        
        // Verify enum conversion
        assertEquals(MessageFileTypeEnum.image, file.getMessageFileType());
        assertEquals(MessageFileTransferMethodEnum.local_file, file.getMessageFileTransferMethod());
    }
    
}