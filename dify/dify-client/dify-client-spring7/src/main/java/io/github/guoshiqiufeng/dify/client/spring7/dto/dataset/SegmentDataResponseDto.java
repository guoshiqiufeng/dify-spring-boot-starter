package io.github.guoshiqiufeng.dify.client.spring7.dto.dataset;

import io.github.guoshiqiufeng.dify.dataset.dto.response.SegmentData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;

/**
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/11 19:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = SegmentDataResponseDtoDeserializer.class)
public class SegmentDataResponseDto implements Serializable {

    private SegmentData data;
}
