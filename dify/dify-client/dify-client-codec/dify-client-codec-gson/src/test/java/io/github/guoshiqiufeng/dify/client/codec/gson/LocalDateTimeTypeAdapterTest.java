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
package io.github.guoshiqiufeng.dify.client.codec.gson;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LocalDateTimeTypeAdapter
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
class LocalDateTimeTypeAdapterTest {

    private LocalDateTimeTypeAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new LocalDateTimeTypeAdapter();
    }

    @Test
    void testWriteLocalDateTime() throws IOException {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 13, 15, 30, 45);
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        // Act
        adapter.write(jsonWriter, dateTime);

        // Assert
        assertEquals("\"2026-01-13T15:30:45\"", stringWriter.toString());
    }

    @Test
    void testWriteNullValue() throws IOException {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        // Act
        adapter.write(jsonWriter, null);

        // Assert
        assertEquals("null", stringWriter.toString());
    }

    @Test
    void testReadLocalDateTime() throws IOException {
        // Arrange
        String json = "\"2026-01-13T15:30:45\"";
        JsonReader jsonReader = new JsonReader(new StringReader(json));

        // Act
        LocalDateTime result = adapter.read(jsonReader);

        // Assert
        assertNotNull(result);
        assertEquals(2026, result.getYear());
        assertEquals(1, result.getMonthValue());
        assertEquals(13, result.getDayOfMonth());
        assertEquals(15, result.getHour());
        assertEquals(30, result.getMinute());
        assertEquals(45, result.getSecond());
    }

    @Test
    void testReadNullValue() throws IOException {
        // Arrange
        String json = "null";
        JsonReader jsonReader = new JsonReader(new StringReader(json));

        // Act
        LocalDateTime result = adapter.read(jsonReader);

        // Assert
        assertNull(result);
    }

    @Test
    void testReadWriteRoundTrip() throws IOException {
        // Arrange
        LocalDateTime original = LocalDateTime.of(2026, 12, 31, 23, 59, 59);
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        // Act - write
        adapter.write(jsonWriter, original);

        // Act - read
        String json = stringWriter.toString();
        JsonReader jsonReader = new JsonReader(new StringReader(json));
        LocalDateTime result = adapter.read(jsonReader);

        // Assert
        assertEquals(original, result);
    }

    @Test
    void testWriteWithMicroseconds() throws IOException {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2026, 6, 15, 10, 20, 30, 123456789);
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);

        // Act
        adapter.write(jsonWriter, dateTime);

        // Assert
        String result = stringWriter.toString();
        assertTrue(result.contains("2026-06-15T10:20:30"));
    }
}
