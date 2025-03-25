package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:32
 */
@Data
public class FileUploadImage implements Serializable {

    private static final long serialVersionUID = 2405030232637136714L;

    /**
     * enabled(bool) 是否开启
     */
    @JsonAlias("enabled")
    private Boolean enabled;
    /**
     * number_limits(int) 图片数量限制，默认 3
     */
    @JsonAlias("number_limits")
    private Integer numberLimits;

    /**
     * transfer_methods(array[string]) 传递方式列表，remote_url ,local_file， 必选一个
     */
    @JsonAlias("transfer_methods")
    private List<String> transferMethods;


}
