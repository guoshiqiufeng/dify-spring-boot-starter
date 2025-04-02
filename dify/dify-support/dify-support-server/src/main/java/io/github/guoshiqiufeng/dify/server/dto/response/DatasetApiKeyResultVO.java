package io.github.guoshiqiufeng.dify.server.dto.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/1 13:41
 */
@Data
public class DatasetApiKeyResultVO implements Serializable {
    private static final long serialVersionUID = 5622933209445579199L;

    private List<DatasetApiKeyResponseVO> data;

}
