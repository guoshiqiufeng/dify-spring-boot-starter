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
package io.github.guoshiqiufeng.dify.core.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for BaseDifyClient
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class BaseDifyClientTest {

    /**
     * Test error message constant in BaseDifyClient
     */
    @Test
    public void testRequestBodyNullErrorConstant() {
        assertEquals("The request body can not be null.", BaseDifyClient.REQUEST_BODY_NULL_ERROR);
    }

    /**
     * Test default URL constant in BaseDifyClient
     */
    @Test
    public void testDefaultBaseUrlConstant() {
        assertEquals("http://localhost", BaseDifyClient.DEFAULT_BASE_URL);
    }

    /**
     * Test the implementation of BaseDifyClient subclass
     */
    @Test
    public void testBaseDifyClientImplementation() {
        // Create an anonymous subclass instance
        BaseDifyClient client = new BaseDifyClient() {
            // No methods to implement, as BaseDifyClient has no abstract methods
        };

        // Verify constants can be accessed by subclasses
        assertNotNull(client);
        assertEquals(BaseDifyClient.REQUEST_BODY_NULL_ERROR, client.REQUEST_BODY_NULL_ERROR);
        assertEquals(BaseDifyClient.DEFAULT_BASE_URL, client.DEFAULT_BASE_URL);
    }
}
