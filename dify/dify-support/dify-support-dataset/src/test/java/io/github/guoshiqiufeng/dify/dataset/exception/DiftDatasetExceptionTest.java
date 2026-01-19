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

import io.github.guoshiqiufeng.dify.core.exception.BaseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DiftDatasetException
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/01/19
 */
class DiftDatasetExceptionTest {

    @Test
    void testConstructorWithDeleteError() {
        DiftDatasetException exception = new DiftDatasetException(DiftDatasetExceptionEnum.DELETE_ERROR);

        assertNotNull(exception);
        assertEquals(38003, exception.getCode());
        assertEquals("删除失败", exception.getMsg());
        assertTrue(exception instanceof BaseException);
    }

    @Test
    void testConstructorWithDataExist() {
        DiftDatasetException exception = new DiftDatasetException(DiftDatasetExceptionEnum.DATA_EXIST);

        assertNotNull(exception);
        assertEquals(38004, exception.getCode());
        assertEquals("数据重复添加", exception.getMsg());
    }

    @Test
    void testConstructorWithDifyAppNotExist() {
        DiftDatasetException exception = new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_APP_NOT_EXIST);

        assertNotNull(exception);
        assertEquals(39001, exception.getCode());
        assertEquals("App 不存在", exception.getMsg());
    }

    @Test
    void testConstructorWithDifyApiKeyInitError() {
        DiftDatasetException exception = new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_API_KET_INIT_ERROR);

        assertNotNull(exception);
        assertEquals(39011, exception.getCode());
        assertEquals("Api Key 初始化失败", exception.getMsg());
    }

    @Test
    void testConstructorWithDifyDataParsingFailure() {
        DiftDatasetException exception = new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_DATA_PARSING_FAILURE);

        assertNotNull(exception);
        assertEquals(39910, exception.getCode());
        assertEquals("数据解析失败", exception.getMsg());
    }

    @Test
    void testConstructorWithDifyApiError() {
        DiftDatasetException exception = new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_API_ERROR);

        assertNotNull(exception);
        assertEquals(39990, exception.getCode());
        assertEquals("远程调用失败", exception.getMsg());
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(DiftDatasetException.class, () -> {
            throw new DiftDatasetException(DiftDatasetExceptionEnum.DELETE_ERROR);
        });
    }

    @Test
    void testExceptionCanBeCaught() {
        try {
            throw new DiftDatasetException(DiftDatasetExceptionEnum.DATA_EXIST);
        } catch (DiftDatasetException e) {
            assertEquals(38004, e.getCode());
            assertEquals("数据重复添加", e.getMsg());
        }
    }

    @Test
    void testExceptionInheritance() {
        DiftDatasetException exception = new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_APP_NOT_EXIST);

        assertTrue(exception instanceof BaseException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    void testExceptionMessage() {
        DiftDatasetException exception = new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_API_ERROR);

        String message = exception.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("39990") || message.contains("远程调用失败"));
    }

    @Test
    void testMultipleExceptionInstances() {
        DiftDatasetException exception1 = new DiftDatasetException(DiftDatasetExceptionEnum.DELETE_ERROR);
        DiftDatasetException exception2 = new DiftDatasetException(DiftDatasetExceptionEnum.DATA_EXIST);

        assertNotEquals(exception1.getCode(), exception2.getCode());
        assertNotEquals(exception1.getMsg(), exception2.getMsg());
    }

    @Test
    void testExceptionWithAllEnumValues() {
        for (DiftDatasetExceptionEnum enumValue : DiftDatasetExceptionEnum.values()) {
            DiftDatasetException exception = new DiftDatasetException(enumValue);

            assertNotNull(exception);
            assertEquals(enumValue.getCode(), exception.getCode());
            assertEquals(enumValue.getMsg(), exception.getMsg());
        }
    }
}
