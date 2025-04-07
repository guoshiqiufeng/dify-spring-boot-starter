package io.github.guoshiqiufeng.dify.chat.utils;

import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

/**
 * 处理 MultipartFile
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/7 16:20
 */
public class MultipartInputStreamFileResource extends InputStreamResource {
    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() {
        return -1;
    }
}
