package io.github.guoshiqiufeng.dify.dataset.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 10:43
 */
@Data
public class MetaDataResponse implements Serializable {

    private String id;

    /**
     * 元数据类型，必填
     */
    private String type;

    /**
     * 元数据名称，必填
     */
    private String name;
}
