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
package io.github.guoshiqiufeng.dify.chat.dto.response.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for {@link CompletionData}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class CompletionDataTest {

    @Test
    public void testCreation() {
        // Arrange & Act
        CompletionData completionData = new CompletionData();

        // Assert
        assertNotNull(completionData);
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        CompletionData completionData1 = new CompletionData();
        CompletionData completionData2 = new CompletionData();

        // Assert
        assertNotEquals(completionData1, completionData2);
        assertNotEquals(completionData1.hashCode(), completionData2.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        CompletionData completionData = new CompletionData();

        // Act
        String toString = completionData.toString();

        // Assert
        assertNotNull(toString);
    }
}
