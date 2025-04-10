package io.github.guoshiqiufeng.dify.dataset.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/09 17:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaData implements Serializable {

    private static final long serialVersionUID = 1083376788375284035L;

    private String id;

    private String type;

    private String name;

    private String value;
}
