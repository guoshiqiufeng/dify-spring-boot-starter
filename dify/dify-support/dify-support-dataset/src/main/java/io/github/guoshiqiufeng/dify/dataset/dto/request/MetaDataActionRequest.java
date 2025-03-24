package io.github.guoshiqiufeng.dify.dataset.dto.request;

import io.github.guoshiqiufeng.dify.dataset.enums.MetaDataActionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/24 10:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MetaDataActionRequest extends BaseDatasetRequest implements Serializable {
    private static final long serialVersionUID = 6701409042040093766L;

    private String datasetId;

    private MetaDataActionEnum action;
}
