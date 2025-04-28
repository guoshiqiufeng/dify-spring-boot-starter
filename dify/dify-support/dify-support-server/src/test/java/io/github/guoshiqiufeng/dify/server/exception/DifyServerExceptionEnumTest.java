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
package io.github.guoshiqiufeng.dify.server.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for {@link DifyServerExceptionEnum}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class DifyServerExceptionEnumTest {

    @Test
    public void testGetCode() {
        // Test the code values for each enum constant
        assertEquals(Integer.valueOf(39910), DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE.getCode());
        assertEquals(Integer.valueOf(29990), DifyServerExceptionEnum.DIFY_API_ERROR.getCode());
    }

    @Test
    public void testGetMsg() {
        // Test the message values for each enum constant
        assertEquals("数据解析失败", DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE.getMsg());
        assertEquals("远程调用失败", DifyServerExceptionEnum.DIFY_API_ERROR.getMsg());
    }

    @Test
    public void testEnumValues() {
        // Ensure we have the expected number of enum values
        assertEquals(2, DifyServerExceptionEnum.values().length);

        // Verify that valueOf works as expected
        assertEquals(DifyServerExceptionEnum.DIFY_DATA_PARSING_FAILURE,
                DifyServerExceptionEnum.valueOf("DIFY_DATA_PARSING_FAILURE"));
        assertEquals(DifyServerExceptionEnum.DIFY_API_ERROR,
                DifyServerExceptionEnum.valueOf("DIFY_API_ERROR"));
    }

    @Test
    public void testEnumConstruction() {
        // Verify that all enum values have non-null code and message
        for (DifyServerExceptionEnum exceptionEnum : DifyServerExceptionEnum.values()) {
            assertNotNull(exceptionEnum.getCode());
            assertNotNull(exceptionEnum.getMsg());
        }
    }
}
