package io.github.guoshiqiufeng.dify.chat.dto.response.parameter;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 14:38
 */
@Data
public class TextToSpeech implements Serializable {

    private Boolean enabled;

    private String voice;

    private String language;
}
