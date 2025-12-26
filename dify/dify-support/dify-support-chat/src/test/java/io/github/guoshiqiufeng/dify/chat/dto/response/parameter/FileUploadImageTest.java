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
 * Test for {@link FileUploadImage}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class FileUploadImageTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        FileUploadImage image = new FileUploadImage();
        Boolean enabled = true;
        Integer numberLimits = 5;
        List<String> transferMethods = Arrays.asList("remote_url", "local_file");

        // Act
        image.setEnabled(enabled);
        image.setNumberLimits(numberLimits);
        image.setTransferMethods(transferMethods);

        // Assert
        assertEquals(enabled, image.getEnabled());
        assertEquals(numberLimits, image.getNumberLimits());
        assertEquals(transferMethods, image.getTransferMethods());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        FileUploadImage image = new FileUploadImage();

        // Assert
        assertNull(image.getEnabled());
        assertNull(image.getNumberLimits());
        assertNull(image.getTransferMethods());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        FileUploadImage image1 = new FileUploadImage();
        image1.setEnabled(true);
        image1.setNumberLimits(5);
        image1.setTransferMethods(Arrays.asList("remote_url", "local_file"));

        FileUploadImage image2 = new FileUploadImage();
        image2.setEnabled(true);
        image2.setNumberLimits(5);
        image2.setTransferMethods(Arrays.asList("remote_url", "local_file"));

        FileUploadImage image3 = new FileUploadImage();
        image3.setEnabled(false);
        image3.setNumberLimits(10);
        image3.setTransferMethods(List.of("local_file"));

        // Assert
        assertEquals(image1, image2);
        assertEquals(image1.hashCode(), image2.hashCode());
        assertNotEquals(image1, image3);
        assertNotEquals(image1.hashCode(), image3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        FileUploadImage image = new FileUploadImage();
        image.setEnabled(true);
        image.setNumberLimits(5);
        image.setTransferMethods(Arrays.asList("remote_url", "local_file"));

        // Act
        String toString = image.toString();

        // Assert
        assertTrue(toString.contains("enabled=true"));
        assertTrue(toString.contains("numberLimits=5"));
        assertTrue(toString.contains("transferMethods=[remote_url, local_file]"));
    }
}
