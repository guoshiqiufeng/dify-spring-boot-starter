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
package io.github.guoshiqiufeng.dify.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseExceptionEnumTest {

    // Test implementation of BaseExceptionEnum for testing
    private enum TestExceptionEnum implements BaseExceptionEnum {
        TEST_ERROR(10001, "Test error message"),
        ANOTHER_ERROR(10002, "Another error message");

        private final Integer code;
        private final String msg;

        TestExceptionEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        @Override
        public Integer getCode() {
            return code;
        }

        @Override
        public String getMsg() {
            return msg;
        }
    }

    @Test
    void testGetCode() {
        assertEquals(Integer.valueOf(10001), TestExceptionEnum.TEST_ERROR.getCode());
        assertEquals(Integer.valueOf(10002), TestExceptionEnum.ANOTHER_ERROR.getCode());
    }

    @Test
    void testGetMsg() {
        assertEquals("Test error message", TestExceptionEnum.TEST_ERROR.getMsg());
        assertEquals("Another error message", TestExceptionEnum.ANOTHER_ERROR.getMsg());
    }

    @Test
    void testGetMessage() {
        assertEquals("Test error message", TestExceptionEnum.TEST_ERROR.getMsg());
        assertEquals("Another error message", TestExceptionEnum.ANOTHER_ERROR.getMsg());
    }

    @Test
    void testEnumValues() {
        // Ensure we have the expected number of enum values
        assertEquals(2, TestExceptionEnum.values().length);

        // Verify that valueOf works as expected
        assertEquals(TestExceptionEnum.TEST_ERROR, TestExceptionEnum.valueOf("TEST_ERROR"));
        assertEquals(TestExceptionEnum.ANOTHER_ERROR, TestExceptionEnum.valueOf("ANOTHER_ERROR"));
    }
}
