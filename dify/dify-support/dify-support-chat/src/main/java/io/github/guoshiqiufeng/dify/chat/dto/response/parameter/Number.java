package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author yanghq
 * @version 1.4.4
 * @since 2025/9/25 14:32
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Number extends TextInput {

    private String type;

    private List<String> options;

    private String placeholder;

    private String hint;
}
