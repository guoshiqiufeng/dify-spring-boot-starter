package io.github.guoshiqiufeng.dify.dataset.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/5/15 14:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetInfoRequest extends BaseDatasetRequest implements Serializable {

    private String datasetId;
}
