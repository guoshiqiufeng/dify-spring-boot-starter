package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Select extends TextInput implements Serializable {

    private String type;

    private List<String> options;
}
