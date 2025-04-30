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
package io.github.guoshiqiufeng.dify.client.spring5.logging;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DifyLoggingControl单元测试
 *
 * @author yanghq
 * @version 0.11.0
 * @since 2025/4/29 17:30
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DifyLoggingControlTest {

    @BeforeEach
    void setUp() {
        // 重置状态，确保每个测试都从干净的状态开始
        DifyLoggingControl.getInstance().reset();
    }

    @Test
    @Order(1)
    @DisplayName("testSingleton")
    void testSingleton() {
        DifyLoggingControl instance1 = DifyLoggingControl.getInstance();
        DifyLoggingControl instance2 = DifyLoggingControl.getInstance();

        assertNotNull(instance1, "单例实例不应为空");
        assertSame(instance1, instance2, "getInstance()应返回相同的实例");
    }

    @Test
    @Order(4)
    @DisplayName("testGetAndMarkFilterFirstTime")
    void testGetAndMarkFilterFirstTime() {
        DifyLoggingControl instance = DifyLoggingControl.getInstance();

        DifyLoggingFilter filter = instance.getAndMarkFilter();
        assertNotNull(filter, "首次获取过滤器不应为空");
    }

    @Test
    @Order(5)
    @DisplayName("testGetAndMarkFilterSecondTime")
    void testGetAndMarkFilterSecondTime() {
        DifyLoggingControl instance = DifyLoggingControl.getInstance();

        // 首次获取
        DifyLoggingFilter firstFilter = instance.getAndMarkFilter();
        assertNotNull(firstFilter, "首次获取过滤器不应为空");

        // 再次获取
        DifyLoggingFilter secondFilter = instance.getAndMarkFilter();
        assertNull(secondFilter, "再次获取过滤器应为空");
    }
}
