package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:30
 */
@Data
public class Enabled implements Serializable {
    /**
     * 是否开启
     */
    private Boolean enabled;
}
