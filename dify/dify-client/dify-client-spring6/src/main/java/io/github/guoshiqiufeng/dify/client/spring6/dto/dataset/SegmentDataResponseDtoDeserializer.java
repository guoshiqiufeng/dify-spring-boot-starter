package io.github.guoshiqiufeng.dify.client.spring6.dto.dataset;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.guoshiqiufeng.dify.dataset.dto.response.SegmentData;

import java.io.IOException;

/**
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/11 19:55
 */
public class SegmentDataResponseDtoDeserializer extends StdDeserializer<SegmentDataResponseDto> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String CONSTANT_DATA = "data";
    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public SegmentDataResponseDtoDeserializer() {
        super(SegmentDataResponseDto.class);
    }

    @Override
    public SegmentDataResponseDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectNode root = MAPPER.readTree(p);

        JsonNode eventNode = root.get(CONSTANT_DATA);
        if (eventNode == null) {
            SegmentData segmentData = MAPPER.treeToValue(root, SegmentData.class);
            return new SegmentDataResponseDto(segmentData);
        }
        SegmentData segmentData = MAPPER.treeToValue(eventNode, SegmentData.class);
        return new SegmentDataResponseDto(segmentData);
    }
}
