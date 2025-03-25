package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:35
 */
@Data
public class UserInputForm implements Serializable {
    /**
     * text-input (object) 文本输入控件
     */
    @JsonAlias("text-input")
    private TextInput textInput;

    /**
     * paragraph(object) 段落文本输入控件
     */
    @JsonAlias("paragraph")
    private Paragraph paragraph;

    /**
     * select(object) 下拉控件
     */
    @JsonAlias("select")
    private Select select;
}
