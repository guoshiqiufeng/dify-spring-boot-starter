package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:44
 */
@Data
public class FileUploadConfig implements Serializable {

    @JsonAlias("file_size_limit")
    private Integer fileSizeLimit;
    @JsonAlias("batch_count_limit")
    private Integer batchCountLimit;
    @JsonAlias("image_file_size_limit")
    private Integer imageFileSizeLimit;
    @JsonAlias("video_file_size_limit")
    private Integer videoFileSizeLimit;
    @JsonAlias("audio_file_size_limit")
    private Integer audioFileSizeLimit;
    @JsonAlias("workflow_file_upload_limit")
    private Integer workflowFileUploadLimit;
}
