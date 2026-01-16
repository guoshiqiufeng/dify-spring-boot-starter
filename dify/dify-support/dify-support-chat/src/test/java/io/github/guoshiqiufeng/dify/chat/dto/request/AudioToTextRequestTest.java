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
///*
// * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package io.github.guoshiqiufeng.dify.chat.dto.request;
//
//import io.github.guoshiqiufeng.dify.core.pojo.DifyFile;
//import org.junit.jupiter.api.Test;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Test for {@link AudioToTextRequest}
// *
// * @author yanghq
// * @version 0.10.0
// * @since 2025/4/27
// */
//public class AudioToTextRequestTest {
//
//    @Test
//    public void testGetterAndSetter() {
//        // Arrange
//        AudioToTextRequest request = new AudioToTextRequest();
//        MultipartFile file = new MockMultipartFile("test-file", "audio.mp3", "audio/mpeg", "test audio content".getBytes());
//
//        // Act
//        request.setFile(file);
//
//        // Assert
//        assertEquals(file, request.getFile());
//    }
//
//    @Test
//    public void testInheritedFields() {
//        // Arrange
//        AudioToTextRequest request = new AudioToTextRequest();
//        String apiKey = "test-api-key";
//        String userId = "test-user-id";
//
//        // Act
//        request.setApiKey(apiKey);
//        request.setUserId(userId);
//
//        // Assert
//        assertEquals(apiKey, request.getApiKey());
//        assertEquals(userId, request.getUserId());
//    }
//
//    @Test
//    public void testEqualsAndHashCode() {
//        // Arrange
//        MultipartFile file1 = new MockMultipartFile("test-file", "audio1.mp3", "audio/mpeg", "content1".getBytes());
//        MultipartFile file2 = new MockMultipartFile("test-file", "audio2.mp3", "audio/mpeg", "content2".getBytes());
//
//        AudioToTextRequest request1 = new AudioToTextRequest();
//        request1.setFile(file1);
//
//        AudioToTextRequest request2 = new AudioToTextRequest();
//        request2.setFile(file1);
//
//        AudioToTextRequest request3 = new AudioToTextRequest();
//        request3.setFile(file2);
//
//        // Assert
//        assertEquals(request1, request2);
//        assertEquals(request1.hashCode(), request2.hashCode());
//        assertNotEquals(request1, request3);
//        assertNotEquals(request1.hashCode(), request3.hashCode());
//    }
//
//    @Test
//    public void testToString() {
//        // Arrange
//        MultipartFile file = new MockMultipartFile("test-file", "audio.mp3", "audio/mpeg", "test audio content".getBytes());
//        AudioToTextRequest request = new AudioToTextRequest();
//        request.setFile(file);
//
//        // Act
//        String toString = request.toString();
//
//        // Assert
//        assertTrue(toString.contains("file=" + file));
//    }
//}
