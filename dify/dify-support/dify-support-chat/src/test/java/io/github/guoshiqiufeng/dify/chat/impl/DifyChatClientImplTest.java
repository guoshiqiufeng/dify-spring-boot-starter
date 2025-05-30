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
package io.github.guoshiqiufeng.dify.chat.impl;

import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.chat.dto.response.AppSiteResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DifyChatClientImpl}
 *
 * @author yanghq
 * @version 1.1.0
 * @since 2024/5/30 14:20
 */
@ExtendWith(MockitoExtension.class)
public class DifyChatClientImplTest {
    @Mock
    private DifyChatClient difyChatClient;

    @InjectMocks
    private DifyChatClientImpl difyChatClientImpl;

    @Test
    public void testSite() {
        // Setup
        AppSiteResponse expectedResponse = new AppSiteResponse();
        when(difyChatClient.site(anyString())).thenReturn(expectedResponse);
        // Execute
        AppSiteResponse result = difyChatClientImpl.site("test-api-key");
        // Verify
        assertNotNull(result);
        assertEquals(expectedResponse, result);
    }
}