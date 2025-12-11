package io.github.guoshiqiufeng.dify.client.spring7.dto.dataset;


import io.github.guoshiqiufeng.dify.dataset.dto.response.SegmentData;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/11 19:55
 */
public class SegmentDataResponseDeserializer extends StdDeserializer<SegmentDataResponse> {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private static final String CONSTANT_DATA = "data";

    public SegmentDataResponseDeserializer() {
        super(SegmentDataResponse.class);
    }

    @Override
    public SegmentDataResponse deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        JsonNode root = MAPPER.readTree(p);

        JsonNode eventNode = root.get(CONSTANT_DATA);
        if (eventNode == null) {
            SegmentData segmentData = MAPPER.treeToValue(root, SegmentData.class);
            return new SegmentDataResponse(segmentData);
        }
        SegmentData segmentData = MAPPER.treeToValue(eventNode, SegmentData.class);
        return new SegmentDataResponse(segmentData);
    }


}
