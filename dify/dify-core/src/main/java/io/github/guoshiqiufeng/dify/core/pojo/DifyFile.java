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
package io.github.guoshiqiufeng.dify.core.pojo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Framework-independent file wrapper for file upload operations.
 * This class replaces Spring's MultipartFile to remove Spring dependency.
 * <p>
 * File can use :<code>DifyFile.from(file)</code> <br>
 * MultipartFile can use :<code>DifyFileConverter.from(file)</code>
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/29
 */
public class DifyFile implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Original filename
     */
    private String filename;

    /**
     * Content type (MIME type)
     */
    private String contentType;

    /**
     * File content as byte array
     */
    private byte[] content;

    /**
     * File size in bytes
     */
    private long size;

    /**
     * Default constructor
     */
    public DifyFile() {
    }

    /**
     * Constructor with all fields
     *
     * @param filename    Original filename
     * @param contentType Content type (MIME type)
     * @param content     File content as byte array
     */
    public DifyFile(String filename, String contentType, byte[] content) {
        this.filename = filename;
        this.contentType = contentType;
        this.content = content;
        this.size = content != null ? content.length : 0;
    }

    /**
     * Create DifyFile from java.io.File
     *
     * @param file The file to read
     * @return DifyFile instance
     * @throws IOException if file reading fails
     */
    public static DifyFile from(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        return from(file.toPath());
    }

    /**
     * Create DifyFile from java.nio.file.Path
     *
     * @param path The path to read
     * @return DifyFile instance
     * @throws IOException if file reading fails
     */
    public static DifyFile from(Path path) throws IOException {
        if (path == null || !Files.exists(path)) {
            throw new IllegalArgumentException("Path does not exist");
        }
        String filename = path.getFileName().toString();
        String contentType = Files.probeContentType(path);
        byte[] content = Files.readAllBytes(path);
        return new DifyFile(filename, contentType, content);
    }

    /**
     * Create DifyFile from InputStream
     *
     * @param inputStream InputStream to read from
     * @param filename    Original filename
     * @param contentType Content type (MIME type)
     * @return DifyFile instance
     * @throws IOException if stream reading fails
     */
    public static DifyFile from(InputStream inputStream, String filename, String contentType) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        buffer.flush();
        return new DifyFile(filename, contentType, buffer.toByteArray());
    }

    /**
     * Create DifyFile from byte array
     *
     * @param content     File content as byte array
     * @param filename    Original filename
     * @param contentType Content type (MIME type)
     * @return DifyFile instance
     */
    public static DifyFile from(byte[] content, String filename, String contentType) {
        return new DifyFile(filename, contentType, content);
    }

    /**
     * Get the original filename
     *
     * @return filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set the original filename
     *
     * @param filename filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Get the content type (MIME type)
     *
     * @return content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the content type (MIME type)
     *
     * @param contentType content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Get the file content as byte array
     *
     * @return file content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Get the file content as byte array
     *
     * @return file content
     */
    public byte[] getBytes() {
        return content;
    }

    /**
     * Set the file content
     *
     * @param content file content as byte array
     */
    public void setContent(byte[] content) {
        this.content = content;
        this.size = content != null ? content.length : 0;
    }

    /**
     * Get the file size in bytes
     *
     * @return file size
     */
    public long getSize() {
        return size;
    }

    /**
     * Check if the file is empty
     *
     * @return true if file is empty or null
     */
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    /**
     * Get file content as InputStream
     *
     * @return InputStream of file content
     */
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content != null ? content : new byte[0]);
    }
}
