package io.github.guoshiqiufeng.dify.dataset.dto.request.file;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/17 17:28
 */
public interface FileOperation {

    public void setFile(MultipartFile file);
}
