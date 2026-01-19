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
package io.github.guoshiqiufeng.dify.dataset.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DiftDatasetExceptionEnum
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/01/19
 */
class DiftDatasetExceptionEnumTest {

    @Test
    void testDeleteError() {
        DiftDatasetExceptionEnum exception = DiftDatasetExceptionEnum.DELETE_ERROR;

        assertEquals(38003, exception.getCode());
        assertEquals("删除失败", exception.getMsg());
    }

    @Test
    void testDataExist() {
        DiftDatasetExceptionEnum exception = DiftDatasetExceptionEnum.DATA_EXIST;

        assertEquals(38004, exception.getCode());
        assertEquals("数据重复添加", exception.getMsg());
    }

    @Test
    void testDifyAppNotExist() {
        DiftDatasetExceptionEnum exception = DiftDatasetExceptionEnum.DIFY_APP_NOT_EXIST;

        assertEquals(39001, exception.getCode());
        assertEquals("App 不存在", exception.getMsg());
    }

    @Test
    void testDifyApiKeyInitError() {
        DiftDatasetExceptionEnum exception = DiftDatasetExceptionEnum.DIFY_API_KET_INIT_ERROR;

        assertEquals(39011, exception.getCode());
        assertEquals("Api Key 初始化失败", exception.getMsg());
    }

    @Test
    void testDifyDataParsingFailure() {
        DiftDatasetExceptionEnum exception = DiftDatasetExceptionEnum.DIFY_DATA_PARSING_FAILURE;

        assertEquals(39910, exception.getCode());
        assertEquals("数据解析失败", exception.getMsg());
    }

    @Test
    void testDifyApiError() {
        DiftDatasetExceptionEnum exception = DiftDatasetExceptionEnum.DIFY_API_ERROR;

        assertEquals(39990, exception.getCode());
        assertEquals("远程调用失败", exception.getMsg());
    }

    @Test
    void testEnumValues() {
        DiftDatasetExceptionEnum[] values = DiftDatasetExceptionEnum.values();

        assertNotNull(values);
        assertEquals(6, values.length);

        // Verify all enum constants are present
        assertTrue(containsEnum(values, DiftDatasetExceptionEnum.DELETE_ERROR));
        assertTrue(containsEnum(values, DiftDatasetExceptionEnum.DATA_EXIST));
        assertTrue(containsEnum(values, DiftDatasetExceptionEnum.DIFY_APP_NOT_EXIST));
        assertTrue(containsEnum(values, DiftDatasetExceptionEnum.DIFY_API_KET_INIT_ERROR));
        assertTrue(containsEnum(values, DiftDatasetExceptionEnum.DIFY_DATA_PARSING_FAILURE));
        assertTrue(containsEnum(values, DiftDatasetExceptionEnum.DIFY_API_ERROR));
    }

    @Test
    void testEnumValueOf() {
        assertEquals(DiftDatasetExceptionEnum.DELETE_ERROR,
                DiftDatasetExceptionEnum.valueOf("DELETE_ERROR"));
        assertEquals(DiftDatasetExceptionEnum.DATA_EXIST,
                DiftDatasetExceptionEnum.valueOf("DATA_EXIST"));
        assertEquals(DiftDatasetExceptionEnum.DIFY_APP_NOT_EXIST,
                DiftDatasetExceptionEnum.valueOf("DIFY_APP_NOT_EXIST"));
        assertEquals(DiftDatasetExceptionEnum.DIFY_API_KET_INIT_ERROR,
                DiftDatasetExceptionEnum.valueOf("DIFY_API_KET_INIT_ERROR"));
        assertEquals(DiftDatasetExceptionEnum.DIFY_DATA_PARSING_FAILURE,
                DiftDatasetExceptionEnum.valueOf("DIFY_DATA_PARSING_FAILURE"));
        assertEquals(DiftDatasetExceptionEnum.DIFY_API_ERROR,
                DiftDatasetExceptionEnum.valueOf("DIFY_API_ERROR"));
    }

    @Test
    void testEnumValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () ->
                DiftDatasetExceptionEnum.valueOf("INVALID_ENUM"));
    }

    @Test
    void testEnumValueOfWithNull() {
        assertThrows(NullPointerException.class, () ->
                DiftDatasetExceptionEnum.valueOf(null));
    }

    @Test
    void testAllEnumCodesAreUnique() {
        DiftDatasetExceptionEnum[] values = DiftDatasetExceptionEnum.values();

        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(values[i].getCode(), values[j].getCode(),
                        "Duplicate code found: " + values[i].name() + " and " + values[j].name());
            }
        }
    }

    @Test
    void testAllEnumMessagesAreNotEmpty() {
        for (DiftDatasetExceptionEnum value : DiftDatasetExceptionEnum.values()) {
            assertNotNull(value.getMsg(), value.name() + " has null message");
            assertFalse(value.getMsg().isEmpty(), value.name() + " has empty message");
        }
    }

    private boolean containsEnum(DiftDatasetExceptionEnum[] values, DiftDatasetExceptionEnum target) {
        for (DiftDatasetExceptionEnum value : values) {
            if (value == target) {
                return true;
            }
        }
        return false;
    }
}
