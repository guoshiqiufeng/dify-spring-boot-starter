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
package io.github.guoshiqiufeng.dify.dataset.dto.request.document;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link SubChunkSegmentation}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class SubChunkSegmentationTest {

    @Test
    public void testNoArgsConstructor() {
        // Act
        SubChunkSegmentation subChunkSegmentation = new SubChunkSegmentation();

        // Assert
        assertNotNull(subChunkSegmentation);
        assertEquals("/n", subChunkSegmentation.getSeparator());
        assertEquals(200, subChunkSegmentation.getMaxTokens());
        assertNull(subChunkSegmentation.getChunkOverlap());
    }

    @Test
    public void testAllArgsConstructor() {
        // Arrange
        String separator = "\\t";
        Integer maxTokens = 300;
        Integer chunkOverlap = 50;

        // Act
        SubChunkSegmentation subChunkSegmentation = new SubChunkSegmentation(separator, maxTokens, chunkOverlap);

        // Assert
        assertNotNull(subChunkSegmentation);
        assertEquals(separator, subChunkSegmentation.getSeparator());
        assertEquals(maxTokens, subChunkSegmentation.getMaxTokens());
        assertEquals(chunkOverlap, subChunkSegmentation.getChunkOverlap());
    }

    @Test
    public void testGetterAndSetter() {
        // Arrange
        SubChunkSegmentation subChunkSegmentation = new SubChunkSegmentation();
        String separator = "\\r\\n";
        Integer maxTokens = 400;
        Integer chunkOverlap = 100;

        // Act
        subChunkSegmentation.setSeparator(separator);
        subChunkSegmentation.setMaxTokens(maxTokens);
        subChunkSegmentation.setChunkOverlap(chunkOverlap);

        // Assert
        assertEquals(separator, subChunkSegmentation.getSeparator());
        assertEquals(maxTokens, subChunkSegmentation.getMaxTokens());
        assertEquals(chunkOverlap, subChunkSegmentation.getChunkOverlap());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        SubChunkSegmentation subChunkSegmentation1 = new SubChunkSegmentation("/n", 200, 50);
        SubChunkSegmentation subChunkSegmentation2 = new SubChunkSegmentation("/n", 200, 50);
        SubChunkSegmentation subChunkSegmentation3 = new SubChunkSegmentation("\\t", 300, 100);

        // Assert
        assertEquals(subChunkSegmentation1, subChunkSegmentation2);
        assertEquals(subChunkSegmentation1.hashCode(), subChunkSegmentation2.hashCode());
        assertNotEquals(subChunkSegmentation1, subChunkSegmentation3);
        assertNotEquals(subChunkSegmentation1.hashCode(), subChunkSegmentation3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        SubChunkSegmentation subChunkSegmentation = new SubChunkSegmentation("/n", 200, 50);

        // Act
        String toString = subChunkSegmentation.toString();

        // Assert
        assertTrue(toString.contains("separator=/n"));
        assertTrue(toString.contains("maxTokens=200"));
        assertTrue(toString.contains("chunkOverlap=50"));
    }
} 