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
package io.github.guoshiqiufeng.dify.server.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link DifyLoginRequest}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class DifyLoginRequestTest {

    @Test
    public void testDefaultConstructor() {
        // Act
        DifyLoginRequest vo = new DifyLoginRequest();

        // Assert
        assertNull(vo.getEmail());
        assertNull(vo.getPassword());
        assertNull(vo.getLanguage());
        assertNull(vo.getRememberMe());
    }

    @Test
    public void testSettersAndGetters() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String language = "en-US";
        Boolean rememberMe = false;

        // Act
        DifyLoginRequest vo = new DifyLoginRequest();
        vo.setEmail(email);
        vo.setPassword(password);
        vo.setLanguage(language);
        vo.setRememberMe(rememberMe);

        // Assert
        assertEquals(email, vo.getEmail());
        assertEquals(password, vo.getPassword());
        assertEquals(language, vo.getLanguage());
        assertEquals(rememberMe, vo.getRememberMe());
    }

    @Test
    public void testBuildMethod() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        // Act
        DifyLoginRequest vo = DifyLoginRequest.build(email, password);

        // Assert
        assertEquals(email, vo.getEmail());
        assertEquals(password, vo.getPassword());
        assertEquals("zh-Hans", vo.getLanguage());
        assertTrue(vo.getRememberMe());
    }

    @Test
    public void testBuildMethodWithNullValues() {
        // Act
        DifyLoginRequest vo = DifyLoginRequest.build(null, null);

        // Assert
        assertNull(vo.getEmail());
        assertNull(vo.getPassword());
        assertEquals("zh-Hans", vo.getLanguage());
        assertTrue(vo.getRememberMe());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        DifyLoginRequest vo1 = new DifyLoginRequest();
        vo1.setEmail("test@example.com");
        vo1.setPassword("password123");
        vo1.setLanguage("zh-Hans");
        vo1.setRememberMe(true);

        DifyLoginRequest vo2 = new DifyLoginRequest();
        vo2.setEmail("test@example.com");
        vo2.setPassword("password123");
        vo2.setLanguage("zh-Hans");
        vo2.setRememberMe(true);

        DifyLoginRequest vo3 = new DifyLoginRequest();
        vo3.setEmail("different@example.com");
        vo3.setPassword("password123");
        vo3.setLanguage("zh-Hans");
        vo3.setRememberMe(true);

        // Assert
        assertEquals(vo1, vo2);
        assertEquals(vo1.hashCode(), vo2.hashCode());
        assertNotEquals(vo1, vo3);
        assertNotEquals(vo1.hashCode(), vo3.hashCode());
    }

    @Test
    public void testSerializable() {
        // Just verify that the class implements Serializable
        DifyLoginRequest vo = new DifyLoginRequest();
        assertTrue(vo instanceof java.io.Serializable);
    }
}
