/*
 * Copyright (c) 2023-2023, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.core.enums.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the {@link MessageFileTransferMethodEnum} class.
 *
 * @author yanghq
 * @version 1.1.0
 * @since 2023/5/30 13:17
 */
public class MessageFileTransferMethodEnumTest {

    @ParameterizedTest
    @EnumSource(MessageFileTransferMethodEnum.class)
    void shouldHaveValidEnumValues(MessageFileTransferMethodEnum method) {
        assertNotNull(method);
        assertNotNull(method.name());
    }

    @Test
    void shouldHaveExpectedEnumValues() {
        assertEquals(2, MessageFileTransferMethodEnum.values().length);
        assertEquals("remote_url", MessageFileTransferMethodEnum.remote_url.name());
        assertEquals("local_file", MessageFileTransferMethodEnum.local_file.name());
    }
}