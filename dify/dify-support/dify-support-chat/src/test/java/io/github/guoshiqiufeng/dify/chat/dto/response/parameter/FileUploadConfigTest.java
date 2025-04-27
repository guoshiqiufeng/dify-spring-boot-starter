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
package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link FileUploadConfig}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class FileUploadConfigTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        FileUploadConfig config = new FileUploadConfig();
        Integer fileSizeLimit = 10;
        Integer batchCountLimit = 5;
        Integer imageFileSizeLimit = 8;
        Integer videoFileSizeLimit = 20;
        Integer audioFileSizeLimit = 15;
        Integer workflowFileUploadLimit = 3;

        // Act
        config.setFileSizeLimit(fileSizeLimit);
        config.setBatchCountLimit(batchCountLimit);
        config.setImageFileSizeLimit(imageFileSizeLimit);
        config.setVideoFileSizeLimit(videoFileSizeLimit);
        config.setAudioFileSizeLimit(audioFileSizeLimit);
        config.setWorkflowFileUploadLimit(workflowFileUploadLimit);

        // Assert
        assertEquals(fileSizeLimit, config.getFileSizeLimit());
        assertEquals(batchCountLimit, config.getBatchCountLimit());
        assertEquals(imageFileSizeLimit, config.getImageFileSizeLimit());
        assertEquals(videoFileSizeLimit, config.getVideoFileSizeLimit());
        assertEquals(audioFileSizeLimit, config.getAudioFileSizeLimit());
        assertEquals(workflowFileUploadLimit, config.getWorkflowFileUploadLimit());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        FileUploadConfig config = new FileUploadConfig();

        // Assert
        assertNull(config.getFileSizeLimit());
        assertNull(config.getBatchCountLimit());
        assertNull(config.getImageFileSizeLimit());
        assertNull(config.getVideoFileSizeLimit());
        assertNull(config.getAudioFileSizeLimit());
        assertNull(config.getWorkflowFileUploadLimit());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        FileUploadConfig config1 = new FileUploadConfig();
        config1.setFileSizeLimit(10);
        config1.setBatchCountLimit(5);
        config1.setImageFileSizeLimit(8);
        config1.setVideoFileSizeLimit(20);
        config1.setAudioFileSizeLimit(15);
        config1.setWorkflowFileUploadLimit(3);

        FileUploadConfig config2 = new FileUploadConfig();
        config2.setFileSizeLimit(10);
        config2.setBatchCountLimit(5);
        config2.setImageFileSizeLimit(8);
        config2.setVideoFileSizeLimit(20);
        config2.setAudioFileSizeLimit(15);
        config2.setWorkflowFileUploadLimit(3);

        FileUploadConfig config3 = new FileUploadConfig();
        config3.setFileSizeLimit(20);
        config3.setBatchCountLimit(10);
        config3.setImageFileSizeLimit(16);
        config3.setVideoFileSizeLimit(40);
        config3.setAudioFileSizeLimit(30);
        config3.setWorkflowFileUploadLimit(6);

        // Assert
        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());
        assertNotEquals(config1, config3);
        assertNotEquals(config1.hashCode(), config3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        FileUploadConfig config = new FileUploadConfig();
        config.setFileSizeLimit(10);
        config.setBatchCountLimit(5);
        config.setImageFileSizeLimit(8);
        config.setVideoFileSizeLimit(20);
        config.setAudioFileSizeLimit(15);
        config.setWorkflowFileUploadLimit(3);

        // Act
        String toString = config.toString();

        // Assert
        assertTrue(toString.contains("fileSizeLimit=10"));
        assertTrue(toString.contains("batchCountLimit=5"));
        assertTrue(toString.contains("imageFileSizeLimit=8"));
        assertTrue(toString.contains("videoFileSizeLimit=20"));
        assertTrue(toString.contains("audioFileSizeLimit=15"));
        assertTrue(toString.contains("workflowFileUploadLimit=3"));
    }
}
