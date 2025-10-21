package io.github.guoshiqiufeng.dify.dataset.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.4.5
 * @since 2025/10/21 11:47
 */
@Data
public class Tag implements Serializable {
    private static final long serialVersionUID = 7599432735911237790L;

    private String id;

    private String name;

    private String type;
}
