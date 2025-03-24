package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:36
 */
@Data
public class TextInput implements Serializable {

    /**
     * label (string) 控件展示标签名
     */
    @JsonAlias("label")
    private String label;
    /**
     * variable (string) 控件 ID
     */
    @JsonAlias("variable")
    private String variable;
    /**
     * required (bool) 是否必填
     */
    @JsonAlias("required")
    private Boolean required;

    @JsonAlias("max_length")
    private Integer maxLength;
    /**
     * default (string) 默认值
     */
    @JsonAlias("default")
    private String defaultValue;
}
