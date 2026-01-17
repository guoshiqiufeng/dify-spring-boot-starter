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
package io.github.guoshiqiufeng.dify.support.impl.dto.dataset;

import io.github.guoshiqiufeng.dify.dataset.dto.response.SegmentData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SegmentDataResponseDto}.
 *
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/12 10:00
 */
class SegmentDataResponseDtoTest {

    @Test
    void testNoArgsConstructor() {
        // Given
        SegmentDataResponseDto dto = new SegmentDataResponseDto();

        // Then
        assertNotNull(dto);
        assertNull(dto.getData());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        SegmentData data = new SegmentData();
        SegmentDataResponseDto dto = new SegmentDataResponseDto(data);

        // Then
        assertNotNull(dto);
        assertEquals(data, dto.getData());
    }

    @Test
    void testDataSetterAndGetter() {
        // Given
        SegmentDataResponseDto dto = new SegmentDataResponseDto();
        SegmentData data = new SegmentData();

        // When
        dto.setData(data);

        // Then
        assertEquals(data, dto.getData());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        SegmentData data1 = new SegmentData();
        data1.setId("id-1");
        data1.setContent("content-1");

        SegmentData data2 = new SegmentData();
        data2.setId("id-2");       // Different value
        data2.setContent("content-2"); // Different value

        SegmentDataResponseDto dto1 = new SegmentDataResponseDto(data1);
        SegmentDataResponseDto dto2 = new SegmentDataResponseDto(data1); // Same data as dto1
        SegmentDataResponseDto dto3 = new SegmentDataResponseDto(data2); // Different data

        // Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        SegmentData data = new SegmentData();
        SegmentDataResponseDto dto = new SegmentDataResponseDto(data);

        // When
        String toString = dto.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("SegmentDataResponseDto"));
    }
}
