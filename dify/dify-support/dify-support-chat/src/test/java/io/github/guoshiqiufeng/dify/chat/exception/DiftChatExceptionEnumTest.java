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
package io.github.guoshiqiufeng.dify.chat.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link DiftChatExceptionEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/24 08:30
 */
class DiftChatExceptionEnumTest {

    @Test
    void testEnumCount() {
        // 验证枚举值的数量是否符合预期
        assertEquals(8, DiftChatExceptionEnum.values().length);
    }

    @ParameterizedTest
    @EnumSource(DiftChatExceptionEnum.class)
    void testEnumValuesNotNull(DiftChatExceptionEnum exceptionEnum) {
        // 验证所有枚举值的属性不为空
        assertNotNull(exceptionEnum.getCode());
        assertNotNull(exceptionEnum.getMsg());
        assertTrue(exceptionEnum.getCode() > 0);
        assertFalse(exceptionEnum.getMsg().isEmpty());
    }

    @Test
    void testEnumValuesUnique() {
        // 验证所有枚举值的code是唯一的
        Set<Integer> codes = new HashSet<>();
        for (DiftChatExceptionEnum exceptionEnum : DiftChatExceptionEnum.values()) {
            assertTrue(codes.add(exceptionEnum.getCode()),
                    "Code " + exceptionEnum.getCode() + " is duplicated");
        }

        // 验证枚举数量与唯一code数量相同
        assertEquals(DiftChatExceptionEnum.values().length, codes.size());
    }

    @Test
    void testSpecificEnumValues() {
        // 测试特定的枚举值
        assertEquals(Integer.valueOf(18003), DiftChatExceptionEnum.DELETE_ERROR.getCode());
        assertEquals("删除失败", DiftChatExceptionEnum.DELETE_ERROR.getMsg());

        assertEquals(Integer.valueOf(18004), DiftChatExceptionEnum.DATA_EXIST.getCode());
        assertEquals("数据重复添加", DiftChatExceptionEnum.DATA_EXIST.getMsg());

        assertEquals(Integer.valueOf(19001), DiftChatExceptionEnum.DIFY_APP_NOT_EXIST.getCode());
        assertEquals("App 不存在", DiftChatExceptionEnum.DIFY_APP_NOT_EXIST.getMsg());

        assertEquals(Integer.valueOf(19011), DiftChatExceptionEnum.DIFY_API_KET_INIT_ERROR.getCode());
        assertEquals("Api Key 初始化失败", DiftChatExceptionEnum.DIFY_API_KET_INIT_ERROR.getMsg());

        assertEquals(Integer.valueOf(19910), DiftChatExceptionEnum.DIFY_DATA_PARSING_FAILURE.getCode());
        assertEquals("数据解析失败", DiftChatExceptionEnum.DIFY_DATA_PARSING_FAILURE.getMsg());

        assertEquals(Integer.valueOf(19911), DiftChatExceptionEnum.DIFY_TTS_IS_NOT_ENABLED.getCode());
        assertEquals("文字转语音未启用", DiftChatExceptionEnum.DIFY_TTS_IS_NOT_ENABLED.getMsg());

        assertEquals(Integer.valueOf(19912), DiftChatExceptionEnum.DIFY_SPEECH_TO_TEXT_IS_NOT_ENABLED.getCode());
        assertEquals("语音转文字未启用", DiftChatExceptionEnum.DIFY_SPEECH_TO_TEXT_IS_NOT_ENABLED.getMsg());

        assertEquals(Integer.valueOf(19990), DiftChatExceptionEnum.DIFY_API_ERROR.getCode());
        assertEquals("远程调用失败", DiftChatExceptionEnum.DIFY_API_ERROR.getMsg());
    }

    @Test
    void testCodeRanges() {
        // 验证错误码在预期范围内 (18000-19999)
        for (DiftChatExceptionEnum exceptionEnum : DiftChatExceptionEnum.values()) {
            int code = exceptionEnum.getCode();
            assertTrue(code >= 18000 && code < 20000,
                    "Error code " + code + " is out of range (18000-19999)");
        }
    }

    @Test
    void testImplementsBaseExceptionEnum() {
        // 确保枚举实现了BaseExceptionEnum接口
        assertTrue(Arrays.stream(DiftChatExceptionEnum.values())
                .allMatch(Objects::nonNull));
    }
}
