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
package io.github.guoshiqiufeng.dify.dataset.dto.request.document;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link Segmentation}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class SegmentationTest {

    @Test
    public void testNoArgsConstructor() {
        // Act
        Segmentation segmentation = new Segmentation();

        // Assert
        assertNotNull(segmentation);
        assertEquals("\\n", segmentation.getSeparator());
        assertEquals(1000, segmentation.getMaxTokens());
    }

    @Test
    public void testAllArgsConstructor() {
        // Arrange
        String separator = "\\t";
        Integer maxTokens = 500;

        // Act
        Segmentation segmentation = new Segmentation(separator, maxTokens);

        // Assert
        assertNotNull(segmentation);
        assertEquals(separator, segmentation.getSeparator());
        assertEquals(maxTokens, segmentation.getMaxTokens());
    }

    @Test
    public void testGetterAndSetter() {
        // Arrange
        Segmentation segmentation = new Segmentation();
        String separator = "\\r\\n";
        Integer maxTokens = 2000;

        // Act
        segmentation.setSeparator(separator);
        segmentation.setMaxTokens(maxTokens);

        // Assert
        assertEquals(separator, segmentation.getSeparator());
        assertEquals(maxTokens, segmentation.getMaxTokens());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        Segmentation segmentation1 = new Segmentation("\\n", 1000);
        Segmentation segmentation2 = new Segmentation("\\n", 1000);
        Segmentation segmentation3 = new Segmentation("\\t", 500);

        // Assert
        assertEquals(segmentation1, segmentation2);
        assertEquals(segmentation1.hashCode(), segmentation2.hashCode());
        assertNotEquals(segmentation1, segmentation3);
        assertNotEquals(segmentation1.hashCode(), segmentation3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        Segmentation segmentation = new Segmentation("\\n", 1000);

        // Act
        String toString = segmentation.toString();

        // Assert
        assertTrue(toString.contains("separator=\\n"));
        assertTrue(toString.contains("maxTokens=1000"));
    }
} 