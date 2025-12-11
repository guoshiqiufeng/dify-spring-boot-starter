package io.github.guoshiqiufeng.dify.client.spring5.dto.dataset;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.guoshiqiufeng.dify.dataset.dto.response.SegmentData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/11 19:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = SegmentDataResponseDeserializer.class)
public class SegmentDataResponse implements Serializable {

    private SegmentData data;
}
