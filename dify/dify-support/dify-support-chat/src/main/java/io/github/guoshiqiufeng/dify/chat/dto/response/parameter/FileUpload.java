package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:31
 */
@Data
public class FileUpload implements Serializable {
    private static final long serialVersionUID = -7707822045842729660L;
    /**
     * image(object) 图片设置 当前仅支持图片类型：png,jpg,jpeg,webp,gif
     */
    @JsonAlias("image")
    private FileUploadImage image;

    private Boolean enabled;

    @JsonAlias("allowed_file_types")
    private List<String> allowedFileTypes;

    @JsonAlias("allowed_file_extensions")
    private List<String> allowedFileExtensions;

    @JsonAlias("allowed_file_upload_methods")
    private List<String> allowedFileUploadMethods;

    @JsonAlias("number_limits")
    private Integer numberLimits;

    private FileUploadConfig fileUploadConfig;

}
