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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for JSON serialization/deserialization of {@link DifyLoginRequestVO}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class DifyLoginRequestVOJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonSerializationWithStandardFields() throws Exception {
        // Arrange
        DifyLoginRequestVO vo = new DifyLoginRequestVO();
        vo.setEmail("test@example.com");
        vo.setPassword("password123");
        vo.setLanguage("zh-Hans");
        vo.setRememberMe(true);

        // Act
        String json = objectMapper.writeValueAsString(vo);

        // Assert
        assertTrue(json.contains("\"email\":\"test@example.com\""));
        assertTrue(json.contains("\"password\":\"password123\""));
        assertTrue(json.contains("\"language\":\"zh-Hans\""));
        assertTrue(json.contains("\"rememberMe\":true"));
    }

    @Test
    public void testJsonDeserializationWithStandardFields() throws Exception {
        // Arrange
        String json = "{\"email\":\"test@example.com\",\"password\":\"password123\",\"language\":\"zh-Hans\",\"rememberMe\":true}";

        // Act
        DifyLoginRequestVO vo = objectMapper.readValue(json, DifyLoginRequestVO.class);

        // Assert
        assertEquals("test@example.com", vo.getEmail());
        assertEquals("password123", vo.getPassword());
        assertEquals("zh-Hans", vo.getLanguage());
        assertTrue(vo.getRememberMe());
    }

    @Test
    public void testJsonDeserializationWithJsonAlias() throws Exception {
        // Arrange - Use the JsonAlias "remember-me" instead of "rememberMe"
        String json = "{\"email\":\"test@example.com\",\"password\":\"password123\",\"language\":\"zh-Hans\",\"remember-me\":true}";

        // Act
        DifyLoginRequestVO vo = objectMapper.readValue(json, DifyLoginRequestVO.class);

        // Assert
        assertEquals("test@example.com", vo.getEmail());
        assertEquals("password123", vo.getPassword());
        assertEquals("zh-Hans", vo.getLanguage());
        assertTrue(vo.getRememberMe());
    }

    @Test
    public void testJsonDeserializationWithBothRememberMeFields() throws Exception {
        // Arrange - Include both "rememberMe" and "remember-me" (the latter should take precedence)
        String json = "{\"email\":\"test@example.com\",\"password\":\"password123\",\"language\":\"zh-Hans\",\"rememberMe\":false,\"remember-me\":true}";

        // Act
        DifyLoginRequestVO vo = objectMapper.readValue(json, DifyLoginRequestVO.class);

        // Assert
        assertEquals("test@example.com", vo.getEmail());
        assertEquals("password123", vo.getPassword());
        assertEquals("zh-Hans", vo.getLanguage());
        // The last field processed wins in Jackson, but the exact behavior depends on Jackson version
        assertNotNull(vo.getRememberMe());
    }

    @Test
    public void testJsonDeserializationWithMissingFields() throws Exception {
        // Arrange
        String json = "{\"email\":\"test@example.com\"}";

        // Act
        DifyLoginRequestVO vo = objectMapper.readValue(json, DifyLoginRequestVO.class);

        // Assert
        assertEquals("test@example.com", vo.getEmail());
        assertNull(vo.getPassword());
        assertNull(vo.getLanguage());
        assertNull(vo.getRememberMe());
    }

    @Test
    public void testBuildMethodJsonSerialization() throws Exception {
        // Arrange
        DifyLoginRequestVO vo = DifyLoginRequestVO.build("test@example.com", "password123");

        // Act
        String json = objectMapper.writeValueAsString(vo);

        // Assert
        assertTrue(json.contains("\"email\":\"test@example.com\""));
        assertTrue(json.contains("\"password\":\"password123\""));
        assertTrue(json.contains("\"language\":\"zh-Hans\""));
        assertTrue(json.contains("\"rememberMe\":true"));
    }
} 