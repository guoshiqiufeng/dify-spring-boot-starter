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
package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link FileUpload}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class FileUploadTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        FileUpload fileUpload = new FileUpload();
        FileUploadImage image = new FileUploadImage();
        Boolean enabled = true;
        List<String> allowedFileTypes = Arrays.asList("text", "image");
        List<String> allowedFileExtensions = Arrays.asList("txt", "jpg", "png");
        List<String> allowedFileUploadMethods = Arrays.asList("remote_url", "local_file");
        Integer numberLimits = 5;
        FileUploadConfig fileUploadConfig = new FileUploadConfig();

        // Act
        fileUpload.setImage(image);
        fileUpload.setEnabled(enabled);
        fileUpload.setAllowedFileTypes(allowedFileTypes);
        fileUpload.setAllowedFileExtensions(allowedFileExtensions);
        fileUpload.setAllowedFileUploadMethods(allowedFileUploadMethods);
        fileUpload.setNumberLimits(numberLimits);
        fileUpload.setFileUploadConfig(fileUploadConfig);

        // Assert
        assertEquals(image, fileUpload.getImage());
        assertEquals(enabled, fileUpload.getEnabled());
        assertEquals(allowedFileTypes, fileUpload.getAllowedFileTypes());
        assertEquals(allowedFileExtensions, fileUpload.getAllowedFileExtensions());
        assertEquals(allowedFileUploadMethods, fileUpload.getAllowedFileUploadMethods());
        assertEquals(numberLimits, fileUpload.getNumberLimits());
        assertEquals(fileUploadConfig, fileUpload.getFileUploadConfig());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        FileUpload fileUpload = new FileUpload();

        // Assert
        assertNull(fileUpload.getImage());
        assertNull(fileUpload.getEnabled());
        assertNull(fileUpload.getAllowedFileTypes());
        assertNull(fileUpload.getAllowedFileExtensions());
        assertNull(fileUpload.getAllowedFileUploadMethods());
        assertNull(fileUpload.getNumberLimits());
        assertNull(fileUpload.getFileUploadConfig());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        FileUploadImage image1 = new FileUploadImage();
        image1.setEnabled(true);

        FileUploadConfig config1 = new FileUploadConfig();
        config1.setFileSizeLimit(10);

        FileUpload fileUpload1 = new FileUpload();
        fileUpload1.setImage(image1);
        fileUpload1.setEnabled(true);
        fileUpload1.setAllowedFileTypes(Arrays.asList("text", "image"));
        fileUpload1.setFileUploadConfig(config1);

        FileUploadImage image2 = new FileUploadImage();
        image2.setEnabled(true);

        FileUploadConfig config2 = new FileUploadConfig();
        config2.setFileSizeLimit(10);

        FileUpload fileUpload2 = new FileUpload();
        fileUpload2.setImage(image2);
        fileUpload2.setEnabled(true);
        fileUpload2.setAllowedFileTypes(Arrays.asList("text", "image"));
        fileUpload2.setFileUploadConfig(config2);

        FileUploadImage image3 = new FileUploadImage();
        image3.setEnabled(false);

        FileUploadConfig config3 = new FileUploadConfig();
        config3.setFileSizeLimit(20);

        FileUpload fileUpload3 = new FileUpload();
        fileUpload3.setImage(image3);
        fileUpload3.setEnabled(false);
        fileUpload3.setAllowedFileTypes(List.of("audio"));
        fileUpload3.setFileUploadConfig(config3);

        // Assert
        assertEquals(fileUpload1, fileUpload2);
        assertEquals(fileUpload1.hashCode(), fileUpload2.hashCode());
        assertNotEquals(fileUpload1, fileUpload3);
        assertNotEquals(fileUpload1.hashCode(), fileUpload3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        FileUploadImage image = new FileUploadImage();
        image.setEnabled(true);

        FileUploadConfig config = new FileUploadConfig();
        config.setFileSizeLimit(10);

        FileUpload fileUpload = new FileUpload();
        fileUpload.setImage(image);
        fileUpload.setEnabled(true);
        fileUpload.setAllowedFileTypes(Arrays.asList("text", "image"));
        fileUpload.setFileUploadConfig(config);

        // Act
        String toString = fileUpload.toString();

        // Assert
        assertTrue(toString.contains("image="));
        assertTrue(toString.contains("enabled=true"));
        assertTrue(toString.contains("allowedFileTypes=[text, image]"));
        assertTrue(toString.contains("fileUploadConfig="));
    }
}
